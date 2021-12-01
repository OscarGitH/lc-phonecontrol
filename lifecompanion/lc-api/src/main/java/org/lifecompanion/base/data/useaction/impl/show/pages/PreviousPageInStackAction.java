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
package org.lifecompanion.base.data.useaction.impl.show.pages;

import java.util.Map;

import org.fxmisc.easybind.EasyBind;
import org.jdom2.Element;

import org.lifecompanion.api.component.definition.StackComponentI;
import org.lifecompanion.base.data.component.utils.ComponentHolder;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.base.data.control.SelectionModeController;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Action to go to the previous page in a selected stack
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PreviousPageInStackAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private StringProperty changedPageParentStackId;
    private ComponentHolder<StackComponentI> changedPageParentStack;

    public PreviousPageInStackAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.CHANGE_PAGE;
        this.order = 0;
        this.nameID = "previous.page.in.stack.name";
        this.staticDescriptionID = "previous.page.in.stack.static.description";
        this.configIconPath = "show/icon_previous_page_stack.png";
        this.changedPageParentStackId = new SimpleStringProperty();
        this.changedPageParentStack = new ComponentHolder<>(this.changedPageParentStackId, this.parentComponentProperty());
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("previous.page.in.stack.variable.description", EasyBind.select(this.changedPageParentStackProperty())
                        .selectObject(StackComponentI::nameProperty).orElse(Translation.getText("stack.none.selected"))));

    }

    public ObjectProperty<StackComponentI> changedPageParentStackProperty() {
        return this.changedPageParentStack.componentProperty();
    }

    @Override
    public void idsChanged(final Map<String, String> changes) {
        super.idsChanged(changes);
        this.changedPageParentStack.idsChanged(changes);
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (this.changedPageParentStackProperty().get() != null && this.changedPageParentStackProperty().get().previousPossibleProperty().get()) {
            SelectionModeController.INSTANCE.goToGridPart(this.changedPageParentStackProperty().get().getPreviousComponent());
        }
    }

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(PreviousPageInStackAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(PreviousPageInStackAction.class, this, nodeP);
    }
    //========================================================================
}
