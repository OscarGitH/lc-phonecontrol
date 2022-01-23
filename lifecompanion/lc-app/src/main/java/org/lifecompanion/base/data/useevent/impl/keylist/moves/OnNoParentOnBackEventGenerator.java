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

package org.lifecompanion.base.data.useevent.impl.keylist.moves;

import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.useevent.category.DefaultUseEventSubCategories;
import org.lifecompanion.base.data.control.KeyListController;
import org.lifecompanion.base.data.useevent.baseimpl.BaseUseEventGeneratorImpl;

public class OnNoParentOnBackEventGenerator extends BaseUseEventGeneratorImpl {

    public OnNoParentOnBackEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 0;
        this.category = DefaultUseEventSubCategories.KEYLIST_MOVES;
        this.nameID = "use.event.on.no.parent.on.back.event.name";
        this.staticDescriptionID = "use.event.on.no.parent.on.back.event.description";
        this.configIconPath = "keylist/icon_go_parent_no_parent_keylist.png";
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    // Class part : "Mode start/stop"
    //========================================================================
    private Runnable listener;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.listener = () -> {
            this.useEventListener.fireEvent(this, null, null);
        };
		KeyListController.INSTANCE.getGoParentKeyNodeNoParentListener().add(listener);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
		KeyListController.INSTANCE.getGoParentKeyNodeNoParentListener().remove(listener);
    }
    //========================================================================
}