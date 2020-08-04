package AST;

public class ForStatement implements Statement {
	public Statement initial;
	public Expression condition;
	public Statement increment;
	public Statement statement;
	public ForStatement(Statement initial, Expression condition, Statement increment, Statement statement) {
		this.initial = initial;
		this.condition = condition;
		this.increment = increment;
		this.statement = statement;
	}
	@Override
	public void execute() throws Exception {
		for(this.initial.execute(); this.condition.eval().asBoolean(); this.increment.execute()){
			this.statement.execute();
		}
	}
	@Override
	public String toString() {
		return "For{init = " + this.initial + "; cond = " + this.condition + "; incr = " + this.increment + "; do = " + this.statement + "}";
	}
}
