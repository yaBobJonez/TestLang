package AST;

import exceptions.VariableDoesNotExistException;
import lib.*;
import test.Token;

public class VariableNode implements Expression {
	public Token token;
	public VariableNode(Token Rvalue) {
		this.token = Rvalue;
	}
	@Override
	public Value eval() throws Exception {
		if(!Variables.variables.containsKey(this.token.value)){ throw new VariableDoesNotExistException(this.token.value); }
		return Variables.variables.get(this.token.value);
	} public String toString(){
		return String.valueOf(this.token);
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
