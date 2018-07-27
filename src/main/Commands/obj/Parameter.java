package main.Commands.obj;

import java.util.function.Predicate;

public enum Parameter {
	TEXT(e -> e instanceof String);

	final Predicate requirement;
	Parameter(Predicate requirement) {
		this.requirement=requirement;
	}
}
