//ConfigurationView.java
//  ViewPart to display check-box trees

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

import java.util.HashMap;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osate.aadl2.ComponentClassifier;

import com.adventium.design.exploration.InstantiateConfiguredModel;
import com.adventium.design.exploration.Say;
import com.adventium.design.exploration.radio_tree.RadioTreeNode;
import com.adventium.design.exploration.radio_tree.RadioTreeViewer;

public class ConfigurationView extends ViewPart {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "com.adventium.design.exploration.views.ConfigurationView";

  private RadioTreeViewer<ConfigNode> viewer;

  public RadioTreeViewer<ConfigNode> getViewer()
  {
    return viewer;
  }
  
  /**
   ******************************************************************************************************************
   * Construct the viewer and set the data model.
   *
   * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   ******************************************************************************************************************
   */
  @Override
  public void createPartControl(Composite parent) {
    // make radio tree viewer
    viewer = new RadioTreeViewer<ConfigNode>(parent, getViewSite(), new ConfigContentProvider());

    // save link to view and viewer
    InstantiateConfiguredModel.view = this;
    // InstantiateConfiguredModel.viewer = viewer;

    // Create the help context id for the viewer's control
    PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "TreeExample.viewer");
  }

  /**
   ******************************************************************************************************************
   * Sets the underlying content provider to a new provider.
   * @param provider provider to use in tree.
   ******************************************************************************************************************
   */
  public void setContentProvider(ConfigContentProvider provider) {
    viewer.setContentProvider(provider);
  }
  /**
   ******************************************************************************************************************
   * Gets the current provider used in the tree.
   * @return tree's provider
   ******************************************************************************************************************
   */
  public ConfigContentProvider getContentProvider() {
    return (ConfigContentProvider) viewer.getContentProvider();
  }

  /**
   ******************************************************************************************************************
   * Sets the focus to this example in eclipse.
   *
   * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
   ******************************************************************************************************************
   */
  @Override
  public void setFocus() {
    viewer.getControl().setFocus();
  }

  /**
   ******************************************************************************************************************
   * Refresh the viewer
   ******************************************************************************************************************
   */
  public void refresh() {
    viewer.refresh();
  }

  /**
   ******************************************************************************************************************
   * /**
   * make type-to-implementation map s:t -> i where s is subcomponent id, t is
   * component type, and i is implementation id
   *
   * @return
   ******************************************************************************************************************
   */
  public HashMap<String, String> makeMap() 
  {
    HashMap<String, String> result = new HashMap<String, String>();
    // walk the check box tree and load map
    ConfigNode root = (ConfigNode) getContentProvider().getRoot();
    //make a string buffer for the hash
    StringBuffer hash = new StringBuffer();
    loadMap(root, result, hash);
    lastHash=hash.toString();
    return result;
  } // end of makeMap

  /**
   * holds string representing hash of last map
   */
  public static String lastHash = "";
  
  /**
   ******************************************************************************************************************
   * Load a HashMap with a mapping between a type, and its implementation that has a 
   * check in the box.
   * @param node
   * @param theMap
   ******************************************************************************************************************
   */
  private void loadMap(ConfigNode node, HashMap<String, String> theMap, StringBuffer hash) 
  {
    // if this node is a type, find its child with a check in a box
    if (node.isComponentType()) { // yes, find its checkbox
      boolean found = false;
      char myChar = '0';
      if (node.getChildren()!=null)
      for (RadioTreeNode<ComponentClassifier> child : node.getChildren()) 
      {
    	  if (child.isChecked()) 
    	  { // then put it in the map
    		  theMap.put(node.toString(), child.toString());
    		  //add myChar to the hash
    		  hash.append(myChar);
    		  // recursively load map for children
    		  loadMap((ConfigNode) child, theMap, hash);
    		  found = true;
    	  }
    	  else  { //increment hash character
    		  myChar++;
    		  int myCharVal = (int)myChar;
    		  if (myCharVal == 58) { 
    			  myChar += 7; // skip :;<=>?@
    		  }
    		  if (myCharVal == 91) { 
    			  myChar += 6; // skip [\]^_`
    		  }
    	  }
      }
      if (!found) {
        Say.it("component type \"" + node.toString() + "\" did not have a checked (or perhaps no) implementation");
      }
    } else {
      RadioTreeNode<ComponentClassifier>[] children = node.getChildren();
      if (children != null) {
        for (RadioTreeNode<ComponentClassifier> child : node.getChildren()) {
          loadMap((ConfigNode) child, theMap, hash);
        }
      }
    }
  } // end of loadMap
}