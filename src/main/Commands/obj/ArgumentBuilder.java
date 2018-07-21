package main.Commands.obj;

import com.google.devtools.common.options.OptionsParser;
import sx.blah.discord.handle.obj.IMessage;

import java.util.regex.Matcher;

public class ArgumentBuilder {
	public static CommandArgument build(Command cmd, IMessage msg, Matcher m) {
		OptionsParser parser = OptionsParser.newOptionsParser(cmd.getOptions());
		parser.parseAndExitUponError(m.group(2).split("\\s+"));
		return new CommandArgument(msg, parser.getOptions(cmd.getOptions()));
	}

}
