package AST;

import lib.*;
import java.util.*;

public class ArrayAccessNode implements Expression {
	public String var;
	public List<Expression> path;
	public ArrayAccessNode(String var, List<Expression> path) {
		this.var = var;
		this.path = path;
	}
	@Override
	public Value eval() throws Exception {
		return this.getArray().get(this.index(this.path.size() - 1));
	}
	public ArrayValue checkForArray(Value array) throws Exception{
		if(array instanceof ArrayValue){
			return (ArrayValue)array;
		} else { throw new Exception("Not an array."); } //Refactor this class: unreadable :(
	}
	public ArrayValue getArray() throws Exception{
		ArrayValue array = this.checkForArray(Variables.get(this.var));
		for(int i = 0; i < this.path.size() - 1; i++){
			array = this.checkForArray(array.get(this.index(i)));
		} return array;
	}
	public int index(int index) throws Exception{
		return this.path.get(index).eval().asInteger();
	}
	public String toString(){
		return "ArrayAccess{name = " + this.var + "; path = " + this.path + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
