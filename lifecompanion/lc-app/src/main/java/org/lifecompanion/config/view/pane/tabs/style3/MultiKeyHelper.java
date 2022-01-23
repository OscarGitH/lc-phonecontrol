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

package org.lifecompanion.config.view.pane.tabs.style3;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.api.action.definition.BaseConfigActionI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.style2.definition.KeyStyleUserI;
import org.lifecompanion.api.style2.property.definition.StylePropertyI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.style2.PropertyChangeListener;
import org.lifecompanion.config.data.action.impl.StyleActions;
import org.lifecompanion.config.data.common.keycollection.GridPartKeyPropertyChangeListener;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.SelectionController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiKeyHelper {

    public static <T, V> void initStyleConfigActionListener(V field,
                                                            Node modifiedIndicator,
                                                            BiConsumer<V, EventHandler<ActionEvent>> actionEventSetter,
                                                            Function<V, T> fieldValueGetter,
                                                            BiConsumer<V, T> fieldValueSetter,
                                                            Function<KeyStyleUserI, StylePropertyI<T>> stylePropertyGetter,
                                                            PropertyChangeListener<?, T> propertyChangeGetter) {
        final EventHandler<ActionEvent> eventHandler = event -> {
            List<StylePropertyI<T>> styleList = SelectionController.INSTANCE.getSelectedKeys().stream().map(stylePropertyGetter).collect(Collectors.toList());
            ConfigActionController.INSTANCE.executeAction(new StyleActions.ChangeMultipleStylePropAction<>(styleList, fieldValueGetter.apply(field)));
        };
        actionEventSetter.accept(field, eventHandler);
        propertyChangeGetter.cachedPropValueProperty().addListener((obs, ov, nv) -> {
            actionEventSetter.accept(field, null);
            fieldValueSetter.accept(field, nv);
            actionEventSetter.accept(field, eventHandler);
        });
        modifiedIndicator.setOnMouseClicked(e -> {
            List<StylePropertyI<T>> styleList = SelectionController.INSTANCE.getSelectedKeys().stream().map(stylePropertyGetter).collect(Collectors.toList());
            ConfigActionController.INSTANCE.executeAction(new StyleActions.ChangeMultipleStylePropAction<>(styleList, null));
        });
        modifiedIndicator.visibleProperty().bind(propertyChangeGetter.cachedSelectedNotNullProperty());
    }

    public static <T, V> void initMultiKeyConfigActionListener(V field,
                                                               BiConsumer<V, EventHandler<ActionEvent>> actionEventSetter,
                                                               Function<V, T> fieldValueGetter,
                                                               BiConsumer<V, T> fieldValueSetter,
                                                               BiFunction<List<GridPartKeyComponentI>, T, BaseConfigActionI> actionConstructor,
                                                               GridPartKeyPropertyChangeListener<?, ?, T> propertyChangeGetter) {
        final EventHandler<ActionEvent> eventHandler = event -> {
            ConfigActionController.INSTANCE.executeAction(actionConstructor.apply(new ArrayList<>(SelectionController.INSTANCE.getSelectedKeys()), fieldValueGetter.apply(field)));
        };
        actionEventSetter.accept(field, eventHandler);
        propertyChangeGetter.cachedPropValueProperty().addListener((obs, ov, nv) -> {
            actionEventSetter.accept(field, null);
            fieldValueSetter.accept(field, nv);
            actionEventSetter.accept(field, eventHandler);
        });
    }

    public static BiConsumer<Spinner<Integer>, EventHandler<ActionEvent>> createSpinnerActionEventListenerSetter() {
        return new BiConsumer<>() {
            private ChangeListener<Number> changeListener;

            @Override
            public void accept(Spinner<Integer> integerSpinner, EventHandler<ActionEvent> actionEventEventHandler) {
                if (actionEventEventHandler != null) {
                    changeListener = (obs, ov, nv) -> actionEventEventHandler.handle(null);
                    integerSpinner.valueProperty().addListener(changeListener);
                } else {
                    integerSpinner.valueProperty().removeListener(changeListener);
                }
            }
        };
    }

    public static BiConsumer<CheckBox, EventHandler<ActionEvent>> createCheckboxActionEventListenerSetter() {
        return new BiConsumer<>() {
            private ChangeListener<Boolean> changeListener;

            @Override
            public void accept(CheckBox checkBox, EventHandler<ActionEvent> actionEventEventHandler) {
                if (actionEventEventHandler != null) {
                    changeListener = (obs, ov, nv) -> actionEventEventHandler.handle(null);
                    checkBox.selectedProperty().addListener(changeListener);
                } else {
                    checkBox.selectedProperty().removeListener(changeListener);
                }
            }
        };
    }

    public static BiConsumer<ToggleSwitch, EventHandler<ActionEvent>> createToggleSwitchActionEventListenerSetter() {
        return new BiConsumer<>() {
            private ChangeListener<Boolean> changeListener;

            @Override
            public void accept(ToggleSwitch toggleSwitch, EventHandler<ActionEvent> actionEventEventHandler) {
                if (actionEventEventHandler != null) {
                    changeListener = (obs, ov, nv) -> actionEventEventHandler.handle(null);
                    toggleSwitch.selectedProperty().addListener(changeListener);
                } else {
                    toggleSwitch.selectedProperty().removeListener(changeListener);
                }
            }
        };
    }

    public static BiConsumer<ToggleGroup, EventHandler<ActionEvent>> createToggleButtonGroupActionEventSetter() {
        return new BiConsumer<>() {
            @Override
            public void accept(ToggleGroup toggleGroup, EventHandler<ActionEvent> actionEventEventHandler) {
                for (Toggle button : toggleGroup.getToggles()) {
                    ToggleButton toggleButton = (ToggleButton) button;
                    toggleButton.setOnAction(actionEventEventHandler);
                }
            }
        };
    }

    public static BiConsumer<Spinner<Integer>, Number> createSpinnerSetValue() {
        return (spinner, value) -> spinner.getValueFactory().setValue(LCUtils.nullToZeroInt(value));
    }

    public static BiConsumer<ToggleSwitch, Boolean> createToggleSwitchSetValue() {
        return (toggleSwitch, value) -> toggleSwitch.setSelected(LCUtils.nullToFalse(value));
    }

    public static BiConsumer<CheckBox, Boolean> createCheckboxSetValue() {
        return (checkBox, value) -> checkBox.setSelected(LCUtils.nullToFalse(value));
    }

    public static Function<ToggleGroup, TextAlignment> createToggleButtonGroupValueGetter(Map<TextAlignment, ToggleButton> textAlignButtons) {
        return group -> (TextAlignment) group.getSelectedToggle().getUserData();
    }

    public static BiConsumer<ToggleGroup, TextAlignment> createToggleButtonGroupValueSetter(Map<TextAlignment, ToggleButton> textAlignButtons) {
        return (group, textAlign) -> group.selectToggle(textAlignButtons.get(textAlign));
    }
}