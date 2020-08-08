package AST;

public class FunctionStatement implements Statement {
	public FunctionNode function;
	public FunctionStatement(FunctionNode function) {
		this.function = function;
	}
	@Override
	public void execute() throws Exception {
		this.function.eval();
	} public String toString(){
		return this.function.toString();
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
