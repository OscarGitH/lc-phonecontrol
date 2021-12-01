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

package org.lifecompanion.base.data.useaction.impl.text.write;

import java.util.Map;

import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.base.data.component.simple.WriterEntry;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.control.events.WritingEventSource;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;

/**
 * Action to write the label of the parent key.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteLabelAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

	public WriteLabelAction() {
		super(GridPartKeyComponentI.class);
		this.category = DefaultUseActionSubCategories.WRITE_TEXT;
		this.nameID = "action.write.label.name";
		this.order = 2;
		this.staticDescriptionID = "action.write.label.static.description";
		this.configIconPath = "text/icon_write_label.png";
		this.parameterizableAction = false;
		this.parentComponentProperty().addListener((obs, ov, nv) -> {
			this.variableDescriptionProperty().unbind();
			if (nv != null) {
				this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.write.label.variable.description", nv.textContentProperty()));
			}
		});
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		GridPartKeyComponentI currentKey = this.parentComponentProperty().get();
		if (currentKey != null) {
			String toWrite = currentKey.textContentProperty().get();
			//If entry doesn't have any image and contains only one char, just append
			if (toWrite != null && toWrite.length() == 1 && currentKey.imageVTwoProperty().get() == null) {
				WritingStateController.INSTANCE.insertText(WritingEventSource.USER_ACTIONS, toWrite);
			} else {
				//Create entry and add image
				WriterEntry entry = new WriterEntry(toWrite, true);
				if (currentKey.imageVTwoProperty().get() != null) {
					entry.imageProperty().set(currentKey.imageVTwoProperty().get());
				}
				WritingStateController.INSTANCE.insert(WritingEventSource.USER_ACTIONS, entry);
			}
		}
	}
	//========================================================================
}
