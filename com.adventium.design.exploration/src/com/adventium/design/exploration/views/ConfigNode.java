//ConfigNode.java
// * Configuration Node for check-box tree noting selection of particular design in the space



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

package com.adventium.design.exploration.views;

import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.xtext.util.StringInputStream;
import org.osate.aadl2.ComponentClassifier;

import com.adventium.design.exploration.Say;
import com.adventium.design.exploration.WriteConfigFile;
import com.adventium.design.exploration.radio_tree.RadioTreeNode;
import com.adventium.design.exploration.radio_tree.RadioTreeViewer;

/**
 ********************************************************************************************************************
 * Configuration Node for check-box tree noting selection of particular design in the space
 * @author pahed, brl
 *
 ********************************************************************************************************************
 */
public class ConfigNode extends RadioTreeNode<ComponentClassifier> {

	  /**
	   * Name of the system implementation of this tree
	   */
	  public static String system_implementation_name = "Choose a system implemenation with the context menu";

	  /**
	   * Name of the system implementation of this tree
	   */
	  public static String package_name = "Choose a system implemenation with the context menu";

  /**
   * Identifier of the subcomponent this is, if any
   */
  private String subcomponent_id = "";

  /**
   ******************************************************************************************************************
   * Basic constructor
   * @param parent Parent node for this element. Null indicates the root node.
   * @param value Component to store in this node.
   ******************************************************************************************************************
   */
  public ConfigNode(ConfigNode parent, ComponentClassifier value) {
    super(parent, value);
  }

  /**
   ******************************************************************************************************************
   * Basic constructor with subcomponent identifier
   * @param parent Parent node for this element. Null indicates the root node.
   * @param value Component to store in this node.
   * @param subcomponent Identifier of this subcomponent
   ******************************************************************************************************************
   */
  public ConfigNode(ConfigNode parent, ComponentClassifier value, String subcomponent) {
    super(parent, value);
    subcomponent_id = subcomponent + ":";
  }

  /**
   ******************************************************************************************************************
   * Constructor for setting type of checkbox operations
   * @param parent Parent node for this element. Null indicates the root node.
   * @param value Component to store in this node.
   * @param childrenAreExclusiveIn true indicates that the children of this node should operate as radio buttons
   * otherwise normal checkbox will be used.
   ******************************************************************************************************************
   */
  public ConfigNode(ConfigNode parent, ComponentClassifier value, boolean childrenAreExclusiveIn) {
    super(parent, value, childrenAreExclusiveIn);
  }

  /**
   ******************************************************************************************************************
   * Constructor for setting type of checkbox operations, with subcomponent identifier
   * @param parent Parent node for this element. Null indicates the root node.
   * @param value Component to store in this node.
   * @param childrenAreExclusiveIn true indicates that the children of this node should operate as radio buttons
   * otherwise normal checkbox will be used.
   ******************************************************************************************************************
   */
  public ConfigNode(ConfigNode parent, ComponentClassifier value, boolean childrenAreExclusiveIn, String subcomponent) {
    super(parent, value, childrenAreExclusiveIn);
    subcomponent_id = subcomponent + ":";
  }

  /**
   ******************************************************************************************************************
   * Extracts the display name from the component
   *
   * @see com.adventium.design.exploration.radio_tree.RadioTreeNode#getDisplayName()
   ******************************************************************************************************************
   */
  @Override
  public String getDisplayName() {
    if (value == null) {
      return package_name;
    }
    return subcomponent_id + ((ComponentClassifier) value).getName();

  }
  
  /**
   ******************************************************************************************************************
   * Extracts the display name from the component with return character after package:
   *
   * @see com.adventium.design.exploration.radio_tree.RadioTreeNode#getDisplayName()
   ******************************************************************************************************************
   */
  public String getColDisplayName() {
    if (value == null) {
      return package_name;
    }
    return subcomponent_id + "\\n"+((ComponentClassifier) value).getName();

  }
  /**
   ******************************************************************************************************************
   * @see com.adventium.design.exploration.radio_tree.RadioTreeNode#shallowClone()
   ******************************************************************************************************************
   */

