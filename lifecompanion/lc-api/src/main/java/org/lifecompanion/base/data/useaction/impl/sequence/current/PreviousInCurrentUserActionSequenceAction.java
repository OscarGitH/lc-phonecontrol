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

package org.lifecompanion.base.data.useaction.impl.sequence.current;

import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.control.UserActionSequenceController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PreviousInCurrentUserActionSequenceAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public PreviousInCurrentUserActionSequenceAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 10;
        this.category = DefaultUseActionSubCategories.UA_SEQUENCE_CURRENT;
        this.nameID = "previous.in.current.user.action.sequence.action.name";
        this.staticDescriptionID = "previous.in.current.user.action.sequence.action.description";
        this.configIconPath = "sequence/previous_in_ua_sequence.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        UserActionSequenceController.INSTANCE.previousItemInSequence();
    }

}
