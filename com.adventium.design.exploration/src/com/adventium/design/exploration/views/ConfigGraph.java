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

import java.util.HashMap;
import java.util.Map.Entry;

public class ConfigGraph {

	public enum Color {
		ERROR,
		SELECTED,
		GRAY,
		STUB,
		NORMAL
	}
	
	private HashMap<String,String> nodeLabels = new HashMap<String,String>();
	private HashMap<String,Color> nodeColors = new HashMap<String,Color>();
	private HashMap<String,Boolean> nodeIsImplementations = new HashMap<String,Boolean>();
	private HashMap<String,Color> connectionColors = new HashMap<String,Color>();

	public void addNode(ConfigNode node) {
		String nodeName = clean(node.getDisplayName());
		String nodeLabel = node.getColDisplayName();
		nodeLabels.put(nodeName, nodeLabel);
		if (nodeColors.containsKey(nodeName)) {
			Color newColor = greaterColorImportance(nodeColors.get(nodeName),getColor(node));
			nodeColors.put(nodeName, newColor);
		} else {
			nodeColors.put(nodeName, getColor(node));
			nodeIsImplementations.put(nodeName, node.isComponentImplementation()); // this better not conflict!!
		}
	}
	
	public void addConnection(ConfigNode parent, ConfigNode child) {
		String parentName = clean(parent.getDisplayName());
		String childName = clean(child.getDisplayName());
		String connectionName = parentName+" -- "+childName;
		if (connectionColors.containsKey(connectionName)) {
			Color newColor = greaterColorImportance(connectionColors.get(connectionName),getColor(child));
			connectionColors.put(connectionName, newColor);
		} else {
			connectionColors.put(connectionName, getColor(child));
		}
	}
	
	/**
	   * Create a new graph in the given file.
	   * @param parent
	   */
	  public String toFileString()
	  {
		  String retVal = "rankdir = \"LR\";\n";

		  retVal += getLegendString();
		  
		  for (String nodeName: nodeLabels.keySet()) {
			  String nodeLabel = nodeLabels.get(nodeName);
			  Color nodeColor = nodeColors.get(nodeName);
			  Boolean nodeIsImplementation = nodeIsImplementations.get(nodeName);
			  
			  retVal += "\t"+nodeName + " [label = \""+nodeLabel+"\""; 
			  if (nodeColor == Color.SELECTED) {
				  retVal += ", color = blue";
			  } else if (nodeColor == Color.GRAY) {
				  retVal += ", color = gray";
			  } else if (nodeColor == Color.ERROR) {
				  retVal += ", color = red";
			  } else if (nodeColor == Color.STUB) {
				  retVal += ", color = magenta";
			  }
			  if (nodeIsImplementation) {
				  retVal += ", shape = ellipse";
			  } else {
				  retVal += ", shape = box";
			  }
			  retVal += "]\n";
		  }
			  
		  for ( Entry<String, Color> connection: connectionColors.entrySet()) {
			  retVal += "\t"+connection.getKey();
			  if (connection.getValue() == Color.SELECTED) {
		  			retVal +=" [color = blue]";
			  } else if (connection.getValue() == Color.GRAY) {
				  retVal += " [color = gray]";
			  } else if (connection.getValue() == Color.ERROR) {
	  		  	retVal += " [color = red]";
			  } else if (connection.getValue() == Color.STUB) {
		  		  	retVal += " [color = magenta]";
			  }
			  retVal += "\n";
		  }
		  
		  return retVal;
	  }
	  

	  private Color getColor(ConfigNode node) {
		  
		  if (! node.onActiveBranch()) {
				//NON_ACTIVE_COLOR;
				return Color.GRAY;
			}
			if (node.parentChildrenNotSelected()) {
				if (node.getDisplayName().contains(".stub")) {
					//IS_STUB_COLOR;
					return Color.STUB;
				} else {
					//ACTIVE_DECISION_COLOR;
					return Color.ERROR;
				}
			}
			if (node.isChecked() && node.allParentsSelected()) {
				//IS_CHOSEN_COLOR;
				return Color.SELECTED;
			}
			return Color.NORMAL;
	  }
	  
	  private Color greaterColorImportance(Color a, Color b) {
		  if ((a == Color.ERROR) || (b == Color.ERROR)) {
			  return Color.ERROR;
		  } else if ((a == Color.STUB) || (b == Color.STUB)) {
			  return Color.STUB;
		  } else if ((a == Color.SELECTED) || (b == Color.SELECTED)) {
			  return Color.SELECTED;
		  } else if ((a == Color.GRAY) || (b == Color.GRAY)) {
			  return Color.GRAY;
		  } else {
			  return Color.NORMAL;
		  }
	  }
	  
