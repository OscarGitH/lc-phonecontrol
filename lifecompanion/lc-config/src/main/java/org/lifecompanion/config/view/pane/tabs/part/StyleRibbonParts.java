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
package org.lifecompanion.config.view.pane.tabs.part;

import org.lifecompanion.config.view.pane.tabs.api.AbstractRibbonTabContent;
import org.lifecompanion.config.view.pane.tabs.style2.part.AbstractGridShapeStyleRibbonPart.PluralGridShapeStyleRibbonPart;
import org.lifecompanion.config.view.pane.tabs.style2.part.AbstractGridShapeStyleRibbonPart.SingleGridShapeStyleRibbonPart;
import org.lifecompanion.config.view.pane.tabs.style2.part.KeyInOtherComponentStyleRibbonPart;
import org.lifecompanion.config.view.pane.tabs.style2.part.KeyInOtherComponentTextStyle;
import org.lifecompanion.config.view.pane.tabs.style2.part.TextDisplayerShapeStyleRibbonPart;
import org.lifecompanion.config.view.pane.tabs.style2.part.TextDisplayerTextStyleRibbonPart;
import org.lifecompanion.config.view.pane.tabs.style3.MultiKeyStyleRibbonPart;
import org.lifecompanion.config.view.pane.tabs.style3.MultiKeyTextStyleRibbonPart;

/**
 * Contains every ribbon part to display for style tab.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StyleRibbonParts {
    public static class StyleGridPart extends AbstractRibbonTabContent {

        public StyleGridPart() {
            super(//
                    RibbonTabPart.create(SingleGridShapeStyleRibbonPart.class), //
                    RibbonTabPart.create(KeyInOtherComponentStyleRibbonPart.class), //
                    RibbonTabPart.create(KeyInOtherComponentTextStyle.class)//
            );
        }
    }

    public static class StyleGridPartStack extends AbstractRibbonTabContent {
        public StyleGridPartStack() {
            super(//
                    RibbonTabPart.create(PluralGridShapeStyleRibbonPart.class), //
                    RibbonTabPart.create(KeyInOtherComponentStyleRibbonPart.class), //
                    RibbonTabPart.create(KeyInOtherComponentTextStyle.class)//
            );
        }
    }

    public static class StyleGridTextEditorPart extends AbstractRibbonTabContent {

        public StyleGridTextEditorPart() {
            super(//
                    RibbonTabPart.create(TextDisplayerShapeStyleRibbonPart.class), //
                    RibbonTabPart.create(TextDisplayerTextStyleRibbonPart.class));
        }
    }

    public static class MultiStyleKeyPart extends AbstractRibbonTabContent {

        public MultiStyleKeyPart() {
            super(//
                    RibbonTabPart.create(MultiKeyStyleRibbonPart.class), //
                    RibbonTabPart.create(MultiKeyTextStyleRibbonPart.class)//
            );
        }
    }

    public static class StyleStackPart extends AbstractRibbonTabContent {

        public StyleStackPart() {
            super(//
                    RibbonTabPart.create(PluralGridShapeStyleRibbonPart.class), //
                    RibbonTabPart.create(KeyInOtherComponentStyleRibbonPart.class), //
                    RibbonTabPart.create(KeyInOtherComponentTextStyle.class)//
            );
        }
    }

    public static class StyleTextEditorPart extends AbstractRibbonTabContent {

        public StyleTextEditorPart() {
            super(//
                    RibbonTabPart.create(TextDisplayerShapeStyleRibbonPart.class), //
                    RibbonTabPart.create(TextDisplayerTextStyleRibbonPart.class)//
            );
        }
    }
}
