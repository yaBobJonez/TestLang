package AST;

public class BinOpNode implements Expression {
	public Expression left;
	public char operator;
	public Expression right;
	public BinOpNode(Expression left, char operator, Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	@Override
	public double eval() throws Exception {
		switch(this.operator) {
			case '+': return this.left.eval() + this.right.eval();
			case '-': return this.left.eval() - this.right.eval();
			case '*': return this.left.eval() * this.right.eval();
			case '/': return this.left.eval() / this.right.eval();
		} return 0;
	} public String toString(){
		return "(" + String.valueOf(this.left) + ", " + String.valueOf(this.operator) + ", " + String.valueOf(this.right) + ")";
	}
}
