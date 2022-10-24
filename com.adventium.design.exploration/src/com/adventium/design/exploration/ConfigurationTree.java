//ConfigurationTree.java
//obsolete, only used by OldConfigurationView
//replaced by ConfigNode and RadioTreeNode
//this class builds a "check-box" tree from a selected component instance in
//an OSATE architecture model

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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.modelsupport.util.AadlUtil;

public class ConfigurationTree implements ITreeContentProvider {

  public ConfigurationTree() {}

  public static Tree root = null;

/**
 * make a check-box tree from a component implementation
 * @param ci  root of OSATE model to be made tree
 * @param comp  environment of tree
 * @return  Tree
 */
  public static Tree makeCheckboxTree(ComponentImplementation ci, Composite comp) {
    root = new Tree(comp, SWT.VIRTUAL | SWT.CHECK);
    TreeItem ti = new TreeItem(ConfigurationTree.root, SWT.NONE);
    ti.setText(ci.getName());
    // add subcomponents
    for (final Subcomponent sub : ci.getAllSubcomponents()) {
      addSubcomponentToTree(ti, sub);
    }
    return root;
  } // end of makeCheckboxTree

  public static void addSubcomponentToTree(TreeItem ti, Subcomponent sub) {
    ComponentImplementation imp = sub.getComponentImplementation();
    if (imp != null) {
      TreeItem forImpl = new TreeItem(ti, SWT.NONE);
      forImpl.setText(imp.getName());
      for (final Subcomponent subsub : imp.getAllSubcomponents()) {
        addSubcomponentToTree(forImpl, subsub);
      }
    } else {
      ComponentType ct = sub.getComponentType();
      if (ct != null) {
        TreeItem forType = new TreeItem(ti, SWT.NONE);
        forType.setText(ct.getName());
        // find any implementations
        for (final ComponentImplementation ci : AadlUtil.getAllComponentImpl()) {
          if (ci.getName().startsWith(ct.getName())) {
            TreeItem forImp = new TreeItem(forType, SWT.CHECK);
            forImp.setText(ci.getName());
            for (final Subcomponent subsub : ci.getAllSubcomponents()) {
              addSubcomponentToTree(forImp, subsub);
            }
          }
        }
      }
    }
    // else throw exception
  } // end of addSubcomponentToTree

  

  

  /**
   * Gets root element of tree
   */
  public Object[] getElements(Object inputElement) {
    if (ConfigurationTree.root == null) {
      ConfigurationTree.root = new Tree(null, 0);
    }
    return new Object[] { ConfigurationTree.root };
  }

  public Object[] getChildren(Object parentElement) {
    if (parentElement instanceof TreeItem) {
      return ((TreeItem) parentElement).getItems();
    } else {
      return null;
    }
  }

  public Object getParent(Object element) {
    if (element instanceof TreeItem) {
      return ((TreeItem) element).getParentItem();
    } else {
      return null;
    }
  }

  public boolean hasChildren(Object element) {
    if (element instanceof TreeItem) {
      return ((TreeItem) element).getItemCount() > 0;
    } else {
      return false;
    }
  }

}
