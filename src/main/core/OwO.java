package main.core;

import main.core.handler.ClientHandler;
import main.core.handler.CommandHandler;
import main.core.handler.DatabaseHandler;
import main.core.handler.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;

public class OwO {
	public static final IDiscordClient client;
	public static final Config         config;
	public static final Logger         logger;

	static {
		logger = LoggerFactory.getLogger("OwO");
		config = Config.getConfig();
		client = ClientHandler.getClient();

	}

	public static void exit(ExitLevel state) {
		System.exit(state.status);
	}

	public static void main(String[] args) {
		EventHandler.init();
		CommandHandler.registerCommands();
		CommandHandler.registerListener();
		DatabaseHandler.init();
	}

	public enum ExitLevel {
		ERROR(-1),
		SUCCESS(0),
		RESTART(2);
		public final int status;

		ExitLevel(int status) {
			this.status = status;
		}
	}
}
