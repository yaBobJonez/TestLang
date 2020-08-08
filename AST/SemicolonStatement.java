package AST;

public class SemicolonStatement implements Statement {
	@Override
	public void execute() throws Exception {
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
