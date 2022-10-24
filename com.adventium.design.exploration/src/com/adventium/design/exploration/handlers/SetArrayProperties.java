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

import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;

import com.adventium.design.exploration.Say;

public class SetArrayProperties extends AbstractHandler {

  public static HashMap<String,String> arraySizeMap = new HashMap<String,String>();
  
	public Object execute(ExecutionEvent event) throws ExecutionException
		{
		Say.it("Setting Array Size Properties");
//		OsateResourceUtil.refreshResourceSet();
		
		for(Object c: IGlobalScopeProvider.class.getClasses()) {
			if (c != null) {
				Say.it("TESTING: "+c.toString());
			}
		}
		return null;
		
		/**
		EList<IEObjectDescription> propertysetlist = EMFIndexRetrieval
				.getAllPropertySetsInWorkspace();
		
		
//for each property set:
	  for (IEObjectDescription psod : propertysetlist)
	    {
	    PropertySetImpl psi = (PropertySetImpl) EcoreUtil.resolve(psod.getEObjectOrProxy(), 
	        OsateResourceUtil.getResource());
	    //property types
	    EList<PropertyType> propertytypelist=psi.getOwnedPropertyTypes();
	    //property values
	    EList<Property> propertylist=psi.getOwnedProperties();
	    //now property constants
	    EList<PropertyConstant> propertyconstantlist=psi.getOwnedPropertyConstants();
	    //if any property types, then skip
	    if ((propertytypelist==null||propertytypelist.size()==0)  //no property types
	    		&&(propertylist==null||propertylist.size()==0)  //no properties
	        &&(propertyconstantlist!=null&&propertyconstantlist.size()>0)) 
	    	{
	    	boolean allconstantsareinteger = true;
	    	//check whether all property constants are aadlinteger
	    	for (PropertyConstant propConst : propertyconstantlist)
	    		{
	    		PropertyType pt=propConst.getPropertyType();
	    		if (!(pt instanceof AadlInteger))
	    			allconstantsareinteger=false;
	    		}  //done checkig if all aadlinteger
	    	if (allconstantsareinteger)
	    		{
	    		Say.it(psi.getFullName()+" has only property constants.");
	    		Shell shell = new Shell(ConfigurationActivator.plugin.getWorkbench().getDisplay());
	    		boolean setconstants = MessageDialog.openQuestion(shell,"?",
	    				"Do you want to change array sizes in "+psi.getFullName()+"?");
	    		if (setconstants)
	    			for (PropertyConstant propConst : propertyconstantlist)
	    				{
	    				PropertyExpression pe = propConst.getConstantValue();
	    				if (pe instanceof IntegerLiteral)
	    					{
	    					int priorsize=(int)((IntegerLiteral)pe).getValue();
	    					InputDialog id=new InputDialog(shell,"Choose array size",
	    							"for "+psi.getFullName()+"::"+propConst.getFullName(),
	    							Integer.toString(priorsize),new IntegerValidator());
	    					id.open();
	    					int newsize=Integer.parseInt(id.getValue());
	    					id.close();
	    					((IntegerLiteral) pe).setValue(newsize);
//	    					IntegerLiteral propertyValue = Aadl2Factory.eINSTANCE.createIntegerLiteral();
//	    					propertyValue.setValue(newsize);
//	    					propConst.setConstantValue(propertyValue);
	    					Say.it("Value of "+propConst.getFullName()+" changed from "+priorsize+" to "+
	    							((IntegerLiteral)propConst.getConstantValue()).getValue()+".");
	    					}
	    				} //done with each array size
	    		for (PropertyConstant pc : psi.getOwnedPropertyConstants())
	    			{
	    			String propertyConstantName = pc.getName();
	    			String propertyConstantValue = Integer.toString((int)((IntegerLiteral)pc.getConstantValue()).getValue());
	    		  Say.it(propertyConstantName+" = "+propertyConstantValue);
	    		  arraySizeMap.put(propertyConstantName,propertyConstantValue);
	    			}
	    		}  //done with all-constant property type
	    	}  //done with this property set
	    }  //done with all property sets
//		OsateResourceUtil.refreshResourceSet();
		return null;
		*/
		}

}