  @Override
  public RadioTreeNode<ComponentClassifier> shallowClone(RadioTreeNode<ComponentClassifier> parent) {
    return new ConfigNode((ConfigNode) parent, (ComponentClassifier) getValue());
  }

  /**
   * savedState is used for saving the state of this node, and
   * inASaveAndResetStateOperation is a flag to indicate we are in the process of a save & reset
   */
  private boolean savedState = false; //only used for saving & reset!
  private static boolean inASaveAndResetStateOperation = false; //only used for saving & reset!

  /**
   * during subset configuration generation, tell whether this node was originally checked
   */
  public boolean wasOriginallyChecked()
  	{return savedState;}
  
  /**
   * Saves the current state recursively.
   */
  public void saveThisState()
  {
	  inASaveAndResetStateOperation = true;
	  savedState = this.isChecked();
	  if (this.hasChildren()) {
		  for (RadioTreeNode<ComponentClassifier> child : getChildren()) {
			  ((ConfigNode)child).saveThisState();
		  }
	  }
	  setChecked(null, false); //and reset to non-checked (starting state!)
  }
  
  /**
   * Resets the state back to what it was when last saved.
   * @param viewer
   */
  public void resetToSavedState(RadioTreeViewer<?> viewer)
  {
	  this.setChecked(viewer, savedState);
	  if (this.hasChildren()) {
		  for (RadioTreeNode<ComponentClassifier> child : getChildren()) {
			  ((ConfigNode)child).resetToSavedState(viewer);
		  }
	  }
	  inASaveAndResetStateOperation = false;
  }
  
  /**
   * This version automatically saves a copy of the tree to a graph file (unless a save/reset state is in progress).
   */
  @Override
  public void setChecked(RadioTreeViewer<?> viewer, boolean check) {
	  super.setChecked(viewer, check);
	  if (! inASaveAndResetStateOperation) {
		  triggerCreateGraphFile();
	  }
  }
  
  /**
   * Used to trigger new graph creation when something changes (see overriden setChecked above).
   */
  private static String graphFileName = "DSE_currentGraph.dot";
  public void triggerCreateGraphFile() {
	  //find root node
	  RadioTreeNode<?> parent = getParent();
	  if ((parent != null) && (parent instanceof ConfigNode)) {
		  ((ConfigNode)parent).triggerCreateGraphFile();
	  } else if (parent == null) {
		  ConfigGraph graph = new ConfigGraph();
		  addToGraph(null, graph);
		  //call recursive createGraphFile on root (which this is!)
		  InputStream input = new StringInputStream("graph DS_configuration {\n"+graph.toFileString()+"}\n");
		  
		  // create "Configuration" folder if needed
		  IWorkspace workspace = ResourcesPlugin.getWorkspace();
		  IWorkspaceRoot root = workspace.getRoot();
		  IProject project  = root.getProject(WriteConfigFile.configProjectName);
		  //IFolder folder = project.getFolder("Folder1");
		  IFile file = project.getFile(graphFileName);
		  //at this point, no resources have been created
		  if (!project.exists())
			try {
				project.create(null);
			} catch (CoreException e1) {
				Say.it(e1.getMessage());
				e1.printStackTrace();
			}
		  if (!project.isOpen())
			try {
				project.open(null);
			} catch (CoreException e1) {
				Say.it(e1.getMessage());
				e1.printStackTrace();
			}
		  //if (!folder.exists()) 
		  //    folder.create(IResource.NONE, true, null);
		  
		   try {
		    if (!file.exists()) {
				file.create(input, IResource.FORCE, null);
		    } else {
				  file.setContents(input, IResource.FORCE, null);
			}
		} catch (CoreException e) {
			Say.it(e.getMessage());
			e.printStackTrace();
		}
		  
	  }
  }
  
