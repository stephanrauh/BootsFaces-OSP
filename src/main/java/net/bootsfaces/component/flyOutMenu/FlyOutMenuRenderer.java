/**
 *  Copyright 2014-2019 Riccardo Massera (TheCoder4.Eu) and Stephan Rauh (http://www.beyondjava.net).
 *  
 *  This file is part of BootsFaces.
 *  
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
 */

package net.bootsfaces.component.flyOutMenu;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

import net.bootsfaces.render.CoreRenderer;
import net.bootsfaces.render.Tooltip;

/** This class generates the HTML code of &lt;b:flyOutMenu /&gt;. */
@FacesRenderer(componentFamily = "net.bootsfaces.component", rendererType = "net.bootsfaces.component.flyOutMenu.FlyOutMenu")
public class FlyOutMenuRenderer extends CoreRenderer {

	/**
	 * This methods generates the HTML code of the current b:flyOutMenu.
	 * <code>encodeBegin</code> generates the start of the component. After the,
	 * the JSF framework calls <code>encodeChildren()</code> to generate the
	 * HTML code between the beginning and the end of the component. For
	 * instance, in the case of a panel component the content of the panel is
	 * generated by <code>encodeChildren()</code>. After that,
	 * <code>encodeEnd()</code> is called to generate the rest of the HTML code.
	 * 
	 * @param context
	 *            the FacesContext.
	 * @param component
	 *            the current b:flyOutMenu.
	 * @throws IOException
	 *             thrown if something goes wrong when writing the HTML code.
	 */
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		FlyOutMenu flyOutMenu = (FlyOutMenu) component;
		ResponseWriter rw = context.getResponseWriter();
		String clientId = flyOutMenu.getClientId();

		// put custom code here
		// Simple demo widget that simply renders every attribute value
		rw.startElement("ul", flyOutMenu);
		rw.writeAttribute("id", clientId, "id");

		Tooltip.generateTooltip(context, flyOutMenu, rw);

		String styleClass = flyOutMenu.getStyleClass();
		if (null == styleClass)
			styleClass = "dropdown-menu";
		else
			styleClass = "dropdown-menu " + flyOutMenu.getStyleClass();
		writeAttribute(rw, "class", styleClass);
		String width = flyOutMenu.getWidth();
		addUnitToWidthIfNecessary(width);
		String style = flyOutMenu.getStyle();
		if (null == style)
			style = "display: block; position: static; margin-bottom: 5px; *width: "+width;
		else
			style = "display: block; position: static; margin-bottom: 5px; *width: " + width +";" + flyOutMenu.getStyle();
		writeAttribute(rw, "style", style);
	}

	private void addUnitToWidthIfNecessary(String width) {
		try {
			Integer.parseInt(width);
			width += "px";
		} catch (NumberFormatException noError) {
		}
	}

	/**
	 * This methods generates the HTML code of the current b:flyOutMenu.
	 * <code>encodeBegin</code> generates the start of the component. After the,
	 * the JSF framework calls <code>encodeChildren()</code> to generate the
	 * HTML code between the beginning and the end of the component. For
	 * instance, in the case of a panel component the content of the panel is
	 * generated by <code>encodeChildren()</code>. After that,
	 * <code>encodeEnd()</code> is called to generate the rest of the HTML code.
	 * 
	 * @param context
	 *            the FacesContext.
	 * @param component
	 *            the current b:flyOutMenu.
	 * @throws IOException
	 *             thrown if something goes wrong when writing the HTML code.
	 */
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		FlyOutMenu flyOutMenu = (FlyOutMenu) component;
		ResponseWriter rw = context.getResponseWriter();
		rw.endElement("ul");
		Tooltip.activateTooltips(context, flyOutMenu);

	}

}
