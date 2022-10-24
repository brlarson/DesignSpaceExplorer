//ConfigContentProvider.java


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

import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.ArrayDimension;
import org.osate.aadl2.ArraySizeProperty;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.PropertyConstant;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.impl.SystemImplementationImpl;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.aadl2.util.Aadl2Util;

import com.adventium.design.exploration.Say;
import com.adventium.design.exploration.handlers.SetArrayProperties;
import com.adventium.design.exploration.radio_tree.RadioTreeContentProvider;

/**
 ********************************************************************************************************************
 * Content provider for the tree viewer - this is the model for the tree.
 * @author pahed
 *
 ********************************************************************************************************************
 */
public class ConfigContentProvider extends RadioTreeContentProvider<ComponentClassifier> {

  /**
   ******************************************************************************************************************
   * Constructor for a empty tree (except root node)
   *
   * @param rootIn tree data to view
   ******************************************************************************************************************
   */
  public ConfigContentProvider() {
    super(new ConfigNode((ConfigNode) null, (ComponentClassifier) null, false));

  }

  /**
   ******************************************************************************************************************
   * Construct a new content provider based on the component provided.
   * @param ci Component to use to build the tree.
   ******************************************************************************************************************
   */
  public ConfigContentProvider(ComponentImplementation ci) {
    this();
    makeTree(ci);
    //make lines that are't implementation selections gray
    setGrayNodes(getRoot());
  }
  /**
   ******************************************************************************************************************
   * Builds the AADL tree by embedding AADL objects into ConfigNodes.
   ******************************************************************************************************************
   */
  public void makeTree(ComponentImplementation ci) {
    System.out.println("!!!!!!!!!!!!makeTree!!!!!!!!!!!!");
    if (ci instanceof SystemImplementationImpl) {
        ConfigNode.system_implementation_name = ci.getName();
        ConfigNode.package_name = "package "+Aadl2Util.getPackageName(ci.getQualifiedName());
      System.out.println(getRoot());
      makeCheckboxTree((ConfigNode) getRoot(), ci);
    } else {
      Say.it(ci.getName() + " must be a system implementation.");
    }
  }
  /**
   ******************************************************************************************************************
   * Makes a subtree for the given parent node.
   * @param parent node to add children to
   * @param ci component which supplies the data for the children
   ******************************************************************************************************************
   */
  private void makeCheckboxTree(ConfigNode parent, ComponentImplementation ci) {
    ConfigNode node = new ConfigNode(parent, ci);
    // add subcomponents
    for (final Subcomponent sub : ci.getAllSubcomponents()) {
      addSubcomponentToTree(node, sub);
    }
  }

  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   * @param parent
   * @param sub
   ******************************************************************************************************************
   */
	private void addSubcomponentToTree(ConfigNode parent, Subcomponent sub)
		{
		ComponentImplementation imp = sub.getComponentImplementation();
		EList<ArrayDimension> dimensions = sub.getArrayDimensions();
		StringBuffer dimensionString = new StringBuffer();
		for (ArrayDimension as : dimensions)
			{
			ArraySizeProperty sizeProperty = as.getSize().getSizeProperty();
			if (sizeProperty!=null&&sizeProperty instanceof PropertyConstant)
				{
// use arraySizeMap
				if (SetArrayProperties.arraySizeMap
						.containsKey(((PropertyConstant) sizeProperty).getName()))
					{
					String dimension = SetArrayProperties.arraySizeMap
							.get(((PropertyConstant) sizeProperty).getName());
					dimensionString.append("[" + dimension + "]");
					}
				else
					{
					PropertyExpression exp = ((PropertyConstant) sizeProperty)
							.getConstantValue();
					if (exp instanceof IntegerLiteral)
						dimensionString
								.append("[" + ((IntegerLiteral) exp).getValue() + "]");
					}
				}
			else
			  dimensionString.append("[" + as.getSize().getSize() + "]");
			}
		if (imp != null)
			{
			ConfigNode forImpl = new ConfigNode(parent, imp, false,
					sub.getName() + dimensionString.toString());
// forImpl.setGrayed(InstantiateConfiguredModel.view.getViewer(), true);
			System.out.println("for subcomponent implementation:" + forImpl);
			for (Subcomponent subsub : imp.getAllSubcomponents())
				{
				addSubcomponentToTree(forImpl, subsub);
				}
			}
		else
			{
			ComponentType ct = sub.getComponentType();
			if (ct != null)
				{
				ConfigNode forType = new ConfigNode(parent, ct, true, 
						sub.getName() + dimensionString.toString());
// forType.setGrayed(InstantiateConfiguredModel.view.getViewer(), false);
				System.out.println("for subcomponent type:" + forType);
				// find any implementations
				String prefix = ct.getQualifiedName();  //.getName();
				for (ComponentImplementation ci : AadlUtil.getAllComponentImpl())
					{ // consider using package to limit the
					// number of implementations
					if (ci.getQualifiedName().startsWith(prefix))
						{
						ConfigNode forImp = new ConfigNode(forType, ci, false);
// forImp.setGrayed(InstantiateConfiguredModel.view.getViewer(), true);
						System.out.println("for implemetation of subcomponent type:" + forImp);
						for (final Subcomponent subsub : ci.getAllSubcomponents())
							{
							addSubcomponentToTree(forImp, subsub);
							}
						}
					}
				}
			}
		}
	} // end of ConfigContentProvider