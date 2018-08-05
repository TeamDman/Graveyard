package main.core.command;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.Converter;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParsingException;
import main.core.handler.CommandHandler;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Command {
	private List<String>                    commands;
	private String                          name;
	private Class<? extends OptionsDefault> optionsClass = OptionsDefault.class;
	private EnumSet<Permissions>            perms;
	private String                          schema;

	public Command(Builder builder) {
		this.name = builder.name;
		this.commands = builder.commands;
		this.perms = builder.perms;
	}

	public List<String> getCommands() {
		return commands;
	}

	public String getName() {
		return name;
	}

	public Class<? extends OptionsDefault> getOptionsClass() {
		return optionsClass;
	}

	public void setOptionsClass(Class<? extends OptionsDefault> optionsClass) {
		this.optionsClass = optionsClass;
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


	protected void assertOption(boolean value, String message) throws CommandHandler.InvalidOptionException {
		if (!value)
			throw new CommandHandler.InvalidOptionException(message);
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public static class Builder {
		public List<String>         commands = Lists.newArrayList();
		public String               name;
		public EnumSet<Permissions> perms    = EnumSet.noneOf(Permissions.class);

		public Builder(String name) {
			this.name = name;
			commands.add(name.toLowerCase());
		}

		public Builder withCommand(String command) {
			commands.add(command.toLowerCase());
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

	public static class RequiredStringConverter implements Converter<String> {
		public RequiredStringConverter() {
		}

		@Override
		public String convert(String input) throws OptionsParsingException {
			if (input.isEmpty())
				throw new OptionsParsingException("Required option was not provided!");
			return input;
		}

		@Override
		public String getTypeDescription() {
			return "a string";
		}
	}
}
