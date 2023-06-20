package com.zenyte;

import com.zenyte.network.NetworkBootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Server {

	/**
	 * The port to listen on.
	 */
	public static int PORT;

	/**
	 * Logger instance.
	 */
	private static final Logger logger = LogManager.getLogger(Server.class);

	/**
	 * Binds the server to the specified port.
	 *
	 * @param port
	 *            The port to bind to.
	 * @return The server instance, for chaining.
	 * @throws IOException
	 */
	public static void bind(final int port) throws IOException {
		//logger.info("Binding to port: " + port + "...");

		NetworkBootstrap.bind(port);
		//GameBootstrap.bind();
	}

	/**
	 * Starts the <code>GameEngine</code>.
	 *
	 * @throws ExecutionException
	 *             if an error occured during background loading.
	 */
	public static void start() throws ExecutionException {
		logger.info("Ready");
	}

}
