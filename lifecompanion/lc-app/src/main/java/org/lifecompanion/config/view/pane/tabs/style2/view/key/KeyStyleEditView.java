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
package org.lifecompanion.config.view.pane.tabs.style2.view.key;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.api.style2.definition.KeyCompStyleI;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.tabs.style2.view.shape.AbstractShapeStyleEditView;

public class KeyStyleEditView extends AbstractShapeStyleEditView<KeyCompStyleI> {
    private ToggleSwitch toggleEnableAutoFontSizing;
    private Node modificationIndicatorToggleAutoFont;
    private ChangeListener<Boolean> changeListenerAutoFontSize;

    public KeyStyleEditView(boolean bindOnModel) {
        super(bindOnModel);
    }

    @Override
    public void initUI() {
        super.initUI();
        this.toggleEnableAutoFontSizing = ConfigUIUtils.createToggleSwitch("key.style.enable.auto.font.size", "key.style.auto.font.size.explain");
        this.fieldGrid.add(this.toggleEnableAutoFontSizing, 0, 4, 2, 1);
        this.fieldGrid.add(modificationIndicatorToggleAutoFont = this.createModifiedIndicator(KeyCompStyleI::autoFontSizeProperty, toggleEnableAutoFontSizing), 2, 4);
    }

    @Override
    public void initListener() {
        super.initListener();
        if (bindOnModel) {
            this.changeListenerAutoFontSize = LCConfigBindingUtils.createSimpleBinding(this.toggleEnableAutoFontSizing.selectedProperty(), this.model,
                    m -> m.autoFontSizeProperty().value().getValue(), (m, v) -> this.createChangePropAction(m.autoFontSizeProperty(), v));
        }
    }


    @Override
    public void bind(final KeyCompStyleI model) {
        super.bind(model);
        if (bindOnModel) {
            this.toggleEnableAutoFontSizing.setSelected(model.autoFontSizeProperty().value().getValue());
            model.autoFontSizeProperty().value().addListener(this.changeListenerAutoFontSize);
        }
    }

    @Override
    public void unbind(final KeyCompStyleI model) {
        super.unbind(model);
        if (bindOnModel) {
            model.autoFontSizeProperty().value().removeListener(this.changeListenerAutoFontSize);
        }
    }

    public ToggleSwitch getToggleEnableAutoFontSizing() {
        return toggleEnableAutoFontSizing;
    }

    public Node getModificationIndicatorToggleAutoFont() {
        return modificationIndicatorToggleAutoFont;
    }
}