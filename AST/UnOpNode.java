package AST;

import exceptions.IllegalOperationException;
import lib.*;

public class UnOpNode implements Expression {
	public char operator;
	public Expression right;
	public UnOpNode(char operator, Expression right) {
		this.operator = operator;
		this.right = right;
	}
	@Override
	public Value eval() throws Exception {
		switch(this.operator){
			case '-': 
				if(this.right.eval() instanceof InfinityValue){
					if( ((InfinityValue)this.right.eval()).negative == false ) return new InfinityValue(true);
					else return new InfinityValue(false);
				} return new NumberValue(- this.right.eval().asDouble());
			case '!': return new BooleanValue(this.right.eval().asBoolean() != false ? false : true);
			default: throw new IllegalOperationException(String.valueOf(this.operator));
		}
	} public String toString(){
		return "(" + String.valueOf(this.operator) + ", " + String.valueOf(this.right) + ")";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
