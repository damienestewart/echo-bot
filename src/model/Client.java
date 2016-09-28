package model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import controller.Controller;

/**
 * The Client class that interacts with the server
 * and does the majority of the heavy lifting for the
 * rest of the application.
 * @author Damiene Stewart
 */
public class Client {
	
	/**
	 * The bot's ID.
	 */
	private String myID;
	
	/**
	 * The bot's login token.
	 */
	private String myToken;
	
	/**
	 * Stores a string describing any login
	 * errors that may have occurred.
	 */
	private String myLoginErrorMessage;
	
	/**
	 * The lobby status.
	 */
	private boolean myLobbyStatus;
	
	/**
	 * The online status of the bot.
	 */
	private int myStatus;
	
	/**
	 * The friend to accept requests from.
	 */
	private Friend myFriend;
	
	/**
	 * The friend list.
	 */
	private ArrayList<Friend> myFriendList;
	
	/**
	 * The controller for this model.
	 */
	private Controller myController;
	
	/**
	 * The data monitoring runnable task..
	 */
	private Runnable myDataMonitor;

	/**
	 * The game lobby monitoring task.
	 */
	private Runnable myGameLobbyMonitor;
	
	/**
	 * Constructs a new client object.
	 */
	public Client(Controller theController) {
		myID = myToken = "Not present";
		myStatus = 3;
		myLobbyStatus = false;
		myLoginErrorMessage = "";
		myFriend = null;
		myDataMonitor = new DataMonitor(this);
		myFriendList = new ArrayList<Friend>();
		myController = theController;
	}
	
