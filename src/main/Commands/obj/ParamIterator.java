package main.Commands.obj;

import java.util.ArrayDeque;
import java.util.Deque;

public class ParamIterator {
	private final Deque<Parameter> params = new ArrayDeque<>();

	<T> T next() {
		return (T) params.pop(); // Bad, but good enough for now.
	}

	void add(Parameter p) {
		params.add(p);
	}
}
