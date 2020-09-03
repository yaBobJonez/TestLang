package AST;

import lib.Value;

public interface Accessible {
	Value get() throws Exception;
	Value set(Value value) throws Exception;
}
