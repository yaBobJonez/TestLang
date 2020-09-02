package AST;

import exceptions.TypeConsumingException;
import lib.ArrayValue;
import lib.MapValue;
import lib.Value;

public class ContainerAssignmentNode implements Expression {
	public ContainerAccessNode container;
	public Expression expr;
	public ContainerAssignmentNode(ContainerAccessNode container, Expression expr) {
		this.container = container;
		this.expr = expr;
	}
	@Override
	public Value eval() throws Exception {
		Value container = this.container.getContainer();
		Value lastIndex = this.container.lastIndex();
		if(container instanceof ArrayValue){
			Value value = this.expr.eval();
			int arrIndex = lastIndex.asInteger();
			((ArrayValue)container).set(arrIndex, value);
			return value;
		} else if(container instanceof MapValue){
			Value value = this.expr.eval();
			((MapValue)container).set(lastIndex, value);
			return value;
		} else throw new TypeConsumingException("array");
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "Assign{array = " + this.container + "; value = " + this.expr + "}";
	}
}
