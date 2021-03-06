////////////////////////////////////////////////////////////
//
// Anime Warfare
// Copyright (C) 2016 TiWinDeTea - contact@tiwindetea.org
//
// This software is provided 'as-is', without any express or implied warranty.
// In no event will the authors be held liable for any damages arising from the use of this software.
//
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it freely,
// subject to the following restrictions:
//
// 1. The origin of this software must not be misrepresented;
//    you must not claim that you wrote the original software.
//    If you use this software in a product, an acknowledgment
//    in the product documentation would be appreciated but is not required.
//
// 2. Altered source versions must be plainly marked as such,
//    and must not be misrepresented as being the original software.
//
// 3. This notice may not be removed or altered from any source distribution.
//
////////////////////////////////////////////////////////////

package org.tiwindetea.animewarfare.gui.game.dialog;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * @author Benoît CORTIER
 */
public abstract class GameDialog {

	private final VBox overlay;
	private final Group dialogElements;

	private boolean alreadyClosed = false;

	GameDialog(VBox overlay) {

		this.overlay = overlay;

		this.dialogElements = new Group();

		this.overlay.setMouseTransparent(false);
		this.overlay.getChildren().add(this.dialogElements);
	}

	protected void addElement(Node element) {
		this.dialogElements.getChildren().add(element);
	}

	protected void removeElement(Node element) {
		this.dialogElements.getChildren().remove(element);
	}

	public void close() {
		if (this.alreadyClosed) {
			return;
		}

		this.dialogElements.getChildren().clear();
		this.overlay.getChildren().remove(this.dialogElements);

		this.alreadyClosed = true;
	}
}
