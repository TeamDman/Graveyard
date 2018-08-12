package impl.commands;

import com.google.devtools.common.options.Option;
import core.command.*;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.Date;

@RegisterCommand
public class IdleRPG extends Command implements IInvocable<IdleRPG.Options> {
	public IdleRPG() {
		super(new Builder("IdleRPG")
				.withRequirement((a, p) -> ((Options) (a.options)).nextlevel != -1 || p.contains(Permissions.ADMINISTRATOR)));
	}

	@Override
	public void invoke(CommandArguments<Options> args) throws Throwable {
		impl.module.IdleRPG.User user = impl.module.IdleRPG.getUser(args.message.getAuthor());
		if (args.options.nextlevel != -1) {
			user.levelup = (new Date().getTime()/1000)+args.options.nextlevel;
			impl.module.IdleRPG.setUser(user);
			RequestBuffer.request(() -> args.message.getChannel().sendMessage("Your next levelup has been set for " + args.options.nextlevel + " seconds in the future."));
		} else
			RequestBuffer.request(() -> args.message.getChannel().sendMessage(new EmbedBuilder()
					.withTitle("IdleRPG")
					.withAuthorIcon(args.message.getAuthor().getAvatarURL())
					.withAuthorName(args.message.getAuthor().getName())
					.appendDesc("Level: " + user.level)
					.appendDesc("\nNext Levelup: " + impl.module.IdleRPG.deltaLevelup(user.levelup) + " seconds")
					.build()));
	}

	@RegisterOptions
	public static class Options extends OptionsDefault {
		@Option(
				name = "nextlevel",
				abbrev = 'x',
				help = "Cheat, time in seconds to set your next levelup",
				defaultValue = "-1"
		)
		public int nextlevel;
	}
}
