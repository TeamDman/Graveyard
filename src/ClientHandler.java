import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

class ClientHandler {

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
			e.printStackTrace();
			return null;
		}
	}

	static IDiscordClient getClient() {
		if (OwO.client == null) {
			return createClient(OwO.config.get("discord-token"),true);
		}
		return OwO.client;
	}
}
