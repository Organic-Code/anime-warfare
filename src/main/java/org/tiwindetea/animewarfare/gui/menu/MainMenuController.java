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

package org.tiwindetea.animewarfare.gui.menu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.lomadriel.lfc.event.EventDispatcher;
import org.tiwindetea.animewarfare.gui.menu.event.MainMenuEvent;
import org.tiwindetea.animewarfare.util.ResourceBundleHelper;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The main menu controller.
 *
 * @author Benoît CORTIER
 * @author Jérôme BOULMIER
 */
public class MainMenuController implements Initializable {
	private static final ResourceBundle BUNDLE
			= ResourceBundleHelper.getBundle("org.tiwindetea.animewarfare.gui.menu.MainMenuController");

	@FXML
	private VBox buttonContainer;

	@FXML
	private Button playButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.playButton.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				MainMenuController.this.buttonContainer.requestFocus();
				MainMenuController.this.playButton.focusedProperty().removeListener(this);
			}
		});
	}

	@FXML
	private void handlePlay() {
		EventDispatcher.getInstance().fire(new MainMenuEvent(MainMenuEvent.Type.PLAY));
	}

	@FXML
	private void handleSettings() {
		EventDispatcher.getInstance().fire(new MainMenuEvent(MainMenuEvent.Type.SETTINGS));
	}

	@FXML
	private void handleAbout() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(BUNDLE.getString("alert.about.title"));
		alert.setHeaderText(BUNDLE.getString("alert.about.header"));
		alert.setContentText(BUNDLE.getString("alert.about.content.text"));

		alert.showAndWait();
	}

	@FXML
	private void handleQuit() {
		EventDispatcher.getInstance().fire(new MainMenuEvent(MainMenuEvent.Type.QUIT));
	}

	@FXML
	private void handleFocusRequest() {
		if (this.buttonContainer.focusedProperty().getValue().booleanValue()) {
			this.playButton.requestFocus();
		}
	}

	@FXML
	private void handleMouseEnteredEvent(MouseEvent event) {
		Node button = (Node) event.getSource();
		button.requestFocus();
	}

	@FXML
	private void handleMouseExitedEvent() {
		this.buttonContainer.requestFocus();
	}
}
