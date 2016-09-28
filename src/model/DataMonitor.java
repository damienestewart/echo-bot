package model;

import java.io.PrintWriter;

import org.json.JSONObject;

import javafx.application.Platform;

/**
 * Data Monitor - monitor data from socket
 * connection with 'data' server. See Config.
 * @author Damiene Stewart
 */
public class DataMonitor extends Monitor {
	
	/**
	 * Pinger class to send ping to socket.
	 */
	private Pinger myPinger;
	
	/**
	 * The writer.
	 */
	private PrintWriter myWriter;
	
	/**
	 * Thread reference so that the thread
	 * may be interrupted while in sleep();
	 */
	private Thread myPingThread;
	
	/**
	 * Initialize the Data Monitor.
	 * @param theClient the client.
	 */
	public DataMonitor(Client theClient) {
		super(theClient, Config.GAME_HOST_NAME, Config.DATA_SERVER_PORT);	
		myWriter = null;
		myPingThread = null;
	}
	
	/**
	 * Signal that the data monitor/processing should stop.
	 */
	public void stopDataMonitor() {
		stopMonitor();
		
		if (myPinger != null) {
			myPinger.stopPinger();
			if (myPingThread.isAlive()) {
				myPingThread.interrupt();
			}
		}
	}
	
	/**
	 * Send status updates to server.
	 * @param theStatus the status to send.
	 */
	public void sendStatusUpdate(int theStatus) {
		JSONObject ob = new JSONObject()
						.put("m", 45)
						.put("s", theStatus);
		myWriter.println(ob.toString());
	}
	
	/**
	 * Start the pinger task.
	 */
	public void startPinger() {
		setupPinger();
	}
	
	/**
	 * {@inheritDoc}
	 * Process the incoming data from the socket.
	 */
	@Override
	protected void processSocketData(Object theData, PrintWriter theWriter) {
		JSONObject data = (JSONObject) theData;
		myWriter = getWriter();
		int messageType = data.getInt("m");
		
		switch (messageType) {
		case 1:
			Platform.runLater(() -> {
				myClient.createFriendList(data.getJSONArray("f"));
			});
			break;
			
		case 35:
			joinCustomGame(data);
			break;
		
		case 51:
			respondToChallenge(myWriter);
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * Set up pinger.
	 */
	private void setupPinger() {
		myPinger = new Pinger(super.mySocket);
		myPingThread = (new Thread(myPinger));
		myPingThread.start();
	}
	
	/**
	 * Respond to initial challenge to log the user onto
	 * the server.
	 * @param theWriter the data writer.
	 */
	private void respondToChallenge(PrintWriter theWriter) {
		JSONObject reply = new JSONObject()
				.put("a", myClient.getToken())
				.put("p", myClient.getID())
				.put("p", myClient.getID())
				.put("b", 35)
				.put("m", 1)
				.put("v", "2.4");
		theWriter.println(reply.toString());
	}
	
	/**
	 * Let the client know that we received a custom game
	 * request, and that it can join if it so chose.
	 * @param theData the game request data.
	 */
	private void joinCustomGame(JSONObject theData) {
		String ip = theData.getString("a");
		String playerID = theData.getString("p");
		int token = theData.getInt("s");
		
		myClient.joinCustomRace(ip, playerID, token);
	}
}
