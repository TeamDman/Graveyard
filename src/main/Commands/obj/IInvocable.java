package main.Commands.obj;

public interface IInvocable<T extends Command.OptionsDefault> {
	public void invoke(CommandArguments<T> args);
}

