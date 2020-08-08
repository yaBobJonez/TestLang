package AST;

public class DoWhileStatement implements Statement {
	public Statement statement;
	public Expression condition;
	public DoWhileStatement(Statement statement, Expression condition) {
		this.statement = statement;
		this.condition = condition;
	}
	@Override
	public void execute() throws Exception {
		do{
			try { this.statement.execute(); }
			catch (BreakStatement e) { break; }
			catch (ContinueStatement e) { /*TODO continue*/ }
		} while (this.condition.eval().asBoolean());
	}
	@Override
	public String toString() {
		return "DoWhile{actions = " + this.statement + "; cond = " + this.condition + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
