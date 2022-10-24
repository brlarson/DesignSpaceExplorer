//SubsetConfigHandler.java
//generate all configurations of selected system instance


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

package com.adventium.design.exploration.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Element;
import org.osate.aadl2.SubprogramGroupImplementation;
import org.osate.aadl2.SubprogramImplementation;
import org.osate.aadl2.instantiation.InstantiateModel;
import org.osate.aadl2.modelsupport.errorreporting.InternalErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.LogInternalErrorReporter;
import org.osate.core.OsateCorePlugin;
import org.osate.ui.dialogs.Dialog;
import org.osate.ui.navigator.AadlNavigatorActionProvider;

import com.adventium.design.exploration.InstantiateConfiguredModel;
import com.adventium.design.exploration.Say;
import com.adventium.design.exploration.WriteConfigFile;
import com.adventium.design.exploration.antlr3generated.MakeTypeToImplementationMapParser;
import com.adventium.design.exploration.views.ConfigContentProvider;
import com.adventium.design.exploration.views.ConfigNode;

public class SubsetConfigHandler extends AbstractHandler {

  private static EObjectAtOffsetHelper eObjectAtOffsetHelper = new EObjectAtOffsetHelper();

  protected static final InternalErrorReporter internalErrorLogger = new LogInternalErrorReporter(OsateCorePlugin.getDefault().getBundle());

  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbench wb = PlatformUI.getWorkbench();
    IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
    IWorkbenchPage page = win.getActivePage();
    IWorkbenchPart part = page.getActivePart();
    final ISelection selection;
    IEditorPart activeEditor = page.getActiveEditor();
    if (part instanceof AadlNavigatorActionProvider) {
      selection = page.getSelection();
      if (selection instanceof TreeSelection) {
        for (Iterator<?> iterator = ((TreeSelection) selection).iterator(); iterator.hasNext();) {
          final Object f = iterator.next();
          if (f instanceof IResource) {
            IEditorReference[] editorRefs = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
            for (int i = 0; i < editorRefs.length; i++) {
              IEditorReference edref = editorRefs[i];
              String pname = edref.getPartName();
              IEditorPart edpart = edref.getEditor(true);
              String fname = ((IResource) f).getName();
              if (pname.equals(fname)) {
                page.closeEditor(edpart, true);
              }
            }

            //Resource res = OsateResourceUtil.getResource((IResource) f);

            try {
              InstantiateModel.rebuildInstanceModelFile((IResource) f);// res);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
      }
    } else if (activeEditor != null) {
      XtextEditor xtextEditor = (activeEditor == null) ? null : (XtextEditor) activeEditor.getAdapter(XtextEditor.class);
      if (part instanceof ContentOutline) {
        selection = ((ContentOutline) part).getSelection();
      } else {
        selection = xtextEditor.getSelectionProvider().getSelection();
      }
      if (xtextEditor != null) {
        // make sure the model has been saved
        if (xtextEditor.isDirty()) {
          xtextEditor.doSave(new NullProgressMonitor());
        }

        xtextEditor.getDocument().readOnly(new IUnitOfWork<EObject, XtextResource>() {

          public EObject exec(XtextResource resource) throws Exception {
            EObject targetElement = null;
            if (selection instanceof IStructuredSelection) {
              IStructuredSelection ss = (IStructuredSelection) selection;
              Object eon = ss.getFirstElement();
              if (eon instanceof EObjectNode) {
                targetElement = ((EObjectNode) eon).getEObject(resource);
              }
            } else {
              targetElement = eObjectAtOffsetHelper.resolveContainedElementAt(resource, ((ITextSelection) selection).getOffset());
            }

            if (targetElement != null) {
              if (targetElement instanceof Element) {
                ComponentImplementation cc = ((Element) targetElement).getContainingComponentImpl();
                if (!(cc instanceof ComponentImplementation)) {
                  Dialog.showInfo("Model Instantiation", "Only component implementations can be instantiated..\n" + "Selected "
                      + targetElement.eClass().getName());
                } else if (cc instanceof SubprogramImplementation || cc instanceof SubprogramGroupImplementation) {
                  Dialog.showInfo("Model Instantiation", "Components of categories subprogram and subprogram group cannot be instantiated.\n"
                      + "Selected " + targetElement.eClass().getName());
                } else {
                  Say.it("Generating unselected subset all possible configurations of "+cc.getName());
//                  System.out.println("instantiate " + cc.getName());
                  try {
                    // load map from check box tree
                    // system implementation name in root
                    ConfigContentProvider provider = InstantiateConfiguredModel.view.getContentProvider();
                    String implementationName = provider.getRoot().toString();
                    MakeTypeToImplementationMapParser.rootSystemImplementation = implementationName;

                    //set to first SUBSET configuration
                    if (provider.getRoot() instanceof ConfigNode) {
                      ((ConfigNode)provider.getRoot()).saveThisState();
                      //set first SUBSET
                      ((ConfigNode)provider.getRoot()).setFirstSubsetConfiguration();
                      //InstantiateConfiguredModel.view.refresh();
                    } else throw new ClassCastException("provider.getRoot() is not a ConfigNode");

                    //loop until all configurations done, or limit reached    
                    int configNumber = 0;  
                    int configLimit = 100; //FIXME! WorkspacePlugin.getDefault().getPreferenceStore().getInt(ConfigurationPreferencePage.CONFIGURATION_LIMIT);
                    boolean moreConfigs = true;
                    while (moreConfigs&&(configNumber<=configLimit))  
                    {           
                      Say.it("\nConfiguration "+configNumber++);
                      System.out.println(provider.getRoot().checkedTreeToString(0));
                      StringBuffer sb = new StringBuffer();
                      // ConfigurationActivator.contentProvider.getRoot().toString();
                      MakeTypeToImplementationMapParser.typeToImplementation = InstantiateConfiguredModel.view.makeMap();
                      for (String name : MakeTypeToImplementationMapParser.typeToImplementation.keySet()) {
                    	  String s=name + "=>" + MakeTypeToImplementationMapParser.typeToImplementation.get(name);
                    	  Say.it(s);
                    	  sb.append(s+"\n");
                      }
                      if (configNumber>=configLimit)
                      	{  //hit limit?
                      	Say.it("Allowed number of configurations reached:  "+configLimit);
                      	Say.it("The limit can be changed at the OSATE2->Preferences->OSATE Preference->Design Space Exporer");
                        Dialog.showError("Subset Configurations", "Allowed number of configurations reached:  "+configLimit+  
                      			"\nThe limit can be changed at \nOSATE2->Preferences->OSATE Preference->Design Space Exporer");
                      	}  //done with hit limit

                      //write configuration file
                      WriteConfigFile.writeConfig(cc.getName(),sb.toString());
                      //get next SUBSET
                      moreConfigs = ((ConfigNode)provider.getRoot()).getNextSubsetConfiguration();
                      //InstantiateConfiguredModel.view.refresh();
                    }
                    ((ConfigNode)provider.getRoot()).resetToSavedState(InstantiateConfiguredModel.view.getViewer());
//                    // make instance model
//                    SystemInstance sinst = InstantiateConfiguredModel.buildInstanceModelFile(cc);
//                    if (sinst == null) {
//                      String message;
//                      message = "Error when instantiating the model";
//                      if (InstantiateModel.getErrorMessage() != null) {
//                        message = message + " - reason: " + InstantiateModel.getErrorMessage()
//                            + "\nRefer to the help content and FAQ for more information";
//                      }
//                      Dialog.showError("Model Instantiate", message);
//                    }
                  } catch (UnsupportedOperationException uoe) {
                    Dialog.showError("All Subset Configurations", "Operation is not supported: " + uoe.getMessage());
                  } catch (Exception other) {
                    other.printStackTrace();
                    Dialog.showError("All Subset Configurations", "Error when instantiating the model");
                  }
                }
              } else {
                Dialog.showInfo("Model Instantiation", "Please select an AADL model element. You selected " + targetElement.eClass().getName() + " "
                    + targetElement.toString());
              }
              return null;
            }
            return null;
          }
        });
      }
    }
    return null;
  }  //end of execute

 
}  //end of SubsetConfigHandler
