package main;

import main.Commands.obj.Command;
import main.Handlers.ClientHandler;
import main.Handlers.CommandHandler;
import main.Handlers.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;

import java.util.ArrayList;

public class OwO {
	public static final IDiscordClient     client;
	public static final Config             config;
	public static final Logger             logger;

	static {
		logger = LoggerFactory.getLogger("OwO");
		config = Config.getGlobalConfig();
		client = ClientHandler.getClient();
	}

	static void exit() {
		System.exit(1);
	}

	public static void main(String[] args) {
		EventHandler.init();
		CommandHandler.registerCommands();
	}
}
