package impl.commands;

import com.google.common.collect.Maps;
import com.google.devtools.common.options.Option;
import core.OwO;
import core.command.*;
import core.handler.EventHandler;
import core.handler.TransientEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RegisterCommand
public class GuessGame extends Command implements IInvocable<GuessGame.Options> {

	public GuessGame() {
		super(new Builder("GuessGame"));
	}

	@Override
	public void invoke(CommandArguments<Options> args) throws Throwable {
		final Pattern                 responsePattern = Pattern.compile(args.options.pattern);
		final HashMap<IUser, Integer> responses       = Maps.newHashMap();
		final AtomicBoolean           listening       = new AtomicBoolean(true);
		EventHandler.addListener(EventHandler.Priority.BOTTOM, MessageReceivedEvent.class, (EventHandler.IListener<MessageReceivedEvent>) event -> {
			if (!listening.get())
				return TransientEvent.ReturnType.UNSUBSCRIBE;

			Matcher m = responsePattern.matcher(event.event.getMessage().getContent());
			if (m.find()) {
				int guess = Integer.valueOf(m.group(1));
				if (guess < args.options.lower || guess > args.options.upper)
					RequestBuffer.request(() -> args.message.getAuthor().getOrCreatePMChannel().sendMessage("Your guess is outside of the valid range (" + args.options.lower + "-" + args.options.upper + " inclusive)"));
				else {
					responses.put(event.event.getAuthor(), guess);
					event.event.getMessage().delete();
				}
			}
			return TransientEvent.ReturnType.DONOTHING;
		});
		Executors.newSingleThreadScheduledExecutor().schedule(() -> {
			OwO.logger.info("Done");
			listening.set(false);
			int          target = new Random().nextInt(args.options.upper + 1 - args.options.lower) + args.options.lower;
			EmbedBuilder embed  = new EmbedBuilder().withTitle("Results:");
			embed.appendDesc("Target: " + target + "\n");
			if (args.options.list) {
				responses.forEach((i, v) -> embed.appendDesc(i.mention() + ": " + v + "\n"));
			}
			responses.values().stream()
					.min(Comparator.comparingInt(i -> Math.abs(target - i)))
					.ifPresentOrElse(
							closest -> embed.appendField("Winners", responses.entrySet().stream()
									.filter(e -> e.getValue().equals(closest))
									.map(e -> e.getKey().mention() + ": " + e.getValue())
									.collect(Collectors.joining("\n")), false),

							() -> embed.appendField("Winners", "No winner chosen.", false)
					);

			RequestBuffer.request(() -> args.message.getChannel().sendMessage(embed.build()));
		}, args.options.timer, TimeUnit.SECONDS);
		RequestBuffer.request(() -> args.message.getChannel().sendMessage("Collecting guesses for the next " + args.options.timer + " seconds."));
	}

	@RegisterOptions
	public static class Options extends OptionsDefault {
		@Option(
				name = "list",
				abbrev = 'g',
				help = "Whether or not to display all users' guesses",
				defaultValue = "false"
		)
		public boolean list;
		@Option(
				name = "lower",
				abbrev = 'l',
				help = "The lower bound of the valid guess range, inclusive",
				defaultValue = "1"
		)
		public int     lower;
		@Option(
				name = "pattern",
				abbrev = 'p',
				help = "The pattern to use when capturing guesses",
				defaultValue = "^(\\d+)$"
		)
		public String  pattern;
		@Option(
				name = "timer",
				abbrev = 't',
				help = "Time in seconds for the bets to be placed",
				defaultValue = "5"
		)
		public int     timer;
		@Option(
				name = "upper",
				abbrev = 'u',
				help = "The upper bound of the valid guess range, inclusive",
				defaultValue = "100"
		)
		public int     upper;


	}
}
