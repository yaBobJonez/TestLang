package AST;

import exceptions.VariableDoesNotExistException;
import lib.Value;

public interface Accessible {
	Value get() throws VariableDoesNotExistException, Exception;
	Value set(Value value) throws Exception;
}
