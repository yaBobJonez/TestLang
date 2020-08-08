package AST;

@SuppressWarnings("serial")
public class BreakStatement extends Exception implements Statement {
	@Override
	public void execute() throws Exception {
		throw this;
	}
	@Override
	public String toString() {
		return "Break";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