  /**
   * Create a new graph in the given file.
   * @param parent
   */
  public void addToGraph(ConfigNode parent, ConfigGraph graph)
  {
	  graph.addNode(this);
	  if(parent != null) {
		  graph.addConnection(parent, this);
	  }
	  // repeat recursively for children
	  if (this.hasChildren()) {
		for (RadioTreeNode<ComponentClassifier> child : getChildren()) {
			((ConfigNode)child).addToGraph(this, graph);
		}
	  }
  }
  
 
  
/**
 * set checks for "first" -- first choice of checks
 */
  public void setFirstConfiguration()
  {
  //if this is a type, and all children are implementations of that type,
  //check first child 
  if (isComponentType())
    {
    boolean allChildrenAreImplementations = true;
    for (RadioTreeNode<ComponentClassifier> child : getChildren())
      if (!((ConfigNode)child).isComponentImplementation())
        allChildrenAreImplementations = false;
    if (allChildrenAreImplementations)
      {  //check the first child, and have it set first config
      ConfigNode first = (ConfigNode)getChildren()[0];
      first.setChecked(null,true);
      first.setFirstConfiguration();
      }
    else
      for (RadioTreeNode<ComponentClassifier> child : getChildren())
        ((ConfigNode)child).setFirstConfiguration();
    }
  //otherwise set first configuration of all children
  else if (hasChildren())
    for (RadioTreeNode<ComponentClassifier> child : getChildren())
      ((ConfigNode)child).setFirstConfiguration();  
  }  //end of setFirstConfiguration
  
  
/**
 * set checks for "first" -- first choice of checks
 * when used to generate a subset of all configurations
 * 
 */
  public void setFirstSubsetConfiguration()
  {
  //if this is a type, and all children are implementations of that type,
  //check first child 
  if (isComponentType())
    {
    boolean allChildrenAreImplementations = true;
    boolean childWasOriginallyChecked = false;
    ConfigNode originallyCheckedChild=null;
    for (RadioTreeNode<ComponentClassifier> child : getChildren())
    	{
      if (!((ConfigNode)child).isComponentImplementation())
        allChildrenAreImplementations = false;
      if (((ConfigNode)child).wasOriginallyChecked())
      	{
      	childWasOriginallyChecked=true;
      	originallyCheckedChild=(ConfigNode)child;
      	}
    	}  //end of for
    if (childWasOriginallyChecked)
    	{
    	originallyCheckedChild.setChecked(null, true);
    	originallyCheckedChild.setFirstSubsetConfiguration();
    	}
    else if (allChildrenAreImplementations)
      {  //check the first child, and have it set first config
      ConfigNode first = (ConfigNode)getChildren()[0];
      first.setChecked(null,true);
      first.setFirstSubsetConfiguration();
      }
    else
      for (RadioTreeNode<ComponentClassifier> child : getChildren())
        ((ConfigNode)child).setFirstSubsetConfiguration();
    }
  //otherwise set first configuration of all children
  else if (hasChildren())
    for (RadioTreeNode<ComponentClassifier> child : getChildren())
      ((ConfigNode)child).setFirstSubsetConfiguration();  
  }  //end of setFirstSubsetConfiguration
  
