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
}
