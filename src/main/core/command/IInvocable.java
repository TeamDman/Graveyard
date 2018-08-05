package main.core.command;

public interface IInvocable<T extends Command.OptionsDefault> {
	public void invoke(CommandArguments<T> args) throws Throwable;
}

