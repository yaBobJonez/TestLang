package AST;

import lib.*;
import test.Token;

public class NumberNode implements Expression {
	public Token token;
	public NumberNode(Token Rvalue){
		this.token = Rvalue;
	}
	@Override
	public Value eval() {
		return new NumberValue(Double.parseDouble(this.token.value));
	}
	public String toString(){
		return String.valueOf(this.token);
	}
}
