//InstantiateConfiguredModel.java
//this class extends the OSATE class InstantiateModel to perform instantiation
//most of the work is done by InstantiateModel, but we override buildInstanceModelFile
//to make instance models by selecting the checked implementation for subcomponent types

//revised Nov 29, 2020 brl

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

import static org.osate.aadl2.modelsupport.util.AadlUtil.getElementCount;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osate.aadl2.ArrayDimension;
import org.osate.aadl2.ArraySize;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;
import org.osate.aadl2.Feature;
import org.osate.aadl2.ModalElement;
import org.osate.aadl2.Mode;
import org.osate.aadl2.PropertyConstant;
import org.osate.aadl2.PrototypeBinding;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.FeatureInstance;
import org.osate.aadl2.instance.InstanceFactory;
import org.osate.aadl2.instance.InstanceObject;
import org.osate.aadl2.instance.ModeInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.impl.SystemInstanceImpl;
import org.osate.aadl2.instance.util.InstanceUtil;
import org.osate.aadl2.instance.util.InstanceUtil.InstantiatedClassifier;
import org.osate.aadl2.instantiation.InstantiateModel;
import org.osate.aadl2.modelsupport.AadlConstants;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.errorreporting.MarkerAnalysisErrorReporter;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import com.adventium.design.exploration.antlr3generated.MakeTypeToImplementationMapLexer;
import com.adventium.design.exploration.antlr3generated.MakeTypeToImplementationMapParser;
import com.adventium.design.exploration.handlers.ChooseHandler;
import com.adventium.design.exploration.handlers.SetArrayProperties;
import com.adventium.design.exploration.views.ConfigurationView;

public class InstantiateConfiguredModel extends InstantiateModel // MyInstantiateModel
	{

//	private IProgressMonitor monitor = super.monitor;
	//private AnalysisErrorReporterManager errManager = super.errManager;
	
	@SuppressWarnings("unused")
	private static String errorMessage = null;

	public static ConfigurationView view;
	private static String SOURCE_FILE_NAME = null;
	
	private static HashMap<ComponentImplementation, ComponentType> originalRealizationMappingCache = new HashMap<ComponentImplementation, ComponentType>();

	// public static RadioTreeViewer<ConfigNode> viewer;

	public InstantiateConfiguredModel(IProgressMonitor pm)
		{
		super(pm);
//		monitor = pm;
		}

	public InstantiateConfiguredModel(final IProgressMonitor pm,
			final AnalysisErrorReporterManager errMgr)
		{
		super(pm, errMgr);
//		monitor = pm;
		//errManager = errMgr;
		}

	/**
	 * opens a FileDialog to let the user choose a signature file then invokes
	 * loadTypeToImplementationMap(signatureFileName)
	 */
	public static void loadTypeToImplementationMap()
		{
		Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());
//				ConfigurationActivator.plugin.getWorkbench().getDisplay());
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.open();
		String filterPath = dialog.getFilterPath();
		String fileName = dialog.getFileName();
		String signatureFileName = filterPath + "/" + fileName;
		Say.it("You chose file: " + signatureFileName);
		loadTypeToImplementationMap(signatureFileName);
		shell.dispose();
		}

	/**
	 * loads a type-to-implementation map from a signature file this map
	 * represents the selection of an implementation for each subcomponent type
	 * 
	 * @param signatureFileName
	 */
	public static void loadTypeToImplementationMap(String signatureFileName)
		{
		try
			{
			// invoke ANTLR parser to load map
			CharStream spec = new ANTLRFileStream(signatureFileName);
			MakeTypeToImplementationMapLexer lexer = new MakeTypeToImplementationMapLexer(
					spec);
			CommonTokenStream tokens = new CommonTokenStream();
			tokens.setTokenSource(lexer);
			MakeTypeToImplementationMapParser parser = new MakeTypeToImplementationMapParser(
					tokens);
			parser.makemap();
			int beginIndex = signatureFileName.lastIndexOf("/");
			SOURCE_FILE_NAME = signatureFileName.substring(beginIndex+1, signatureFileName.length()-4);
			Say.it("Configuration file for "
					+ MakeTypeToImplementationMapParser.rootSystemImplementation
					+ " loaded.");
			Say.it(MakeTypeToImplementationMapParser.typeToImplementation.toString());
			}
		catch (FileNotFoundException e)
			{
			Say.it("The signature file \"" + signatureFileName
					+ "\" could not be found.");
			e.printStackTrace();
			}
		catch (IOException e)
			{
			Say.it("IOException in loadTypeToImplementationMap of file "
					+ signatureFileName);
			e.printStackTrace();
			}
		catch (RecognitionException e)
			{
			Say.it("RecognitionException in loadTypeToImplementationMap of file "
					+ signatureFileName);
			Say.it(e.getMessage());
			e.printStackTrace();
			}
		} // end of loadTypeToImplementationMap

	/**
	 * given a subcomponent identifier, and a component type, looks up the
	 * selected implementation in the type-to-implementation map
	 * 
	 * @param type
	 * @param subcomponentID
	 * @return
	 */
	protected ComponentImplementation chooseImplementationFromType(
			ComponentType type, String subcomponentID)
		{
		if (MakeTypeToImplementationMapParser.typeToImplementation == null)
			{
			Say.it("Type to Implementation Map is Uninitialized");
			return null;
			}
		else
			{
			String typeName = subcomponentID + ":" + type.getName();
			String implementationName = MakeTypeToImplementationMapParser.typeToImplementation
					.get(typeName);
			if (implementationName == null)
				{
				Say.it("No implementation found in map for " + typeName);
				return null;
				}
			else
				Say.it("Chosen implementation for " + typeName + " is "
						+ implementationName);
			// find all possible implementations for this type
			for (ComponentImplementation ci : AadlUtil.getAllComponentImpl())
				{
				
			
				String ciName = ci.getName();
				if (ciName.equalsIgnoreCase(implementationName))
					{
					originalRealizationMappingCache.put(ci, ci.getOwnedRealization().getImplemented());
					ci.getOwnedRealization().setImplemented(type);
					return ci;
					}
				}
			// if no find
			Say.it(implementationName
					+ " was not found among component implementations in the workspace.");
			return null;
			}
		} // end of chooseImplementationFromType

