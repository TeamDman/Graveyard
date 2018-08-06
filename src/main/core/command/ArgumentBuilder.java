package main.core.command;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.OptionsParser;
import main.core.handler.CommandHandler;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentBuilder {
	@SuppressWarnings("unchecked")
	public static CommandArguments build(Command cmd, IMessage msg, String body) throws Throwable {
		OptionsParser parser = OptionsParser.newOptionsParser(cmd.getOptionsClass());
		parser.setAllowResidue(false);
		parser.parse(preprocess(body, cmd.getSchema()));
		return new CommandArguments(cmd, msg, parser.getOptions(cmd.getOptionsClass()), parser);
	}

	private static List<String> preprocess(String in, String schema) throws CommandHandler.InvalidOptionException {
		List<String> matchList    = Lists.newArrayList();
		Pattern      regex        = Pattern.compile("\"([^\"]*)\"|'([^']*)'|[^\\s]+");//("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
		Matcher      regexMatcher = regex.matcher(in);
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
		if (!schema.isEmpty() && matchList.stream().noneMatch(s -> s.startsWith("-"))) {
			if (matchList.size() < StringUtils.countMatches(schema, "$"))
				throw new CommandHandler.InvalidOptionException("Not enough arguments to satisfy the schema.");
			ArrayList<String> schemaList = Lists.newArrayList(schema.split("\\s+"));
			Iterator<String>  iter       = matchList.iterator();
			schemaList.replaceAll(s -> {
				switch (s) {
					case "$":
						return iter.next();
					case "...":
						StringBuilder b = new StringBuilder();
						iter.forEachRemaining(b::append);
						return b.toString();
					default:
						return s;
				}
			});
			return schemaList;
		}
		return matchList;
	}

	@SuppressWarnings("unchecked")
	public static CommandArguments buildEmpty(Command cmd) {
		return new CommandArguments(cmd, null, null, null);
	}

}
