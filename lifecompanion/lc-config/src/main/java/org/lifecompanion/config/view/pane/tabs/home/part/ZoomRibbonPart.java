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

package org.lifecompanion.config.view.pane.tabs.home.part;

import java.text.DecimalFormat;

import org.controlsfx.glyphfont.FontAwesome;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.component.baseimpl.DisplayableComponentBaseImpl;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.api.ui.config.ConfigurationProfileLevelEnum;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Zoom level ribbon part.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ZoomRibbonPart extends RibbonBasePart<DisplayableComponentBaseImpl> implements LCViewInitHelper {
	private static final DecimalFormat DECIMAL_ZOOM_FORMAT = new DecimalFormat("#");

	/**
	 * Button modify zoom level
	 */
	private Button buttonResetZoom, buttonIncreaseZoom, buttonDecreaseZoom;

	/**
	 * Label to display zoom level
	 */
	private Label labelZoomLevel;

	public ZoomRibbonPart() {
		this.initAll();
	}

	@Override
	public void initUI() {
		GridPane gridPane = new GridPane();
		gridPane.setHgap(5.0);
		gridPane.setVgap(5.0);
		gridPane.setAlignment(Pos.CENTER);
		this.buttonIncreaseZoom = UIUtils.createGraphicButton(
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.SEARCH_PLUS).size(20).color(LCGraphicStyle.MAIN_DARK),
				"tooltip.buttons.zoom.increase");
		this.buttonDecreaseZoom = UIUtils.createGraphicButton(
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.SEARCH_MINUS).size(20).color(LCGraphicStyle.SECOND_DARK),
				"tooltip.buttons.zoom.decrease");
		this.buttonResetZoom = UIUtils.createRightTextButton(Translation.getText("reset.zoom.level.label"),
				LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.UNDO).size(18).color(LCGraphicStyle.SECOND_DARK), "tooltip.buttons.zoom.reset");
		this.labelZoomLevel = new Label();
		gridPane.add(this.buttonDecreaseZoom, 0, 0);
		gridPane.add(this.buttonIncreaseZoom, 1, 0);
		gridPane.add(new Separator(Orientation.HORIZONTAL), 0, 1, 2, 1);
		gridPane.add(this.buttonResetZoom, 0, 2, 2, 1);
		gridPane.add(new Separator(Orientation.HORIZONTAL), 0, 3, 2, 1);
		gridPane.add(this.labelZoomLevel, 0, 4, 2, 1);
		GridPane.setHgrow(this.buttonDecreaseZoom, Priority.ALWAYS);
		GridPane.setHgrow(this.buttonIncreaseZoom, Priority.ALWAYS);
		this.labelZoomLevel.setMaxWidth(Double.MAX_VALUE);
		this.labelZoomLevel.setAlignment(Pos.CENTER);
		this.setTitle(Translation.getText("ribbon.part.zoom.level"));
		this.setContent(gridPane);
	}

	@Override
	public void initListener() {
		this.buttonDecreaseZoom.setOnAction(e -> AppController.INSTANCE.zoomOut());
		this.buttonIncreaseZoom.setOnAction(e -> AppController.INSTANCE.zoomIn());
		this.buttonResetZoom.setOnAction(e -> AppController.INSTANCE.resetZoom());
	}

	@Override
	public void initBinding() {
		ConfigUIUtils.bindShowForLevelFrom(this, ConfigurationProfileLevelEnum.NORMAL);
		this.labelZoomLevel.textProperty()
				.bind(Bindings.createStringBinding(
						() -> ZoomRibbonPart.DECIMAL_ZOOM_FORMAT.format(AppController.INSTANCE.configurationScaleProperty().get() * 100.0) + " %",
						AppController.INSTANCE.configurationScaleProperty()));
	}

	// Class part : "Bind/unbind"
	//========================================================================
	@Override
	public void bind(final DisplayableComponentBaseImpl modelP) {}

	@Override
	public void unbind(final DisplayableComponentBaseImpl modelP) {}
	//========================================================================
}
