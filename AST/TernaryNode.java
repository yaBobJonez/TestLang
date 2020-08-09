package AST;

import lib.Value;

public class TernaryNode implements Expression {
	public Expression condition;
	public Expression trueActions;
	public Expression falseActions;
	public TernaryNode(Expression condition, Expression trueActions, Expression falseActions) {
		this.condition = condition;
		this.trueActions = trueActions;
		this.falseActions = falseActions;
	}
	@Override
	public Value eval() throws Exception {
		if(this.condition.eval().asBoolean()){
			return this.trueActions.eval();
		} else {
			return this.falseActions.eval();
		}
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "Ternary{cond = " + this.condition + "; true = " + this.trueActions + "; false = " + this.falseActions + "}";
	}
}
