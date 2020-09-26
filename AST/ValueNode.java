package AST;

import lib.*;

public class ValueNode implements Expression {
	public Value value;
	public ValueNode(Number value) {
		this.value = new NumberValue(value);
	} public ValueNode(String value) {
		this.value = new StringValue(value);
	} public ValueNode(boolean value) {
		this.value = new BooleanValue(value);
	} public ValueNode(Function value) {
		this.value = new FunctionValue(value);
	} public ValueNode(Object value) {
		if(value.equals("nan")) this.value = new NanValue();
		else if(value.equals((Object)Double.POSITIVE_INFINITY)) this.value = new InfinityValue(false);
		else if(value.equals((Object)Double.NEGATIVE_INFINITY)) this.value = new InfinityValue(true);
		else this.value = new NullValue();
	}
	public ValueNode(Value value) {
		this.value = value;
	}
	@Override
	public Value eval() {
		return this.value;
	}
	public String toString(){
		return String.valueOf(this.value);
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
