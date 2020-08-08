package AST;

public class IfElseStatement implements Statement {
	public Expression condition;
	public Statement ifStatement;
	public Statement elseStatement;
	public IfElseStatement(Expression condition, Statement ifStatement, Statement elseStatement) {
		this.condition = condition;
		this.ifStatement = ifStatement;
		this.elseStatement = elseStatement;
	}
	@Override
	public void execute() throws Exception {
		boolean result = this.condition.eval().asBoolean();
		if(result != false){
			this.ifStatement.execute();
		} else if(this.elseStatement != null){
			this.elseStatement.execute();
		}
	}
	@Override
	public String toString() {
		String str = "If{condition = " + this.condition + "; then = " + this.ifStatement;
		if(this.elseStatement != null){ str += "; else = " + this.elseStatement; }
		return str += "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
