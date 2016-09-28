package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

/**
 * Pinger class. In order to keep the connection alive
 * this class will ping the server with {"m":37} and the
 * server will reply with the same.
 * @author Damiene Stewart
 */
public class Pinger implements Runnable {
	
	/**
	 * Data socket.
	 */
	private Socket mySocket;
	
	/**
	 * Toggles whether or not the socket should continue.
	 */
	private boolean myContinue;
	
	/**
	 * Construct a new server pinger object.
	 * @param theDataSocket The data socket connected
	 * to the server.
	 */
	public Pinger(Socket theDataSocket) {
		mySocket = theDataSocket;
		myContinue = true;
	}
	
	@Override
	public void run() {
		JSONObject pingData = new JSONObject()
								.put("m", 37);
		PrintWriter dataSocketOutput = null;
		
		try {
			dataSocketOutput = new PrintWriter(mySocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (myContinue && !mySocket.isClosed()) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				break;
			}
			
			dataSocketOutput.println(pingData.toString());
		}
	}
	
	/**
	 * End the thread's run loop.
	 */
	public void stopPinger() {
		myContinue = false;
		Thread.currentThread().interrupt();
	}
}
