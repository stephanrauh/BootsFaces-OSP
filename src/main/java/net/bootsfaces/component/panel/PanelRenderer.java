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

package net.bootsfaces.component.panel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

import net.bootsfaces.component.accordion.Accordion;
import net.bootsfaces.component.ajax.AJAXRenderer;
import net.bootsfaces.component.icon.IconRenderer;
import net.bootsfaces.render.CoreRenderer;
import net.bootsfaces.render.Responsive;
import net.bootsfaces.render.Tooltip;

/** This class generates the HTML code of &lt;b:panel /&gt;. */
@FacesRenderer(componentFamily = "net.bootsfaces.component", rendererType = "net.bootsfaces.component.panel.Panel")
public class PanelRenderer extends CoreRenderer {
	/**
	 * This methods receives and processes input made by the user. More
	 * specifically, it ckecks whether the user has interacted with the current
	 * b:panel. The default implementation simply stores the input value in the
	 * list of submitted values. If the validation checks are passed, the values
	 * in the <code>submittedValues</code> list are store in the backend bean.
	 *
	 * @param context
	 *            the FacesContext.
	 * @param component
	 *            the current b:panel.
	 */
	@Override
	public void decode(FacesContext context, UIComponent component) {
		Panel panel = (Panel) component;

		String clientId = panel.getClientId(context);
		String jQueryClientID = clientId.replace(":", "_");
		// the panel uses two ids to send requests to the server!
		new AJAXRenderer().decode(context, component, panel.getClientId(context));
		new AJAXRenderer().decode(context, component, jQueryClientID+"_content");

		String collapseStateId = clientId.replace(":", "_") + "_collapsed";

		String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(collapseStateId);

		if (submittedValue != null) {
			if (Boolean.valueOf(submittedValue) != panel.isCollapsed())
				panel.setCollapsed(Boolean.valueOf(submittedValue));
		}
	}

	/**
	 * This methods generates the HTML code of the current b:panel.
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
	 *            the current b:panel.
	 * @throws IOException
	 *             thrown if something goes wrong when writing the HTML code.
	 */
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		Panel panel = (Panel) component;
		ResponseWriter rw = context.getResponseWriter();
		String clientId = panel.getClientId();

		String jQueryClientID = clientId.replace(":", "_");

		boolean isCollapsible = panel.isCollapsible();
		String accordionParent = panel.getAccordionParent();
		String responsiveCSS = Responsive.getResponsiveStyleClass(panel, false).trim();
		boolean isResponsive = responsiveCSS.length() > 0;
		
		if (null == accordionParent && (isCollapsible || isResponsive)) {
			rw.startElement("div", panel);
			rw.writeAttribute("class", "panel-group " + responsiveCSS, null);
			rw.writeAttribute("id", clientId, "id");
		}

		String _look = panel.getLook();
		String _title = panel.getTitle();
		String _titleClass = panel.getTitleClass();
		String _styleClass = panel.getStyleClass();
		if (null == _styleClass) {
			_styleClass = "";
		} else {
			_styleClass += " ";
		}
		
		String icon = panel.getIcon();
		String faicon = panel.getIconAwesome();
		boolean fa = false; // flag to indicate whether the selected icon set is
		// Font Awesome or not.
		if (faicon != null) {
			icon = faicon;
			fa = true;
		}
		
		rw.startElement("div", panel);
		if (!((isCollapsible || isResponsive) && null == accordionParent)) {
			rw.writeAttribute("id", clientId, "id");
		}
		writeAttribute(rw, "dir", panel.getDir(), "dir");

		// render all data-* attributes
		renderPassThruAttributes(context, component, null, true);

		AJAXRenderer.generateBootsFacesAJAXAndJavaScript(context, panel, rw, false);
		Tooltip.generateTooltip(context, panel, rw);
		String _style = panel.getStyle();
		if (null != _style && _style.length() > 0) {
			rw.writeAttribute("style", _style, "style");
		}

		if (_look != null) {
			rw.writeAttribute("class", _styleClass + "panel panel-" + _look, "class");
		} else {
			rw.writeAttribute("class", _styleClass + "panel panel-default", "class");
		}

		UIComponent head = panel.getFacet("heading");
		if (head != null || _title != null) {
			rw.startElement("div", panel);
			rw.writeAttribute("class", "panel-heading", "class");
			String _titleStyle = panel.getTitleStyle();
			if (null != _titleStyle) {
				rw.writeAttribute("style", _titleStyle, "style");
			}
			if (_title != null) {
				rw.startElement("h4", panel);
				if (_titleClass != null) {
					rw.writeAttribute("class", _titleClass, "class");
				} else {
					rw.writeAttribute("class", "panel-title", "class");
				}
				if (isCollapsible) {
					writeTitleLink(panel, rw, jQueryClientID, accordionParent);
				}

				if (icon != null) {

					Object ialign = panel.getIconAlign(); // Default Left

					if (ialign != null && ialign.equals("right")) {
						_title = _title != null ? _title + " " : null;
						writeText(rw, _title, null);
						IconRenderer.encodeIcon(rw, component, icon, fa, panel.getIconSize(), panel.getIconRotate(), panel.getIconFlip(), panel.isIconSpin(), null, null, false, false, false, false,
								panel.isIconBrand(), panel.isIconInverse(), panel.isIconLight(), panel.isIconPulse(), panel.isIconRegular(), panel.isIconSolid());
					} else {
						IconRenderer.encodeIcon(rw, component, icon, fa, panel.getIconSize(), panel.getIconRotate(), panel.getIconFlip(), panel.isIconSpin(), null, null, false, false, false, false,
								panel.isIconBrand(), panel.isIconInverse(), panel.isIconLight(), panel.isIconPulse(), panel.isIconRegular(), panel.isIconSolid());
						_title = _title != null ? " " + _title : null;
						writeText(rw, _title, null);
					}

				} else {
					if (component.getChildCount() > 0) {
						_title = _title != null ? " " + _title : null;
						writeText(rw, _title, null);
					} else {
						writeText(rw, _title, null);
					}
				}
//				rw.writeText(_title, null);
				if (isCollapsible) {
					rw.endElement("a");
				}
				rw.endElement("h4");
			} else {
				if (isCollapsible) {
					writeTitleLink(panel, rw, jQueryClientID, accordionParent);
				}
				head.encodeAll(context);
				if (isCollapsible) {
					rw.endElement("a");
				}
			}
			rw.endElement("div");
		}

