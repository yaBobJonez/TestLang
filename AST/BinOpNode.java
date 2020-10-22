package AST;

import exceptions.IllegalOperationException;
import lib.*;

public class BinOpNode implements Expression { //TODO refactor
	public Expression left;
	public String operator;
	public Expression right;
	public BinOpNode(Expression left, String operator, Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	@Override
	public Value eval() throws Exception {
		Value left = this.left.eval();
		Value right = this.right.eval();
		if(left instanceof InfinityValue || right instanceof InfinityValue){
			switch(this.operator){
				default: return new InfinityValue(false);
			}
		} else if(left instanceof NullValue || right instanceof NullValue){
			switch(this.operator){
				default: throw new IllegalOperationException("null value detected");
			}
		} else if((left instanceof StringValue) && (right instanceof NumberValue)){
			String string = left.asString();
			int times = right.asInteger();
			switch(this.operator){
				case "*": return new StringValue(new String(new char[times]).replace("\0", string));
				default: throw new IllegalOperationException(this.operator);
			}
		} else if((left instanceof StringValue) && (right instanceof StringValue)){
			String str1 = left.asString();
			String str2 = right.asString();
			switch(this.operator){
				case ".": return new StringValue(str1 + str2);
				default: throw new IllegalOperationException(this.operator);
			}
		} else if((left instanceof ArrayValue) && (right instanceof ArrayValue)){
			ArrayValue str1 = (ArrayValue)left;
			ArrayValue str2 = (ArrayValue)right;
			switch(this.operator){
				case "+": { str1.elements.addAll(str2.elements); return new ArrayValue(str1); }
				default: throw new IllegalOperationException(this.operator);
			}
		} else if(left instanceof ArrayValue){
			ArrayValue str1 = (ArrayValue)left;
			String str2 = right.asString();
			switch(this.operator){
				case "+": { str1.elements.add(new StringValue(str2)); return new ArrayValue(str1); }
				default: throw new IllegalOperationException(this.operator);
			}
		}
		else {
			double number1 = left.asDouble();
			double number2 = right.asDouble();
			switch(this.operator) {
				case "+": return new NumberValue(number1 + number2);
				case "-": return new NumberValue(number1 - number2);
				case "*": return new NumberValue(number1 * number2);
				case "/": 
					if(number2 == 0.0 && number1 == 0.0) return new NanValue();
					else if(number2 == 0.0) return new NullValue();
					return new NumberValue(number1 / number2);
				case "%": return new NumberValue(number1 % number2);
				case "^": return new NumberValue(Math.pow(number1, number2));
				case ".": return new StringValue(left.asString() + right.asString());
				default: throw new IllegalOperationException(this.operator);
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
