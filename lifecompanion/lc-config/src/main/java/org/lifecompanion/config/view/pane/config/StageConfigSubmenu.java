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
package org.lifecompanion.config.view.pane.config;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Stage configuration
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StageConfigSubmenu extends ScrollPane implements UserConfigSubmenuI, LCViewInitHelper {
    /**
     * Spinner to set the frame width/height
     */
    private Spinner<Integer> spinnerFrameWidth, spinnerFrameHeight;

    /**
     * Spinner unsaved modification
     */
    private Spinner<Integer> spinnerUnsavedModification;

    /**
     * Spinner to set config selection size
     */
    private Spinner<Double> spinnerStrokeSize, spinnerDashSize;

    /**
     * To enable/disable fullscreen
     */
    private ToggleSwitch toggleEnableFullScreen;

    /**
     * To enable/disable tips on startup
     */
    private ToggleSwitch toggleEnableTipsStartup;
    private ToggleSwitch toggleEnableLaunchLCSystemStartup;
    private ToggleSwitch toggleEnableRecordAndSendSessionStats;
    private ToggleSwitch toggleEnableAutoShowVirtualKeyboard;

    public StageConfigSubmenu() {
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        int row = 0;
        Label labelConfigGeneral = createTitleLabel("user.config.part.ui.general");
        toggleEnableLaunchLCSystemStartup = ConfigUIUtils.createToggleSwitch("user.config.launch.lc.startup", null);
        toggleEnableRecordAndSendSessionStats = ConfigUIUtils.createToggleSwitch("user.config.enable.session.stats", null);
        toggleEnableAutoShowVirtualKeyboard = ConfigUIUtils.createToggleSwitch("user.config.auto.show.virtual.keyboard", null);

        //Selection parameter
        this.spinnerStrokeSize = UIUtils.createDoubleSpinner(1.0, 20.0, 3.0, 1.0, 110.0);
        Label labelStrokeSize = new Label(Translation.getText("user.config.selection.stroke.size"));
        GridPane.setHgrow(labelStrokeSize, Priority.ALWAYS);
        Label labelDashSize = new Label(Translation.getText("user.config.selection.dash.size"));
        this.spinnerDashSize = UIUtils.createDoubleSpinner(1.0, 20.0, 3.0, 1.0, 110.0);
        GridPane gridPaneStyleParam = createConfigPane();
        gridPaneStyleParam.add(labelStrokeSize, 0, row);
        gridPaneStyleParam.add(this.spinnerStrokeSize, 1, row++);
        gridPaneStyleParam.add(labelDashSize, 0, row);
        gridPaneStyleParam.add(this.spinnerDashSize, 1, row++);
        Label labelConfigStylePart = createTitleLabel("user.config.part.ui.config");

        //Frame parameter
        this.spinnerFrameWidth = UIUtils.createIntSpinner(10, Integer.MAX_VALUE, 50, 100, 110);
        this.spinnerFrameHeight = UIUtils.createIntSpinner(10, Integer.MAX_VALUE, 50, 100, 110);
        Label labelWidth = new Label(Translation.getText("user.config.stage.width"));
        GridPane.setHgrow(labelWidth, Priority.ALWAYS);
        Label labelHeight = new Label(Translation.getText("user.config.stage.height"));
        this.toggleEnableFullScreen = ConfigUIUtils.createToggleSwitch("user.config.stage.fullscreen", null);
        GridPane.setMargin(this.toggleEnableFullScreen, new Insets(5.0, 0.0, 5.0, 0.0));
        GridPane gridPaneStageParam = createConfigPane();
        gridPaneStageParam.add(this.toggleEnableFullScreen, 0, row++, 2, 1);
        gridPaneStageParam.add(labelWidth, 0, row);
        gridPaneStageParam.add(this.spinnerFrameWidth, 1, row++);
        gridPaneStageParam.add(labelHeight, 0, row);
        gridPaneStageParam.add(this.spinnerFrameHeight, 1, row++);
        Label labelStagePart = createTitleLabel("user.config.stage.title");

        //Unsaved modification
        Label labelConfigTitle = createTitleLabel("user.config.configuration.title");
        this.spinnerUnsavedModification = UIUtils.createIntSpinner(1, 5000, 5, 10, 110.0);
        Label labelUnsavedThreshold = new Label(Translation.getText("user.config.unsaved.modification.threshold"));
        GridPane.setHgrow(labelUnsavedThreshold, Priority.ALWAYS);
        GridPane gridPaneConfiguration = createConfigPane();
        gridPaneConfiguration.add(labelUnsavedThreshold, 0, row);
        gridPaneConfiguration.add(this.spinnerUnsavedModification, 1, row++);

        //Tips
        Label labelConfigTips = createTitleLabel("user.config.tips.title");
        toggleEnableTipsStartup = ConfigUIUtils.createToggleSwitch("user.config.tips.show.startup", null);
        GridPane.setHgrow(toggleEnableTipsStartup, Priority.ALWAYS);
        GridPane.setMargin(this.toggleEnableTipsStartup, new Insets(5.0, 0.0, 5.0, 0.0));

        //Add
        VBox totalBox = new VBox(10.0, labelConfigGeneral, toggleEnableRecordAndSendSessionStats, labelConfigStylePart, gridPaneStyleParam, labelConfigTitle, gridPaneConfiguration,/* labelConfigTips,
				toggleEnableTipsStartup,*/ labelStagePart, gridPaneStageParam);
        if (SystemType.current() == SystemType.WINDOWS) {
            totalBox.getChildren().add(1, toggleEnableAutoShowVirtualKeyboard);
            totalBox.getChildren().add(1, toggleEnableLaunchLCSystemStartup);
        }
        this.setFitToWidth(true);
        this.setContent(totalBox);
    }
    //========================================================================

    private Label createTitleLabel(String id) {
        Label labelConfigStylePart = new Label(Translation.getText(id));
        labelConfigStylePart.getStyleClass().add("menu-part-title");
        labelConfigStylePart.setMaxWidth(Double.MAX_VALUE);
        return labelConfigStylePart;
    }

    private GridPane createConfigPane() {
        GridPane gridPaneStageParam = new GridPane();
        gridPaneStageParam.setVgap(3.0);
        gridPaneStageParam.setHgap(5.0);
        return gridPaneStageParam;
    }

    @Override
    public void updateFields() {
        this.spinnerFrameWidth.getValueFactory().setValue(UserBaseConfiguration.INSTANCE.mainFrameWidthProperty().get());
        this.spinnerFrameHeight.getValueFactory().setValue(UserBaseConfiguration.INSTANCE.mainFrameHeightProperty().get());
        this.spinnerStrokeSize.getValueFactory().setValue(UserBaseConfiguration.INSTANCE.selectionStrokeSizeProperty().get());
        this.spinnerDashSize.getValueFactory().setValue(UserBaseConfiguration.INSTANCE.selectionDashSizeProperty().get());
        this.toggleEnableFullScreen.setSelected(UserBaseConfiguration.INSTANCE.launchMaximizedProperty().get());
        this.toggleEnableTipsStartup.setSelected(UserBaseConfiguration.INSTANCE.showTipsOnStartupProperty().get());
        this.spinnerUnsavedModification.getValueFactory()
                .setValue(UserBaseConfiguration.INSTANCE.unsavedChangeInConfigurationThresholdProperty().get());
        this.toggleEnableLaunchLCSystemStartup.setSelected(UserBaseConfiguration.INSTANCE.launchLCSystemStartupProperty().get());
        this.toggleEnableRecordAndSendSessionStats.setSelected(UserBaseConfiguration.INSTANCE.recordAndSendSessionStatsProperty().get());
        this.toggleEnableAutoShowVirtualKeyboard.setSelected(UserBaseConfiguration.INSTANCE.autoVirtualKeyboardShowProperty().get());
    }

    @Override
    public void updateModel() {
        UserBaseConfiguration.INSTANCE.mainFrameWidthProperty().set(this.spinnerFrameWidth.getValue());
        UserBaseConfiguration.INSTANCE.mainFrameHeightProperty().set(this.spinnerFrameHeight.getValue());
        UserBaseConfiguration.INSTANCE.launchMaximizedProperty().set(this.toggleEnableFullScreen.isSelected());
        UserBaseConfiguration.INSTANCE.selectionStrokeSizeProperty().set(this.spinnerStrokeSize.getValue());
        UserBaseConfiguration.INSTANCE.selectionDashSizeProperty().set(this.spinnerDashSize.getValue());
        UserBaseConfiguration.INSTANCE.showTipsOnStartupProperty().set(this.toggleEnableTipsStartup.isSelected());
        UserBaseConfiguration.INSTANCE.unsavedChangeInConfigurationThresholdProperty().set(this.spinnerUnsavedModification.getValue());
        UserBaseConfiguration.INSTANCE.launchLCSystemStartupProperty().set(toggleEnableLaunchLCSystemStartup.isSelected());
        UserBaseConfiguration.INSTANCE.recordAndSendSessionStatsProperty().set(toggleEnableRecordAndSendSessionStats.isSelected());
        UserBaseConfiguration.INSTANCE.autoVirtualKeyboardShowProperty().set(this.toggleEnableAutoShowVirtualKeyboard.isSelected());
    }

    @Override
    public Region getView() {
        return this;
    }

    @Override
    public String getTabTitleId() {
        return "user.config.tab.stage";
    }

}