  /**
   * move checks to the "next" configuration
   * goes through children, if child is checked, first tries to getNextConfiguration of child
   * then moves check to next child, and make its children first (initial) configuration
   * 
   * @return whether this or a child incremented to the next checked configuration
   */
  public boolean getNextConfiguration()
  {
	  if (!hasChildren()) {
		  return false;
	  }
	  if (isComponentType()) {
		  boolean previousImplementationWasChecked = false;
		  RadioTreeNode<ComponentClassifier>[] children = getChildren();
		  for (int ch=0; ch<children.length; ch++) {
			  if (previousImplementationWasChecked) { //was previous child checked?
				  children[ch].setChecked(null,true);
				  ((ConfigNode)children[ch]).setFirstConfiguration();
				  return true;
			  } else if (((ConfigNode)children[ch]).isComponentImplementation() &&
					     children[ch].isChecked()) {  //this child is checked
				  if (((ConfigNode)children[ch]).getNextConfiguration())  { //does child have next config?
					  return true;  //yes, don't move
				  } else {
					  //no, say this (the previous) child was checked  
					  previousImplementationWasChecked = true;
					  //uncheck this child
					  children[ch].setChecked(null,false);
				  }
			  } else { // neither previous child, nor this one were checked.
				  ((ConfigNode)children[ch]).setFirstConfiguration();
			  }
		  }
	  } else { //not component type, go through children until next configuration
		  RadioTreeNode<ComponentClassifier>[] children = getChildren();
		  for (int ch=0; ch<children.length; ch++) {
			  if (((ConfigNode)children[ch]).getNextConfiguration()) {
				  //set previous children to first
				  for (int ch2=0; ch2<ch;ch2++) {
					  ((ConfigNode)children[ch2]).setFirstConfiguration();
				  }
				  return true;
			  }
		  }
	  }
	  return false;
	}  //end getNextConfiguration

  /**
   * move checks to the "next" configuration for (unchecked) subsets
   * goes through children, if child is checked, first tries to getNextConfiguration of child
   * then moves check to next child, and make its children first (initial) configuration
   * 
   * @return whether this or a child incremented to the next checked configuration
   */
  public boolean getNextSubsetConfiguration()
  {
	  if (!hasChildren()) {
		  return false;
	  }
	  if (isComponentType()) {
		  boolean previousImplementationWasChecked = false;
		  RadioTreeNode<ComponentClassifier>[] children = getChildren();
	    //does this have an originally-checked child?
	    for (RadioTreeNode<ComponentClassifier> child : getChildren())
	    	if (((ConfigNode)child).wasOriginallyChecked())
	    		return ((ConfigNode)child).getNextSubsetConfiguration();
	    //otherwise use standard next child
		  for (int ch=0; ch<children.length; ch++) {
			  if (previousImplementationWasChecked) { //was previous child checked?
				  children[ch].setChecked(null,true);
				  ((ConfigNode)children[ch]).setFirstSubsetConfiguration();
				  return true;
			  } else if (((ConfigNode)children[ch]).isComponentImplementation() &&
					     children[ch].isChecked()) {  //this child is checked
				  if (((ConfigNode)children[ch]).getNextSubsetConfiguration())  { //does child have next config?
					  return true;  //yes, don't move
				  } else {
					  //no, say this (the previous) child was checked  
					  previousImplementationWasChecked = true;
					  //uncheck this child
					  children[ch].setChecked(null,false);
				  }
			  } else { // neither previous child, nor this one were checked.
				  ((ConfigNode)children[ch]).setFirstSubsetConfiguration();
			  }
		  }
	  } else { //not component type, go through children until next configuration
		  RadioTreeNode<ComponentClassifier>[] children = getChildren();
		  for (int ch=0; ch<children.length; ch++) {
			  if (((ConfigNode)children[ch]).getNextSubsetConfiguration()) {
				  //set previous children to first
				  for (int ch2=0; ch2<ch;ch2++) {
					  ((ConfigNode)children[ch2]).setFirstSubsetConfiguration();
				  }
				  return true;
			  }
		  }
	  }
	  return false;
	}  //end getNextSubsetConfiguration


  /**
   * 
   * @return
   */
	@Override
	public String getWhyCantInstantiateMsg() {
		String retStr = "";
		RadioTreeNode<ComponentClassifier>[] children = getChildren();
	    if (children != null) {
	    	if ((parentChildrenNotSelected() || hasChildWithProblem())) {
	    		retStr += this.getDisplayName() + " needs a selection.  ";
	    		for (int i = 0; i < children.length; i++) {
		        	retStr += children[i].getWhyCantInstantiateMsg();
		        }
	    	}
	      }
		return retStr;
	}
  
}  //end of ConfigNode
