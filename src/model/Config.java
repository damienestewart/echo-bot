package model;

/**
 * Container for configuration variables.
 * @author Damiene Stewart
 */
public class Config {
	
	/**
	 * Version of the game this bot was created for.
	 */
	public final static String GAME_VERSION = "2.4";
	
	/**
	 * Host name for the game.
	 */
	public final static String GAME_HOST_NAME = "minttuentrypoint.dirtybit.no";
	
	/**
	 * HTTPS address for the game.
	 */
	public final static String GAME_HTTPS_ADDRESS = "https://minttuentrypoint.dirtybit.no:6389";
	
	/**
	 * Data server port for sending and receiving configuration data,
	 * and other data such as friends list.
	 */
	public final static int DATA_SERVER_PORT = 6689;
	
	/**
	 * Game server port for sending and recieiving data from game/game lobby.
	 */
	public final static int GAME_SERVER_PORT = 6789;
}
