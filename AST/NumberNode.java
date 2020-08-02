package AST;

import test.Token;

public class NumberNode implements Expression {
	public Token token;
	public NumberNode(Token Rvalue){
		this.token = Rvalue;
	}
	@Override
	public double eval() {
		return Double.valueOf(this.token.value);
	}
	public String toString(){
		return String.valueOf(this.token);
	}
}
