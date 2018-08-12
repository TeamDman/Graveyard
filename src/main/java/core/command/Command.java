package core.command;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import core.handler.CommandHandler;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public abstract class Command {
	public final  List<BiPredicate<CommandArguments, EnumSet<Permissions>>> requirements;
	private final List<String>                                              commands;
	private final String                                                    name;
	private       Class<? extends OptionsDefault>                           optionsClass = OptionsDefault.class;
	private final EnumSet<Permissions>                                      perms;
	private       String                                                    schema;

	public Command(Builder builder) {
		this.name = builder.name;
		this.commands = builder.commands;
		this.requirements = builder.requirements;
		this.perms = builder.perms;
		this.schema = builder.schema;
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
			final String msg = "You are missing permissions for this command.\n" + getPerms().stream()
					.filter(p -> !has.contains(p))
					.map(Enum::name)
					.collect(Collectors.joining("\n"));
			throw new CommandHandler.InvalidPermissionsException(msg);
		}
		if (requirements.stream().anyMatch(x -> x.negate().test(args, has))) {
			final String msg = "You do not satisfy the requirement to use this command with these arguments.";
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
		public final List<String>                                              commands     = Lists.newArrayList();
		public final String                                                    name;
		EnumSet<Permissions>                                      perms        = EnumSet.noneOf(Permissions.class);
		final List<BiPredicate<CommandArguments, EnumSet<Permissions>>> requirements = Lists.newArrayList();
		String                                                    schema       = "";

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

		public Builder withRequirement(BiPredicate<CommandArguments, EnumSet<Permissions>> check) {
			requirements.add(check);
			return this;
		}

		public Builder withSchema(String schema) {
			this.schema = schema;
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
				help = "Displays command usage details.",
				defaultValue = "false"
		)
		public boolean help;
	}
}
