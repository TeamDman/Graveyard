package main.Commands.obj;

import java.util.function.Predicate;

public enum Parameter {
	TEXT(e -> e instanceof String);

	Predicate requirement;
	Parameter(Predicate requirement) {
		this.requirement=requirement;
	}
}
