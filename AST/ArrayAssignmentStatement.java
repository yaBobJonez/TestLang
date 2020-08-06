package AST;

import lib.*;

public class ArrayAssignmentStatement implements Statement {
	public String name;
	public Expression index;
	public Expression value;
	public ArrayAssignmentStatement(String name, Expression index, Expression value) {
		this.name = name;
		this.index = index;
		this.value = value;
	}
	@Override
	public void execute() throws Exception {
		Value item = Variables.get(this.name);
		if(item instanceof ArrayValue){
			ArrayValue array = (ArrayValue)item;
			array.set(this.index.eval().asInteger(), this.value.eval());
		} else { throw new Exception("Not an array."); }
	}
	@Override
	public String toString() {
		return "Assign{array = " + this.name + "; index = " + this.index + "; value = " + this.value + "}";
	}
}
