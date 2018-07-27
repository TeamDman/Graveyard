package main.Handlers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.devtools.common.options.OptionsParser;
import eu.infomas.annotation.AnnotationDetector;
import main.Commands.obj.*;
import main.OwO;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandHandler {
	public static final  List<Command> commands       = Lists.newArrayList();
	private static final Pattern       commandPattern = Pattern.compile("OwO\\s+(\\S+)\\s*(.*)");

	public static void registerCommands() {
		try {
			new AnnotationDetector(new AnnotationDetector.TypeReporter() {
				@Override
				public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
					try {
						Class<?> clazz = Class.forName(className);
						Object   inst  = clazz.newInstance();
						if (inst instanceof Command && annotation == RegisterCommand.class) {
							Command         command   = ((Command) inst);
							RegisterCommand registrar = ((RegisterCommand) clazz.getAnnotation(annotation));
							OwO.logger.debug("Found command {} with annotation {}", inst, annotation);
							registerCommand(command, clazz);
						}
					} catch (Throwable e) {
						OwO.logger.warn("Exception registering command from " + className, e);
					}
				}

				@Override
				@SuppressWarnings("unchecked")
				public Class<? extends Annotation>[] annotations() {
					return new Class[]{RegisterCommand.class};
				}

			}).detect("main.Commands");
		} catch (IOException e) {
			OwO.logger.error("Error detecting command register annotation, aborting", e);
			OwO.exit(OwO.ExitLevel.ERROR);
		}

		EventHandler.addListener(MessageReceivedEvent.class, new EventHandler.Listener<MessageReceivedEvent>() {
			@Override
			public TransientEvent.ReturnType handle(TransientEvent<MessageReceivedEvent> event) {
				Matcher m = CommandHandler.commandPattern.matcher(event.event.getMessage().getContent());
				if (m.find())
					CommandHandler.findCommand(m.group(1))
							.ifPresent(c -> CommandHandler.invokeCommand(c, ArgumentBuilder.build(c, event.event.getMessage(), m)));
				return TransientEvent.ReturnType.DONOTHING;
			}
		});
	}

	private static void registerCommand(Command c, Class clazz) {
		if (c.name == null)
			OwO.logger.warn("Command {} is missing a valid name. Skipping", clazz.getName());
		else if (c.commands == null || c.commands.length == 0)
			OwO.logger.warn("Command {} is missing valid commands. Skipping", c.name);
		else
			commands.add(c);
	}

	private static Optional<Command> findCommand(String cmd) {
		return commands.stream()
				.filter(v -> Arrays.asList(v.commands).contains(cmd))
				.findFirst();
	}

	private static void invokeCommand(Command c, CommandArguments args) {
		if (args == null) {
			OwO.logger.warn("Missing arguments object invoking command {}. Skipping", c.name);
			return;
		}
		if (args.options.help) {
			args.message.getChannel().sendMessage(new EmbedBuilder()
					.withTitle(c.name)
					.appendDesc(args.parser.describeOptions(Maps.newHashMap(), OptionsParser.HelpVerbosity.LONG))
					.build());
		} else if (!c.hasPerms(args.message.getAuthor(), args.message.getChannel())) {
			EnumSet<Permissions> has = args.message.getAuthor().getPermissionsForGuild(args.message.getGuild());
			RequestBuffer.request(() ->
					args.message.getChannel().sendMessage("You are missing permissions for this command.\n"
							+ c.getPerms().stream()
							.filter(p -> !has.contains(p))
							.map(Enum::name)
							.collect(Collectors.joining("\n"))));
		} else {
			try {
				if (c instanceof IInvocable)
					((IInvocable) c).invoke(args);
				else
					throw new Exception("Command isn't invocable!");
			} catch (Throwable e) {
				OwO.logger.warn("Error executing command '" + c.name + "'", e);
				args.message.getChannel().sendMessage(new EmbedBuilder()
						.withTitle("Error executing command")
						.appendDesc(e.toString())
						.build()
				);
			}
		}
	}
}