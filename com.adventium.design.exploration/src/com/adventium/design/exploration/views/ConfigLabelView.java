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

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


public class ConfigLabelView extends LabelProvider
		implements IColorProvider, IFontProvider, ILabelProvider {

	FontRegistry registry = new FontRegistry();
	
	private final Font BOLD = registry.getBold(Display.getCurrent().getSystemFont().getFontData()[0].getName());
	private final Font ITALIC = registry.getItalic(Display.getCurrent().getSystemFont().getFontData()[0].getName());

	private final Color GRAY = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	private final Color RED = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	private final Color BLUE = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	private final Color MAGENTA = Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA);
	
	// Implementations have an image, types do not.
	private final Image ACTIVE_DECISION_IMAGE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_WARNING);
	
	// Checked elements are bold, nothing else is.
	// Non-active elements are colored gray.
	// Active "gray" elements are blue.
	// Active Decision objects are red.
	private final Font IS_CHECKED_FONT = BOLD;
	private final Font IS_STUB_FONT = ITALIC;
	private final Color IS_CHOSEN_COLOR = BLUE;
	private final Color ACTIVE_DECISION_COLOR = RED;
	private final Color NON_ACTIVE_COLOR = GRAY;
	private final Color IS_STUB_COLOR = MAGENTA;
	
	@Override
	public Image getImage(Object element) {
		if ((element instanceof ConfigNode) && (!(((ConfigNode)element).onActiveBranch()))) {
			return null;
		}
		//if ((element instanceof ConfigNode) && (((ConfigNode)element).isGrayed())) {
		//	return null;
		//}
		if ((element instanceof ConfigNode) && 
			((((ConfigNode)element).parentChildrenNotSelected()) ||
			 (((ConfigNode)element).hasChildWithProblem()))) {
			return ACTIVE_DECISION_IMAGE;
		}
		return null;
	}
	
	public Color getBackground(Object element) {
		return null;
	}
	

	public Color getForeground(Object element) {
		
		if ((element instanceof ConfigNode) && (!(((ConfigNode)element).onActiveBranch()))) {
			return NON_ACTIVE_COLOR;
		}
		if ((element instanceof ConfigNode) && (((ConfigNode)element).parentChildrenNotSelected())) {
			if (((ConfigNode)element).getDisplayName().contains(".stub")) {
				return IS_STUB_COLOR;
			} else {
				return ACTIVE_DECISION_COLOR;
			}
		}
		if ((element instanceof ConfigNode) && ((ConfigNode)element).isChecked() && ((ConfigNode)element).allParentsSelected()) {
			return IS_CHOSEN_COLOR;
		}
		return null;
	}

	public Font getFont(Object element) {
		
		if (((ConfigNode)element).getDisplayName().contains(".stub")) {
			return IS_STUB_FONT;
		}
		if ((element instanceof ConfigNode) && ((ConfigNode)element).isChecked()) {
			return IS_CHECKED_FONT;
		}
		return null;
	}

}
