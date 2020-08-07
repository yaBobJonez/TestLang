package AST;

public class ArrayAssignmentStatement implements Statement {
	public ArrayAccessNode array;
	public Expression value;
	public ArrayAssignmentStatement(ArrayAccessNode array, Expression value) {
		this.array = array;
		this.value = value;
	}
	@Override
	public void execute() throws Exception {
		this.array.getArray().set(this.array.path.size() - 1, this.value.eval());
	}
	@Override
	public String toString() {
		return "Assign{array = " + this.array + "; value = " + this.value + "}";
	}
}
