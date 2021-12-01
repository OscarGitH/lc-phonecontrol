/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lifecompanion.base.data.useaction.impl.speak.text;

import java.util.Map;

import org.jdom2.Element;

import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.control.UseVariableController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Simple action to speak a user choosen text
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SpeakTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
	private StringProperty textToSpeak;

	public SpeakTextAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.SPEAK_TEXT;
		this.nameID = "action.speak.text.name";
		this.staticDescriptionID = "action.speak.text.static.description";
		this.configIconPath = "sound/icon_speak_text.png";
		this.parameterizableAction = true;
		this.order = 0;
		this.textToSpeak = new SimpleStringProperty();
		this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.speak.text.variable.description", this.textToSpeak));
	}

	public StringProperty textToSpeakProperty() {
		return this.textToSpeak;
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		VoiceSynthesizerController.INSTANCE.speakSync(UseVariableController.INSTANCE.createText(this.textToSpeak.get(), variables));
	}
	//========================================================================

	// Class part : "XML"
	//========================================================================
	@Override
	public Element serialize(final IOContextI contextP) {
		Element node = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(SpeakTextAction.class, this, node);
		return node;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(SpeakTextAction.class, this, nodeP);
	}
	//========================================================================
}
