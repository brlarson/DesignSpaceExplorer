//ConfigurationPreferencePage.java

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


package com.adventium.design.exploration.ui.preferences;

//import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
//import org.osate.workspace.WorkspacePlugin;

import com.adventium.design.exploration.ConfigurationActivator;
import com.adventium.design.exploration.Say;

/**
 * @author brl
 * put up Eclipse preferences for configuration plugin
 */
public class ConfigurationPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{


public static int signature_limit = 100;  //the limit of how many iterationas
//  used by PreferenceInitializer to set default
//  get value by  (used in AllConfigHandler)
//   int configLimit = WorkspacePlugin.getDefault().getPreferenceStore().getInt(ConfigurationPreferencePage.CONFIGURATION_LIMIT);

private static int signature_upper_limit = 10000;  //used only to specify valid range

private IntegerFieldEditor sl;

public static String ALLOW_CENTER_TO_MOVE = "allowCenterToMove";
public static String CONFIGURATION_LIMIT = "configurationLimit";
//public static String SHOW_SIGNATURE_PLOT = "showSignaturePlot";

public static ConfigurationPreferencePage thePage=null;

///**
// * move scale factor
// */
//static double scaleFactor = 0.01;
//static int iterationLimit = 1000;
//
///**
// * move threshold
// */
//static double threshold = 0.001;

  /**
   * 
   */
  public ConfigurationPreferencePage()
    {
    super(GRID);
    setPreferenceStore(ConfigurationActivator.plugin.getPreferenceStore());
    setDescription("Design Space Explorer preferences\n");
//    getPreferenceStore().setDefault(CONFIGURATION_LIMIT, signature_limit);
    thePage=this;
    }

  /**
   * @param style
   */
  public ConfigurationPreferencePage(int style)
    {
    super(style);
    thePage=this;
    }

  /**
   * @param title
   * @param style
   */
  public ConfigurationPreferencePage(String title, int style)
    {
    super(title, style);
    thePage=this;
    }

  /**
   * @param title
   * @param image
   * @param style
   */
  public ConfigurationPreferencePage(String title, ImageDescriptor image,
      int style)
    {
    super(title, image, style);
    thePage=this;
    }

  /* 
   * create the field editors
   */
  @Override
  protected void createFieldEditors()
    {
//    BooleanFieldEditor be = new BooleanFieldEditor(ConfigurationPreferencePage.ALLOW_CENTER_TO_MOVE,
//        "Allow selected center design (signature file) to move when arranging in a plane",
//        getFieldEditorParent());
//    addField(be);
    sl = new IntegerFieldEditor(//IPreferenceConstants.
    		CONFIGURATION_LIMIT,
    		"Configuration Generation Limit",
        getFieldEditorParent());
    sl.setValidRange(1, ConfigurationPreferencePage.signature_upper_limit);
//    getPreferenceStore().setDefault(CONFIGURATION_LIMIT, signature_limit);
//    sl.setStringValue(String.valueOf(ConfigurationPreferencePage.signature_limit));
    addField(sl);
    Say.it("Default configuration limit="+getSignatureLimit());
//    BooleanFieldEditor plot = new BooleanFieldEditor(ConfigurationPreferencePage.SHOW_SIGNATURE_PLOT,
//        "Show plot of signature X-Y locations",
//        getFieldEditorParent());
//    addField(plot);
//    DoubleFieldEditor sf = new DoubleFieldEditor(ConfigurationPreferencePage.ALLOW_CENTER_TO_MOVE,
//        "Allow selected center design (signature file) to move when arranging in a plane",
//        getFieldEditorParent());

    }

  
 public int getSignatureLimit()
   {
   return getPreferenceStore().getInt(CONFIGURATION_LIMIT);
//   if (sl==null)
//  	 return signature_limit;
////  	 createFieldEditors();
//   return sl.getIntValue();
   }

public void init(IWorkbench arg0) {
	// TODO Auto-generated method stub
	
}
}
