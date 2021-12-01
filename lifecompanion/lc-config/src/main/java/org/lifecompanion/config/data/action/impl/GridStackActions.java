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

package org.lifecompanion.config.data.action.impl;

import javafx.scene.control.ContentDisplay;
import org.lifecompanion.api.action.definition.UndoRedoActionI;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.StackComponentI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.base.data.component.keyoption.BasicKeyOption;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Actions relative to grid stack.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridStackActions {

    // Class part : "Add/remove"
    //========================================================================
    public static class AddGridInStackAction implements UndoRedoActionI {
        private final StackComponentI stack;
        private GridComponentI component;
        private boolean select, notify;

        public AddGridInStackAction(final StackComponentI stackP, final GridComponentI componentP, final boolean select, final boolean notify) {
            this(stackP, select, notify);
            this.component = componentP;

        }

        public AddGridInStackAction(final StackComponentI stackP, final boolean select, final boolean notify) {
            this.stack = stackP;
            this.select = select;
            this.notify = notify;
        }

        @Override
        public void doAction() throws LCException {
            //Create component when needed
            if (this.component == null) {
                GridComponentI gridToCopy = this.stack.displayedComponentProperty().get() != null ? this.stack.displayedComponentProperty().get()
                        : this.stack.getComponentList().get(0);
                this.component = (GridComponentI) gridToCopy.duplicate(true);
                this.component.userNameProperty().set(null);
                this.component.forEachKeys((key) -> {
                    //Set simple option
                    key.changeKeyOption(new BasicKeyOption(), true);
                    key.textContentProperty().set("");
                    key.userNameProperty().set(null);
                    key.imageVTwoProperty().set(null);
                    key.textPositionProperty().set(ContentDisplay.CENTER);
                    //Remove all actions
                    key.getActionManager().clear();
                });
            }
            //Add
            this.component.dispatchRemovedPropertyValue(false);
            this.stack.getComponentList().add(this.component);
            //Display added
            this.stack.displayedComponentProperty().set(this.component);
            if (this.select)
                SelectionController.INSTANCE.setSelectedPart(this.component);
            if (notify)
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("notification.grid.added.to.stack"));
        }

        @Override
        public String getNameID() {
            return "action.add.grid.to.stack";
        }

        @Override
        public void undoAction() throws LCException {
            this.stack.getComponentList().remove(this.component);
        }

        @Override
        public void redoAction() throws LCException {
            this.select = false;
            this.notify = false;
            this.doAction();
        }
    }

    public static class RemoveGridInStackAction implements UndoRedoActionI {
        private int previousIndex = -1;
        private final StackComponentI stack;
        private final GridComponentI component;

        public RemoveGridInStackAction(final StackComponentI stackP, final GridComponentI componentP) {
            this.stack = stackP;
            this.component = componentP;
        }

        @Override
        public void doAction() throws LCException {
            executeRemove(true);
        }

        @Override
        public String getNameID() {
            return "action.remove.grid.to.stack";
        }

        @Override
        public void undoAction() throws LCException {
            if (previousIndex != -1) {
                this.stack.getComponentList().add(this.previousIndex, this.component);
            }
        }

        @Override
        public void redoAction() throws LCException {
            executeRemove(false);
        }

        private void executeRemove(boolean notify) throws LCException {
            if (stack.getComponentList().size() <= 1) {
                LCException.newException().withMessageId("error.remove.grid.stack.empty").buildAndThrow();
            }
            this.component.dispatchRemovedPropertyValue(false);
            this.previousIndex = this.stack.getComponentList().indexOf(this.component);
            this.stack.getComponentList().remove(this.component);
            if (notify) {
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("notification.grid.removed.from.stack")));
            }
        }
    }
    //========================================================================

    // Class part : "Shift component"
    //========================================================================
    public static class ShiftUpStackComponent implements UndoRedoActionI {
        private final StackComponentI stack;
        private final GridComponentI component;

        public ShiftUpStackComponent(final StackComponentI stackP, final GridComponentI componentP) {
            this.stack = stackP;
            this.component = componentP;
        }

        @Override
        public void doAction() throws LCException {
            this.stack.shiftUpComponent(this.component);
        }

        @Override
        public String getNameID() {
            return "action.shiftup.grid.to.stack";
        }

        @Override
        public void undoAction() throws LCException {
            this.stack.shiftDownComponent(this.component);
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }
    }

    public static class ShiftDownStackComponent implements UndoRedoActionI {
        private final StackComponentI stack;
        private final GridComponentI component;

        public ShiftDownStackComponent(final StackComponentI stackP, final GridComponentI componentP) {
            this.stack = stackP;
            this.component = componentP;
        }

        @Override
        public void doAction() throws LCException {
            this.stack.shiftDownComponent(this.component);
        }

        @Override
        public String getNameID() {
            return "action.shiftdown.grid.to.stack";
        }

        @Override
        public void undoAction() throws LCException {
            this.stack.shiftUpComponent(this.component);
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }
    }
    //========================================================================

}
