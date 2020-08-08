package AST;

import lib.Value;

public interface Expression extends Node{
	Value eval() throws Exception;
}