	  private String clean(String name) {
		  return name.replace(".", "__").replaceAll(" ", "__").replaceAll(":", "__");
	  }
	  
	  private String getLegendString() {
		  String retStr = "";
		  retStr += "{ rank = min;\n";
		  retStr += "  Legend [shape=none, margin=0, label=<\n";
		  retStr += "  <table border=\"0\" cellborder=\"1\" cellspacing=\"0\" cellpadding=\"4\">\n";
		  retStr += "   <TR>\n";
		  retStr += "    <TD colspan=\"3\"><B>Legend</B></TD>\n";
		  retStr += "   </TR>\n";
		  retStr += "   <TR>\n";
		  retStr += "    <TD rowspan=\"5\">Color</TD>\n";
		  retStr += "    <TD cellpadding=\"4\">\n";
		  retStr += "     <table border=\"1\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
		  retStr += "      <TR>\n";
		  retStr += "        <TD bgcolor=\"red\"><font color=\"red\">XX</font></TD>\n";
		  retStr += "      </TR>\n";
		  retStr += "     </table>\n";
		  retStr += "    </TD>\n";
		  retStr += "    <TD align=\"left\">Selection on this branch is needed</TD>\n";
		  retStr += "   </TR>\n";
		  retStr += "   <TR>\n";
		  retStr += "    <TD cellpadding=\"4\">\n";
		  retStr += "     <table border=\"1\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
		  retStr += "      <TR>\n";
		  retStr += "        <TD bgcolor=\"magenta\"><font color=\"magenta\">XX</font></TD>\n";
		  retStr += "      </TR>\n";
		  retStr += "     </table>\n";
		  retStr += "     </TD>\n";
		  retStr += "    <TD align=\"left\">Stub implementation (selection needed)</TD>\n";
		  retStr += "   </TR>\n";
		  retStr += "   <TR>\n";
		  retStr += "    <TD cellpadding=\"4\">\n";
		  retStr += "     <table border=\"1\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
		  retStr += "      <TR>\n";
		  retStr += "        <TD bgcolor=\"blue\"><font color=\"blue\">XX</font></TD>\n";
		  retStr += "      </TR>\n";
		  retStr += "     </table>\n";
		  retStr += "     </TD>\n";
		  retStr += "    <TD align=\"left\">Selected branch</TD>\n";
		  retStr += "   </TR>\n";
		  retStr += "   <TR>\n";
		  retStr += "    <TD cellpadding=\"4\">\n";
		  retStr += "     <table border=\"1\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
		  retStr += "      <TR>\n";
		  retStr += "        <TD bgcolor=\"black\"><font color=\"black\">XX</font></TD>\n";
		  retStr += "      </TR>\n";
		  retStr += "     </table>\n";
		  retStr += "    </TD>\n";
		  retStr += "    <TD align=\"left\">Branch with incomplete selection</TD>\n";
		  retStr += "   </TR>\n";
		  retStr += "   <TR>\n";
		  retStr += "    <TD cellpadding=\"4\">\n";
		  retStr += "     <table border=\"1\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
		  retStr += "      <TR>\n";
		  retStr += "        <TD bgcolor=\"gray\"><font color=\"gray\">XX</font></TD>\n";
		  retStr += "      </TR>\n";
		  retStr += "     </table>\n";
		  retStr += "    </TD>\n";
		  retStr += "    <TD align=\"left\">Unselected branch</TD>\n";
		  retStr += "   </TR>\n";;
		  retStr += "   <TR>\n";
		  retStr += "    <TD rowspan=\"2\">Shape</TD>\n";
		  retStr += "    <TD cellpadding=\"4\">\n";
		  retStr += "     <table border=\"1\" style=\"rounded\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
		  retStr += "      <TR>\n";
		  retStr += "        <TD bgcolor=\"white\"><font color=\"white\">XX</font></TD>\n";
		  retStr += "      </TR>\n";
		  retStr += "     </table>\n";
		  retStr += "    </TD>\n";
		  retStr += "    <TD align=\"left\">Implementation</TD>\n";
		  retStr += "   </TR>\n";
		  retStr += "   <TR>\n";
		  retStr += "    <TD cellpadding=\"4\">\n";
		  retStr += "     <table border=\"1\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
		  retStr += "      <TR>\n";
		  retStr += "        <TD bgcolor=\"white\"><font color=\"white\">XX</font></TD>\n";
		  retStr += "      </TR>\n";
		  retStr += "     </table>\n";
		  retStr += "    </TD>\n";
		  retStr += "    <TD align=\"left\">Type Declaration</TD>\n";
		  retStr += "   </TR>\n";
		  retStr += "  </table>\n";
		  retStr += " >];\n";
		  retStr += "}\n\n";
		  return retStr;
	  }
}
