package AST;

public class OutputStatement implements Statement {
	public Expression expression;
	public OutputStatement(Expression expression) {
		this.expression = expression;
	}
	@Override
	public void execute() throws Exception {
		System.out.println(this.expression.eval());
	} public String toString() {
		return "Output{" + this.expression + "}";
	}
}
