package main.Commands.obj;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.OptionsParser;
import com.google.devtools.common.options.OptionsParsingException;
import main.OwO;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.util.regex.Matcher;

public class ArgumentBuilder {
	@SuppressWarnings("unchecked")
	public static CommandArgument build(Command cmd, IMessage msg, Matcher m) {
		try {
			OptionsParser parser = OptionsParser.newOptionsParser(cmd.getOptions());
			parser.setAllowResidue(true);
			parser.parse(m.group(2).split("\\s+"));
			return new CommandArgument(cmd, msg, parser.getOptions(cmd.getOptions()), parser);
		} catch (OptionsParsingException exOpt) {
			msg.getChannel().sendMessage(new EmbedBuilder(){{
				appendField("Command Option Error", exOpt.getLocalizedMessage(), true);
			}}.build());
		} catch (IllegalArgumentException | IllegalStateException e) {
			OwO.logger.error("Error encountered invoking command {}", cmd.name);
			e.printStackTrace();
		}
		return null;
	}

}
