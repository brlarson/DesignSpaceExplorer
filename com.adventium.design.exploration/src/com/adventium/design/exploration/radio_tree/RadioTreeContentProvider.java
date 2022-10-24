//RadioTreeContentProvider.java

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

package com.adventium.design.exploration.radio_tree;

import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;

/**
 ********************************************************************************************************************
 * Content provider for the tree viewer - this is the model for the tree.
 * 
 * @author pahed
 *
 ********************************************************************************************************************
 */
public class RadioTreeContentProvider<T> implements IStructuredContentProvider, ITreeContentProvider
	{

	private RadioTreeNode<T> root;
	private Vector<RadioTreeNode<T>> grayNodes = new Vector<RadioTreeNode<T>>();

	/**
	 ******************************************************************************************************************
	 * Constructor with the viewsite and tree data defined.
	 *
	 * @param rootIn
	 *          tree data to view
	 ******************************************************************************************************************
	 */
	public RadioTreeContentProvider(RadioTreeNode<T> rootIn)
		{
		root = rootIn;
		}

	/**
	 ******************************************************************************************************************
	 * Sets the root node. Note: the tree will need to be refreshed after this
	 * call.
	 * 
	 * @param rootIn
	 *          new root to use.
	 ******************************************************************************************************************
	 */
	public void setRoot(RadioTreeNode<T> rootIn)
		{
		root = rootIn;
		}

	/**
	 ******************************************************************************************************************
	 * TODO [Description of Method]
	 *
	 * @return the root
	 ******************************************************************************************************************
	 */
	public RadioTreeNode<T> getRoot()
		{
		return root;
		}

	
	/**
	 ******************************************************************************************************************
	 * Gets the children for a given node.
	 *
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 ******************************************************************************************************************
	 */
	public RadioTreeNode<T>[] getElements(Object parent)
		{
		@SuppressWarnings("unchecked")
		RadioTreeNode<T>[] result = new RadioTreeNode[1];
		result[0] = root;
		// root.getGrayNodes(grayNodes);

		return result;
		}

	/**
	 ******************************************************************************************************************
	 * Gets the parent for a given child
	 *
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 ******************************************************************************************************************
	 */
	@SuppressWarnings("unchecked")
	public RadioTreeNode<T> getParent(Object child)
		{
		if (child instanceof TreeNode)
			{
			return ((RadioTreeNode<T>) child).getParent();
			}
		return null;
		}

	/**
	 ******************************************************************************************************************
	 * Gets children for a node.
	 *
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 ******************************************************************************************************************
	 */
	@SuppressWarnings("unchecked")
	public RadioTreeNode<T>[] getChildren(Object parent)
		{
		if (parent instanceof RadioTreeNode<?>)
			{
			return ((RadioTreeNode<T>) parent).getChildren();
			}
		return new RadioTreeNode[0];
		}

	/**
	 ******************************************************************************************************************
	 * Check if node has children.
	 *
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 ******************************************************************************************************************
	 */
	public boolean hasChildren(Object parent)
		{
		if (parent instanceof TreeNode)
			{
			return ((TreeNode) parent).hasChildren();
			}
		return false;
		}

	/**
	 ******************************************************************************************************************
	 * get nodes of tree supposed to be gray
	 * 
	 * @return
	 ******************************************************************************************************************
	 */
	public Object[] getGrayNodes()
		{
		return grayNodes.toArray();
		}

	/**
	 * set gray nodes
	 */
	public void setGrayNodes(RadioTreeNode<T> node)
		{
		if (node == null)
			setGrayNodes(root);
		else
			{
			if ((getParent(node)!=null)&&!getParent(node).childrenAreExclusive)
				grayNodes.add(node);
			if (hasChildren(node))
			for (RadioTreeNode<T> child : getChildren(node))
				setGrayNodes(child);
			}
		}  //end of setGrayNodes

	}
