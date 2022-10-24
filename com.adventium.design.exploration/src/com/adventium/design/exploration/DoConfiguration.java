//DoConfiguration.java
//this is an obsolete class; use ConfigurationActivator instead

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

import java.io.IOException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.Element;
import org.osate.ui.handlers.AaxlReadOnlyHandlerAsJob;

public class DoConfiguration extends AaxlReadOnlyHandlerAsJob
{

@Override
protected String getActionName()
  {
  return "Generate Instance Model for Selected Configuration";
  }

@Override
protected void doAaxlAction(IProgressMonitor monitor, Element root)
  {
  System.out.println("configure button pushed");  
  if (ConfigurationActivator.consoleStream!=null)
    {  //BLESS plugin console
    try 
      {
      ConfigurationActivator.consoleStream.write("Generating instance model for "+
          "configuration chosen for system implementation:");
      } 
    catch (IOException ioe) {System.out.println(ioe.getMessage());}
    }
  else
    System.out.println("the console stream has not been activated");
  }  //end of doAaxlAction 

}
