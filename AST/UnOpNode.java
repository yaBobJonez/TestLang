package AST;

import lib.*;

public class UnOpNode implements Expression {
	public char operator;
	public Expression right;
	public UnOpNode(char operator, Expression right) {
		this.operator = operator;
		this.right = right;
	}
	@Override
	public Value eval() throws Exception {
		switch(this.operator){
			case '-': return new NumberValue(- this.right.eval().asDouble());
			default: return this.right.eval();
		}
	} public String toString(){
		return "(" + String.valueOf(this.operator) + ", " + String.valueOf(this.right) + ")";
	}
}
