package view;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import model.Friend;

/**
 * GUI class for application. This is a Java FX
 * built GUI. There are bound to be quite a few
 * bugs to fix.
 * @author Damiene Stewart
 * @version 1.0
 */
public class EchoBotGUI extends Application {
	
	/**
	 * A count of successfully logged in clients.
	 */
	private int myLogins;
	
	/**
	 * A reference to the controller class that will work with the
	 * model to get things done.
	 */
	private List<Controller> myControllerList;
	
	/**
	 * The primary stage.
	 */
	private Stage myStage;
	
	/**
	 * The login scene. Later on this should be removed with
	 * simply the scene group/root? That way simply the
	 * roots can be swapped (according to stackoverflow.com).
	 */
	private Scene myLoginScene;
	
	/**
	 * The main, working scene for the GUI application. This
	 * is where the user will go after login.
	 */
	private Scene myMainScene;
	
	/**
	 * This property indicates whether or not the loading
	 * image should be shown.
	 */
	private BooleanProperty myShowLoadingImage;
	
	/**
	 * The server socket that ensures that only one
	 * instance of this application is run at a time.
	 */
	@SuppressWarnings("unused")
	private ServerSocket myServerSocket;
	
	/**
	 * Create an instance of the GUI class, initializing the
	 * instance variables.
	 */
	public EchoBotGUI() {
		myControllerList = new ArrayList<Controller>();
		myShowLoadingImage = new SimpleBooleanProperty(false);
		myStage = null;
		myLoginScene = null;
		myMainScene = null;
		myLogins = 0;
	}
	
	/**
	 * Static start method to kick things off on the
	 * Java FX application thread.
	 * @param theArgs the command-line arguments, unused.
	 */
	public static void main(String... theArgs) {
		launch();
	}
	
	/**
	 * {@inheritDoc}
	 * This will get called from launch() in the main thread.
	 * @param thePrimaryStage the application's stage.
	 * @throws Exception - good to know!
	 */
	@Override
	public void start(Stage thePrimaryStage) throws Exception {
		myStage = thePrimaryStage;
		setup();
	}
		
	/**
	 * Provides initial application setup and login scene
	 * construction.
	 */
	private void setup() {
		try {
			myServerSocket = new ServerSocket(9090);
		} catch (UnknownHostException e) {
			Alert stop = new Alert(AlertType.ERROR);
			stop.setHeaderText(null);
			stop.setContentText("Only one instance of this program is permitted at a time.");
			stop.showAndWait();
			Platform.exit();
			System.exit(0);
		} catch (IOException e) {
			Alert stop = new Alert(AlertType.ERROR);
			stop.setHeaderText(null);
			stop.setContentText("Only one instance of this program is permitted at a time.");
			stop.showAndWait();
			Platform.exit();
			System.exit(0);
		}
		
		// Configure login scene as it will be needed as
		// soon as the application launches.
		myLoginScene = createLoginScene();
		
		// Stage setup.
		myStage.setTitle("EchoBot Pro v1.0");
		myStage.setScene(myLoginScene);
		myStage.setOnCloseRequest(event -> {
			shutdown();
		});
		myStage.sizeToScene();
		myStage.setResizable(false);
		myStage.show();
		
		Alert agreement = new Alert(AlertType.CONFIRMATION);
		agreement.setTitle("Agreement Notice");
		agreement.setHeaderText(null);
		agreement.setContentText("By using this software you agree that the author is in no way liable"
				+ " for any consequences arising from it's use. In other words, you accept all responsiblity"
				+ " for your use of this software.\n\n"
				+ "You also agree that you are not affiliated with Dirtybit AS "
				+ "(Fun Run 2's developer), and"
				+ " you agree that you understand that Echo Bot is also in no way affiliated with Dirtybit AS or"
				+ " any of it's products.\n");
		agreement.getButtonTypes().clear();
		agreement.getButtonTypes().addAll(ButtonType.CANCEL, new ButtonType("Agree"));
		
		Optional<ButtonType> result = agreement.showAndWait();
		
		if (!result.get().getText().equals("Agree")) {
			Platform.exit();
		}
	}

