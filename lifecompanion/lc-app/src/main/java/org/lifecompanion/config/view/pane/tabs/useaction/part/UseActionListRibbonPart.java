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
package org.lifecompanion.config.view.pane.tabs.useaction.part;

import javafx.beans.InvalidationListener;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.pane.tabs.useaction.part.multi.MultiUseActionListRibbonPart;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Part to display and manage action on a selected component for a given event type.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseActionListRibbonPart extends RibbonBasePart<UseActionTriggerComponentI> implements LCViewInitHelper {
    /**
     * List view to manage actions
     */
    private UseActionListManageView useActionListManageView;

    private final UseActionEvent eventType;
    private final boolean alwaysDisplay;

    /**
     * Create a component to manager action
     *
     * @param eventTypeP     the event type of the action to manage
     * @param alwaysDisplayP if we need to display this component when the action list is empty
     */
    public UseActionListRibbonPart(final UseActionEvent eventTypeP, final boolean alwaysDisplayP) {
        this.eventType = eventTypeP;
        this.alwaysDisplay = alwaysDisplayP;
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.useActionListManageView = new UseActionListManageView(this.eventType, this.alwaysDisplay, null);
        this.setContent(this.useActionListManageView);
        //Base for this tab (title with event type)
        this.setTitle(Translation.getText("action.list.title", Translation.getText(this.eventType.getEventLabelId())));
    }

    @Override
    public void initBinding() {
        //Bind on current grid component
        SelectionController.INSTANCE.selectedComponentBothProperty().addListener((obs, ov, nv) -> {
            if (nv instanceof UseActionTriggerComponentI) {
                this.model.set((UseActionTriggerComponentI) nv);
            } else {
                this.model.set(null);
            }
        });
    }
    //========================================================================

    @Override
    public void bind(final UseActionTriggerComponentI model) {
        this.useActionListManageView.modelProperty().set(model);
    }

    @Override
    public void unbind(final UseActionTriggerComponentI model) {
        this.useActionListManageView.modelProperty().set(null);
    }
}