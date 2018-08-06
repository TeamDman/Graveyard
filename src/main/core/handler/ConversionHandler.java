package main.core.handler;

import com.google.devtools.common.options.Converter;
import main.core.OwO;
import sx.blah.discord.handle.obj.IChannel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversionHandler {
	public static class ChannelConverter implements Converter<IChannel> {
		final Pattern channelPattern = Pattern.compile("(\\d+)");

		@Override
		public IChannel convert(String input) {
			Matcher m = channelPattern.matcher(input);
			return m.find() ? OwO.client.getChannelByID(Long.valueOf(m.group(1))) : null;
		}

		@Override
		public String getTypeDescription() {
			return "a channel";
		}
	}
}
