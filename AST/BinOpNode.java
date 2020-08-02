package AST;

import lib.*;

public class BinOpNode implements Expression {
	public Expression left;
	public char operator;
	public Expression right;
	public BinOpNode(Expression left, char operator, Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	@Override
	public Value eval() throws Exception {
		double left = this.left.eval().asDouble();
		double right = this.right.eval().asDouble();
		switch(this.operator) {
			case '+': return new NumberValue(left + right);
			case '-': return new NumberValue(left - right);
			case '*': return new NumberValue(left * right);
			case '/': return new NumberValue(left / right);
			default: return new NumberValue(left + right);
		}
	} public String toString(){
		return "(" + String.valueOf(this.left) + ", " + String.valueOf(this.operator) + ", " + String.valueOf(this.right) + ")";
	}
}
