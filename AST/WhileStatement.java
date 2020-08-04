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
			this.statement.execute();
		}
	}
	@Override
	public String toString() {
		return "While{condition = " + this.condition + "; do = " + this.statement + "}";
	}
}
