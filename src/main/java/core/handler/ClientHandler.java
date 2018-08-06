package core.handler;

import core.OwO;
import core.Config;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class ClientHandler {

	private static IDiscordClient createClient(String token, boolean login) {
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(token);
		try {
			if (login) {
				return clientBuilder.login();
			} else {
				return clientBuilder.build();
			}
		} catch (DiscordException e) {
			OwO.logger.error("Discord token not present. Check OwO-Bot.properties for valid token",e);
			OwO.exit(OwO.ExitLevel.ERROR);
			return null;
		}
	}

	public static IDiscordClient getClient() {
		if (OwO.client == null) {
			return createClient(OwO.config.get(Config.OwOProperty.DISCORD_TOKEN),true);
		}
		return OwO.client;
	}
}
