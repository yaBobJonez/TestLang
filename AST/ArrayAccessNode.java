package AST;

import lib.*;
import java.util.*;

import exceptions.TypeConsumingException;

public class ArrayAccessNode implements Expression {
	public String var;
	public List<Expression> path;
	public ArrayAccessNode(String var, List<Expression> path) {
		this.var = var;
		this.path = path;
	}
	@Override
	public Value eval() throws Exception {
		Value object = Variables.get(this.var);
		if(object instanceof ArrayValue){
			return this.getArray().get(this.lastIndex());
		} else if(object instanceof MapValue){
			return ((MapValue)object).array.get(this.path.get(0).eval());
		} else throw new TypeConsumingException("array");
	}
	public ArrayValue checkForArray(Value array) throws Exception{
		if(array instanceof ArrayValue){
			return (ArrayValue)array;
		} else throw new TypeConsumingException("array");
	} public MapValue checkForMap(Value map) throws Exception{
		if(map instanceof MapValue){
			return (MapValue)map;
		} else throw new TypeConsumingException("associative array");
	}
	public ArrayValue getArray() throws Exception{
		ArrayValue array = this.checkForArray(Variables.get(this.var));
		int last = this.path.size() - 1;
		for(int i = 0; i < last; i++){
			array = this.checkForArray(array.get(this.index(i)));
		} return array;
	}
	public int index(int index) throws Exception{
		return this.path.get(index).eval().asInteger();
	} public int lastIndex() throws Exception{
		return this.index(this.path.size() - 1);
	}
	public String toString(){
		return "ArrayAccess{name = " + this.var + "; path = " + this.path + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
