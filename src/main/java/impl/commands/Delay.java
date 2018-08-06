package impl.commands;

import com.google.devtools.common.options.Option;
import core.OwO;
import core.command.*;
import core.handler.EventHandler;
import core.handler.TransientEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.util.RequestBuffer;

import java.util.Timer;
import java.util.TimerTask;

@RegisterCommand
public class Delay extends Command implements IInvocable<Delay.Options> {
	public Delay() {
		super(new Builder("Delay"));
	}

	@SuppressWarnings("unused")
	public void invoke(CommandArguments<Options> args) {
		EventHandler.addListener(MessageReceivedEvent.class, new EventHandler.Listener<MessageReceivedEvent>() {
			@Override
			public TransientEvent.ReturnType handle(TransientEvent<MessageReceivedEvent> event) {
				if (event.event.getAuthor().equals(args.message.getAuthor())) {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							RequestBuffer.request(() -> event.event.getMessage().removeReaction(OwO.client.getOurUser(), ReactionEmoji.of("⏱")));
							EventHandler.onMessage.handle(event.event);
						}
					}, args.options.delay);
					RequestBuffer.request(() -> event.event.getMessage().addReaction(ReactionEmoji.of("⏱")));
					event.setCanceled(true);
					return TransientEvent.ReturnType.UNSUBSCRIBE;
				}
				return TransientEvent.ReturnType.DONOTHING;
			}
		});
		RequestBuffer.request(() -> args.message.getChannel().sendMessage("Your next message's evaluation will be delayed by " + args.options.delay + "ms."));
	}

	@CommandOptions
	public static class Options extends Command.OptionsDefault {
		@Option(
				name = "time",
				abbrev = 't',
				help = "Time in ms that the next message will be delayed",
				defaultValue = "1000"
		)
		public int delay;
	}
}
