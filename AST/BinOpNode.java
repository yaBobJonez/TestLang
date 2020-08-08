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
		Value left = this.left.eval();
		Value right = this.right.eval();
		if((left instanceof StringValue) && (right instanceof NumberValue)){
			String string = left.asString();
			int times = right.asInteger();
			switch(this.operator){
				default: return new StringValue(new String(new char[times]).replace("\0", string));
			}
		} else if((left instanceof StringValue) && (right instanceof StringValue)){
			String str1 = left.asString();
			String str2 = right.asString();
			switch(this.operator){
				default: return new StringValue(str1 + str2);	//TODO simplify
			}
		} else if((left instanceof ArrayValue) && (right instanceof StringValue)){
			String str1 = left.asString();
			String str2 = right.asString();
			switch(this.operator){
				default: return new StringValue(str1 + str2);
			}
		}
		else {
			double number1 = left.asDouble();
			double number2 = right.asDouble();
			switch(this.operator) {
				case '+': return new NumberValue(number1 + number2);
				case '-': return new NumberValue(number1 - number2);
				case '*': return new NumberValue(number1 * number2);
				case '/': return new NumberValue(number1 / number2);
				default: return new NumberValue(number1 + number2);
			}
		}
	} public String toString(){
		return "(" + String.valueOf(this.left) + ", " + String.valueOf(this.operator) + ", " + String.valueOf(this.right) + ")";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
