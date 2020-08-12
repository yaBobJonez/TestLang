package AST;

import lib.*;

public class ArrayAssignmentStatement implements Statement {
	public ArrayAccessNode array;
	public Expression value;
	public ArrayAssignmentStatement(ArrayAccessNode array, Expression value) {
		this.array = array;
		this.value = value;
	}
	@Override
	public void execute() throws Exception {
		Value object = Variables.get(this.array.var);
		if(object instanceof ArrayValue){
			this.array.getArray().set(this.array.lastIndex(), this.value.eval());
		} else if(object instanceof MapValue){
			this.array.checkForMap(object).set(this.array.path.get(0).eval(), this.value.eval());
		}
	}
	@Override
	public String toString() {
		return "Assign{array = " + this.array + "; value = " + this.value + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