	/**
	 * Constructs and returns the login scene.
	 */
	private Scene createLoginScene() {
		BorderPane root = new BorderPane();
		Scene loginScene = new Scene(root);
		addElementsToLoginScene(root);
		return loginScene;
	}
	
	
	/**
	 * Continues with the construction of the login scene
	 * by adding the necessary elements. Some of this code
	 * seems really similar to that of the main scene. Maybe
	 * the main things out into separate functions?
	 * @param root the scene's root of which to add elements.
	 */
	private void addElementsToLoginScene(BorderPane root) {
		// Create grid.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		ColumnConstraints colOne = new ColumnConstraints(120);
		ColumnConstraints colTwo = new ColumnConstraints(100, 200, 300);
		colTwo.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(colOne, colTwo);
		
		// Create lists.
		List<Label> elabels = new ArrayList<Label>();
		List<Label> plabels = new ArrayList<Label>();
		List<TextField> eFields = new ArrayList<TextField>();
		List<PasswordField> pFields = new ArrayList<PasswordField>();
		
		// Create email/password labels.
		for (int i = 1; i <= 3; i++) {
			elabels.add(new Label("Email for bot " + i + ":"));
			plabels.add(new Label("Password for bot " + i + ":"));
		}
		
		// Create email/password fields.
		for (int i = 1; i <= 3; i++) {
			// Set up email field.
			TextField emailField = new TextField();
			emailField.setPromptText("Bot " + i +"'s email address.");
			eFields.add(emailField);
			
			// Set up password field.
			PasswordField passwordField = new PasswordField();
			passwordField.setPromptText("Bot " + i +"'s password.");
			pFields.add(passwordField);
		}
		
		// Create login loading image.
		Image loadingImage = new Image((new File("images/loading.gif")).toURI().toString());
		ImageView loadingImageView = new ImageView(loadingImage);
		loadingImageView.visibleProperty().bind(myShowLoadingImage);
		
		// Create login button.
		Button loginButton = new Button("Login");
		loginButton.setOnAction(event -> {
			handleLogin(eFields, pFields);
		}); 
		
		// Adjust alignment of elements.
		for (int i = 0; i < 3; i++) {
			GridPane.setHalignment(elabels.get(i), HPos.LEFT);
			GridPane.setHalignment(plabels.get(i), HPos.LEFT);
			GridPane.setHalignment(eFields.get(i), HPos.RIGHT);
			GridPane.setHalignment(pFields.get(i), HPos.RIGHT);
		}
		
		GridPane.setHalignment(loadingImageView, HPos.CENTER);
		GridPane.setHalignment(loginButton, HPos.RIGHT);
		
		// Add elements to grid.
		for (int i = 0, z = 0; i < 3; i++, z+=2) {
			grid.add(elabels.get(i), 0, z);
			grid.add(plabels.get(i), 0, z+1);
			grid.add(eFields.get(i), 1, z);
			grid.add(pFields.get(i), 1, z+1);
		}
		
		grid.add(loadingImageView, 1, 6);
		grid.add(loginButton, 1, 6);
		
		root.setTop(getMenuBar());
		root.setCenter(grid);
	}
	
	/**
	 * Handle the login button being pressed. Process data entered.
	 * @param eFields list of email fields.
	 * @param pFields list of password fields.
	 */

