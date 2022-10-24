//OldConfigurationView.java
//this was the original view replaced by the RadioTree/ConfigNode

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

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreePathViewerSorter;//WAS: ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.impl.SystemImplementationImpl;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import com.adventium.design.exploration.ConfigurationTree;

public class OldConfigurationView extends ViewPart {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "com.adventium.design.exploration.views.ConfigurationView";
  // public static ComponentImplementation top=null; //top level implementation

  private TreeViewer viewer;
  // private CheckboxTreeViewer viewer;
  // private TableViewer viewer;
  private Action action1;
  private Action action2;
  private Action doubleClickAction;

  //private String[] implementationNames = new String[] { "Configuration Selection", "Will Go Here" };

  private Object[] EMPTY_ARRAY = null;

  class ViewContentProvider implements ITreeContentProvider {

    Composite comp;

    ViewContentProvider(Composite context) {
      comp = context;
    }
    
    public Object[] getElements(Object parent) {
      if (ConfigurationTree.root == null) {
        for (ComponentImplementation ci : AadlUtil.getAllComponentImpl()) {
          if (ci instanceof SystemImplementationImpl) {
            ConfigurationTree.makeCheckboxTree(ci, comp);
            // top=ci;
            // if (top==null) System.out.println("no system implementation found");
          }
        }
      }
      return new Object[] { ConfigurationTree.root };
    }

    public Object[] getChildren(Object parent) {
      if (parent instanceof Tree) {
        return ((Tree) parent).getItems();
      }
      if (parent instanceof TreeItem) {
        return ((TreeItem) parent).getItems();
      } else if (parent instanceof ComponentImplementation) {
        return ((ComponentImplementation) parent).getAllSubcomponents().toArray();
      } else {
        return EMPTY_ARRAY;
      }
    }

    public Object getParent(Object element) {
      if (element instanceof TreeItem) {
        return ((TreeItem) element).getParentItem();
      } else if (element instanceof ComponentInstance) {
        return ((ComponentInstance) element).getContainingComponentInstance();
      } else if (element instanceof ComponentImplementation) {
        return ((ComponentImplementation) element).getContainingComponentImpl();
      } else if (element instanceof ComponentType) {
        return ((ComponentType) element).getContainingComponentImpl();
      }
      return null;
    }

    public boolean hasChildren(Object element) {
      if (element instanceof Tree) {
        return ((Tree) element).getItemCount() > 0;
      } else if (element instanceof TreeItem) {
        return ((TreeItem) element).getItemCount() > 0;
      } else if (element instanceof ComponentImplementation) {
        return ((ComponentImplementation) element).getAllSubcomponents().size() > 0;
      } else {
        return false;
      }
    }
  }

  class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

    public String getColumnText(Object obj, int index) {
      if (obj instanceof NamedElement) {
        return ((NamedElement) obj).getName();
      } else {
        return getText(obj);
      }
    }

    public Image getColumnImage(Object obj, int index) {
      return getImage(obj);
    }

    public Image getImage(Object obj) {
      return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
    }
  }

  class NameSorter extends TreePathViewerSorter {}

  /**
   * The constructor.
   */
  public OldConfigurationView() {
    // InstantiateConfiguredModel.view = this;
  }

  /**
   * This is a callback that will allow us
   * to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(Composite parent) {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    // viewer = new CheckboxTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    // viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
    viewer.setContentProvider(new ViewContentProvider(parent));
    // viewer.setContentProvider(new ConfigurationTree());
    viewer.setLabelProvider(new ViewLabelProvider());
    // viewer.setSorter(new NameSorter());
    viewer.setInput(getViewSite());

    // Create the help context id for the viewer's control
    PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.adventium.design.exploration.viewer");
    makeActions();
    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();
  }

  private void hookContextMenu() {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      //FIXEM?? @Override
      public void menuAboutToShow(IMenuManager manager) {
        OldConfigurationView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }

  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(IMenuManager manager) {
    manager.add(action1);
    manager.add(new Separator());
    manager.add(action2);
  }

  private void fillContextMenu(IMenuManager manager) {
    manager.add(action1);
    manager.add(action2);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void fillLocalToolBar(IToolBarManager manager) {
    manager.add(action1);
    manager.add(action2);
  }

  private void makeActions() {
    action1 = new Action() {

      @Override
      public void run() {
        showMessage("Action 1 executed");
      }
    };
    action1.setText("Action 1");
    action1.setToolTipText("Action 1 tooltip");
    action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

    action2 = new Action() {

      @Override
      public void run() {
        showMessage("Action 2 executed");
      }
    };
    action2.setText("Action 2");
    action2.setToolTipText("Action 2 tooltip");
    action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    doubleClickAction = new Action() {

      @Override
      public void run() {
        ISelection selection = viewer.getSelection();
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        showMessage("Double-click detected on " + obj.toString());
      }
    };
  }

  private void hookDoubleClickAction() {
    viewer.addDoubleClickListener(new IDoubleClickListener() {

    	//FIXEM?? @Override
      public void doubleClick(DoubleClickEvent event) {
        doubleClickAction.run();
      }
    });
  }
  private void showMessage(String message) {
    MessageDialog.openInformation(viewer.getControl().getShell(), "Design Space Explorer", message);
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus() {
    viewer.getControl().setFocus();
  }

  public String chooseImplementation(String typeName, Set<String> impls) {
    if (impls.size() > 2) {
      //implementationNames = (String[]) impls.toArray();
      showMessage("Choose implmentation for type: " + typeName);
    } else if (impls.size() == 1) {
      return (String) impls.toArray()[0];
    }
    return "";
  } // end of chooseImplementation

}