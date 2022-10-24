//DaPlane.java
//determine plane coordinates within [-1..1, -1..1] for multiple configurations
//looks through the folder of signatures, randomly assigns starting points
//and then moves them bit by bit towards those signatures which are most similar
//there is a repeling "force" so all the points don't clump together


/*
--------------------------------------------------------------------------
Copyright 2021 Adventium Enterprises, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--------------------------------------------------------------------------
*/

package com.adventium.design.exploration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.adventium.design.exploration.antlr3generated.MakeTypeToImplementationMapParser;
import com.adventium.design.exploration.ui.preferences.ConfigurationPreferencePage;


/**
 * Calculate X-Y position of designs so that similar designs are closer
 * in the X-Y plane [-1..1]
 * @author brl
 *
 */
public class DaPlane
{
/**
 * Record to hold design information for plotting X-Y position
 * @author brl
 *
 */
  public static class Record
  {
  String signatureFile;  //file name of signature file of design
  int index;  //index number of this design, center design is index 0
  double x;  //current X-coordinate
  double y;  //current Y-coordinate
  String[] typeImplPair;  //array of implementation selections for type subcomponents
  
  } //end of Record

  public static String daplaneFolderName = "DaPlane";
  public static String xyFileName = "xy";

/**
 * move scale factor
 */
static double scaleFactor = 0.01;
static int iterationLimit = 1000;
static double repulsionFactor = 10.0;
static double targetMaxDistance = 1.4;

/**
 * move threshold
 */
static double threshold = 0.1;

/**
 * allow center to move
 */
//public static boolean allowCenterToMove = false;

/**
 * display points
 */
//public static boolean displayPoints = true; 

/**
 * array of design records
 */
static Record[] r;

/**
 * similarity between designs
 * How many implementation selections are the same?
 */
static int[][] similarity; 

/**
 * find X-Y coordinates for collection of design alternatives
 */
  public static void 
findDesignXY()
  {
  Random numberGenerator = new Random();
  IFolder signatureFolder = ResourcesPlugin.getWorkspace().getRoot()
    .getProject(WriteConfigFile.configProjectName).getFolder(WriteConfigFile.signatureFolderName);
  //choose center design
  Shell shell = new Shell(ConfigurationActivator.plugin.getWorkbench().getDisplay());
  FileDialog dialog = new FileDialog(shell, SWT.OPEN);
  dialog.open();
  String filterPath = dialog.getFilterPath();
  String fileName = dialog.getFileName();
  String centerDesignSignatureFileName = filterPath + "/" + fileName;
  Say.it("You chose file: " + centerDesignSignatureFileName+" as the center design.");
  shell.dispose();
  //find out how many signatures are in folder with center design
  IResource[] resources = null;
  try { resources = signatureFolder.members(); }
  catch (CoreException e)
    { Say.it(e.getMessage()); e.printStackTrace(); }
  int numDesigns=resources.length;
  
  //create arrays
  r = new Record[numDesigns];
  similarity = new int[numDesigns][numDesigns];
  //put center design in r
  r[0] = new Record();
  r[0].signatureFile = fileName;
  r[0].index = 0;
  r[0].x = 0.0;
  r[0].y = 0.0;
  //load type-implementation pairs for center design
  InstantiateConfiguredModel.loadTypeToImplementationMap(centerDesignSignatureFileName);
//  FileInputStream fileStream = null;
//  try { fileStream = new FileInputStream(centerDesignSignatureFileName);}
//  catch (FileNotFoundException e)
//    { Say.it("File not found:  "+centerDesignSignatureFileName);  e.printStackTrace(); }
//String fileString = fileStream.toString();
String fileString = MakeTypeToImplementationMapParser.typeToImplementation.toString();
  r[0].typeImplPair = fileString.substring(1,fileString.length()-1).split(", ");
  int i=1;
  for (IResource resource : resources)
    if (!resource.getName().equalsIgnoreCase(fileName)&&!resource.getName().startsWith("."))
    {
    r[i] = new Record();
    r[i].signatureFile = resource.getName();
    r[i].index = i;
    //generate random double between -1 and 1
    r[i].x = (numberGenerator.nextDouble()*2)-1.0;
    r[i].y = (numberGenerator.nextDouble()*2)-1.0;
    Say.it("node "+i+" start at ["+r[i].x+","+r[i].y+"]");
//    try { fileStream = new FileInputStream(filterPath + "/" + resource.getName());}
//    catch (FileNotFoundException e)
//      { Say.it("File not found:  "+resource.getName());  e.printStackTrace(); }
//    fileString = fileStream.toString();
    InstantiateConfiguredModel.loadTypeToImplementationMap(filterPath + "/" +resource.getName());
    fileString = MakeTypeToImplementationMapParser.typeToImplementation.toString();
    r[i].typeImplPair = fileString.substring(1,fileString.length()-1).split(", ");
    i++;
    }  //done for (IResource
  numDesigns=i;
  //determine similarity
  int maxSimularity=0;
  for (int j=0;j<numDesigns;j++)
    for (int k=0;k<numDesigns;k++)
      {
      int count=0;
      for (int jIndex=0; jIndex<r[j].typeImplPair.length; jIndex++) 
        for (int kIndex=0; kIndex<r[k].typeImplPair.length; kIndex++) 
          {
          String jstring = r[j].typeImplPair[jIndex];
          String kstring = r[k].typeImplPair[kIndex];
          if (jstring.equalsIgnoreCase(kstring))
            count++;
          }
      similarity[j][k]=count;
      if (count>maxSimularity) maxSimularity=count;
      Say.it("similarity of "+j+" to "+k+" is "+count);
      }  //done determine similarity
  double totalMove=10.0;
  double maxDistance= 0.10;
  for (int iteration=0;(iteration<iterationLimit)&&(totalMove>threshold); iteration++)
    {
    maxDistance= 0.10;
    //determine gradient for each node
    double[] moveX = new double[numDesigns];
    double[] moveY = new double[numDesigns];
    for (int node=0;node<numDesigns;node++)
      {
      double gradX = 0.0;
      double gradY = 0.0;
      for (int other=0;other<numDesigns; other++)
        if (node!=other)
          {
          double distance = Math.sqrt(square(r[node].x-r[other].x)+square(r[node].y-r[other].y));
          //subtract similarity divided by distance^2
          if (distance>0.0)
            {
            gradX-=((r[node].x-r[other].x)*similarity[node][other])/(distance*maxSimularity);
            gradY-=((r[node].y-r[other].y)*similarity[node][other])/(distance*maxSimularity);
            //          gradX-=((r[node].x-r[other].x)*similarity[node][other])/(square(distance)*maxSimularity);
            //          gradY-=((r[node].y-r[other].y)*similarity[node][other])/(square(distance)*maxSimularity);
            //add 1/distance^3
            gradX+=(r[node].x-r[other].x)/(repulsionFactor*cube(distance));
            gradY+=(r[node].y-r[other].y)/(repulsionFactor*cube(distance)); 
            } 
          if (distance > maxDistance)
            maxDistance=distance;     
          }         
      moveX[node]=(gradX*scaleFactor);  ///(gradX*gradX+gradY*gradY);
      moveY[node]=(gradY*scaleFactor);   ///(gradX*gradX+gradY*gradY);
      }  //done determine gradient
    //now change X-Y coordinates
    boolean allowCenterToMove= ConfigurationActivator.plugin.getPreferenceStore().getBoolean(ConfigurationPreferencePage.ALLOW_CENTER_TO_MOVE);
    for (int node=(allowCenterToMove?0:1);node<numDesigns;node++)
      {
      r[node].x += moveX[node];
      r[node].y += moveY[node];
      while (r[node].x > 1.0) r[node].x-=0.1;
      while (r[node].x < -1.0) r[node].x+=0.1;
      while (r[node].y > 1.0) r[node].y-=0.1;
      while (r[node].y < -1.0) r[node].y+=0.1;
      Say.it("node "+node+" moved to ["+r[node].x+","+r[node].y+"]");
      }  //done moving  
    totalMove=0.0;
    for (int node=0;node<numDesigns;node++)
      totalMove+=Math.abs(moveX[node])+Math.abs(moveY[node]); 
    Say.it("iteration "+iteration+"; total move = "+totalMove+"; max distance = "+maxDistance);
    if (maxDistance<targetMaxDistance)
      repulsionFactor = repulsionFactor*0.9;
    } //end of while
  //make string buffer for file output  
  StringBuffer sb = new StringBuffer();
  //write-out results 
  sb.append("center starting choice:  "+centerDesignSignatureFileName+"\n");
  Say.it("center starting choice:  "+centerDesignSignatureFileName);
  for (int me=0;me<numDesigns;me++)
    {
    int closestNode=-1;
    double closestDistance= 10.0;
    for (int you=0;you<numDesigns;you++)
      if (me!=you)
        {
        double distance=Math.sqrt(square(r[me].x-r[you].x)+square(r[me].y-r[you].y));
        if (distance<closestDistance)
          {
          closestDistance=distance;
          closestNode=you;
          }
        }    
    String clo=(closestNode==-1?"Error determining closest node to "+me:
      "The closest to "+me+" is "+closestNode+" with distance of "+
      closestDistance+" and similarity of "+similarity[me][closestNode]+".");
    Say.it(clo);
    sb.append(clo+"\n");
    }
  for (int d=0;d<numDesigns;d++)
    {
    String line=r[d].index+" "+r[d].signatureFile+" "+r[d].x+" "+r[d].y;
    sb.append(line+"\n");
    Say.it(line);
    }
  writeDaPlane(sb.toString());
//  if (WorkspacePlugin.getDefault().getPreferenceStore().getBoolean(ConfigurationPreferencePage.SHOW_SIGNATURE_PLOT)) 
//    displayXYlocations(r,numDesigns);
  }  //end of findDesignXY

public static double square(double d)
  {return d*d;}

public static double cube(double d)
  {return d*d*d;}

/**
 * writes plane coordinates to a file in daplaneFolderName folder and
 * xyFileName+".txt" file
 * @param content
 */
public static void writeDaPlane(String content)
  {
  IWorkspace workspace = ResourcesPlugin.getWorkspace();
  IProject configurationProject = workspace.getRoot().getProject(WriteConfigFile.configProjectName);
  if (!configurationProject.exists())
    try
      {
      configurationProject.create(null);
      }
    catch (CoreException e1)
      {
      Say.it("CoreException when creating project \""+WriteConfigFile.configProjectName+"\"");
      e1.printStackTrace();
      }
  if (!configurationProject.isOpen())
    try
      {
      configurationProject.open(null);
      }
    catch (CoreException e1)
      {
      Say.it("CoreException when opening project \""+WriteConfigFile.configProjectName+"\"");
      e1.printStackTrace();
      }
  IFolder signatureFolder = configurationProject.getFolder(daplaneFolderName);
  if (!signatureFolder.exists())
    try
      {
      signatureFolder.create(false, false, null);
      }
    catch (CoreException e1)
      {
      Say.it("CoreException when creating folder \""+daplaneFolderName+"\"");
      e1.printStackTrace();
      }
  String fileName = xyFileName+".txt";   
  IFile xyFile = signatureFolder.getFile(fileName);
//  FileOutputStream outputStream = new FileOutputStream(signatureFile);
  try { xyFile.delete(true,false,null); }
  catch (CoreException e1)
    { }
  try
    {
	  
    xyFile.create(new FileInputStream(content), true, null);
//    xyFile.setContents(new StringBufferInputStream(content),true,false,null);
//    signatureFile.appendContents(new StringBufferInputStream(content),true,false,null);
//    signatureFile.close ???
    }
  catch (CoreException e)
    {
    Say.it("CoreException when writing \""+xyFile.getName()+"\"");
    Say.it(e.getMessage());
    e.printStackTrace();
    } catch (FileNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} 
  }  //end of writeDaPlane

//static void displayXYlocations(Record[] r, int numDesigns)
//  {
//  final Shell shell = new Shell();
//  shell.setSize(300, 250);
//  shell.open();
//
//  // use LightweightSystem to create the bridge between SWT and draw2D
//  final LightweightSystem lws = new LightweightSystem(shell);
//
//  // create a new XY Graph.
//  XYGraph xyGraph = new XYGraph();
//  xyGraph.setTitle("Design Signature Plot");
//  xyGraph.primaryYAxis.setAutoScale(true);
//  xyGraph.primaryXAxis.setAutoScale(true);
//    // set it as the content of LightwightSystem
//  lws.setContents(xyGraph);
//
//  // create a trace data provider, which will provide the data to the
//  // trace.
//  CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
//  traceDataProvider.setBufferSize(numDesigns);
//  double[] xData = new double[numDesigns]; 
//  double[] yData = new double[numDesigns]; 
//  for (int i=0;i<numDesigns;i++)
//    {
//    xData[i] = r[i].x;
//    yData[i] = r[i].y;
//    }
//  traceDataProvider.setCurrentXDataArray(xData);
//  traceDataProvider.setCurrentYDataArray(yData);
//
//  // create the trace
//  //  Trace trace = new Trace("Trace1-XY Plot", xyGraph.getPrimaryXAxis(), xyGraph.getPrimaryYAxis(), traceDataProvider);
//  Trace trace = new Trace("", xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);
//
//  // set trace property
//  trace.setPointStyle(PointStyle.XCROSS);
//
//  // add the trace to xyGraph
//  xyGraph.addTrace(trace);
//
//  Display display = Display.getDefault();
//  while (!shell.isDisposed()) 
//    {
//    if (!display.readAndDispatch())
//      display.sleep();
//    }
//  }  //end of displayXYlocations

}  //end of DaPlane
