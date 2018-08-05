package main.core.command;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import main.core.handler.CommandHandler;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Command {
	public List<String>                    commands;
	public String                          name;
	public Class<? extends OptionsDefault> optionsClass;
	public EnumSet<Permissions>            perms;

	public Command(Builder builder) {
		this.commands = builder.commands;
		this.optionsClass = builder.optionsClass;
		this.perms = builder.perms;
	}

	public Class<? extends OptionsDefault> getOptionsClass() {
		return optionsClass;
	}

	public void assertPermissions(CommandArguments args) throws CommandHandler.InvalidPermissionsException {
		if (args.message.getChannel() instanceof IPrivateChannel)
			throw new CommandHandler.InvalidPermissionsException("commands aren't supported in private channels");
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

	public static class Builder {
		public List<String>                    commands     = Lists.newArrayList();
		public String                          name;
		public Class<? extends OptionsDefault> optionsClass = OptionsDefault.class;
		public EnumSet<Permissions>            perms        = EnumSet.noneOf(Permissions.class);

		public Builder(String name) {
			this.name = name;
			commands.add(name.toLowerCase());
		}

		public Builder withCommand(String command) {
			commands.add(command.toLowerCase());
			return this;
		}

		public Builder withOptions(Class<? extends OptionsDefault> clazz) {
			this.optionsClass = clazz;
			return this;
		}

		public Builder withPermissions(EnumSet<Permissions> perms) {
			this.perms = perms;
			return this;
		}

		public Builder withPermission(Permissions perm) {
			perms.add(perm);
			return this;
		}
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