	private void handleLogin(List<TextField> eFields, List<PasswordField> pFields) {
		// TODO Auto-generated method stub
		int bot1V = validate(eFields.get(0).getText(), pFields.get(0).getText());
		int bot2V = validate(eFields.get(1).getText(), pFields.get(1).getText());
		int bot3V = validate(eFields.get(2).getText(), pFields.get(2).getText());
		
		if (bot1V == -1 || bot2V == -1 || bot3V == -1) {
			Alert incomplete = new Alert(AlertType.ERROR);
			incomplete.setHeaderText(null);
			incomplete.setContentText("One or more of the bot information is incomplete. Please recheck and try again.");
			incomplete.showAndWait();
		} else if (bot1V == 0 && bot2V == 0 && bot3V == 0) {
			Alert empty = new Alert(AlertType.ERROR);
			empty.setHeaderText(null);
			empty.setContentText("You must enter the information of at least one bot.");
			empty.showAndWait();
		} else {
			myControllerList.clear();
			myLogins = 0;
			showLoadingImage(true);
			Controller c = null;
			
			if (bot1V == 1) {
				c = new Controller(this);
				c.login(eFields.get(0).getText(), pFields.get(0).getText());
				myControllerList.add(c);
			}
			
			if (bot2V == 1) {
				c = new Controller(this);
				c.login(eFields.get(1).getText(), pFields.get(1).getText());
				myControllerList.add(c);
			}
			
			if (bot3V == 1) {
				c = new Controller(this);
				c.login(eFields.get(2).getText(), pFields.get(2).getText());
				myControllerList.add(c);
			}
			
			for (int i = 0; i < myControllerList.size(); i++) {
				if(!myControllerList.get(i).isClientLoggedIn()) {
					return;
				};
			}
			
			for (int i = 0; i < myControllerList.size(); i++) {
				myControllerList.get(i).startDataMonitor();
			}
		}
	}
	
	/**
	 * Validate information entered. Is it empty? Is one empty?
	 * @param theEmail the email
	 * @param thePassword the password
	 * @return 0, 1, -1 if both empty, both have value, one has value and other empty,
	 * respectively.
	 */
	private int validate(String theEmail, String thePassword) {
		if (!theEmail.isEmpty() && !thePassword.isEmpty()) {
			return 1;
		} else if (theEmail.isEmpty() && thePassword.isEmpty()) {
			return 0;
		}
		return -1;
	}

	/**
	 * Constructs and returns the main scene.
	 */
	private Scene createMainScene() {
		BorderPane root = new BorderPane();
		Scene mainScene = new Scene(root);
		addElementsToMainScene(root);
		return mainScene;
	}
	
	/**
	 * Continues with the construction of the main scene by
	 * adding the necessary elements.
	 * @param root the root of which to add elements.
	 */
	private void addElementsToMainScene(BorderPane root) {
		// Create grid.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		
		// Create lists.
		List<Label> labels = new ArrayList<Label>();
		List<ComboBox<Friend>> friends = new ArrayList<ComboBox<Friend>>();
		List<Button> buttons = new ArrayList<Button>();
		
		// Create labels.
		for (int i = 0; i < myControllerList.size(); i++) {
			labels.add(new Label("Bot " + (i+1) + " - accepting requests from:"));
		}
		
		// Create list.
		for (int i = 0; i < myControllerList.size(); i++) {
			final int z = i;
			ComboBox<Friend> fr = new ComboBox<Friend>(FXCollections.observableArrayList(myControllerList.get(i).getFriendList()));
			
			fr.setOnKeyPressed(event -> {
				if (event.getCode().equals(KeyCode.ENTER)) {
					Friend f = fr.getValue();
					if (f != null) {
						myControllerList.get(z).setFriend(f);
						Alert info = new Alert(AlertType.INFORMATION);
						info.setHeaderText(null);
						info.setContentText("Bot will accept requests from " + f.getUserName() + ".");
						info.show();
					}
				}
			});
			
			friends.add(fr);
		}
		
		// Create set button.
		for (int i = 0; i < myControllerList.size(); i++) {
			final int z = i;
			Button goButton = new Button("Go");
			goButton.setOnAction(event -> {
				Friend f = friends.get(z).getValue();
				if (f != null) {
					myControllerList.get(z).setFriend(f);
					Alert info = new Alert(AlertType.INFORMATION);
					info.setHeaderText(null);
					info.setContentText("Bot will accept requests from " + f.getUserName() + ".");
					info.show();
				}
			});
			buttons.add(goButton);
		}
		
		
		
		// Set alignment.
		for (int i = 0; i < myControllerList.size(); i++) {
			GridPane.setHalignment(labels.get(i), HPos.LEFT);
			GridPane.setHalignment(friends.get(i), HPos.RIGHT);
			GridPane.setHalignment(buttons.get(i), HPos.RIGHT);
		}
		
		// Add elements.
		for (int i = 0; i < myControllerList.size(); i++) {
			grid.add(labels.get(i), 0, i);
			grid.add(friends.get(i), 1, i);
			grid.add(buttons.get(i), 2, i);
		}
		
		root.setTop(getMenuBar());
		root.setCenter(grid);
	}
	
