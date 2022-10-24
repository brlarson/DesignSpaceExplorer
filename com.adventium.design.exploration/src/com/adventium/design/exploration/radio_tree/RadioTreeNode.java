//RadioTreeNode.java

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

import org.eclipse.jface.viewers.TreeNode;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ComponentType;

/**
 ********************************************************************************************************************
 * TODO - Fill in an overview of this object
 * @author pahed
 *
 ********************************************************************************************************************
 */
public abstract class RadioTreeNode<T> extends TreeNode {

  private final int TABSIZE = 2;
  private final String SPACES = "                                                          ";
  protected boolean childrenAreExclusive = true;
  private boolean grayed = false;
  private boolean checked = false;

  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   * @param value
   ******************************************************************************************************************
   */
  public RadioTreeNode(RadioTreeNode<T> parent, T value, boolean childrenAreExclusiveIn) {
    super(value);
    childrenAreExclusive = childrenAreExclusiveIn;
    if (parent != null) {
      parent.addChild(this);
    }
  }
  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   * @param value
   ******************************************************************************************************************
   */
  public RadioTreeNode(RadioTreeNode<T> parent, T value) {
    this(parent, value, false);
  }

  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   * @param to1
   ******************************************************************************************************************
   */
  @SuppressWarnings("unchecked")
  private void addChild(RadioTreeNode<T> to1) {
    TreeNode[] children = getChildren();
    if (children == null) {
      RadioTreeNode<T>[] kids = new RadioTreeNode[1];
      kids[0] = to1;
      to1.setParent(this);
      setChildren(kids);
    } else {
      RadioTreeNode<T>[] kids = new RadioTreeNode[children.length + 1];
      System.arraycopy(children, 0, kids, 0, children.length);
      kids[kids.length - 1] = to1;
      to1.setParent(this);
      setChildren(kids);
    }
  }
  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   *
   * @see org.eclipse.jface.viewers.TreeNode#getChildren()
   ******************************************************************************************************************
   */
  @SuppressWarnings("unchecked")
  @Override
  public RadioTreeNode<T>[] getChildren() {
    return (RadioTreeNode[]) super.getChildren();
  }
  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   *
   * @see org.eclipse.jface.viewers.TreeNode#getParent()
   ******************************************************************************************************************
   */
  @SuppressWarnings("unchecked")
  @Override
  public RadioTreeNode<T> getParent() {
    return (RadioTreeNode<T>) super.getParent();
  }
  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   * @param childrenAreExclusive the childrenAreExclusive to set
   ******************************************************************************************************************
   */
  public void setChildrenAreExclusive(boolean childrenAreExclusive) {
    this.childrenAreExclusive = childrenAreExclusive;
  }
  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   *
   * @see java.lang.Object#toString()
   ******************************************************************************************************************
   */
  @Override
  public String toString() {
    return getDisplayName();
  }

  /**
   ******************************************************************************************************************
   * TODO [Description of Method]
   ******************************************************************************************************************
   */
  public RadioTreeNode<T> getResults(RadioTreeNode<T> parent, RadioTreeViewer<T> viewer, int level) {

    boolean selected = viewer.getChecked(this);
    RadioTreeNode<T> result = null;
    if (selected) {
      // Note: the clone is added as a child to the parent.
      result = shallowClone(parent);
      RadioTreeNode<T>[] children = getChildren();
      if (children != null) {
        for (int i = 0; i < children.length; i++) {
          children[i].getResults(result, viewer, level + 1);
        }
      }
    }

    return result;
  }

  /**
   ******************************************************************************************************************
   * Test if this node is "checked" in the GUI viewer provided.
   * @param viewer viewer that this object is viewed in.
   * @return true means user has selected the item, false otherwise.
   ******************************************************************************************************************
   */
  public boolean isChecked(RadioTreeViewer<?> viewer) {
    return viewer.getChecked(this);
  }
  
  /**
   * Sets this node to checked in the viewer
   * @param viewer RadioTreeViewer
   */
   public void setChecked(RadioTreeViewer<?> viewer, boolean check)
   {
	 if (viewer != null) {
		 viewer.setChecked(this, check);
	 }
     checked = check;
   }  //end of setChecked
   
   /**
    * Returns if this node is to be grayed in the viewer (used by ConfigLabelView)
    * @param viewer RadioTreeViewer
    */
   public boolean isChecked() {
 	   return checked;
   }
   
   /**
    * Sets this node to grayed in the viewer
    * @param viewer RadioTreeViewer
    */
    public void setGrayed(RadioTreeViewer<?> viewer, boolean isGray)
    {
      viewer.setGrayed(this, isGray);
      viewer.setChecked(this, true);
      grayed = isGray;
      checked = true;
    }  //end of setGrayed
   
    /**
     * Returns if this node is to be grayed in the viewer (used by ConfigLabelView)
     * @param viewer RadioTreeViewer
     */
    public boolean isGrayed() {
  	   return grayed;
    }
    
    /**
     * Returns the number of children directly under this that have been checked. Or -1 if this element has no children.
     * @return number of check marked direct children or -1
     */
    public int numChildrenChecked() {
    	int retVal = 0;
    	RadioTreeNode<T>[] children = getChildren();
        if (children != null) {
          for (int i = 0; i < children.length; i++) {
            if (children[i].checked) {
            	retVal += 1;
            }
          }
          return retVal;
        }
        return -1;
    }
    
