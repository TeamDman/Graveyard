package main.Handlers;

import com.google.common.collect.Maps;
import com.google.devtools.common.options.OptionsParser;
import eu.infomas.annotation.AnnotationDetector;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import main.OwO;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandHandler {
	public static final ArrayList<Command> commands = new ArrayList<>();

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
							command.name = registrar.name();
							command.commands = registrar.cmds();
							registerCommand((Command) inst);
						}
					} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
						OwO.logger.warn("Exception registering command from {}", className);
						e.printStackTrace();
					}
				}

				@Override
				@SuppressWarnings("unchecked")
				public Class<? extends Annotation>[] annotations() {
					return new Class[]{RegisterCommand.class};
				}

			}).detect("main.Commands");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void registerCommand(Command c) {
		commands.add(c);
	}

	public static Optional<Command> findCommand(String cmd) {
		return commands.stream()
				.filter(v -> Arrays.stream(v.commands).anyMatch(cmd::equals))
				.findFirst();
	}

	public static void invokeCommand(Command c, CommandArgument args) {
		if (args == null) {
			OwO.logger.warn("Missing arguments object invoking command {}. Skipping.", c.name);
			return;
		}
		if (args.options.help) {
			args.message.getChannel().sendMessage(new EmbedBuilder()
					.withTitle(c.name)
					.appendDesc(args.parser.describeOptions(Maps.newHashMap(), OptionsParser.HelpVerbosity.LONG))
					.build());
		} else if (!c.hasPerms(args.message.getAuthor(), args.message.getGuild())) {
			EnumSet<Permissions> has = args.message.getAuthor().getPermissionsForGuild(args.message.getGuild());
			RequestBuffer.request(() ->
					args.message.getChannel().sendMessage("You are missing permissions for this command.\n"
							+ c.getPerms().stream()
							.filter(p -> !has.contains(p))
							.map(Enum::name)
							.collect(Collectors.joining("\n"))));
		} else {
			try {
				c.getClass().getDeclaredMethod("invoke", args.getClass()).invoke(c, args);
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				OwO.logger.error("Error executing command {}", c);
				e.printStackTrace();
				args.message.getChannel().sendMessage(new EmbedBuilder()
						.withTitle("Error executing command")
						.appendDesc(e.toString())
						.build()
				);
			}
		}
	}
}