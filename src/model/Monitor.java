package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Abstract class containing core methods
 * that a data stream monitor must implement.
 * @author Damiene Stewart
 */
public abstract class Monitor implements Runnable {

	/**
	 * The socket to communicate with and monitor.
	 */
	protected Socket mySocket;
	
	/**
	 * The client object.
	 */
	protected Client myClient;
	
	/**
	 * Flag indicating whether or not the run loop should
	 * continue.
	 */
	private boolean myContinue;
	
	/**
	 * Constructs a new Monitor object.
	 * @param theClient the client object.
	 * @param theHost the host to connect the socket to.
	 * @param thePort the port to connect the socket to.
	 */
	public Monitor(Client theClient, String theHost, int thePort) {
		try {
			mySocket = new Socket(theHost, thePort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		myClient = theClient;
		myContinue = true;
	}
	
	/**
	 * Run the thread process.
	 */
	@Override
	public void run() {
		BufferedReader dataSocketInput = null;
		PrintWriter writer = getWriter();
		
		try {
			dataSocketInput = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		while(myContinue && !mySocket.isClosed()) {
			try {
				Object data = readSocketData(dataSocketInput);
				
				if (data == null) {
					break;
				}
				
				processSocketData(data, writer);
			} catch (IllegalStateException e) {
				break;
			}
		}
	}
	
	/**
	 * Signal that the data monitor/processing should stop.
	 */
	public void stopMonitor() {
		myContinue = false;
		try {
			mySocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Process the data being read from the socket.
	 * @param theData the the data being processed.
	 * @param theWriter the writer to send the response with.
	 */
	protected abstract void processSocketData(Object theData, PrintWriter theWriter);
	
	/**
	 * Read the socket data.
	 * @return socket data as an Object. Need to recast.
	 * @throws IllegalStateException if no data received.
	 */
	protected Object readSocketData(BufferedReader theInput) {
		String data = null;
		try {
			data = theInput.readLine();
		} catch (IOException e) {
			return null;
		}
		
		if (data == null)
			throw new IllegalStateException("No data retrieved from data socket.");
		
		if (data.charAt(0) == '{') {
			return new JSONObject(data);
		} else {
			return new JSONArray(data);
		}
	}
	
	/**
	 * Get the writer to write data to the socket.
	 * @return PrintWriter to write data.
	 */
	protected PrintWriter getWriter() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(mySocket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return writer;
	}
	
	/**
	 * Return the socket being monitored.
	 * @return the socket.
	 */
	protected Socket getSocket() {
		return mySocket;
	}
}
