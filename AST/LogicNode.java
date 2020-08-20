package AST;

import exceptions.IllegalOperationException;
import lib.*;

public class LogicNode implements Expression {
	public Expression left;
	public String operator;
	public Expression right;
	public LogicNode(Expression left, String operator, Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	@Override
	public Value eval() throws Exception {
		Value left = this.left.eval();
		Value right = this.right.eval();
		double str1, str2;
		if((left instanceof StringValue) && (right instanceof StringValue)){
			str1 = right.asString().compareTo(left.asString());
			str2 = 0;
		} else {
			str1 = left.asDouble();
			str2 = right.asDouble();
		} switch(this.operator){
			case "<": return new BooleanValue(str1 < str2);
			case ">": return new BooleanValue(str1 > str2);
			case "<=": return new BooleanValue(str1 <= str2);
			case ">=": return new BooleanValue(str1 >= str2);
			case "!=": return new BooleanValue(str1 != str2);
			case "&": return new BooleanValue((str1 != 0) && (str2 != 0));
			case "|": return new BooleanValue((str1 != 0) || (str2 != 0));
			case "==": return new BooleanValue(str1 == str2);
			default: throw new IllegalOperationException(this.operator);
		}
	} public String toString(){
		return "(" + String.valueOf(this.left) + ", " + String.valueOf(this.operator) + ", " + String.valueOf(this.right) + ")";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
