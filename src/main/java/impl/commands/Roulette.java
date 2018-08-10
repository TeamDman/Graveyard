package impl.commands;

import com.google.devtools.common.options.Option;
import core.command.*;
import sx.blah.discord.util.RequestBuffer;

import java.util.Random;

@RegisterCommand
public class Roulette extends Command implements IInvocable<Roulette.Options> {
	public Roulette() {
		super(new Builder("Roulette"));
	}

	@Override
	public void invoke(CommandArguments<Options> args) throws Throwable {
		boolean dead = new Random().nextInt(args.options.chance) == 0;
		RequestBuffer.request(() -> {
			if (dead) {
				args.message.getChannel().sendMessage("Bang!");
			} else {
				args.message.getChannel().sendMessage("Click...");
			}
		});

	}

	@RegisterOptions
	public static class Options extends OptionsDefault {
		@Option(
				name = "chambers",
				abbrev = 'c',
				help = "The number of chambers in the revolver",
				defaultValue = "6"
		)
		public int chance;

	}
}
