//WriteConfigFile.java
//write-out configuration signature file
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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.adventium.design.exploration.views.ConfigurationView;

public class WriteConfigFile
{

public static String configProjectName = "Configuration";
public static String signatureFolderName = "signatures";

/**
 * write configuration signature to project configProjectName, folder signatureFolderName,
 * with filename created by a hash function 
 * @param componentName
 * @param content
 */
public static void writeConfig(String componentName, String content)
  {
  IWorkspace workspace = ResourcesPlugin.getWorkspace();
  IProject configurationProject = workspace.getRoot().getProject(configProjectName);
  if (!configurationProject.exists())
    try
      {
      configurationProject.create(null);
      }
    catch (CoreException e1)
      {
      Say.it("CoreException when creating project \""+configProjectName+"\"");
      e1.printStackTrace();
      }
  if (!configurationProject.isOpen())
    try
      {
      configurationProject.open(null);
      }
    catch (CoreException e1)
      {
      Say.it("CoreException when opening project \""+configProjectName+"\"");
      e1.printStackTrace();
      }
  IFolder signatureFolder = configurationProject.getFolder(signatureFolderName);
  if (!signatureFolder.exists())
    try
      {
      signatureFolder.create(false, false, null);
      }
    catch (CoreException e1)
      {
      Say.it("CoreException when creating folder \""+signatureFolderName+"\"");
      e1.printStackTrace();
      }
  String fileName = componentName.replace('.','_')+"_"+
		  ConfigurationView.lastHash+".txt";   
//  String fileName = componentName.replace('.','_')+"_"+
//	      Integer.toHexString(content.hashCode())+".txt";   
  IFile signatureFile = signatureFolder.getFile(fileName);
//  FileOutputStream outputStream = new FileOutputStream(signatureFile);
  try { signatureFile.delete(true,false,null); }
  catch (CoreException e1)
    { }
  try
    {
    String signatureString = componentName+"\n"+content;
    signatureFile.create(new ByteArrayInputStream(signatureString.getBytes("UTF-8")), true, null);
//    signatureFile.setContents(new StringBufferInputStream(componentName+"\n"+content),true,false,null);
//    signatureFile.appendContents(new StringBufferInputStream(content),true,false,null);
//    signatureFile.close ???
    }
  catch (CoreException e)
    {
//    Dump.it("CoreException when writing \""+signatureFile.getName()+"\"");
    Say.it(e.getMessage());
    e.printStackTrace();
    }
  catch (UnsupportedEncodingException e)
    {
    Say.it("UTF-8 is not a string encoding supported on this platform.");
    e.printStackTrace();
    } 
  }  //end of writeConfig



}  //end of writeConfig
