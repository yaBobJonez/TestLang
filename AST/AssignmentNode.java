package AST;

import lib.Value;
import lib.Variables;

public class AssignmentNode implements Expression {
	public String variable;
	public Expression expression;
	public AssignmentNode(String variable, Expression expression) {
		this.variable = variable;
		this.expression = expression;
	}
	@Override
	public Value eval() throws Exception {
		Value result = expression.eval();
		Variables.set(this.variable, result);
		return result;
	} public String toString(){
		return "Assign{" + this.variable + " = " + this.expression + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
