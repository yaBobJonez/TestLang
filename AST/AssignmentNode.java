package AST;

import lib.Value;

public class AssignmentNode implements Expression {
	public Accessible target;
	public String operator;
	public Expression expression;
	public AssignmentNode(Accessible target, String operator, Expression expression) {
		this.target = target;
		this.operator = operator;
		this.expression = expression;
	}
	@Override
	public Value eval() throws Exception {
		if(this.operator == null) return this.target.set(this.expression.eval());
		Expression left = new ValueNode(this.target.get());
		Expression right = new ValueNode(this.expression.eval());
		return this.target.set(new BinOpNode(left, this.operator, right).eval());
	} public String toString(){
		return "Assign{target = " + this.target.toString() + "; operator = " + (this.operator != null ? this.operator : "") +
		"=; expr = " + this.expression.toString() + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
