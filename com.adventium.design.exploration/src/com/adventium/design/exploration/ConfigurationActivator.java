//ConfigurationActivator.java
//  This is the Eclipse "Activator" for the Design Space Explorer plug-in
//  as designated by the ord.aadl.configuration/plugin.xml descriptor file


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

import javax.swing.UIManager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.adventium.design.exploration.ui.preferences.PreferenceInitializer;

/**
 * The activator class controls the plug-in life cycle
 */
public class ConfigurationActivator extends AbstractUIPlugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "com.adventium.design.exploration"; //$NON-NLS-1$

  // The shared instance
  public static ConfigurationActivator plugin;

  //private Map<String, Injector> injectors = Collections.synchronizedMap(Maps.<String, Injector> newHashMapWithExpectedSize(1));

  // the console for Configuration
  public static MessageConsole console;
  // the message stream for the console
  public static MessageConsoleStream consoleStream;

  // the content provider
  // public static ConfigContentProvider contentProvider;

  /**
   * The constructor
   */
  public ConfigurationActivator() {
    // create the console
    console = new MessageConsole("Design Space Explorer Console", null);
    // get its message stream
    consoleStream = console.newMessageStream();
    // activate console when this stream is written to
//    consoleStream.setActivateOnWrite(true);
    // add console to the console plugin
    IConsole[] consoles = { console };
    ConsolePlugin.getDefault().getConsoleManager().addConsoles(consoles);
    // show the console
//    ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
    // make content provider
    // contentProvider = new ConfigContentProvider();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
   //HSS11April19 removed following  line, now putting it back HSS16May2019..FIXME??
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    // Call the preference initializer manually because eclipse will not call it if the preference page uses a scoped preference store
    new PreferenceInitializer().initializeDefaultPreferences();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static ConfigurationActivator getDefault() {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given
   * plug-in relative path
   *
   * @param path the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path) {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  // public Injector getInjector(String language) {
  // synchronized (injectors) {
  // Injector injector = injectors.get(language);
  // if (injector == null) {
  // injectors.put(language, injector = createInjector(language));
  // }
  // return injector;
  // }
  // }
  //
  // protected Injector createInjector(String language) {
  // try {
  // Module runtimeModule = getRuntimeModule(language);
  // Module sharedStateModule = getSharedStateModule();
  // Module uiModule = getUiModule(language);
  // Module mergedModule = Modules2.mixin(runtimeModule, sharedStateModule, uiModule);
  // return Guice.createInjector(mergedModule);
  // } catch (Exception e) {
  // logger.error("Failed to create injector for " + language);
  // logger.error(e.getMessage(), e);
  // throw new RuntimeException("Failed to create injector for " + language, e);
  // }
  // }

}
