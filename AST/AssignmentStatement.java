package AST;

import lib.Value;
import lib.Variables;

public class AssignmentStatement implements Statement {
	public String variable;
	public Expression expression;
	public AssignmentStatement(String variable, Expression expression) {
		this.variable = variable;
		this.expression = expression;
	}
	@Override
	public void execute() throws Exception {
		Value result = expression.eval();
		Variables.set(this.variable, result);
	} public String toString(){
		return "Assign{" + this.variable + " = " + this.expression + "}";
	}
}
