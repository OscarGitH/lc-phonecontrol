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
package org.lifecompanion.ui.common.pane.specific.cell;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editaction.PluginActions;
import org.lifecompanion.controller.editaction.UserCompActions.EditUserCompAction;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.DragController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.UserCompLoadingTask;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * List cell for {@link UserCompDescriptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserCompListCell extends ListCell<UserCompDescriptionI> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserCompListCell.class);

    private ImageView userCompImage;
    private Label labelName;
    protected BorderPane boxContent;
    private Tooltip tooltip;

    public UserCompListCell() {
        this.getStyleClass().addAll("background-transparent", "soft-selection-cell");
        //Base content
        this.boxContent = new BorderPane();
        this.labelName = new Label();
        this.labelName.prefWidthProperty().bind(this.widthProperty().subtract(20));
        this.labelName.setWrapText(true);
        this.labelName.setTextAlignment(TextAlignment.CENTER);
        this.labelName.setAlignment(Pos.CENTER);
        this.labelName.getStyleClass().addAll("text-h4", "text-fill-dimgrey");

        this.tooltip = FXControlUtils.createTooltip(Translation.getText("tooltip.explain.user.comp.add"));

        //Label style and positions
        VBox boxLabel = new VBox(this.labelName);
        boxLabel.setAlignment(Pos.CENTER);
        this.labelName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(boxLabel, Priority.ALWAYS);
        //Images view
        this.userCompImage = new ImageView();
        this.userCompImage.setFitHeight(70.0);
        this.userCompImage.fitWidthProperty().bind(this.widthProperty().subtract(20));
        this.userCompImage.setPreserveRatio(true);
        this.userCompImage.setSmooth(true);
        this.setAlignment(Pos.CENTER);
        //Global content
        this.boxContent.setCenter(this.userCompImage);
        this.boxContent.setBottom(boxLabel);
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        //On drag : start drag for comp
        this.setOnDragDetected((ea) -> {
            UserCompDescriptionI item = this.getItem();
            if (item != null) {
                // Only if the component is already loaded  start dragging
                if (item.getUserComponent().isLoaded()) {
                    Dragboard dragboard = this.startDragAndDrop(TransferMode.ANY);
                    DragController.INSTANCE.currentDraggedUserCompProperty().set(item);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(item.nameProperty().get());
                    dragboard.setContent(content);
                }
            }
        });
        // On mouse press : load component when needed
        this.setOnMousePressed(me -> {
            UserCompDescriptionI item = this.getItem();
            // Try to load it if it wasn't loaded yet
            if (item != null && !item.getUserComponent().isLoaded()) {
                // First, check plugin warning
                LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
                PluginActions.warnOnPluginDependencies(this,
                        new File(IOHelper.getUserCompPath(currentProfile.getID(), item.getSavedComponentId()) + File.separator + LCConstant.USER_COMP_XML_NAME),
                        () -> {
                            UserCompLoadingTask loadTask = IOHelper.createUserCompLoadingTask(item, currentProfile);
                            loadTask.setOnSucceeded(event -> LOGGER.info("User component loaded on first mouse press"));
                            loadTask.setOnFailed(event -> {
                                Throwable error = event.getSource().getException();
                                UserCompListCell.LOGGER.warn("Couldn't load user component", error);
                                //Disable drag
                                StringBuilder sb = new StringBuilder();
                                if (error instanceof LCException) {
                                    sb.append(((LCException) error).getUserMessage());
                                } else {
                                    sb.append(Translation.getText("user.comp.loading.failed.message.start"));
                                    sb.append(error.getClass().getSimpleName()).append(" : ").append(error.getMessage());
                                }
                                DialogUtils
                                        .alertWithSourceAndType(this, AlertType.ERROR)
                                        .withHeaderText(Translation.getText("user.comp.loading.failed.header"))
                                        .withContentText(sb.toString())
                                        .show();
                            });
                            AsyncExecutorController.INSTANCE.addAndExecute(false, false, loadTask);
                        });
            }
        });
        //Double clic : edit
        this.setOnMouseClicked(me -> {
            UserCompDescriptionI item = this.getItem();
            if (item != null && me.getClickCount() > 1) {
                ConfigActionController.INSTANCE.executeAction(new EditUserCompAction(this, item));
            }
        });
    }

    @Override
    protected void updateItem(final UserCompDescriptionI itemP, final boolean emptyP) {
        super.updateItem(itemP, emptyP);
        if (itemP == null || emptyP) {
            this.userCompImage.imageProperty().unbind();
            this.userCompImage.imageProperty().set(null);
            this.labelName.textProperty().unbind();
            this.setGraphic(null);
            this.setTooltip(null);
        } else {
            itemP.requestImageLoad();
            this.userCompImage.imageProperty().bind(itemP.componentImageProperty());
            this.labelName.textProperty().bind(itemP.nameProperty());
            this.setGraphic(this.boxContent);
            this.setTooltip(tooltip);
        }
    }
}