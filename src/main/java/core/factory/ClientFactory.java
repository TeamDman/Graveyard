package core.factory;

import core.OwO;
import core.Config;
import core.i18n.Console;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class ClientFactory {
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
			OwO.logger.error(Console.ERROR_EXCEPTION_CONFIG_MISSINGTOKEN,e);
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