	/**
	 * Sets the value of the property bound to the loading
	 * image's visible property.
	 * @param theBooelan the value of which to set the property
	 * to.
	 */
	public void showLoadingImage(boolean theBooelan) {
		myShowLoadingImage.set(theBooelan);
	}
	
	/**
	 * Removes the login scene and displays the main scene.
	 * The login scene is discarded until it is needed again.
	 */
	public void showMainScene() {
		myLogins++;
		
		if (myLogins == myControllerList.size()) {
			myMainScene = createMainScene();
			
			myStage.setScene(myMainScene);
			myStage.sizeToScene();
			
			myLoginScene = null;
			
			myStage.sizeToScene();
			myStage.setResizable(false);
		}
	}
	
	/**
	 * Show the alert if the user has neglected to enter
	 * correct details.
	 * @param theMessage the alert message to display.
	 */
	public void showLoginAlert(String theMessage) {
		showLoadingImage(false);
		Alert loginAlert = new Alert(AlertType.ERROR);
		loginAlert.setHeaderText(null);
		loginAlert.setContentText(theMessage);
		loginAlert.showAndWait();
	}
	
	/**
	 * Constructs and returns menu bar.
	 * @param root the root to attach menu bar.
	 * @return the menu bar.
	 */
	public MenuBar getMenuBar() {
		MenuBar menuBar = new MenuBar();
		
		// File menu.
		Menu fileMenu = new Menu("File");
		
		// Exit file menu item
		MenuItem exitMenuItem = new MenuItem("Exit");
		exitMenuItem.setOnAction(event -> {
			Platform.exit();
		});
		
		// Add exit menu item
		fileMenu.getItems().add(exitMenuItem);
		
		// Help menu.
		Menu aboutMenu = new Menu("Help");
		
		// About menu item.
		MenuItem aboutMenuItem = new MenuItem("About");
		aboutMenuItem.setOnAction(event -> {
			Alert about = new Alert(AlertType.INFORMATION);
			about.setTitle("About Echo Bot v1.0");
			about.setHeaderText(null);
			about.setContentText("Echo Bot is a proof-of-concept bot for the Fun Run 2 game."
					+ " It is designed to play a custom friend game with you, and simply"
					+ " echo your movements. This version (free) does not allow you to play"
					+ " quick races with the bot.\n\n"
					+ "For questions/concerns/bug reports please send an email to "
					+ "enlightenedprogramming@gmail.com.");
			about.showAndWait();
		});
		
		// Instructions menu item.
		MenuItem instructionsMenuItem = new MenuItem("Instructions");
		instructionsMenuItem.setOnAction(event -> {
			Alert instructions = new Alert(AlertType.INFORMATION);
			instructions.setTitle("Instructions");
			instructions.setHeaderText(null);
			instructions.setContentText("1. Make a new account for the bot - set an email address and password.\n"
					+ "2. Add your main account as a friend of the bot.\n"
					+ "3. Log in to that account by entering it's email address and password here.\n"
					+ "4. Start up the game on your phone and log into your main account.\n"
					+ "5. Select yourself (or anyone else) as the person the bot should accept requests from.");
			instructions.showAndWait();
		});
		
		// Add menu items.
		aboutMenu.getItems().addAll(aboutMenuItem, instructionsMenuItem);
		
		// Add about menu.
		menuBar.getMenus().addAll(fileMenu, aboutMenu);
		
		return menuBar;
	}
	
	/**
	 * Initiate shutdown sequence.
	 */
	private void shutdown() {
		for (int i = 0; i < myControllerList.size(); i++) {
			myControllerList.get(i).shutdown();
		}
	}
}
