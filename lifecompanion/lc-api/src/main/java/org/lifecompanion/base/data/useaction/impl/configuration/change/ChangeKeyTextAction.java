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
package org.lifecompanion.base.data.useaction.impl.configuration.change;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.fxmisc.easybind.EasyBind;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.component.utils.ComponentHolder;
import org.lifecompanion.base.data.control.UseVariableController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Map;

public class ChangeKeyTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private StringProperty targetKeyId;
    private ComponentHolder<GridPartKeyComponentI> targetKey;
    private StringProperty wantedKeyText;

    public ChangeKeyTextAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 1;
        this.category = DefaultUseActionSubCategories.CHANGE;
        this.nameID = "action.change.key.text.name";
        this.staticDescriptionID = "action.change.key.text.static.description";
        this.configIconPath = "configuration/icon_change_key_text.png";
        this.targetKeyId = new SimpleStringProperty();
        this.wantedKeyText = new SimpleStringProperty();
        this.targetKey = new ComponentHolder<>(this.targetKeyId, this.parentComponentProperty());
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("action.change.key.text.variable.description", EasyBind.select(this.targetKeyProperty())
                        .selectObject(GridPartKeyComponentI::nameProperty).orElse(Translation.getText("key.none.selected")), this.wantedKeyText));
    }

    public ObjectProperty<GridPartKeyComponentI> targetKeyProperty() {
        return this.targetKey.componentProperty();
    }

    public StringProperty wantedKeyTextProperty() {
        return this.wantedKeyText;
    }

    @Override
    public void idsChanged(final Map<String, String> changes) {
        super.idsChanged(changes);
        this.targetKey.idsChanged(changes);
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI wantedKeyChanged = this.targetKey.componentProperty().get();
        if (wantedKeyChanged != null) {
            StringProperty textContent = wantedKeyChanged.textContentProperty();
            if (!textContent.isBound()) {
                LCUtils.runOnFXThread(() -> textContent.set(UseVariableController.INSTANCE.createText(this.wantedKeyText.get(), variables)));
            }
        }
    }

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(ChangeKeyTextAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(ChangeKeyTextAction.class, this, nodeP);
    }
    //========================================================================
}
