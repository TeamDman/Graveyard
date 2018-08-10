package impl.commands;

import com.google.common.collect.Sets;
import com.google.devtools.common.options.Option;
import core.OwO;
import core.command.*;
import core.handler.EventHandler;
import core.handler.TransientEvent;
import core.listener.ReactionListener;
import javafx.util.Pair;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.Set;

@RegisterCommand
public class Repeat extends Command implements IInvocable<Repeat.Options> {
	private static final Set<Pair<IUser, Integer>> waitingForNext = Sets.newConcurrentHashSet();

	public Repeat() {
		super(new Builder("Repeat"));
	}

	@Override
	public void invoke(CommandArguments<Options> args) throws Throwable {
		final BooleanContainer interrupt = new BooleanContainer();
		EventHandler.addListener(MessageReceivedEvent.class, new EventHandler.IListener<MessageReceivedEvent>() {
			@Override
			public TransientEvent.ReturnType handle(TransientEvent<MessageReceivedEvent> event) {
				if (event.event.getAuthor().equals(args.message.getAuthor())) {
					new Thread() {
						@Override
						public void run() {
							try {
								for (int i = 0; i < args.options.iterations; i++) {
									if (interrupt.stop)
										break;
									EventHandler.onMessage.handle(event.event);
									Thread.sleep(args.options.time);
								}
							} catch (InterruptedException e) {
								OwO.logger.warn("An exception occurred while repeating", e);
								RequestBuffer.request(() -> args.message.getChannel().sendMessage("An exception occurred while repeating."));
							}
						}
					}.start();
					event.setCanceled(true);
					return TransientEvent.ReturnType.UNSUBSCRIBE;
				}
				return TransientEvent.ReturnType.DONOTHING;
			}
		});
		RequestBuffer.request(() -> {
			IMessage message = args.message.getChannel().sendMessage("Your next message will be repeated " + args.options.iterations + " times, with " + args.options.time + "ms intervals.\nReact with the stopwatch to cancel.");
			RequestBuffer.request(() -> message.addReaction(ReactionEmoji.of("⏱")));
			new ReactionListener(message, (event) -> {
				if (event.getUser().equals(args.message.getAuthor())) {
					interrupt.stop = true;
					RequestBuffer.request(message::removeAllReactions);
					RequestBuffer.request(() -> message.getChannel().sendMessage("Repeat canceled."));
					return TransientEvent.ReturnType.UNSUBSCRIBE;
				}
				return TransientEvent.ReturnType.DONOTHING;
			}, null, null);
		});
	}

	@RegisterOptions
	public static class Options extends OptionsDefault {
		@Option(
				name = "iterations",
				abbrev = 'i',
				help = "Number of times to repeat",
				defaultValue = "3"
		)
		public int iterations;

		@Option(
				name = "delay",
				abbrev = 't',
				help = "Time between each execution in ms",
				defaultValue = "500"
		)
		public int time;
	}

	private final class BooleanContainer {
		boolean stop = false;
	}
}
