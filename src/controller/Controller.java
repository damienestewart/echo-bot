package controller;

import java.util.List;

import model.Client;
import model.Friend;
import view.EchoBotGUI;

/**
 * Controller class that communicates with
 * both the GUI and the Client.
 * @author Damiene Stewart
 */
public class Controller {
	
	/**
	 * A reference to the GUI class (view).
	 */
	private EchoBotGUI myGUI;
	
	/**
	 * A reference to the Client class (model).
	 */
	private Client myClient;
	
	/**
	 * Logged in status of client.
	 */
	private boolean myClientLoggedInStatus;
	
	/**
	 * Constructs an instance of the controller class.
	 * @param theGUI the GUI reference.
	 */
	public Controller(EchoBotGUI theGUI) {
		myGUI = theGUI;
		myClient = new Client(this);
		myClientLoggedInStatus = false;
	}
	
	/**
	 * Login function initiated from the GUI.
	 * @param theEmail the bot's email.
	 * @param thePassword the bot's password.
	 */
	public void login(String theEmail, String thePassword) {
		if (myClient.login(theEmail, thePassword)) {
			myClientLoggedInStatus = true;
		} else {
			myClientLoggedInStatus = false;
			myGUI.showLoginAlert(myClient.getLoginErrorMessage());
		}
	}
	
	/**
	 * Start the data monitor.
	 */
	public void startDataMonitor() {
		myClient.startDataMonitor();
	}
	
	/**
	 * Returns the client logged in status.
	 * @return true if the client has successfully logged in, false otherwise.
	 */
	public boolean isClientLoggedIn() {
		return myClientLoggedInStatus;
	}
	
	/**
	 * Toggle whether the user has progressed to a logged in stage or not.
	 * @param theLoggedInStatus true if user has successfully logged in, false otherwise.
	 */
	public void setLoggedIn(boolean theLoggedInStatus) {
		if (theLoggedInStatus) {
			myGUI.showMainScene();
		} else {
			// TODO Logged out. Display reason for log out.
		}
	}
	
	/**
	 * Return the friend list from the client (model).
	 * @return the list of the bot's friends.
	 */
	public List<Friend> getFriendList() {
		return myClient.getFriendList();
	}
	
	/**
	 * Set the friend that the bot should listen to
	 * and respond to requests from.
	 * @param theFriend
	 */
	public void setFriend(Friend theFriend) {
		myClient.setMyFriend(theFriend);
	}

	/**
	 * Initiate shutdown procedure.
	 */
	public void shutdown() {
		myClient.shutdown();
	}
}
