package AST;

import lib.*;

public class ArrayAccessNode implements Expression {
	String var;
	Expression index;
	public ArrayAccessNode(String var, Expression index) {
		this.var = var;
		this.index = index;
	}
	@Override
	public Value eval() throws Exception {
		Value var = Variables.get(this.var);
		if(var instanceof ArrayValue){
			ArrayValue array = (ArrayValue)var;
			return array.get(this.index.eval().asInteger());
		} else { throw new Exception("Not an array."); }
	}
	public String toString(){
		return "ArrayAccess{name = " + this.var + "; index = " + this.index + "}";
	}
}
