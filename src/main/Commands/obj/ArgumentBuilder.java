package main.Commands.obj;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.OptionsParser;
import com.google.devtools.common.options.OptionsParsingException;
import main.OwO;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentBuilder {
	@SuppressWarnings("unchecked")
	public static CommandArguments build(Command cmd, IMessage msg, Matcher m) {
		try {
			OptionsParser parser = OptionsParser.newOptionsParser(cmd.getOptionsClass());
			parser.setAllowResidue(true);
			parser.parse(preprocess(m.group(2)));
			return new CommandArguments(cmd, msg, parser.getOptions(cmd.getOptionsClass()), parser);
		} catch (OptionsParsingException exOpt) {
			msg.getChannel().sendMessage(new EmbedBuilder(){{
				appendField("Command Option Error", exOpt.getLocalizedMessage(), true);
			}}.build());
		} catch (Throwable e) {
			OwO.logger.error("Error encountered invoking command " + cmd.name, e);
		}
		return null;
	}

	private static List<String> preprocess(String in) {
		List<String> matchList = Lists.newArrayList();
		Pattern regex = Pattern.compile("\"([^\"]*)\"|'([^']*)'|[^\\s]+");//("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
		Matcher regexMatcher = regex.matcher(in);
		while (regexMatcher.find()) {
			if (regexMatcher.group(1) != null) {
				// Add double-quoted string without the quotes
				matchList.add(regexMatcher.group(1));
			} else if (regexMatcher.group(2) != null) {
				// Add single-quoted string without the quotes
				matchList.add(regexMatcher.group(2));
			} else {
				// Add unquoted word
				matchList.add(regexMatcher.group());
			}
		}
		return matchList;
	}

	@SuppressWarnings("unchecked")
	public static CommandArguments buildEmpty(Command cmd) {
		return new CommandArguments(cmd, null, null, null);
	}

}
