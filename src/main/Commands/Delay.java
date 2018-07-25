package main.Commands;

import com.google.common.collect.Sets;
import com.google.devtools.common.options.Option;
import javafx.util.Pair;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import main.Handlers.EventHandler;
import main.Handlers.TransientEvent;
import main.OwO;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@RegisterCommand()
public class Delay extends Command {
	private static Set<Pair<IUser, Integer>> waitingForNext = Sets.newConcurrentHashSet();

	public Delay() {
		super("Delay", "delay", Options.class, null);
		EventHandler.addListener(MessageReceivedEvent.class, new EventHandler.Listener<MessageReceivedEvent>() {
			@Override
			public TransientEvent.ReturnType handle(TransientEvent<MessageReceivedEvent> event) {
				if (Delay.popUserIfWaiting(event.event)) {
					event.setCanceled(true);
					return TransientEvent.ReturnType.UNSUBSCRIBE;
				}
				return TransientEvent.ReturnType.DONOTHING;
			}
		});
	}

	public static boolean popUserIfWaiting(MessageReceivedEvent event) {
		for (Pair<IUser, Integer> pair : waitingForNext)
			if (pair.getKey().equals(event.getAuthor())) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						RequestBuffer.request(() -> event.getMessage().removeReaction(OwO.client.getOurUser(), ReactionEmoji.of("⏱")));
						EventHandler.onMessage.handle(event);
					}
				}, pair.getValue());
				RequestBuffer.request(() -> event.getMessage().addReaction(ReactionEmoji.of("⏱")));
				waitingForNext.remove(pair);
				return true;
			}
		return false;
	}

	@SuppressWarnings("unused")
	public void invoke(CommandArgument<Options> args) {
		Pair<IUser, Integer> pair = new Pair<>(args.message.getAuthor(), args.options.delay);
		waitingForNext.add(pair);
		args.message.getChannel().sendMessage("Your next message's evaluation will be delayed by " + args.options.delay + "ms.");
	}

	public static class Options extends Command.OptionsDefault {
		@Option(
				name = "Time",
				abbrev = 't',
				help = "Time in ms that the next message will be delayed",
				defaultValue = "1000"
		)
		public int delay;
	}
}
