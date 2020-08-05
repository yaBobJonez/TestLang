package AST;

import lib.*;

@SuppressWarnings("serial")
public class ReturnStatement extends Exception implements Statement {
	public Expression expression;
	public Value result;
	public ReturnStatement(Expression expression) {
		this.expression = expression;
	}
	@Override
	public void execute() throws Exception {
		this.result = this.expression.eval();
		throw this;
	} public String toString(){
		return "Return";
	} public Value outcome(){
		return this.result;
	}
}
