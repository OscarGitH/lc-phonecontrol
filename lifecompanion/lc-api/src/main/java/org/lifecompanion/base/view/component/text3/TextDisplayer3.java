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

package org.lifecompanion.base.view.component.text3;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.api.component.definition.WriterDisplayerI;
import org.lifecompanion.api.component.definition.text.TextBoundsProviderI;
import org.lifecompanion.api.component.definition.text.TextDisplayerLineI;
import org.lifecompanion.api.component.definition.text.TextDisplayerWordI;
import org.lifecompanion.api.component.definition.text.TextDisplayerWordPartI;
import org.lifecompanion.api.control.events.WritingEventSource;
import org.lifecompanion.api.image2.ImageElementI;
import org.lifecompanion.api.style2.definition.TextCompStyleI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.base.view.component.text.TextDisplayerBaseImplView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.predict4all.nlp.Separator;

import java.util.ArrayList;
import java.util.List;

public class TextDisplayer3 extends Pane implements LCViewInitHelper {
    private final WriterDisplayerI textDisplayer;

    private DoubleBinding maxWidthProperty;
    private ReadOnlyDoubleProperty height;

    private TextDisplayerBaseImplView<?> parentView;
    private javafx.scene.shape.Line caretLine;
    private TextBoundsProviderImpl BOUNDS_PROVIDER = new TextBoundsProviderImpl();
    private List<Node> previousChildren;

    public TextDisplayer3(final WriterDisplayerI textDisplayer, DoubleBinding doubleBinding, ReadOnlyDoubleProperty height,
                          TextDisplayerBaseImplView<?> parentView) {
        this.textDisplayer = textDisplayer;
        this.maxWidthProperty = doubleBinding;
        this.height = height;
        this.parentView = parentView;
        previousChildren = new ArrayList<>();
        this.initAll();
    }

    @Override
    public void initUI() {
        caretLine = new javafx.scene.shape.Line();
        caretLine.setStrokeWidth(2.0);
        getChildren().add(caretLine);
    }

    @Override
    public void initBinding() {
        // TODO : Batch updates ?
        this.textDisplayer.setCachedLinesUpdateListener(lines -> {
            if (Platform.isFxApplicationThread()) {
                this.repaint(lines);
            } else {
                Platform.runLater(() -> repaint(lines));
            }
        }, maxWidthProperty, BOUNDS_PROVIDER);
    }

    @Override
    public void initListener() {
        this.setOnMouseReleased(me -> WritingStateController.INSTANCE.moveCaretToPosition(WritingEventSource.USER_PHYSICAL_INPUT, this.textDisplayer, me.getX(), me.getY()));
    }

    private void repaint(List<TextDisplayerLineI> lines) {
        this.getChildren().removeAll(previousChildren);
        previousChildren.clear();

        // Generate lines
        TextCompStyleI textStyle = this.textDisplayer.getTextDisplayerTextStyle();

        final TextAlignment textAlignment = textStyle.textAlignmentProperty().value().getValue();

        // Initialize caret
        int caretPosition = WritingStateController.INSTANCE.caretPosition().get();
        int caretLineIndex = 0;

        double y = 0.0;
        for (int l = 0; l < lines.size(); l++) {
            TextDisplayerLineI line = lines.get(l);
            final double lineTextHeight = line.getTextHeight();
            final double lineImageHeight = line.getImageHeight(textDisplayer);

            double x = textAlignment == TextAlignment.LEFT ? 0.0
                    : textAlignment == TextAlignment.CENTER ? maxWidthProperty.get() / 2.0 - line.getWidth() / 2.0
                    : maxWidthProperty.get() - line.getWidth();

            List<TextDisplayerWordI> words = line.getWords();
            for (TextDisplayerWordI word : words) {
                List<TextDisplayerWordPartI> parts = word.getParts();
                double xInWord = x;

                boolean caretDisplayed = false;

                // Display each word part
                for (TextDisplayerWordPartI part : parts) {
                    if (textDisplayer.enableImageProperty().get() && part.isImageStart() && part.getEntry().imageProperty().get() != null) {
                        ImageElementI imageForPart = part.getEntry().imageProperty().get();
                        imageForPart.requestImageLoad(textDisplayer.getID(), part.getImageWidth(), part.getHeight(), true, true);
                        Image loadedImage = imageForPart.loadedImageProperty().get();
                        if (loadedImage != null) {
                            final Rectangle2D imgBounds = getImageBoundsToDrawOn(xInWord, y + lineImageHeight, loadedImage.getWidth(),
                                    loadedImage.getHeight(), part.getImageWidth(), textDisplayer.imageHeightProperty().get());
                            ImageView imageView = new ImageView(loadedImage);
                            imageView.setLayoutX(imgBounds.getMinX());
                            imageView.setLayoutY(imgBounds.getMinY());
                            imageView.setFitWidth(imgBounds.getWidth());
                            imageView.setFitHeight(imgBounds.getHeight());
                            previousChildren.add(imageView);
                        }
                        // TODO : what if image is not yet loaded ?
                    }

                    Text textNode = createTextForPart(textStyle, part, null, lineTextHeight, xInWord, lineImageHeight + y);
                    previousChildren.add(textNode);

                    // If caret is located in the word
                    if (caretPosition >= part.getCaretStart() && caretPosition <= part.getCaretEnd()) {
                        double caretX = part.getCaretPosition(caretPosition, BOUNDS_PROVIDER, textStyle) + xInWord;
                        caretLineIndex = displayCaret(textStyle, caretX, caretLine, lineImageHeight + y, l, line);
                        caretDisplayed = true;
                    }

                    xInWord += part.getWidth();
                }

                // Caret is not located in any of the word part, but is located at the end of the word (after separator)
                if (word.getCaretEnd() == caretPosition && !caretDisplayed) {
                    caretLineIndex = displayCaret(textStyle, x + word.getWidth(), caretLine, lineImageHeight + y, l, line);
                    caretDisplayed = true;
                }

                // Display the end stop char of the word
                if (word.getWordSeparatorChar() != null && Separator.getSeparatorFor(word.getWordSeparatorChar()) != Separator.NEWLINE) {
                    Text sepNode = createTextForPart(textStyle, null, word.getWordSeparatorChar(), lineTextHeight, xInWord,
                            lineImageHeight + y);
                    previousChildren.add(sepNode);
                }
                x += word.getWidth();
            }

            // Go to next line : skip the line total height + the wanted spacing
            y += lineImageHeight + lineTextHeight + textDisplayer.lineSpacingProperty().get();
        }

        this.getChildren().addAll(previousChildren);
        caretLine.toFront();
        this.setPrefHeight(y);

        // Update scroll relative to the caret
        this.parentView.updateCaretScroll(getCaretPercent(lines, caretLineIndex, caretLine, y));
    }

