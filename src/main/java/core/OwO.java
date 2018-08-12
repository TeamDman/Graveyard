package core;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;
import core.factory.ClientFactory;
import core.handler.CommandHandler;
import core.handler.EventHandler;
import core.handler.ModuleHandler;
import core.i18n.Console;
import core.i18n.LocLogger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;

import java.util.Locale;

public class OwO {
	public static final IDiscordClient   client;
	public static final Config           config;
	public static final IMessageConveyor i18n;
	public static final LocLogger        logger;

	static {
		i18n = new MessageConveyor(Locale.CANADA);
		logger = new LocLogger(LoggerFactory.getLogger("OwO"), i18n);
		config = Config.getConfig();
		client = ClientFactory.getClient();
	}

	public static void exit(ExitLevel state) {
		System.exit(state.status);
	}

	public static void main(String[] args) {
		EventHandler.init();
		CommandHandler.init();
		ModuleHandler.init();

		logger.info(Console.INFO_LOADED);
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
