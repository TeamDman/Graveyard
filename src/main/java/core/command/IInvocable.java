package core.command;

public interface IInvocable<T extends Command.OptionsDefault> {
	void invoke(CommandArguments<T> args) throws Throwable;
}