	/**
	 * Login and retrieve the bot's ID and Login token as
	 * these are necessary for subsequent interactions with 
	 * server.
	 * @param theEmailAddress the bot's email address.
	 * @param thePassword the bot's password.
	 * @return true if login was successful.
	 */
	public boolean login(String theEmail, String thePassword) {
		URL host = null;
		HttpsURLConnection con = null;
		
		try {
			host = new URL(Config.GAME_HTTPS_ADDRESS);
			con = (HttpsURLConnection) host.openConnection();
			
			con = configureConnection(con);
			
			sendLoginRequest(new DataOutputStream(con.getOutputStream()), 
					configureLoginJSONData(theEmail, thePassword));
			
			JSONObject responseData = readLoginResponse(con.getInputStream());
			
			// TODO Extend to incorporate error messages from server.
			if (responseData.getInt("m") == 3) {
				if (responseData.has("a") && responseData.has("p")) {
					myID = responseData.getString("p");
					myToken = responseData.getString("a");
					return true;
				} else {
					myLoginErrorMessage = "Bad email:password (" + theEmail + ":" + thePassword +").";
				}
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Start the data monitor task.
	 */
	public void startDataMonitor() {
		// Start the data monitor.
		((DataMonitor) myDataMonitor).startPinger();
		(new Thread(myDataMonitor)).start();
	}
	
	/**
	 * Return the login error message. Clears the message
	 * afterwards.
	 * @return A message about a recent login failure.
	 */
	public String getLoginErrorMessage() {
		return myLoginErrorMessage;
	}
	
	/**
	 * Construct the friend list from the incoming JSON
	 * data.
	 * @param theData the incoming data.
	 */
	public void createFriendList(JSONArray theData) {
		for(int i = 0; i < theData.length(); i++) {
			JSONObject friend = theData.getJSONObject(i);
			Friend f = new Friend(friend.getString("p"), friend.getString("n"));
			myFriendList.add(f);
		}
		
		myController.setLoggedIn(true);
	}
	
	/**
	 * Retrieves the bot's id.
	 * @return the bot's id.
	 */
	public String getID() {
		return myID;
	}
	
	/**
	 * Retrieves the bot's login token.
	 * @return the bot's login token.
	 */
	public String getToken() {
		return myToken;
	}
	
	/**
	 * Get the bot's lobby status. Returns true if
	 * the bot is currently in a custom game lobby,
	 * false otherwise.
	 * @return true if the bot is in a custom game lobby.
	 */
	public boolean getLobbyStatus() {
		return myLobbyStatus;
	}
	
	/**
	 * Return the bot's friend list.
	 * @return the friend list.
	 */
	public List<Friend> getFriendList() {
		return myFriendList;
	}
	
	/**
	 * Returns the friend of the bot. Can be null.
	 * @return the bot's friend.
	 */
	public Friend getFriend() {
		return myFriend;
	}
	
	/**
	 * Returns the bot's online status.
	 * @return the status of the bot.
	 */
	public int getStatus() {
		return myStatus;
	}
	
	/**
	 * Set the current friend that whose game invites
	 * the bot should acknowledge.
	 * @param theFriend whose requests should be accepted.
	 */
	public void setMyFriend(Friend theFriend) {
		if (myGameLobbyMonitor != null) {
			((Monitor) myGameLobbyMonitor).stopMonitor();
			myLobbyStatus = false;
		}
		myFriend = theFriend;
	}
	
	/**
	 * Set the bot's status.
	 * @param theStatus the bot's current status.
	 */
	public void setStatus(int theStatus) {
		if (theStatus < 1 || theStatus > 3)
			throw new IllegalArgumentException("The status: " + theStatus + " is invalid.");
		myStatus = theStatus;
		((DataMonitor) myDataMonitor).sendStatusUpdate(theStatus);
	}
	
	/**
	 * Join a custom race request. This means that the
	 * bot will enter a custom game lobby.
	 */
	public void joinCustomRace(String theIP, String thePlayerID, int theToken) {
		if (myFriend != null && myFriend.getPlayerID().equals(thePlayerID) && myLobbyStatus == false) {
			myGameLobbyMonitor = new GameLobbyMonitor(this, theIP, theToken);
			(new Thread(myGameLobbyMonitor)).start();
			myLobbyStatus = true;
		}
	}
	
	/**
	 * Toggle the lobby status of the bot.
	 */
	public void toggleLobbyStatus() {
		myLobbyStatus = myLobbyStatus ^ true;
	}
	
	/**
	 * Initiate shutdown sequence.
	 */
	public void shutdown() {
		if (myDataMonitor != null) {
			((DataMonitor) myDataMonitor).stopDataMonitor();
		}
		
		if (myGameLobbyMonitor != null) {
			((Monitor) myGameLobbyMonitor).stopMonitor();
		}
	}
	
	/**
	 * Configure the HTTPS connection.
	 * @param theConnection the HTTPS connection.
	 */
	private HttpsURLConnection configureConnection(HttpsURLConnection theConnection) {
		// Set method.
		try {
			theConnection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		// Set headers.
		theConnection.setRequestProperty("Host", "minttuentrypoint.dirtybit.no:6389");
		theConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		theConnection.setRequestProperty("Connection", "keep-alive");
		theConnection.setRequestProperty("Accept", "*/*");
		theConnection.setRequestProperty("User-Agent", "Fun%20Run%202/47 CFNetwork/711.4.6 Darwin/14.0.0");
		theConnection.setRequestProperty("Accept-Language", "en-us");
		theConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		
		// Set other options.
		theConnection.setUseCaches(false);
		theConnection.setDoOutput(true);
		theConnection.setDoInput(true);
		
		return theConnection;
	}
	
	/**
	 * Returns the JSON version of the login information to send
	 * to the server.
	 * @param theEmail the bot's email address.
	 * @param thePassword the bot's password.
	 * @return the JSON version of the login information.
	 */
	private JSONObject configureLoginJSONData(String theEmail, String thePassword) {
		JSONObject data = new JSONObject();
		data.put("e", theEmail)
			.put("d", "")
			.put("w", thePassword)
			.put("m", 3);
		
		return data;
	}
	
	/**
	 * Read the login response data from the server.
	 * @param theInputStream the input stream to read data from.
	 * @return the read data.
	 */
	private JSONObject readLoginResponse(InputStream theInputStream) {
		GZIPInputStream gzipped = null;
		BufferedReader reader = null;
		String line = null;
		StringBuffer response = new StringBuffer();
		
		try {
			gzipped = new GZIPInputStream(theInputStream);
			reader = new BufferedReader(new InputStreamReader(gzipped));
			
			while((line = reader.readLine()) != null) {
				response.append(line);
			}
			
			reader.close();
			
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		JSONObject responseData = new JSONObject(response.toString());
		
		return responseData;
	}
	
	/**
	 * Send the login request to the server.
	 * @param theOutputStream the output stream to write to.
	 * @param data the request data to be written.
	 */
	private void sendLoginRequest(DataOutputStream theOutputStream, JSONObject data) {
		try {
			theOutputStream.writeBytes(data.toString());
			theOutputStream.flush();
			theOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
