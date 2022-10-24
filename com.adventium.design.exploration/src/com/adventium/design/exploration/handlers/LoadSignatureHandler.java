//LoadSignatureHandler.java

//reads a signature file

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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instantiation.InstantiateModel;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import com.adventium.design.exploration.InstantiateConfiguredModel;
import com.adventium.design.exploration.antlr3generated.MakeTypeToImplementationMapParser;

public class LoadSignatureHandler extends AbstractHandler
{

  public Object execute(ExecutionEvent event) throws ExecutionException
    {
    InstantiateConfiguredModel.loadTypeToImplementationMap();
    try
      {
      String rootString = MakeTypeToImplementationMapParser.rootSystemImplementation;
      //find root implementation
      ComponentImplementation cc = findRootImplementation(rootString);
      // find all possible implementations for this type
      if (cc==null)
        throw new ExecutionException("System implemenation not found for name:  "+rootString);
      // make instance model
      SystemInstance sinst = InstantiateConfiguredModel.buildInstanceModelFile(cc);
      if (sinst == null) {
        String message;
        message = "Error when instantiating the model";
        if (InstantiateModel.getErrorMessage() != null) {
          message = message + " - reason: " + InstantiateModel.getErrorMessage()
              + "\nRefer to the help content and FAQ for more information";
        }
        Dialog.showError("Model Instantiate", message);
      }
    } catch (UnsupportedOperationException uoe) {
      Dialog.showError("Model Instantiate", "Operation is not supported: " + uoe.getMessage());
    } catch (Exception other) {
      other.printStackTrace();
      Dialog.showError("Model Instantiate", "Error when instantiating the model");
    }

    return null;
    }

  public ComponentImplementation findRootImplementation(String implementationName)
    {
    for (ComponentImplementation ci : AadlUtil.getAllComponentImpl()) 
      {
      String ciName = ci.getName();
      if (ciName.equalsIgnoreCase(implementationName)) 
        return ci;
      }
    return null;
    }  //end of findRootImplementation

}