		rw.startElement("div", panel);
		rw.writeAttribute("id", jQueryClientID + "_content", null);
		writeAttribute(rw, "dir", panel.getDir(), "dir");

		String _contentClass = panel.getContentClass();
		if (null == _contentClass)
			_contentClass = "";
		if (isCollapsible || isResponsive) {
			_contentClass += " panel-collapse collapse"; // in
			if (!panel.isCollapsed())
				_contentClass += " in";
		}
		_contentClass = _contentClass.trim();
		if (_contentClass.length() > 0)
			rw.writeAttribute("class", _contentClass, "class");
		String _contentStyle = panel.getContentStyle();
		if (null != _contentStyle && _contentStyle.length() > 0) {
			rw.writeAttribute("style", _contentStyle, "style");
		}
		// create the body
		rw.startElement("div", panel);
		rw.writeAttribute("id", clientId + "_body", "id");
		rw.writeAttribute("class", "panel-body ui-hidden-container", "class");
		if (panel.isContentDisabled()) {
			rw.startElement("fieldset", panel);
			rw.writeAttribute("disabled", "disabled", "null");
		}

	}

	private void writeTitleLink(Panel panel, ResponseWriter rw, String jQueryClientID, String accordionParent)
			throws IOException {
		String sclass = "panel-title-link ";
		rw.startElement("a", panel);
		rw.writeAttribute("data-toggle", "collapse", "null");
		rw.writeAttribute("data-target", "#" + jQueryClientID + "_content", "null");
		String style = "display:block;";
		if (!panel.isShowCollapseLink()) {
			style += "outline:none";
		} else {
			style += "outline:none;text-decoration:underline;";

		}
		rw.writeAttribute("style", style, "style"); //let the anchor
		rw.writeAttribute("href", "javascript:;", "null");
		if (panel.isCollapsed()) {
			sclass += " collapsed";
		}
		rw.writeAttribute("class", sclass, null);
		if(null != accordionParent) {
			Accordion accordion = (Accordion)panel.getParent();
			if (!accordion.isMultiple())
				rw.writeAttribute("data-parent", "#" + accordionParent, null);
		}
	}

	/**
	 * This methods generates the HTML code of the current b:panel.
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
	 *            the current b:panel.
	 * @throws IOException
	 *             thrown if something goes wrong when writing the HTML code.
	 */
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		Panel panel = (Panel) component;
		ResponseWriter rw = context.getResponseWriter();
		if (panel.isContentDisabled()) {
			rw.endElement("fieldset");
		}
		String clientId = panel.getClientId();
		rw.endElement("div"); // panel-body
		UIComponent foot = panel.getFacet("footer");
		if (foot != null) {
			rw.startElement("div", panel); // modal-footer
			rw.writeAttribute("class", "panel-footer", "class");
			foot.encodeAll(context);

			rw.endElement("div"); // panel-footer
		}

		rw.endElement("div");
		rw.endElement("div"); // panel-body
		boolean isCollapsible = panel.isCollapsible();
		String accordionParent = panel.getAccordionParent();
		String responsiveCSS = Responsive.getResponsiveStyleClass(panel, false).trim();
		boolean isResponsive = responsiveCSS.length() > 0;		

		if ((isCollapsible||isResponsive) && null == accordionParent) {
			rw.endElement("div");
			
			if (isCollapsible) {
				String jQueryClientID = clientId.replace(":", "_");
				rw.startElement("input", panel);
				rw.writeAttribute("type", "hidden", null);
				String hiddenInputFieldID = jQueryClientID + "_collapsed";
				rw.writeAttribute("name", hiddenInputFieldID, "name");
				rw.writeAttribute("id", hiddenInputFieldID, "id");
				rw.writeAttribute("value", String.valueOf(panel.isCollapsed()), "value");
				rw.endElement("input");
				Map<String, String> eventHandlers = new HashMap<String, String>();
				eventHandlers.put("expand", "document.getElementById('" + hiddenInputFieldID + "').value='false';");
				eventHandlers.put("collapse", "document.getElementById('" + hiddenInputFieldID + "').value='true';");
				eventHandlers.put("expanded","if(typeof PrimeFaces != 'undefined'){PrimeFaces.invokeDeferredRenders('" + clientId + "_body');}");				
				new AJAXRenderer().generateBootsFacesAJAXAndJavaScriptForJQuery(context, component, rw, "#"+jQueryClientID+"_content", eventHandlers);
			}
		}
		Tooltip.activateTooltips(context, panel);
	}
	//  $('#j_idt40_j_idt43content').on('show.bs.collapse', function(){ document.getElementById('j_idt40_j_idt43_collapsed').value='false'; });
}
