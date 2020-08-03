package AST;

import lib.*;

public class ConditionNode implements Expression {
	public Expression left;
	public String operator;
	public Expression right;
	public ConditionNode(Expression left, String operator, Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	@Override
	public Value eval() throws Exception {
		Value left = this.left.eval();
		Value right = this.right.eval();
		if((left instanceof StringValue) && (right instanceof StringValue)){
			String str1 = left.asString();
			String str2 = right.asString();
			switch(this.operator){
				case "<": return new BooleanValue(str1.compareTo(str2) > 0);
				case ">": return new BooleanValue(str1.compareTo(str2) < 0);
				case "==":
				default: return new BooleanValue(str1.equals(str2));
			}
		}
		else {
			double number1 = left.asDouble();
			double number2 = right.asDouble();
			switch(this.operator) {
				case "<": return new BooleanValue(number1 < number2);
				case ">": return new BooleanValue(number1 > number2);
				case "==":
				default: return new BooleanValue(number1 == number2);
			}
		}
	} public String toString(){
		return "(" + String.valueOf(this.left) + ", " + String.valueOf(this.operator) + ", " + String.valueOf(this.right) + ")";
	}
}
