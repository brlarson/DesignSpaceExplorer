//Say.java

//write to both System.out and configuration console

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


public class Say
{

	public Say()
		{
		}
/**
 * Say "it" (the string s) to console and host console if running from sources
 * @param s
 */
  public static void
it(String s)
  {
  s += "\n";  //add a newline at the end
  if (ConfigurationActivator.plugin == null)
    ConfigurationActivator.plugin = new ConfigurationActivator();
  if (ConfigurationActivator.consoleStream!=null)
  	{  //Configuration plugin console
    try {ConfigurationActivator.consoleStream.write(s);} 
    catch (IOException ioe) {System.out.println(ioe.getMessage());}
  	}
  System.out.println(s);
  }
  
}
