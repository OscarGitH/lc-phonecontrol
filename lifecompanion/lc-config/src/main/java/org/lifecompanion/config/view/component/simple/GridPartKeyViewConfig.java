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
package org.lifecompanion.config.view.component.simple;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.action.definition.BaseConfigActionI;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.StackComponentI;
import org.lifecompanion.api.component.definition.keyoption.KeyOptionI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.image2.ImageElementI;
import org.lifecompanion.api.ui.AddTypeEnum;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.component.keyoption.simplercomp.KeyListNodeKeyOption;
import org.lifecompanion.base.data.component.simple.GridPartKeyComponent;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.control.KeyListController;
import org.lifecompanion.base.data.image2.ImageDictionaries;
import org.lifecompanion.base.data.useaction.impl.show.moveto.MoveToGridAction;
import org.lifecompanion.base.data.useaction.impl.show.moveto.MoveToKeyAction;
import org.lifecompanion.base.data.useaction.impl.show.movetoc.MoveToGridAndGoBackAction;
import org.lifecompanion.base.data.useaction.impl.show.pages.NextPageAndLoopInStackAction;
import org.lifecompanion.base.data.useaction.impl.show.pages.NextPageInStackAction;
import org.lifecompanion.base.data.useaction.impl.show.pages.PreviousPageInStackAction;
import org.lifecompanion.base.view.component.simple.GridPartKeyViewBase;
import org.lifecompanion.config.data.action.impl.GridActions;
import org.lifecompanion.config.data.action.impl.GridActions.InverseKeyAction;
import org.lifecompanion.config.data.action.impl.GridActions.SetComponentAction;
import org.lifecompanion.config.data.action.impl.KeyActions.ChangeImageAction;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.DragController;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.component.option.ButtonComponentOption;
import org.lifecompanion.config.view.component.option.SelectableOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Configuration view for key component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridPartKeyViewConfig extends GridPartKeyViewBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(GridPartKeyViewConfig.class);
    private SelectableOption<GridPartKeyComponent> selectableOption;
    private ImageView imageViewOptionType;
    private Button buttonSimulateActions;

    @Override
    public void initUI() {
        super.initUI();
        //Select option
        this.selectableOption = new SelectableOption<>(this.model, false);
        this.getChildren().add(this.selectableOption);
        this.selectableOption.bindSize(this);
        //Key option
        this.imageViewOptionType = new ImageView();
        this.imageViewOptionType.setTranslateX(2.0);
        this.imageViewOptionType.setTranslateY(2.0);
        this.getChildren().add(this.imageViewOptionType);
        Consumer<KeyOptionI> keyOptionChanged = (option) -> {
            //Get the enum for the value
            if (option != null) {
                if (option.getIconUrl() != null) {
                    this.imageViewOptionType.setImage(IconManager.get(option.getIconUrl(), 16, 16, true, true));
                } else {
                    this.imageViewOptionType.setImage(null);
                }
            }
            //Remove the icon
            else {
                this.imageViewOptionType.setImage(null);
            }
        };
        this.model.keyOptionProperty().addListener((obs, ov, nv) -> {
            keyOptionChanged.accept(nv);
        });
        keyOptionChanged.accept(this.model.keyOptionProperty().get());

        // Execute move action
        this.buttonSimulateActions = new Button();
        ButtonComponentOption.applyButtonBaseStyle(this.buttonSimulateActions, FontAwesome.Glyph.SHARE);
        this.buttonSimulateActions.translateXProperty().bind(widthProperty().subtract(buttonSimulateActions.getPrefWidth() + 5));
        this.getChildren().add(this.buttonSimulateActions);
    }

    @Override
    public void initBinding() {
        super.initBinding();
        InvalidationListener invalidationListenerMoveAction = inv -> {
            this.buttonSimulateActions.setVisible(
                    // Key list actions
                    KeyListController.INSTANCE.isKeySimulatedAsKeyListActions(this.model)
                            // Classic move actions
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToGridAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToKeyAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextPageInStackAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousPageInStackAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextPageAndLoopInStackAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToGridAndGoBackAction.class) != null
            );
        };
        invalidationListenerMoveAction.invalidated(null);
        this.model.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).addListener(invalidationListenerMoveAction);
        this.model.getActionManager().componentActions().get(UseActionEvent.OVER).addListener(invalidationListenerMoveAction);
        model.keyOptionProperty().addListener(invalidationListenerMoveAction);
        final ChangeListener<KeyOptionI> keyOptionChangeListener = (obs, ov, nv) -> {
            if (ov instanceof KeyListNodeKeyOption) {
                ((KeyListNodeKeyOption) ov).currentSimplerKeyContentContainerProperty().removeListener(invalidationListenerMoveAction);
            }
            if (nv instanceof KeyListNodeKeyOption) {
                ((KeyListNodeKeyOption) nv).currentSimplerKeyContentContainerProperty().addListener(invalidationListenerMoveAction);
            }
        };
        keyOptionChangeListener.changed(null, null, model.keyOptionProperty().get());
        model.keyOptionProperty().addListener(keyOptionChangeListener);
    }

    @Override
    public void initListener() {
        super.initListener();
        //Selection
        this.setOnMouseClicked((ea) -> {
            //On alt down : try to execute move actions
            if (ea.isAltDown()) {
                this.executeActionSimulations();
            }
            //Select key
            else {
                SelectionController.INSTANCE.selected(this.model, ea.isShortcutDown(), ea.isShiftDown());
                this.toFront();
                //On selection, update possible selection (because enter event is not fired)
                GridPartComponentI possiblySelected = SelectionController.INSTANCE.getFirstUnselectedParent(this.model);
                possiblySelected.showPossibleSelectedProperty().set(true);
            }
        });
        //On mouse entered/exited, update show possible selected / update loaded image (if needed)
        this.setOnMouseEntered((ea) -> {
            // Possibly selected
            GridPartComponentI possiblySelected = SelectionController.INSTANCE.getFirstUnselectedParent(this.model);
            possiblySelected.showPossibleSelectedProperty().set(true);
        });
        this.setOnMouseExited((ea) -> {
            GridPartComponentI possiblySelected = SelectionController.INSTANCE.getFirstUnselectedParent(this.model);
            possiblySelected.showPossibleSelectedProperty().set(false);
        });
        //Drag over key : accept image and components
        this.setOnDragOver((ea) -> {
            if (DragController.INSTANCE.isDragShouldBeAcceptedOn(AddTypeEnum.GRID_PART, true)) {
                ea.acceptTransferModes(TransferMode.ANY);
            }
            //Accept raw images from computer : will try to add/find them in gallery
            if (isCopyingRawImagesFromComputer(ea)) {
                ea.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });
        this.setOnDragDropped((ea) -> {
            //Add comp
            if (DragController.INSTANCE.isDragComponentIsPresentOn(AddTypeEnum.GRID_PART)) {
                GridComponentI parent = this.model.gridParentProperty().get();
                if (parent != null) {
                    GridPartComponentI dragged = DragController.INSTANCE.createNewCompFor(AddTypeEnum.GRID_PART, parent, model);
                    if (dragged != null) {
                        int columnIndex = this.model.getColumnIndex(ea.getX());
                        int rowIndex = this.model.getRowIndex(ea.getY());
                        GridActions.SetComponentAction action = new SetComponentAction(parent.getGrid(), rowIndex, columnIndex, dragged);
                        ConfigActionController.INSTANCE.executeAction(action);
                    } else if (DragController.INSTANCE.isAddInStackComponent()) {
                        BaseConfigActionI action = DragController.INSTANCE.createAddInStackAction(this.model);
                        if (action != null) {
                            ConfigActionController.INSTANCE.executeAction(action);
                        }
                    } else if (DragController.INSTANCE.isAddInStackUserComponent()) {
                        BaseConfigActionI action = DragController.INSTANCE.createAddUserCompInStackAction(this.model);
                        if (action != null) {
                            ConfigActionController.INSTANCE.executeAction(action);
                        }
                    }
                    DragController.INSTANCE.resetCurrentDraggedComp();
                }
            }
            //Accept other key
            GridPartKeyComponentI dragged = DragController.INSTANCE.currentDraggedKeyProperty().get();
            if (dragged != null) {
                ConfigActionController.INSTANCE.executeAction(new InverseKeyAction(this.model, dragged));
                DragController.INSTANCE.currentDraggedKeyProperty().set(null);
            }
            //Accept raw images (if change image is enabled)
            if (isCopyingRawImagesFromComputer(ea) && !this.model.keyOptionProperty().get().disableImageProperty().get()) {
                Optional<File> firstValidImage = ea.getDragboard().getFiles().stream().filter(LCUtils::isSupportedImage).findFirst();
                if (firstValidImage.isPresent()) {
                    try {
                        if (this.model.keyOptionProperty().get() == null || !this.model.keyOptionProperty().get().disableImageProperty().get()) {
                            ImageElementI imageElement = ImageDictionaries.INSTANCE.getOrAddToUserImagesDictionary(firstValidImage.get());
                            ConfigActionController.INSTANCE.executeAction(new ChangeImageAction(this.model, imageElement));
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Couldn't add dragged image to gallery", e);
                    }
                }
            }

        });
        //On drag start : drag the key
        this.setOnDragDetected((ea) -> {
            Dragboard dragboard = this.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            //Tale a snapshot of the key as dragged image
            content.putImage(this.snapshot(null, null));
            dragboard.setContent(content);
            //Set the dragged key
            DragController.INSTANCE.currentDraggedKeyProperty().set(this.model);
        });
        this.buttonSimulateActions.setOnAction(e -> executeActionSimulations());
    }

    private boolean isCopyingRawImagesFromComputer(DragEvent ea) {
        return ea.getTransferMode() == TransferMode.MOVE
                || ea.getTransferMode() == TransferMode.COPY && ea.getDragboard() != null && ea.getDragboard().getFiles() != null
                && !ea.getDragboard().getFiles().isEmpty() && ea.getDragboard().getFiles().stream().anyMatch(LCUtils::isSupportedImage);
    }

    /**
     * On alt shortcut : try to simulation actions
     */
    private void executeActionSimulations() {
        if (KeyListController.INSTANCE.isKeySimulatedAsKeyListActions(model)) {
            KeyListController.INSTANCE.simulateKeyListKeyActions(model);
        }
        MoveToGridAction moveGrid = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToGridAction.class);
        MoveToKeyAction moveKey = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToKeyAction.class);
        NextPageInStackAction nextPageStackAction = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextPageInStackAction.class);
        NextPageAndLoopInStackAction nextPageAndLoopInStackAction = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextPageAndLoopInStackAction.class);
        PreviousPageInStackAction previousPageAction = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousPageInStackAction.class);
        MoveToGridAndGoBackAction moveToGridAndGoBackAction = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToGridAndGoBackAction.class);
        if (moveGrid != null) {
            GridComponentI targetGrid = moveGrid.targetGridProperty().get();
            if (targetGrid != null) {
                SelectionController.INSTANCE.setSelectedPart(targetGrid);
            }
        } else if (moveKey != null) {
            GridPartKeyComponentI targetKey = moveKey.targetKeyProperty().get();
            if (targetKey != null) {
                SelectionController.INSTANCE.setSelectedPart(targetKey);
            }
        } else if (nextPageStackAction != null) {
            StackComponentI targetStack = nextPageStackAction.changedPageParentStackProperty().get();
            if (targetStack != null) {
                GridComponentI nextComponent = targetStack.getNextComponent();
                if (nextComponent != null) {
                    SelectionController.INSTANCE.setSelectedPart(nextComponent);
                }
            }
        } else if (previousPageAction != null) {
            StackComponentI targetStack = previousPageAction.changedPageParentStackProperty().get();
            if (targetStack != null) {
                GridComponentI previousComponent = targetStack.getPreviousComponent();
                if (previousComponent != null) {
                    SelectionController.INSTANCE.setSelectedPart(previousComponent);
                }
            }
        } else if (nextPageAndLoopInStackAction != null) {
            StackComponentI targetStack = nextPageAndLoopInStackAction.changedPageParentStackProperty().get();
            if (targetStack != null) {
                GridComponentI nextComponent = targetStack.getNextComponent();
                if (nextComponent != null) {
                    SelectionController.INSTANCE.setSelectedPart(nextComponent);
                } else if (!targetStack.getComponentList().isEmpty()) {
                    SelectionController.INSTANCE.setSelectedPart(targetStack.getComponentList().get(0));
                }
            }
        } else if (moveToGridAndGoBackAction != null) {
            final GridComponentI targetGrid = moveToGridAndGoBackAction.targetGridProperty().get();
            if (targetGrid != null) {
                SelectionController.INSTANCE.setSelectedPart(targetGrid);
            }
        }
    }
}
