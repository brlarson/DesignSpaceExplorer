//ConfigureHandler.java

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
import org.osate.aadl2.instance.SystemInstance;
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

public class ConfigureHandler extends AbstractHandler {

  private static EObjectAtOffsetHelper eObjectAtOffsetHelper = new EObjectAtOffsetHelper();

  protected static final InternalErrorReporter internalErrorLogger = new LogInternalErrorReporter(OsateCorePlugin.getDefault().getBundle());

  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbench wb = PlatformUI.getWorkbench();
    IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
    IWorkbenchPage page = win.getActivePage();
    IWorkbenchPart part = page.getActivePart();
    final ISelection selection;
    IEditorPart activeEditor = page.getActiveEditor();
    if (part instanceof AadlNavigatorActionProvider) 
    {
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
    } 
    else if (activeEditor != null) 
    {
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
//                	/** NO LONGER OUTPUTTING INSTANCE FILE ----- FIXME!!
                  System.out.println("instantiate " + cc.getName());
                  try {
                    // load map from check box tree
                    // system implementation name in root
                    ConfigContentProvider provider = InstantiateConfiguredModel.view.getContentProvider();
                    String implementationName = provider.getRoot().toString();
                    MakeTypeToImplementationMapParser.rootSystemImplementation = implementationName;
                    StringBuffer sb = new StringBuffer();
                    // ConfigurationActivator.contentProvider.getRoot().toString();
                    MakeTypeToImplementationMapParser.typeToImplementation = InstantiateConfiguredModel.view.makeMap();
                    for (String name : MakeTypeToImplementationMapParser.typeToImplementation.keySet()) {
                      String s="<choice type="+name +" name=" + MakeTypeToImplementationMapParser.typeToImplementation.get(name)+"/>";
                      Say.it(s);
                      sb.append(s+"\n");
                    }
                    WriteConfigFile.writeConfig(cc.getName(),sb.toString());
                    // make instance model
                    SystemInstance sinst = InstantiateConfiguredModel.buildInstanceModelFile(cc);
                    if (sinst == null) {
                      String message;
                      message = "Error when instantiating the model: "+provider.getRoot().getWhyCantInstantiateMsg();
                      if (InstantiateModel.getErrorMessage() != null) {
                        message = message + " - reason: \"" + InstantiateModel.getErrorMessage() + "\""
                            + "\nCheck Console for a subcomponent type lacking an implementation";
                      }
                      Dialog.showError("Model Instantiate", message);
                    }
                  } catch (UnsupportedOperationException uoe) {
                    Dialog.showError("Model Instantiate", "Operation is not supported: \n \"" + uoe.getMessage() +"\"");
                  } catch (Exception other) {
                    other.printStackTrace();
                    Dialog.showError("Model Instantiate", "Error when instantiating the model:\n  \"" + other.getMessage() +"\"");
                  }
//                  **/
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
  }

  // @Override
  // public boolean isEnabled() {
  // IWorkbench wb = PlatformUI.getWorkbench();
  // IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
  // IWorkbenchPage page = win.getActivePage();
  // IWorkbenchPart part = page.getActivePart();
  // IEditorPart activeEditor = page.getActiveEditor();
  // if (activeEditor == null)
  // return false;
  // XtextEditor xtextEditor = (XtextEditor) activeEditor.getAdapter(XtextEditor.class);
  // final ISelection selection;
  // if (xtextEditor != null) {
  // if (part instanceof ContentOutline) {
  // selection = ((ContentOutline) part).getSelection();
  // } else {
  // selection = (ITextSelection) xtextEditor.getSelectionProvider().getSelection();
  // }
  // }
  // Resource resource = xtextEditor.getResource();
  // Resource resource = xtextEditor.getEditorSite().g;
  // EObject targetElement = null;
  // if (selection instanceof IStructuredSelection) {
  // IStructuredSelection ss = (IStructuredSelection) selection;
  // Object eon = ss.getFirstElement();
  // if (eon instanceof EObjectNode) {
  // targetElement = ((EObjectNode)eon).getEObject(resource);
  // }
  // } else {
  // targetElement = eObjectAtOffsetHelper.resolveElementAt(resource,
  // ((ITextSelection)selection).getOffset());
  // }
  //
  // return true;
  // }

  /*
   * XXX buildInstanceModelFile has moved to InstantiateModel
   */

}
