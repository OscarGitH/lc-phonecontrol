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

package org.lifecompanion.config.view.useaction.impl.text.prediction;

import org.controlsfx.control.ToggleSwitch;

import org.lifecompanion.api.component.definition.useaction.UseActionConfigurationViewI;
import org.lifecompanion.api.component.definition.useevent.UseVariableDefinitionI;
import org.lifecompanion.base.data.useaction.impl.text.prediction.WriteWordPredictionAction;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Configuration view for {@link WriteWordPredictionAction}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteWordPredictionConfigView extends VBox implements UseActionConfigurationViewI<WriteWordPredictionAction> {

	private ToggleSwitch toggleEnableSpaceAfter;

	@Override
	public void initUI() {
		this.toggleEnableSpaceAfter = ConfigUIUtils.createToggleSwitch("word.prediction.add.space.after", null);
		this.getChildren().addAll(this.toggleEnableSpaceAfter);
	}

	@Override
	public void editStarts(final WriteWordPredictionAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.toggleEnableSpaceAfter.setSelected(action.addSpaceProperty().get());
	}

	@Override
	public void editEnds(final WriteWordPredictionAction action) {
		action.addSpaceProperty().set(this.toggleEnableSpaceAfter.isSelected());
	}

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public Class<WriteWordPredictionAction> getConfiguredActionType() {
		return WriteWordPredictionAction.class;
	}

}
