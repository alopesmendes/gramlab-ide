/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package org.gramlab.core.umlv.unitex.undo;

import javax.swing.undo.AbstractUndoableEdit;

import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphBox;

/**
 * Class used to save the state of the graph before translate a box
 * 
 * @author Decreton Julien
 */
public class TranslationEdit extends AbstractUndoableEdit {
	/**
	 * box to translate
	 */
	private final GenericGraphBox boxe;
	/**
	 * length of X, Y shift in pixels
	 */
	private final int x;
	private final int y;

	/**
	 * @param boxe
	 *            the boxe to translate
	 * @param x
	 *            length of X shift in pixels
	 * @param y
	 *            length of Y shift in pixels
	 */
	public TranslationEdit(GenericGraphBox boxe, int x, int y) {
		this.boxe = boxe;
		this.x = x;
		this.y = y;
	}

	@Override
	public void undo() {
		super.undo();
		boxe.translate(-x, -y);
	}

	@Override
	public void redo() {
		super.redo();
		boxe.translate(x, y);
	}
}