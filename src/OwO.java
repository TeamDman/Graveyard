import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;

class OwO {
	static final IDiscordClient client;
	static final Config         config;
	static final Logger         logger;

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
	}


}