    /**
     * Returns true if all the parent, grandparent, etc. (to the root) are all checked, or
     * there is a decision yet to be made on that path (so parent & it's siblings are all unchecked).
     * @return true if this is on an active branch, false otherwise.
     */
    public boolean onActiveBranch() {
    	RadioTreeNode<T> parent = this.getParent();
    	if (parent == null) {
    		return true;
    	} else if ((checked) || (grayed)) {
    		return parent.onActiveBranch();
    	} else { // if (! checked) || (! grayed)
    		for (RadioTreeNode<T> node: parent.getChildren()) {
				if ((node.checked) && (! node.grayed)) {
					return false; // another sibling was selected
				}
			}
    		return parent.onActiveBranch();
    	}
    }
    
    /**
     * Returns true if none of this elements siblings are checked. (A decision still needs to be made.)
     * @return true if there isn't an active decision for the parent of this node, false otherwse.
     */
    public boolean parentChildrenNotSelected() {
    	RadioTreeNode<T> parent = this.getParent();
    	return ((parent != null) && (parent.numChildrenChecked() == 0));
    }
    
    /**
     * Returns true if any of the children below it have problems.
     * @return true if a child has an issue
     */
    public boolean hasChildWithProblem() {
    	boolean retVal = false;
    	if (hasChildren()) {
    		for (RadioTreeNode<T> node: getChildren()) {
    			if (node.onActiveBranch()) {
    				retVal |= node.hasChildWithProblem() || node.parentChildrenNotSelected();
    			}
    		}
    		return retVal;
    	} else {
    		return false;	
    	}
    }
    
    /**
     * Returns true if all it's parents above it are selected.
     * @return true if parent, grand parent, etc are all selected.
     */
    public boolean allParentsSelected() {
    	RadioTreeNode<T> parent = this.getParent();
    	if (parent == null) {
    		return true;
    	} else if (parent.isChecked()) {
    		return parent.allParentsSelected();
    	} else {
    		return false;
    	}
    }
    
  /**
   ******************************************************************************************************************
   * turn a RadioTree into string
   * @param level
   * @return String
   ******************************************************************************************************************
   */
  public String treeToString(int level) {
    String result = SPACES.substring(0, level * TABSIZE) + toString() + "\n";
    RadioTreeNode<T>[] children = getChildren();
    if (children != null) {
      for (int i = 0; i < children.length; i++) {
        // if children[i].
        result += children[i].treeToString(level + 1);
      }
    }
    return result;
  }

  /**
   ******************************************************************************************************************
   * make string out of selected (checked) tree
   * @param level
   * @return String
   ******************************************************************************************************************
   */
  public String checkedTreeToString(int level) {
    String result = SPACES.substring(0, level * TABSIZE) + toString() + "\n";
    RadioTreeNode<T>[] children = getChildren();
    if (children != null) 
      if (children.length==1)
        result += children[0].checkedTreeToString(level + 1);
      else
      {
      for (int i = 0; i < children.length; i++) {
        if (!childrenAreExclusive||
        	!isComponentType()||
            (children[i].isComponentImplementation() && children[i].isChecked()))
        result += children[i].checkedTreeToString(level + 1);
      }
    }
    return result;
  }
  
  /**
   ******************************************************************************************************************
   * Get Display Name
   * @return String
   ******************************************************************************************************************
   */
  public abstract String getDisplayName();

  /**
   ******************************************************************************************************************
   * Shallow Clone
   * @return
   ******************************************************************************************************************
   */
  public abstract RadioTreeNode<T> shallowClone(RadioTreeNode<T> parent);

  /**
   ******************************************************************************************************************
   * Fills list of gray nodes (children not exclusive) in tree from this node
   * @return
   ******************************************************************************************************************
   */
  public void getGrayNodes(Vector<RadioTreeNode<T>> list) {
    RadioTreeNode<T>[] children = getChildren();
    if (children != null) {
    	for (int i = 0; i < children.length; i++) {
    		if ((!childrenAreExclusive) || (children.length == 1)) {
    			list.addElement(children[i]);
    		}
    		children[i].getGrayNodes(list);
    	}
    }
  }


  /**
   ******************************************************************************************************************
   * check if node is of the component type
   * @return whether this node holds a component type
   ******************************************************************************************************************
   */
  public boolean isComponentType() {
    return value instanceof ComponentType;
  }

  /**
   ******************************************************************************************************************
   * check if node is of the component implementation
   * @return whether this node holds a component type
   ******************************************************************************************************************
   */
  public boolean isComponentImplementation() {
    return value instanceof ComponentImplementation;
  }
  
  /**
   * Overriding equal so not just the object is checked, but also the direct parent.
   * NOTE: it may be possible that we need to walk further up tree to see differences!! //TODO
   */
  @Override
  public boolean equals(Object obj) {
	  if (obj instanceof RadioTreeNode) {
		  @SuppressWarnings("unchecked") RadioTreeNode<T> tn = (RadioTreeNode<T>)obj;
		  return ((this.value == tn.getValue()) && (this.getParent() == tn.getParent()));
	  }
	  return false;
  }
  
  
  /**
   * 
   * @return
   */
	public String getWhyCantInstantiateMsg() {
		String retStr = "";
		RadioTreeNode<T>[] children = getChildren();
	    if (children != null) {
	        if (childrenAreExclusive && (numChildrenChecked() != 1)) {
	          retStr += this.getDisplayName() + " needs a selected implementation.  ";
	        }
	        for (int i = 0; i < children.length; i++) {
	        	retStr += children[i].getWhyCantInstantiateMsg();
	        }
	      }
		return retStr;
	}
   
}
