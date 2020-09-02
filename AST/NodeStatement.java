package AST;

public class NodeStatement implements Statement {
	public Expression expr;
	public NodeStatement(Expression expr) {
		this.expr = expr;
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public void execute() throws Exception {
		this.expr.eval();
	}
	@Override
	public String toString() {
		return this.expr.toString();
	}
}
