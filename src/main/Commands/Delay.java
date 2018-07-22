package main.Commands;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.Option;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import main.OwO;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IIDLinkedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

@RegisterCommand(name = "Delay", cmds = {"delay"})
public class Delay extends Command {
	@SuppressWarnings("unused")
	public void invoke(CommandArgument<Options> args) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				OwO.client.getDispatcher().dispatch(new MessageReceivedEvent(
						new Message(
								args.message.getClient(),
								args.message.getLongID(),
								String.join(" ", args.parser.getResidue()),
								args.message.getAuthor(),
								args.message.getChannel(),
								args.message.getTimestamp(),
								args.message.getEditedTimestamp().orElseGet(()->null),
								args.message.mentionsEveryone(),
								new ArrayList<>(args.message.getMentions().stream().map(IIDLinkedObject::getLongID).collect(Collectors.toList())),
								new ArrayList<>(args.message.getRoleMentions().stream().map(IIDLinkedObject::getLongID).collect(Collectors.toList())),
								args.message.getAttachments(),
								args.message.isPinned(),
								convert(args.message.getEmbeds()),
								args.message.getReactions(),
								args.message.getWebhookLongID(),
								args.message.getType()
						)
				));
			}
		}, args.options.delay);
	}

	@Override
	public Class<? extends OptionsDefault> getOptions() {
		return Options.class;
	}

	private ArrayList<Embed> convert(List<IEmbed> list) {
		ArrayList<Embed> rtn = Lists.newArrayList();
		for (IEmbed e : list) {
			rtn.add((Embed) e);
		}
		return rtn;
	}

	public static class Options extends Command.OptionsDefault {
		@Option(
				name = "Delay",
				abbrev = 'd',
				help = "Delays the execution of another command",
				defaultValue = "1000"
		)
		public int delay;
	}
}
