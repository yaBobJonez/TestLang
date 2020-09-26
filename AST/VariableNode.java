package AST;

import exceptions.VariableDoesNotExistException;
import lib.*;
import test.Token;

public class VariableNode implements Expression, Accessible {
	public Token token;
	public VariableNode(Token Rvalue) {
		this.token = Rvalue;
	}
	@Override
	public Value eval() throws Exception {
		return this.get();
	}
	public Value get() throws Exception {
		if(!Variables.variables.containsKey(this.token.value)){ 
			//throw new VariableDoesNotExistException(this.token.value); //Previously threw an error.
			return new NullValue();
		} return Variables.variables.get(this.token.value);
	} public Value set(Value value) throws Exception {
		Variables.set(this.token.value, value);
		return value;
	}
	public String toString(){
		return String.valueOf(this.token);
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