    private int displayCaret(TextCompStyleI textStyle, double caretX, javafx.scene.shape.Line caretLine, double y, int l, TextDisplayerLineI line) {
        caretLine.setStartX(caretX + caretLine.getStrokeWidth() / 2.0);
        caretLine.setEndX(caretLine.getStartX());
        caretLine.setStartY(y);
        caretLine.setEndY(y + line.getTextHeight());
        caretLine.setStroke(textStyle.colorProperty().value().getValue());
        return l;
    }

    private double getCaretPercent(List<TextDisplayerLineI> lines, int caretLineIndex, javafx.scene.shape.Line caretLine, double y) {
        if (caretLineIndex == lines.size() - 1)
            return 1.0;
        if (caretLineIndex == 0)
            return 0.0;
        return (caretLine.getStartY() - (this.height.get() / 2.0)) / (this.getLayoutBounds().getHeight() - this.height.get());
    }

    private Text createTextForPart(TextCompStyleI textStyle, TextDisplayerWordPartI part, Character stopChar, double textHeight, double x, double y) {
        Text textNode = new Text(part != null ? part.getPart() : String.valueOf(stopChar));
        textNode.setFont(textStyle.fontProperty().get());
        textNode.setFill(part != null && part.getEntry().fontColorProperty().get() != null ? part.getEntry().fontColorProperty().get()
                : textStyle.colorProperty().value().getValue());
        textNode.layoutXProperty().set(x);
        textNode.layoutYProperty().set(textHeight + y);
        textNode.setTextOrigin(VPos.BOTTOM);
        return textNode;
    }

    /**
     * To get the drawing bounds of an image (this keep ratio)
     *
     * @return the bounds to keep the ratio of the given image, and to center it.<br>
     * If the image can respect the width with the wanted height the image width will change, if not height will change to respect the wanted width
     */
    private Rectangle2D getImageBoundsToDrawOn(final double x, final double y, final double width, final double height, final double wantedWidth,
                                               final double wantedHeight) {
        double newWidth = width / height * wantedHeight;
        if (newWidth > wantedWidth) {
            double newHeight = height / width * wantedWidth;
            double nY = y - wantedHeight + (wantedHeight - newHeight) / 2.0;
            return new Rectangle2D(x, nY, wantedWidth, newHeight);
        } else {
            double nX = x + wantedWidth / 2.0 - newWidth / 2.0;
            return new Rectangle2D(nX, y - wantedHeight, newWidth, wantedHeight);
        }
    }

    private static class TextBoundsProviderImpl implements TextBoundsProviderI {

        @Override
        public Bounds getBounds(final String text, final TextCompStyleI textStyle) {
            Font originalFont = textStyle.fontProperty().get();
            return LCUtils.getTextBounds(text, originalFont);
        }

    }
}
