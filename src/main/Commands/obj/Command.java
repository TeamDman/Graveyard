package main.Commands.obj;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import main.Handlers.CommandHandler;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.Permissions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.stream.Collectors;

public abstract class Command {
	public String[]                        commands;
	public String                          name;
	public Class<? extends OptionsDefault> optionsClass;
	public EnumSet<Permissions>            perms;

	public Command(@Nonnull String name, String command, @Nullable Class<? extends OptionsDefault> optionsClass, @Nullable EnumSet<Permissions> perms) {
		this(name, new String[]{command}, optionsClass, perms);
	}

	public Command(@Nonnull String name, @Nonnull String[] commands, @Nullable Class<? extends OptionsDefault> optionsClass, @Nullable EnumSet<Permissions> perms) {
		if (commands.length == 0)
			throw new IllegalArgumentException("Commands must not be empty");
		this.name = name;
		this.commands = commands;
		this.optionsClass = optionsClass == null ? OptionsDefault.class : optionsClass;
		this.perms = perms == null ? EnumSet.noneOf(Permissions.class) : perms;
	}

	public Class<? extends OptionsDefault> getOptionsClass() {
		return optionsClass;
	}

	public void assertPermissions(CommandArguments args) throws CommandHandler.InvalidPermissionsException {
		if (args.message.getChannel() instanceof IPrivateChannel)
			throw new CommandHandler.InvalidPermissionsException("Commands aren't supported in private channels");
		EnumSet<Permissions> has = args.message.getAuthor().getPermissionsForGuild(args.message.getGuild());
		if (!has.containsAll(getPerms())) {
			String msg = "You are missing permissions for this command.\n" + getPerms().stream()
					.filter(p -> !has.contains(p))
					.map(Enum::name)
					.collect(Collectors.joining("\n"));
			throw new CommandHandler.InvalidPermissionsException(msg);
		}
	}

	public EnumSet<Permissions> getPerms() {
		return perms;
	}

	public static class OptionsDefault extends OptionsBase {
		@Option(
				name = "help",
				abbrev = '?',
				help = "Displays command information.",
				defaultValue = "false"
		)
		public boolean help;
	}
}
