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

package org.lifecompanion.base.data.useaction.impl.configuration.frame;

import java.util.Map;

import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import javafx.stage.Stage;

public class SwitchFullscreenAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	public SwitchFullscreenAction() {
		super(UseActionTriggerComponentI.class);
		this.order = 0;
		this.category = DefaultUseActionSubCategories.FRAME;
		this.nameID = "action.switch.fullscreen.frame.name";
		this.staticDescriptionID = "action.switch.fullscreen.frame.description";
		this.configIconPath = "configuration/icon_enable_fullscreen.png";
		this.parameterizableAction = false;
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		if (!AppController.INSTANCE.isOnEmbeddedDevice()) {
			final Stage mainFrame = AppController.INSTANCE.getMainStage();
			LCUtils.runOnFXThread(() -> {
				mainFrame.setMaximized(!mainFrame.isMaximized());
			});
		}
	}
}
