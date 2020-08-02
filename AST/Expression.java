package AST;

import lib.Value;

public interface Expression {
	Value eval() throws Exception;
}
