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

package org.lifecompanion.config.view.component.option;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.component.definition.RootGraphicComponentI;
import org.lifecompanion.api.component.definition.SelectableComponentI;
import org.lifecompanion.api.ui.config.ConfigOptionComponentI;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Option to select component, add to add some optional buttons to it.<br>
 * The optional buttons are displayed only if component is selected.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ButtonComponentOption extends BaseOptionRegion<SelectableComponentI> implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ButtonComponentOption.class);
    /**
     * Grid that contains the buttons to display
     */
    private GridPane gridButtons;

    /**
     * Button that allow selection of this component model
     */
    private ToggleButton buttonSelect;

    /**
     * The current row/column in grid add
     */
    private int currentRow, currentColumn;

    /**
     * List of current option in this component
     */
    private List<ConfigOptionComponentI> options;

    public ButtonComponentOption(final SelectableComponentI modelP) {
        super(modelP);
        this.options = new ArrayList<>();
        this.initAll();
    }

    @Override
    public void initUI() {
        this.gridButtons = new GridPane();
        this.gridButtons.setPickOnBounds(false);
        //Select button
        this.buttonSelect = new ToggleButton();
        ButtonComponentOption.applyComponentOptionButtonStyle(this.buttonSelect, FontAwesome.Glyph.HAND_ALT_UP);
        this.gridButtons.add(this.buttonSelect, this.currentColumn++, this.currentRow++);
        //Display the grid
        this.getChildren().add(this.gridButtons);
    }

    @Override
    public void initListener() {
        //Select or unselect
        this.buttonSelect.setOnAction((ea) -> {
            //Root component
            if (this.model instanceof RootGraphicComponentI) {
                if (this.model.selectedProperty().get()) {
                    SelectionController.INSTANCE.setSelectedRoot(null);
                } else {
                    SelectionController.INSTANCE.setSelectedRoot((RootGraphicComponentI) this.model);
                }
            }
            //Grid component
            else if (this.model instanceof GridPartComponentI) {
                if (this.model.selectedProperty().get()) {
                    SelectionController.INSTANCE.setSelectedPart(null);
                } else {
                    SelectionController.INSTANCE.setSelectedPart((GridPartComponentI) this.model);
                }
            } else {
                ButtonComponentOption.LOGGER
                        .warn("The current selectable model type in select button option doesn't allow selection on SelectionController");
            }
        });
        if (this.model instanceof RootGraphicComponentI) {
            MoveButtonHelper.install(() -> (RootGraphicComponentI) model, buttonSelect);
        }
    }

    // Class part : "Option"
    //========================================================================
    private void addOption(final ConfigOptionComponentI option, final List<Node> component) {
        for (Node comp : component) {
            this.addOptionUnique(option, comp);
        }
    }

    private void addOptionUnique(final ConfigOptionComponentI option, final Node component) {
        if (option.getOrientation() == Orientation.HORIZONTAL) {
            this.gridButtons.add(component, this.currentColumn++, 0);
        } else if (option.getOrientation() == Orientation.VERTICAL) {
            this.gridButtons.add(component, 0, this.currentRow++);
        }
        //Default : hide
        if (option.hideOnUnselect()) {
            component.setVisible(false);
        }
    }

    public void addOption(final ConfigOptionComponentI option) {
        this.options.add(option);
        this.addOption(option, option.getOptions());
    }

    //========================================================================

    // Class part : "Selection hide/visible"
    //========================================================================
    @Override
    public void initBinding() {
        this.model.selectedProperty().addListener((ChangeListener<Boolean>) (observableP, oldValueP, newValueP) -> {
            //Change button
            this.buttonSelect.setSelected(newValueP);
            if (oldValueP && !newValueP) {
                this.unselected();
            }
            if (!oldValueP && newValueP) {
                this.selected();
            }
            //To front
            this.getParent().toFront();
        });
    }

    private void selected() {
        //Show option
        for (ConfigOptionComponentI option : this.options) {
            if (option.hideOnUnselect()) {
                List<Node> nodes = option.getOptions();
                for (Node c : nodes) {
                    c.setVisible(true);
                }
            }
        }
    }

    private void unselected() {
        //Hide option
        for (ConfigOptionComponentI option : this.options) {
            if (option.hideOnUnselect()) {
                List<Node> nodes = option.getOptions();
                for (Node c : nodes) {
                    c.setVisible(false);
                }
            }
        }
    }

    //========================================================================

    // Class part : "Style"
    //========================================================================

    /**
     * To set the base component option style on a button
     *
     * @param button the button we want to create the style
     * @param glyph  the icon to show in the button
     */
    public static void applyComponentOptionButtonStyle(final ButtonBase button, final Enum<?> glyph) {
        if (glyph != null) {
            button.setGraphic(LCGlyphFont.FONT_AWESOME.create(glyph).sizeFactor(1).color(Color.WHITE));
        }
        Circle buttonShape = new Circle(1.0);//Radius is ignored when != 0
        button.setShape(buttonShape);
        button.setCenterShape(true);
        button.getStyleClass().add("option-button-base");
        //FIX : when adding a group to root, if the pref size is not set, the button is not visible ?
        button.setPrefSize(24.0, 24.0);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
    }

    /**
     * To set the base on option button
     *
     * @param button
     * @param glyph
     */
    public static void applyButtonBaseStyle(final ButtonBase button, final Enum<?> glyph) {
        button.setGraphic(LCGlyphFont.FONT_AWESOME.create(glyph).sizeFactor(1).color(Color.WHITE));
        button.setShape(new Circle(16.0));
        //FIX : set a space for text allow the icon to be correctly displayed
        button.setText(" ");
        button.getStyleClass().addAll("image-base-button", "option-op-button");
        //FIX : when adding a group to root, if the pref size is not set, the button is not visible ?
        button.setPrefSize(24.0, 24.0);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
    }
    //========================================================================

}
