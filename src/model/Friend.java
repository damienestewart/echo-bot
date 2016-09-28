package model;

/**
 * A simple friend class to encapsulate
 * each friend that the bot has added.
 * @author Damiene Stewart
 */
public class Friend {
	
	/**
	 * The friend's ID.
	 */
	private String myPlayerID;
	
	/**
	 * The friend's user name.
	 */
	private String myUserName;
	
	/**
	 * Construct a new friend object.
	 * @param thePlayerID friend's ID.
	 * @param theUserName username.
	 */
	public Friend(String thePlayerID, String theUserName) {
		myPlayerID = thePlayerID;
		myUserName = theUserName;
	}

	/**
	 * Return the friend's ID.
	 * @return the friend's ID.
	 */
	public String getPlayerID() {
		return myPlayerID;
	}
	
	/**
	 * Return the user name.
	 * @return the friend's user name.
	 */
	public String getUserName() {
		return myUserName;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(myUserName);
		return sb.toString();
	}
}
