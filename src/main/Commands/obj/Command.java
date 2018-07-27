package main.Commands.obj;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import sx.blah.discord.handle.obj.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public abstract class Command {
	public String[]                        commands;
	public String                          name;
	public Class<? extends OptionsDefault> optionsClass;
	public EnumSet<Permissions>            perms;

	public Command(@Nonnull String name, @Nonnull String[] commands, @Nullable Class<? extends OptionsDefault> optionsClass, @Nullable EnumSet<Permissions> perms) {
		if (commands.length == 0)
			throw new IllegalArgumentException("Commands must not be empty");
		this.name = name;
		this.commands = commands;
		this.optionsClass = optionsClass == null ? OptionsDefault.class : optionsClass;
		this.perms = perms == null ? EnumSet.noneOf(Permissions.class) : perms;
	}

	public Command(String name, String command, @Nullable Class<? extends OptionsDefault> optionsClass, @Nullable EnumSet<Permissions> perms) {
		this(name, new String[]{command}, optionsClass, perms);
	}

	public Class<? extends OptionsDefault> getOptionsClass() {
		return optionsClass;
	}

	public boolean hasPerms(IUser user, IChannel channel) {
		if (channel instanceof IPrivateChannel)
			return false;
		return user.getPermissionsForGuild(channel.getGuild()).containsAll(getPerms());
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
