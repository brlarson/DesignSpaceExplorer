//RadioTreeViewer.java


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

package com.adventium.design.exploration.radio_tree;

import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;

import com.adventium.design.exploration.views.ConfigLabelView;

/**
 ********************************************************************************************************************
 * Provides a tree viewer where all items under a parent node are selectable as "radio" buttons.
 * When a node is selected, any node under the same parent that is already selected is unselected (i.e. radio button).
 * If the item that was previously selected is a parent node and that node is "expanded" then it will not
 * only be de-selected (radio button behavior) but also collapsed. If the newly selected node is a parent node then it
 * will be expanded.
 *
 * If the user re-selects the selected node then the node is de-selected. (OLD, this is not currently true.)
 *
 * @author pahed
 *
 ********************************************************************************************************************
 */
public class RadioTreeViewer<T> extends CheckboxTreeViewer {

  /**
   ******************************************************************************************************************
   * Constructor with parent widget and eclipse viewsite.
   * @param parent
   * @param iViewSiteIn
   ******************************************************************************************************************
   */
public RadioTreeViewer(Composite parent, IViewSite viewSite, IStructuredContentProvider contentProvider) {
    super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    
    // Create the help context id for the viewer's control
    PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), "TreeExample.viewer");

    addCheckStateListener(new ICheckStateListener() {

      public void checkStateChanged(CheckStateChangedEvent e) {
        Object obj = e.getElement();
        if (obj instanceof RadioTreeNode) {
          @SuppressWarnings("unchecked") RadioTreeNode<T> tn = (RadioTreeNode<T>) obj;
          if ((! tn.isGrayed()) && (tn.isChecked())) {
        	  setCheckedElement(tn, false);
          } else {
	          setCheckedElement(tn, true);
	          RadioTreeNode<T> parent = tn.getParent();
	          if (parent == null || !parent.childrenAreExclusive) {
	            return;
	          }
	          RadioTreeNode<T>[] children = parent.getChildren();
	          RadioTreeNode<T> oldNode = null;
	          for (int i = 0; i < children.length; i++) {
	            if (!children[i].equals(tn) && getChecked(children[i])) {
	              oldNode = children[i];
	              setCheckedElement(children[i], false);
	            }
	          }
	          if (oldNode != null) {
	            collapseToLevel(oldNode, 1);
	          }
	          expandToLevel(tn, 1);
          }
        }
        refresh(true);
      }
    });

    setContentProvider(contentProvider);
    setInput(viewSite);
    setupMenus(viewSite);
    // resize the row height using a MeasureItem listener
    this.getTree().addListener(SWT.MeasureItem, new Listener() {
       public void handleEvent(Event event) {
          // height cannot be per row so simply set
    	  FontMetrics fm = new GC(Display.getCurrent()).getFontMetrics();
    	  int height = fm.getHeight(); 
          event.height = height+3; // this gives space for bolded fonts.
          event.width = 500; // enough room for initial instructions
       }
    });
    setGrayedElements();
    setLabelProvider(new ConfigLabelView());
  }

  /**
   ******************************************************************************************************************
   * Builds menus for the tree view.
   * @param viewSite
   ******************************************************************************************************************
   */
  private void setupMenus(IViewSite viewSite) {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      public void menuAboutToShow(IMenuManager manager) {
        Action action = new Action() {

          @SuppressWarnings("unchecked")
          @Override
          public void run() {
            RadioTreeNode<T> x = ((RadioTreeContentProvider<T>) getContentProvider()).getRoot();

            RadioTreeNode<T> selectedTree = getResults(x);
            if (selectedTree == null) {
              System.out.println("No elements selected");
            } else {
              System.out.println(selectedTree.treeToString(0));
            }
          }
        };
        action.setText("Dump Selected from the whole tree");
        manager.add(action);
      }
    });
    menuMgr.addMenuListener(new IMenuListener() {

      public void menuAboutToShow(IMenuManager manager) {
        Action action = new Action() {

          @SuppressWarnings("unchecked")
          @Override
          public void run() {
            RadioTreeNode<T> x = ((RadioTreeContentProvider<T>) getContentProvider()).getRoot();

            if (x == null) {
              System.out.println("No elements");
            } else {
              System.out.println(x.treeToString(0));
            }
          }
        };
        action.setText("Dump the whole tree");
        manager.add(action);
      }
    });
    Menu menu = menuMgr.createContextMenu(getControl());
    getControl().setMenu(menu);
    viewSite.registerContextMenu(menuMgr, this);
  }
  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   * @param root
   * @return
   ******************************************************************************************************************
   */
  public RadioTreeNode<T> getResults(RadioTreeNode<T> root) {
    System.out.println("===========================");
    return root.getResults(null, this, 0);
  }

  /**
   ******************************************************************************************************************
   * Reset all the nodes of this tree such that they are appropriately grayed.
   * This will also refresh the gui.
   ******************************************************************************************************************
   */
  @SuppressWarnings("unchecked")
public void setGrayedElements() {
	RadioTreeNode<T> root = ((RadioTreeContentProvider<T>) getContentProvider()).getRoot();
	Vector<RadioTreeNode<T>> list = new Vector<RadioTreeNode<T>>();
	list.add(root);
    root.getGrayNodes(list);
    for (RadioTreeNode<T> node: list) {
    	node.setGrayed(this, true);
    	node.setChecked(this, true);
    }
    refresh();
  }

  /**
   ******************************************************************************************************************
   * Set the given node's checked status to the boolean value b.
   * @param child not to change checked status of
   * @param b true if child is to be checked, false otherwise
   ******************************************************************************************************************
   */
  private void setCheckedElement(RadioTreeNode<T> child, boolean b) {
	  child.setChecked(this, b);
  }
}