// @Override
// protected void populateComponentInstance(ComponentInstance ci, int index) {
// ComponentImplementation impl =
// InstanceUtil.getComponentImplementation(ci, 0, classifierCache);
// ComponentType type = InstanceUtil.getComponentType(ci, 0,
// classifierCache);
// if (ci.getContainingComponentInstance() instanceof SystemInstance) {
// monitor.subTask("Creating instances in " + ci.getName());
// }
//
// //if only component type, select component instance
// if (impl==null)
// impl = chooseImplementationFromType(type);
// /*
// * add modes & transitions. Must do this before adding subcomponents
// * because we need to know what are the ModeInstances when processing
// * modal subcomponents.
// */
// if (impl != null) {
// fillModesAndTransitions(ci, impl.getAllModes(),
// impl.getAllModeTransitions());
// } else if (type != null) {
// fillModesAndTransitions(ci, type.getAllModes(),
// type.getAllModeTransitions());
// }
// if (impl != null) {
// if (monitor.isCanceled()) {
// return;
// // }
// //
// // // Recursively add subcomponents
// // instantiateSubcomponents(ci, impl);
// //
// // // EList callseqlist = compimpl.getXAllCallSequence();
// // // for (Iterator it = callseqlist.iterator(); it.hasNext();) {
// // // CallSequence cseq = (CallSequence) it.next();
// // //
// // // EList calllist = cseq.getCall();
// // // for (Iterator iit = calllist.iterator(); iit.hasNext();) {
// // // final SubprogramSubcomponent call =
// // // (SubprogramSubcomponent) iit.next();
// // // instantiateSubcomponent(ci, cseq, call);
// // // }
// // // }
// }
//
// // do it only if subcomponent has type
// if (type != null) {
// instantiateFeatures(ci);
// if (monitor.isCanceled()) {
// return;
// }
// instantiateFlowSpecs(ci);
// if (monitor.isCanceled()) {
// return;
// }
// }
// return;
// } // end of populateComponentInstance

	/**
	 * load map first, either by LoadSignatureHandler, or from check box tree
	 * loadTypeToImplementationMap(); add it to a resource; otherwise we cannot
	 * attach error messages to the instance file
	 * 
	 * @param ci
	 * @return
	 * @throws Exception
	 */
	// @Override
	public static SystemInstance buildInstanceModelFile(
			final ComponentImplementation ci) throws Exception
		{
		ComponentImplementation ici = ci;
		final String iciRealName = ici.getFullName();
		String iciNewName = ici.getFullName()+"_"+ConfigurationView.lastHash;
		if (SOURCE_FILE_NAME != null) {
			iciNewName = SOURCE_FILE_NAME.replaceFirst("_", "."); //quick name change if from a sig file...
			SOURCE_FILE_NAME = null;
		}
		
		final InstantiateConfiguredModel instantiateModel = new InstantiateConfiguredModel(
				new NullProgressMonitor(),
				new AnalysisErrorReporterManager(
						new MarkerAnalysisErrorReporter.Factory(
								AadlConstants.INSTANTIATION_OBJECT_MARKER)));
		
		//Change the name & create a file
		ici.setName(iciNewName);
//copied from InstantiateModel:
    URI instanceURI = InstantiateModel.getInstanceModelURI(ci);
    IFile file = OsateResourceUtil.toIFile(instanceURI);
    if (file != null && file.isAccessible()) {
      file.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
    }
    ResourceSet resourceSet = new ResourceSetImpl();
    Resource aadlResource = resourceSet.createResource(instanceURI);
    aadlResource.save(null);
    aadlResource.unload();
		
//		URI instanceURI = null; InstantiateModel.getInstanceModelURI(ici);
//		Resource aadlResource = ChooseHandler.resource;
//		if (aadlResource == null) {
//			aadlResource = null; OsateResourceUtil.getEmptyAaxl2Resource(instanceURI);
//		}
		//Change name back, instantiate & save to file with a last minute name change done before saving in my createSystemInstance method
		ici.setName(iciRealName);
		SystemInstance root = instantiateModel.createSystemInstance(ici, aadlResource, iciNewName.replaceAll("\\.", "_")+"_Instance");
		if (root == null){
			errorMessage = InstantiateConfiguredModel.getErrorMessage();
		}
		//Change back all the realizations
		for (Entry<ComponentImplementation, ComponentType> entry: originalRealizationMappingCache.entrySet()) {
			entry.getKey().getOwnedRealization().setImplemented(entry.getValue());
		}
		originalRealizationMappingCache.clear();
		return root;
		}

	
	/**
	 * Copy from createSystemInstance of parent class, but adding & changing the name!
	 * @param ci
	 * @param aadlResource
	 * @param newInstanceName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public SystemInstance createSystemInstance(final ComponentImplementation ci, final Resource aadlResource, final String newInstanceName)
			throws Exception {
	    SystemInstance result = createSystemInstance(ci,aadlResource);
//		List<SystemInstance> resultList;
//		SystemInstance result;
//
//		result = null;
//
//		final TransactionalEditingDomain domain = TransactionalEditingDomain.Registry.INSTANCE
//				.getEditingDomain("org.osate.aadl2.ModelEditingDomain");
//		// We execute this command on the command stack because otherwise, we will not
//		// have write permissions on the editing domain.
//		Command cmd = new RecordingCommand(domain) {
//			public SystemInstance instance;
//
//			@Override
//			protected void doExecute() {
//				try {
//					instance = createSystemInstanceInt(ci, aadlResource, true);
//				} catch (InterruptedException e) {
//					// Do nothing. Will be thrown after execute.
//				}
//			}
//
//			@Override
//			public List<SystemInstance> getResult() {
//				return Collections.singletonList(instance);
//			}
//		};
//
//		((TransactionalCommandStack) domain.getCommandStack()).execute(cmd, null);
//		if (monitor.isCanceled()) {
//			throw new InterruptedException();
//		}
//
//		try {
//			
//			//at the last minute change the name!
//			for (EObject content: aadlResource.getContents()) {
//				if (content instanceof SystemInstanceImpl) {
//					((SystemInstanceImpl)content).setName(newInstanceName);
//				}
//			}
//			
//			// We're done: Save the model.
//			// We don't respond to a cancel at this point
//			monitor.subTask("Saving instance model");
//			aadlResource.save(null);
//		} catch (IOException e) {
//			e.printStackTrace();
//			setErrorMessage(e.getMessage());
//			return null;
//		}
//
//		resultList = (List<SystemInstance>) cmd.getResult();
//		result = resultList.get(0);
    if (result != null)
		result.setName(newInstanceName);
		return result;
	}
	
// /**
// * write out signature file given selection tree
// * @param ci
// * @return
// */
// public static String writeSignatureFile(final ComponentImplementation ci)
// {
// Say.it(ConfigurationActivator.contentProvider.getRoot().toString());
//
// String signatureFileName = "";
//
// return signatureFileName;
// }

	/**
	 * get component implementation from map if component type uses the
	 * classifierCache correctly--thanks to Lutz Wrage this is where the magic
	 * happens to harness OSATE's instance model generation
	 */
	@Override
	protected ComponentImplementation getComponentImplementation(
			ComponentInstance ci, int index,
			HashMap<InstanceObject, InstantiatedClassifier> classifierCache)
		{
		ComponentImplementation impl = null;
		if (ci instanceof SystemInstance)
			{
			impl = ((SystemInstance) ci).getComponentImplementation();
			}
		else
			{
			final InstantiatedClassifier ic = InstanceUtil
					.getInstantiatedClassifier(ci, 0, classifierCache);

			if (ic != null)
				{
				final ComponentClassifier cc = (ComponentClassifier) ic.getClassifier();

				if (cc != null && cc instanceof ComponentImplementation)
					{
					impl = (ComponentImplementation) cc;
					}
				else if (cc != null && cc instanceof ComponentType)
					{ // get it from our map
					impl = chooseImplementationFromType((ComponentType) cc,
							ci.getSubcomponent().getName());
					// set the classifier cache to point to our implementation
					EList<PrototypeBinding> protoBindings = classifierCache.get(ci).getBindings();
					classifierCache.replace(ci, new InstantiatedClassifier(impl,protoBindings));
//					classifierCache.get(ci).classifier = impl;
					}
				}
			}
		
		return impl;
		}

	
