package AST;

import exceptions.TypeConsumingException;
import lib.ArrayValue;
import lib.MapValue;
import lib.Value;

public class ContainerAssignmentStatement implements Statement {
	public ContainerAccessNode container;
	public Expression expr;
	public ContainerAssignmentStatement(ContainerAccessNode container, Expression expr) {
		this.container = container;
		this.expr = expr;
	}
	@Override
	public void execute() throws Exception {
		Value container = this.container.getContainer();
		Value lastIndex = this.container.lastIndex();
		if(container instanceof ArrayValue){
			int arrIndex = lastIndex.asInteger();
			((ArrayValue)container).set(arrIndex, this.expr.eval());
		} else if(container instanceof MapValue){
			((MapValue)container).set(lastIndex, this.expr.eval());
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
