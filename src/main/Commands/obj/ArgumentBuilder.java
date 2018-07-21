package main.Commands.obj;

import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayDeque;
import java.util.Deque;

public class ArgumentBuilder {
	public static  CommandArgument build(Command c, IMessage m) {
		ParamVisitor visitor = new ParamVisitor();
		c.collectParameters(visitor::visit);

		return new CommandArgument(m, visitor.getParams());
	}

	private static class ParamVisitor {
		Deque<Parameter> params = new ArrayDeque<>();

		void visit(Parameter p) {
//			if (p.)
		}

		ParamIterator getParams() {
			return null; // TODO;
		}
	}
}
