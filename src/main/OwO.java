package main;

import main.Handlers.ClientHandler;
import main.Handlers.CommandHandler;
import main.Handlers.EventHandler;
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
	}

	public enum ExitLevel {
		ERROR(-1),
		SUCCESS(0),
		RESTART(2);
		public int status;

		ExitLevel(int status) {
			this.status = status;
		}
	}
}
