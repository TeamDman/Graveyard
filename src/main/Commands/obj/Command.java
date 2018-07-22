package main.Commands.obj;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;

public abstract class Command {

	public String               name;
	public String[]             commands;

	public Class<? extends OptionsDefault> getOptions() {
		return OptionsDefault.class;
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

	public EnumSet<Permissions> getPerms() {
		return EnumSet.noneOf(Permissions.class);
	}

	public boolean hasPerms(IUser user, IGuild guild) {
		if (!user.getPermissionsForGuild(guild).containsAll(getPerms()))
			return false;
		return true;
	}
}
