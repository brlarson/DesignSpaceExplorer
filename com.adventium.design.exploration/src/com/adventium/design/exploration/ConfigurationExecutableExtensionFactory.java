//ConfigurationExecutableExtensionFactory.java
//  I don't know what this does, and it's not referenced by other classes in the project

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

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

import com.adventium.design.exploration.ConfigurationActivator;
import com.google.inject.Injector;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class ConfigurationExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return ConfigurationActivator.getDefault().getBundle();
	}
	
	@Override
	protected Injector getInjector() {
		return null;  //ConfigurationActivator.getDefault().getInjector(ConfigurationActivator.PLUGIN_ID);
	}
	
	
}