/*
 * Add feature instances to component instance
 */
	@Override
	protected void instantiateFeatures(final ComponentInstance ci)
			throws InterruptedException
		{
		for (final Feature feature : getInstantiatedClassifier(ci).getClassifier()
				.getAllFeatures())
			{
// if (monitor.isCanceled()) {
// throw new InterruptedException();
// }
			final EList<ArrayDimension> dims = feature.getArrayDimensions();

			if (dims.isEmpty())
				{
				fillFeatureInstance(ci, feature, false, 0);
				}
			else
				{
				// feature dimension should always be one
				class ArrayInstantiator
					{
					void process(int dim)
						{
						ArraySize arraySize = dims.get(dim).getSize();
						PropertyConstant pc = (PropertyConstant)arraySize.getSizeProperty();
						String propertyConstantName = pc.getName();
						long count = getElementCount(arraySize, ci);
						if (SetArrayProperties.arraySizeMap.containsKey(propertyConstantName))
							count = Long.parseLong(SetArrayProperties.arraySizeMap.get(propertyConstantName));
						for (int i = 1; i <= count; i++)
							{
							try
                {
                fillFeatureInstance(ci, feature, false, i);
                }
              catch (InterruptedException e)
                {
                e.printStackTrace();
                }
							}
						}
					}
				new ArrayInstantiator().process(0);
				}
			}
		} // end of instantiateFeatures

	/**
	 * instantiate features in feature group take into account that they can be
	 * declared as arrays
	 */
	@Override
	protected void instantiateFGFeatures(final FeatureInstance fgi,
			List<Feature> flist, final boolean inverse)
		{
		for (final Feature feature : flist)
			{
			final EList<ArrayDimension> dims = feature.getArrayDimensions();

			if (dims.isEmpty())
				{
				try
          {
          fillFeatureInstance(fgi, feature, inverse, 0);
          }
        catch (InterruptedException e)
          {
          e.printStackTrace();
          }
				}
			else
				{
				class ArrayInstantiator
					{
					void process(int dim)
						{
//substitute array size from map if there						
						ArraySize arraySize = dims.get(dim).getSize();
						PropertyConstant pc = (PropertyConstant)arraySize.getSizeProperty();
						String propertyConstantName = pc.getName();
						long count = getElementCount(arraySize,
								fgi.getContainingComponentInstance());
						if (SetArrayProperties.arraySizeMap.containsKey(propertyConstantName))
							count = Long.parseLong(SetArrayProperties.arraySizeMap.get(propertyConstantName));
						for (int i = 0; i < count; i++)
							{
							try
                {
                fillFeatureInstance(fgi, feature, inverse, i + 1);
                }
              catch (InterruptedException e)
                {
                e.printStackTrace();
                }
							}
						}
					}
				new ArrayInstantiator().process(0);
				}
			}
		} // end of instantiateFGFeatures

	@Override
	protected void instantiateSubcomponents(final ComponentInstance ci,
			ComponentImplementation impl) throws InterruptedException
		{
		for (final Subcomponent sub : impl.getAllSubcomponents())
			{
// if (monitor.isCanceled()) {
// throw new InterruptedException();
// }
			if (hasSubcomponentInstance(ci, sub))
				{
//				errManager.error(ci,
				errorMessage=		"Cyclic containment dependency: Subcomponent '" + sub.getName()
								+ "' has already been instantiated as enclosing component.";
				}
			else
				{
				final EList<ArrayDimension> dims = sub.getArrayDimensions();
				Stack<Long> indexStack = new Stack<Long>();

				if (dims.isEmpty())
					{
					instantiateSubcomponent(ci, sub, sub, indexStack, 0);
					}
				else
					{
					final int dimensions = dims.size();
					class ArrayInstantiator
						{
						void process(int dim, Stack<Long> indexStack)
								throws InterruptedException
							{
							// index starts with one
							ArraySize arraySize = dims.get(dim).getSize();
							PropertyConstant pc = (PropertyConstant)arraySize.getSizeProperty();
							String propertyConstantName = pc.getName();
							long count = getElementCount(arraySize, ci);
							if (SetArrayProperties.arraySizeMap.containsKey(propertyConstantName))
								count = Long.parseLong(SetArrayProperties.arraySizeMap.get(propertyConstantName));
							for (int i = 1; i <= count; i++)
								{
//								if (monitor.isCanceled())
//									{
//									throw new InterruptedException();
//									}
								if (dim + 1 < dimensions)
									{
									indexStack.push(Long.valueOf(i));
									process(dim + 1, indexStack);
									indexStack.pop();
									}
								else
									{
									instantiateSubcomponent(ci, sub, sub, indexStack, i);
									}
								}
							}
						}
					new ArrayInstantiator().process(0, indexStack);
					}
				}
			}
		} // end of instantiateSubcomponents
	

	/*
	 * check to see if the specified subcomponent already exists as component
	 * instance in the ancestry
	 */
	private boolean hasSubcomponentInstance(ComponentInstance ci, Subcomponent sub) {
		ComponentInstance parent = ci;
		while (parent != null && !(parent instanceof SystemInstance)) {
			Subcomponent psc = parent.getSubcomponent();
			if (psc == sub) {
				return true;
			}
			parent = (ComponentInstance) parent.eContainer();
		}
		return false;
	}

	/* HSS: this is a direct copy of the parent method, but the classifier is set after the cache is populated. */
	@Override
	protected void instantiateSubcomponent(final ComponentInstance parent, final ModalElement mm,
			final Subcomponent sub, Stack<Long> indexStack, int index) throws InterruptedException {
		
		
		final ComponentInstance newInstance = InstanceFactory.eINSTANCE.createComponentInstance();
		final ComponentClassifier cc;
		final InstantiatedClassifier ic;
		
		newInstance.setSubcomponent(sub);
		newInstance.setCategory(sub.getCategory());
		newInstance.setName(sub.getName());
		newInstance.getIndices().addAll(indexStack);
		newInstance.getIndices().add(Long.valueOf(index));
		parent.getComponentInstances().add(newInstance);
		
		for (Mode mode : mm.getAllInModes()) {
			ModeInstance mi = parent.findModeInstance(mode);

			if (mi != null) {
				newInstance.getInModes().add(mi);
			}
		}
		populateComponentInstance(newInstance, index);
		
		// HSS: first populate & do everything in the original, then set the classifier & category
		ic = getInstantiatedClassifier(newInstance);
		if (ic == null) {
			cc = null;
		} else {
			cc = (ComponentClassifier) ic.getClassifier();
		}
		if (cc == null) {
			errorMessage = "Instantiated subcomponent doesn't have a component classifier";
		} else {
			newInstance.setClassifier(cc);
			newInstance.setCategory(cc.getCategory());
		}

	}
	

} // end of InstantiateConfiguredModel
