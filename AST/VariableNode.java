package AST;

import lib.*;
import test.Token;

public class VariableNode implements Expression {
	public Token token;
	public VariableNode(Token Rvalue) {
		this.token = Rvalue;
	}
	@Override
	public Value eval() throws Exception {
		if(!Variables.variables.containsKey(this.token.value)){ throw new Exception("Variable "+this.token.value+" doesn't exist."); }
		return Variables.variables.get(this.token.value);
	} public String toString(){
		return String.valueOf(this.token);
	}
}
