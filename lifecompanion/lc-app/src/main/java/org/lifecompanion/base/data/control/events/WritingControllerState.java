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

package org.lifecompanion.base.data.control.events;

import org.lifecompanion.api.control.events.WritingControllerStateI;
import org.lifecompanion.api.prediction.WordPredictionResultI;

public class WritingControllerState implements WritingControllerStateI {
	private final String typedTextBeforeCaret, typedTextAfterCaret;
	private final WordPredictionResultI wordPredictionResult;

	public WritingControllerState(String typedTextBeforeCaret, String typedTextAfterCaret, WordPredictionResultI wordPredictionResult) {
		super();
		this.typedTextBeforeCaret = typedTextBeforeCaret;
		this.typedTextAfterCaret = typedTextAfterCaret;
		this.wordPredictionResult = wordPredictionResult;
	}

	public WritingControllerState() {
		this(null, null, null);
	}

	@Override
	public String getTypedTextBeforeCaret() {
		return typedTextBeforeCaret;
	}

	@Override
	public String getTypedTextAfterCaret() {
		return typedTextAfterCaret;
	}

	@Override
	public WordPredictionResultI getWordPredictionResult() {
		return wordPredictionResult;
	}

	@Override
	public String toString() {
		return "WritingControllerState{" +
				"typedTextBeforeCaret='" + typedTextBeforeCaret + '\'' +
				", typedTextAfterCaret='" + typedTextAfterCaret + '\'' +
				", wordPredictionResult=" + wordPredictionResult +
				'}';
	}
}