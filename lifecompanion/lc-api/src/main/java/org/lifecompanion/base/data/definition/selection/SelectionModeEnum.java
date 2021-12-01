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

package org.lifecompanion.base.data.definition.selection;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import org.lifecompanion.api.definition.selection.SelectionModeI;
import org.lifecompanion.base.data.definition.selection.impl.*;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Enum for every available selection modes.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum SelectionModeEnum {
	//TODO : add parameters, description...
	MOUSE_CLIC("selection.mode.direct.mouse.clic", "selection.mode.direct.mouse.clic.description", "icon_selection_mode_clic.png",
			DirectActivationSelectionMode.class, true, false, false, false), //
	AUTO_MOUSE_CLIC("selection.mode.auto.mouse.clic", "selection.mode.auto.mouse.clic.description", "icon_select_auto_activation.png",
			AutoActivationSelectionMode.class, true, true, true, false), //
	SCAN_KEY_HORIZONTAL("selection.mode.scan.direct.key.horizontal", "selection.mode.scan.direct.key.horizontal.description",
			"icon_selection_key_horizontal.png", HorizontalDirectKeyScanSelectionMode.class, false, true, false, true), //
	SCAN_ROW_COLUMN("selection.mode.scan.row.column.name", "selection.mode.scan.row.column.description", "icon_selection_row_column.png",
			RowColumnScanSelectionMode.class, false, true, false, true), //
	SCAN_KEY_VERTICAL("selection.mode.scan.direct.key.vertical", "selection.mode.scan.direct.key.vertical.description",
			"icon_selection_key_vertical.png", VerticalDirectKeyScanSelectionMode.class, false, true, false, true), //
	SCAN_COLUMN_ROW("selection.mode.scan.column.row.name", "selection.mode.scan.column.row.description", "icon_selection_column_row.png",
			ColumnRowScanSelectionMode.class, false, true, false, true);

	public static final String ICON_URL_SELECTION_MODE = "selection-mode/";

	private final String nameId;
	private final String descriptionId;
	private final String logoUrl;
	private final Class<? extends SelectionModeI> modeClass;

	// Class part : "Enable/disable parameters"
	//========================================================================
	//	/**
	//	 * To enable disable general progress property
	//	 */
	//	private ReadOnlyBooleanProperty enableProgressViewColor, enableDrawProgress;
	//	/**
	//	 * To enable auto clic mode
	//	 */
	//	private ReadOnlyBooleanProperty enableAutoClicTimeProperty;
	//
	//	/**
	//	 * To enable general scanning property
	//	 */
	//	private ReadOnlyBooleanProperty enableScanPause, enableFirstPause, enableMaxScanBeforeStop, enableScanningOrder, enableStartOnClic,
	//			enableSkipEmptyComponent;

	/**
	 * Some parameters that determines the selection mode
	 */
	private final ReadOnlyBooleanProperty useProgressDraw, useAutoClic, useScanning, usePointer;
	//========================================================================

	SelectionModeEnum(final String nameIdP, final String descriptionIdP, final String logoUrlP, final Class<? extends SelectionModeI> modeClassP,
			final boolean usePointer, final boolean progressDrawMode, final boolean autoClicMode, final boolean scanningMode) {
		this.nameId = nameIdP;
		this.descriptionId = descriptionIdP;
		this.logoUrl = logoUrlP;
		this.modeClass = modeClassP;
		//Enable disable with mode
		this.useProgressDraw = new ReadOnlyBooleanWrapper(progressDrawMode);
		this.useAutoClic = new ReadOnlyBooleanWrapper(autoClicMode);
		this.useScanning = new ReadOnlyBooleanWrapper(scanningMode);
		this.usePointer = new ReadOnlyBooleanWrapper(usePointer);
		//
		//		if (progressDrawMode) {
		//			this.enableDrawProgress = new ReadOnlyBooleanWrapper(true);
		//			this.enableProgressViewColor = new ReadOnlyBooleanWrapper(true);
		//		} else {
		//			this.enableDrawProgress = new ReadOnlyBooleanWrapper(false);
		//			this.enableProgressViewColor = new ReadOnlyBooleanWrapper(false);
		//		}
		//		if (autoClicMode) {
		//			this.enableAutoClicTimeProperty = new ReadOnlyBooleanWrapper(true);
		//		} else {
		//			this.enableAutoClicTimeProperty = new ReadOnlyBooleanWrapper(false);
		//		}
		//		if (scanningMode) {
		//			enableScanPause = new ReadOnlyBooleanWrapper(true);
		//			enableFirstPause = new ReadOnlyBooleanWrapper(true);
		//			enableMaxScanBeforeStop = new ReadOnlyBooleanWrapper(true);
		//			enableScanningOrder = new ReadOnlyBooleanWrapper(true);
		//			enableStartOnClic = new ReadOnlyBooleanWrapper(true);
		//			enableSkipEmptyComponent = new ReadOnlyBooleanWrapper(true);
		//		} else {
		//			enableScanPause = new ReadOnlyBooleanWrapper(false);
		//			enableFirstPause = new ReadOnlyBooleanWrapper(false);
		//			enableMaxScanBeforeStop = new ReadOnlyBooleanWrapper(false);
		//			enableScanningOrder = new ReadOnlyBooleanWrapper(false);
		//			enableStartOnClic = new ReadOnlyBooleanWrapper(false);
		//			enableSkipEmptyComponent = new ReadOnlyBooleanWrapper(false);
		//		}
	}

	public Class<? extends SelectionModeI> getModeClass() {
		return this.modeClass;
	}

	public String getName() {
		return Translation.getText(this.nameId);
	}

	public String getDescription() {
		return Translation.getText(this.descriptionId);
	}

	public String getLogoUrl() {
		return this.logoUrl;
	}

	// Class part : "Enable/disable parameters"
	//========================================================================
	//	public ReadOnlyBooleanProperty getEnableProgressViewColor() {
	//		return enableProgressViewColor;
	//	}
	//
	//	public ReadOnlyBooleanProperty getEnableDrawProgress() {
	//		return enableDrawProgress;
	//	}
	//
	//	public ReadOnlyBooleanProperty getEnableAutoClicTimeProperty() {
	//		return enableAutoClicTimeProperty;
	//	}
	//
	//	public ReadOnlyBooleanProperty getEnableScanPause() {
	//		return enableScanPause;
	//	}
	//
	//	public ReadOnlyBooleanProperty getEnableFirstPause() {
	//		return enableFirstPause;
	//	}
	//
	//	public ReadOnlyBooleanProperty getEnableMaxScanBeforeStop() {
	//		return enableMaxScanBeforeStop;
	//	}
	//
	//	public ReadOnlyBooleanProperty getEnableScanningOrder() {
	//		return enableScanningOrder;
	//	}
	//
	//	public ReadOnlyBooleanProperty getEnableStartOnClic() {
	//		return enableStartOnClic;
	//	}
	//
	//	public ReadOnlyBooleanProperty getEnableSkipEmptyComponent() {
	//		return enableSkipEmptyComponent;
	//	}
	public ReadOnlyBooleanProperty useProgressDrawProperty() {
		return this.useProgressDraw;
	}

	public ReadOnlyBooleanProperty useAutoClicProperty() {
		return this.useAutoClic;
	}

	public ReadOnlyBooleanProperty useScanningProperty() {
		return this.useScanning;
	}

	public ReadOnlyBooleanProperty usePointerProperty() {
		return this.usePointer;
	}
	//========================================================================

	public static SelectionModeEnum getEnumFor(final Class<? extends SelectionModeI> type) {
		SelectionModeEnum[] modes = SelectionModeEnum.values();
		for (SelectionModeEnum selectionModeEnum : modes) {
			if (type.equals(selectionModeEnum.getModeClass())) {
				return selectionModeEnum;
			}
		}
		return null;
	}

}
