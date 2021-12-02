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

package org.lifecompanion.config.view.pane.general.view.simplercomp.keylist;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.lifecompanion.api.component.definition.simplercomp.KeyListNodeI;
import org.lifecompanion.base.data.common.ListBindingWithMapper;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.component.simplercomp.KeyListLeaf;
import org.lifecompanion.base.data.component.simplercomp.KeyListLinkLeaf;
import org.lifecompanion.base.data.component.simplercomp.KeyListNode;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.data.action.impl.KeyListActions;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeyListContentConfigView extends VBox implements LCViewInitHelper {
    private static final double TREE_VIEW_HEIGHT = 200.0;
    private final ObjectProperty<KeyListNodeI> rootKeyListNode;


    private Button buttonAddKey, buttonAddCategory, buttonAddLinkKey, buttonDelete, buttonMoveUp, buttonMoveDown, buttonCut, buttonCopy, buttonPaste, buttonExportKeys, buttonImportKeys, buttonShowHideProperties;

    private HBox selectionPathContainer;
    private KeyListNodePropertiesEditionView keyListNodePropertiesEditionView;

    private TreeView<KeyListNodeI> keyListTreeView;

    private final ObjectProperty<KeyListNodeI> cutOrCopiedNode;
    private final HashMap<KeyListNodeI, KeyListNodeTreeItem> keyListTreeItems;
    private final BooleanProperty propertiesShowing;

    public KeyListContentConfigView() {
        this.rootKeyListNode = new SimpleObjectProperty<>();
        this.cutOrCopiedNode = new SimpleObjectProperty<>();
        this.keyListTreeItems = new HashMap<>();
        this.propertiesShowing = new SimpleBooleanProperty(true);
        initAll();
    }

    ObjectProperty<KeyListNodeI> cutOrCopiedNodeProperty() {
        return cutOrCopiedNode;
    }

    public ObjectProperty<KeyListNodeI> rootKeyListNodeProperty() {
        return rootKeyListNode;
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        selectionPathContainer = new HBox(5.0);
        selectionPathContainer.setAlignment(Pos.CENTER_LEFT);

        // TOP : list + add button
        this.buttonAddCategory = createActionButton(FontAwesome.Glyph.FOLDER, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.add.category");
        this.buttonAddKey = createActionButton(FontAwesome.Glyph.PICTURE_ALT, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.add.key");
        this.buttonAddLinkKey = createActionButton(FontAwesome.Glyph.LINK, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.add.link");
        buttonMoveUp = createActionButton(FontAwesome.Glyph.CHEVRON_UP, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.move.up");
        buttonMoveDown = createActionButton(FontAwesome.Glyph.CHEVRON_DOWN, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.move.down");
        buttonCopy = createActionButton(FontAwesome.Glyph.COPY, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.copy");
        buttonCut = createActionButton(FontAwesome.Glyph.CUT, LCGraphicStyle.MAIN_PRIMARY, "tooltip.keylist.button.cut");
        buttonPaste = createActionButton(FontAwesome.Glyph.PASTE, LCGraphicStyle.MAIN_DARK, "tooltip.keylist.button.paste");
        buttonDelete = createActionButton(FontAwesome.Glyph.TRASH, LCGraphicStyle.SECOND_DARK, "tooltip.keylist.button.delete");

        // Command buttons
        GridPane gridButtons = new GridPane();
        gridButtons.setHgap(2.0);
        gridButtons.setVgap(2.0);
        gridButtons.setAlignment(Pos.CENTER);
        int rowIndex = 0;
        gridButtons.add(buttonAddKey, 0, rowIndex);
        gridButtons.add(buttonAddCategory, 1, rowIndex++);
        gridButtons.add(buttonDelete, 0, rowIndex);
        gridButtons.add(buttonAddLinkKey, 1, rowIndex++);
        gridButtons.add(new Separator(Orientation.HORIZONTAL), 0, rowIndex++, 2, 1);
        gridButtons.add(buttonCopy, 0, rowIndex);
        gridButtons.add(buttonCut, 1, rowIndex++);
        gridButtons.add(buttonPaste, 0, rowIndex++, 2, 1);
        gridButtons.add(new Separator(Orientation.HORIZONTAL), 0, rowIndex++, 2, 1);
        gridButtons.add(buttonMoveUp, 0, rowIndex);
        gridButtons.add(buttonMoveDown, 1, rowIndex++);

        keyListTreeView = new TreeView<>();
        keyListTreeView.setCellFactory(tv -> new KeyListNodeTreeCell(this));
        keyListTreeView.setShowRoot(false);
        keyListTreeView.setMaxHeight(TREE_VIEW_HEIGHT);
        keyListTreeView.setMinHeight(TREE_VIEW_HEIGHT);
        keyListTreeView.setFixedCellSize(KeyListNodeTreeCell.CELL_HEIGHT + 5);
        HBox.setHgrow(keyListTreeView, Priority.ALWAYS);

        this.buttonExportKeys = UIUtils.createLeftTextButton(Translation.getText("general.configuration.view.key.list.button.export.keys"), LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).size(14).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonExportKeys.setMaxWidth(Double.MAX_VALUE);
        this.buttonExportKeys.setAlignment(Pos.CENTER);

        this.buttonImportKeys = UIUtils.createLeftTextButton(Translation.getText("general.configuration.view.key.list.button.import.keys"), LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(14).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonImportKeys.setMaxWidth(Double.MAX_VALUE);
        this.buttonImportKeys.setAlignment(Pos.CENTER);

        HBox boxExportImportsButtons = new HBox(10.0, buttonImportKeys, buttonExportKeys);
        boxExportImportsButtons.setAlignment(Pos.CENTER);

        HBox boxTreeAndCommands = new HBox(5.0, keyListTreeView, gridButtons);
        boxTreeAndCommands.setAlignment(Pos.CENTER);
        VBox.setVgrow(boxTreeAndCommands, Priority.ALWAYS);

        buttonShowHideProperties = UIUtils.createLeftTextButton(Translation.getText("keylist.config.hide.key.properties"), LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.EYE_SLASH).size(18).color(LCGraphicStyle.MAIN_DARK), "TODO");

        keyListNodePropertiesEditionView = new KeyListNodePropertiesEditionView();
        VBox.setMargin(keyListNodePropertiesEditionView, new Insets(5, 0, 0, 0));

        // Total
        this.setSpacing(2.0);
        this.getChildren().addAll(boxExportImportsButtons, selectionPathContainer, boxTreeAndCommands, buttonShowHideProperties, keyListNodePropertiesEditionView);
        this.setAlignment(Pos.CENTER);
    }

    private Button createActionButton(FontAwesome.Glyph glyph, Color color, String tooltip) {
        final Button button = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(glyph)
                .size(22).color(color), tooltip);
        button.setAlignment(Pos.CENTER);
        GridPane.setHalignment(button, HPos.CENTER);
        return button;
    }
    //========================================================================


    // LISTENER
    //========================================================================
    @Override
    public void initListener() {
        this.buttonAddKey.setOnAction(createAddNodeListener(KeyListLeaf::new));
        this.buttonAddLinkKey.setOnAction(createAddNodeListener(KeyListLinkLeaf::new));
        this.buttonAddCategory.setOnAction(createAddNodeListener(KeyListNode::new));
        this.buttonMoveUp.setOnAction(createMoveNodeListener(-1));
        this.buttonMoveDown.setOnAction(createMoveNodeListener(+1));
        this.buttonDelete.setOnAction(e -> ifSelectedItemNotNull(selectedNode -> removeNode(selectedNode, "keylist.action.removed.action.notification.title")));

        this.buttonCopy.setOnAction(e -> ifSelectedItemNotNull(selectedNode -> {
            KeyListNodeI duplicated = (KeyListNodeI) selectedNode.duplicate(true);
            duplicated.textProperty().set(Translation.getText("general.configuration.view.key.list.copy.label.key.text") + " " + duplicated.textProperty().get());
            cutOrCopiedNode.set(duplicated);
        }));
        this.buttonCut.setOnAction(e -> ifSelectedItemNotNull(selectedNode -> {
            cutOrCopiedNode.set(selectedNode);
            removeNode(selectedNode, "keylist.action.cut.action.notification.title");
        }));
        this.buttonPaste.setOnAction(e -> {
            final KeyListNodeI toPaste = cutOrCopiedNode.get();
            if (toPaste != null) {
                cutOrCopiedNode.set(null);
                addNodeToSelectedDestination(toPaste);
            }
        });

        this.buttonExportKeys.setOnAction(e ->
                ConfigActionController.INSTANCE.executeAction(new KeyListActions.ExportKeyListsAction(buttonExportKeys, this.rootKeyListNode.get())));
        this.buttonImportKeys.setOnAction(e ->
                ConfigActionController.INSTANCE.executeAction(new KeyListActions.ImportKeyListsAction(buttonImportKeys, this::addNodeToSelectedDestination)));

        buttonShowHideProperties.setOnAction(e -> toggleProperties());
    }

    private void toggleProperties() {
        if (this.propertiesShowing.get()) hideProperties();
        else showProperties();
    }

    private void hideProperties() {
        keyListNodePropertiesEditionView.setVisible(false);
        keyListNodePropertiesEditionView.setManaged(false);
        keyListTreeView.setMaxHeight(Double.MAX_VALUE);
        buttonShowHideProperties.setText(Translation.getText("keylist.config.show.key.properties"));
        this.propertiesShowing.set(false);
    }

    private void showProperties() {
        keyListNodePropertiesEditionView.setVisible(true);
        keyListNodePropertiesEditionView.setManaged(true);
        keyListTreeView.setMaxHeight(TREE_VIEW_HEIGHT);
        buttonShowHideProperties.setText(Translation.getText("keylist.config.hide.key.properties"));
        this.propertiesShowing.set(true);
    }

    private void removeNode(KeyListNodeI selectedNode, String notificationTitle) {
        final KeyListNodeI parentNode = selectedNode.parentProperty().get();
        int previousIndex = parentNode.getChildren().indexOf(selectedNode);
        parentNode.getChildren().remove(selectedNode);
        LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText(notificationTitle, selectedNode.getHumanReadableText()), true, "keylist.action.remove.cancel", () -> {
            if (!parentNode.getChildren().contains(selectedNode)) {
                if (previousIndex > 0 && previousIndex <= parentNode.getChildren().size()) {
                    parentNode.getChildren().add(previousIndex, selectedNode);
                } else {
                    parentNode.getChildren().add(0, selectedNode);
                }
                selectAndScrollTo(selectedNode);
            }
        }));
    }

    private EventHandler<ActionEvent> createMoveNodeListener(final int indexMove) {
        return ae -> {
            ifSelectedItemNotNull(selectedNode -> {
                final ObservableList<KeyListNodeI> children = selectedNode.parentProperty().get().getChildren();
                int index = children.indexOf(selectedNode);
                if (index + indexMove >= 0 && index + indexMove < children.size()) {
                    Collections.swap(children, index, index + indexMove);
                    selectAndScrollTo(selectedNode);
                }
            });
        };
    }

    private EventHandler<ActionEvent> createAddNodeListener(Supplier<KeyListNodeI> supplier) {
        return ev -> addNodeToSelectedDestination(supplier.get());
    }
    //========================================================================

    // BINDING
    //========================================================================
    @Override
    public void initBinding() {
        this.buttonPaste.disableProperty().bind(this.cutOrCopiedNode.isNull());
        this.buttonCopy.disableProperty().bind(keyListTreeView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonCut.disableProperty().bind(keyListTreeView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonMoveUp.disableProperty().bind(keyListTreeView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonMoveDown.disableProperty().bind(keyListTreeView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonDelete.disableProperty().bind(keyListTreeView.getSelectionModel().selectedItemProperty().isNull());
        this.rootKeyListNode.addListener((obs, ov, nv) -> {
            keyListTreeItems.clear();
            if (nv != null) {
                this.keyListTreeView.setRoot(new KeyListNodeTreeItem(nv));
                updatePathForSelection(null);
            } else {
                this.keyListTreeView.setRoot(null);
                this.keyListTreeView.getSelectionModel().clearSelection();
            }
        });
        final MonadicBinding<KeyListNodeI> currentSelectedNode = EasyBind
                .select(keyListTreeView.getSelectionModel().selectedItemProperty())
                .selectObject(TreeItem::valueProperty)
                .orElse((KeyListNodeI) null);
        currentSelectedNode.addListener((obs, ov, nv) -> updatePathForSelection(nv));
        this.keyListNodePropertiesEditionView.selectedNodeProperty().bind(currentSelectedNode);

    }
    //========================================================================

    // SELECTION PATH
    //========================================================================
    private void updatePathForSelection(KeyListNodeI selected) {
        selectionPathContainer.getChildren().clear();
        if (selected != null) {
            KeyListNodeI current = selected;
            while (current != null) {
                KeyListNodeI nodeForLink = current;
                final KeyListNodeI currentParent = nodeForLink.parentProperty().get();
                createAndAddLinkForNode(nodeForLink);
                current = currentParent;
                if (current != null) {
                    selectionPathContainer.getChildren().add(0, LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(10).color(LCGraphicStyle.MAIN_DARK));
                }
            }
        } else if (rootKeyListNode.get() != null) {
            createAndAddLinkForNode(rootKeyListNode.get());
        }
    }

    private void createAndAddLinkForNode(KeyListNodeI nodeForLink) {
        final Hyperlink hyperlink = new Hyperlink((nodeForLink.parentProperty().get() == null ? Translation.getText("general.configuration.view.keylist.root.node.text") : nodeForLink.getHumanReadableText()));
        if (nodeForLink != rootKeyListNode.get()) {
            hyperlink.setOnAction(e -> {
                selectAndScrollTo(nodeForLink);
            });
        }
        selectionPathContainer.getChildren().add(0, hyperlink);
    }
    //========================================================================


    // FUNCTIONAL
    //========================================================================
    private void addNodeToSelectedDestination(KeyListNodeI toAdd) {
        addNodeToSelectedDestination(List.of(toAdd));
    }

    private void addNodeToSelectedDestination(List<KeyListNodeI> toAdd) {
        if (keyListTreeView.getRoot() != null) {
            final TreeItem<KeyListNodeI> selectedItem = this.keyListTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() != null) {
                final KeyListNodeI selectedNode = selectedItem.getValue();
                if (selectedNode.isLeafNode()) {
                    final KeyListNodeI parentNode = selectedNode.parentProperty().get();
                    final int i = parentNode.getChildren().indexOf(selectedNode);
                    parentNode.getChildren().addAll(i + 1, toAdd);
                } else {
                    selectedNode.getChildren().addAll(0, toAdd);
                }
            } else {
                keyListTreeView.getRoot().getValue().getChildren().addAll(toAdd);
            }
            if (LangUtils.isNotEmpty(toAdd)) {
                selectAndScrollTo(toAdd.get(0));
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("notification.keylist.node.added").withMsDuration(LCGraphicStyle.SHORT_NOTIFICATION_DURATION_MS));
            }
        }
    }

    public void selectAndScrollTo(KeyListNodeI item) {
        final KeyListNodeTreeItem keyListNodeTreeItem = this.keyListTreeItems.get(item);
        if (keyListNodeTreeItem != null) {
            keyListTreeView.getSelectionModel().select(keyListNodeTreeItem);

            // Scroll to selection
            final int selectedIndex = this.keyListTreeView.getSelectionModel().getSelectedIndex();
            int indexToSelect = selectedIndex;
            // try to go back 3 index behind (better for UX)
            while (indexToSelect-- > 0 && selectedIndex - indexToSelect < 2) ;
            this.keyListTreeView.scrollTo(indexToSelect);
        }
    }

    private void ifSelectedItemNotNull(Consumer<KeyListNodeI> action) {
        final TreeItem<KeyListNodeI> selectedItem = this.keyListTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() != null) {
            action.accept(selectedItem.getValue());
        }
    }
    //========================================================================

    // TREE ITEM
    //========================================================================
    private class KeyListNodeTreeItem extends TreeItem<KeyListNodeI> {
        public KeyListNodeTreeItem(KeyListNodeI value) {
            super(value);
            keyListTreeItems.put(value, this);
            if (!value.isLeafNode()) {
                ListBindingWithMapper.mapContent(getChildren(), value.getChildren(), KeyListNodeTreeItem::new);
            }
            this.expandedProperty().addListener((obs, ov, nv) -> selectAndScrollTo(value));
        }
    }
    //========================================================================

}
