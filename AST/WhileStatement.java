package AST;

public class WhileStatement implements Statement {
	public Expression condition;
	public Statement statement;
	public WhileStatement(Expression condition, Statement statement) {
		this.condition = condition;
		this.statement = statement;
	}
	@Override
	public void execute() throws Exception {
		while(this.condition.eval().asBoolean()){
			try { this.statement.execute(); }
			catch (BreakStatement e) { break; }
			catch (ContinueStatement e) {}
		}
	}
	@Override
	public String toString() {
		return "While{condition = " + this.condition + "; do = " + this.statement + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
