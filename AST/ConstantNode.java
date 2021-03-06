package AST;

import exceptions.VariableDoesNotExistException;
import lib.*;
import test.Token;

public class ConstantNode implements Expression {
	public Token token;
	public ConstantNode(Token Rvalue) {
		this.token = Rvalue;
	}
	@Override
	public Value eval() throws Exception {
		if(!Constants.constants.containsKey(this.token.value)){ throw new VariableDoesNotExistException(this.token.value); }
		return Constants.constants.get(this.token.value);
	} public String toString(){
		return String.valueOf(this.token);
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
