package core;

import core.handler.ClientHandler;
import core.handler.CommandHandler;
import core.handler.DatabaseHandler;
import core.handler.EventHandler;
import impl.handler.IdleRPGHandler;
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
		CommandHandler.init();
		DatabaseHandler.init();
		IdleRPGHandler.init();
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
