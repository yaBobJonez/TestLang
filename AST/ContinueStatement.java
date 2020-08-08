package AST;

@SuppressWarnings("serial")
public class ContinueStatement extends Exception implements Statement {
	@Override
	public void execute() throws Exception {
		throw this;
	}
	@Override
	public String toString() {
		return "Continue";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
