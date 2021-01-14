package AST;

import lib.*;
import lib.ClassValue;
import test.Token;

import java.util.*;

import exceptions.IllegalPropertyAccessException;
import exceptions.TypeConsumingException;

public class ContainerAccessNode implements Expression, Accessible {
	public Expression expr;
	public boolean exprIsVar;
	public List<Expression> path;
	public byte caller;
	public ContainerAccessNode(Expression expr, List<Expression> path) {
		this.expr = expr;
		this.exprIsVar = expr instanceof VariableNode;
		this.path = path;
	}
	public ContainerAccessNode(Token var, List<Expression> path) {
		this.expr = new VariableNode(var);
		this.path = path;
	}
	@Override
	public Value eval() throws Exception {
		return this.get();
	}
	public Value get() throws Exception {
		Value container = this.getContainer();
		Value lastIndex = this.lastIndex();
		if(container instanceof ArrayValue) return ((ArrayValue)container).get(lastIndex.asInteger());
		else if(container instanceof MapValue) return ((MapValue)container).get(lastIndex);
		else if(container instanceof StringValue) return ((StringValue)container).access(lastIndex);
		else if(container instanceof InstanceValue){ //Honestly, IDK how this even works...
			InstanceValue obj = ((InstanceValue)container);
			if(obj.privateList.contains(lastIndex.asString())) throw new IllegalPropertyAccessException(lastIndex.asString());
			return obj.get(lastIndex);
		}
		else if(container instanceof ClassValue) return ((ClassValue)container).get(lastIndex);
		else throw new TypeConsumingException("array", container.getClass().getSimpleName());
	} public Value set(Value value) throws Exception {
		Value container = this.getContainer();
		Value lastIndex = this.lastIndex();
		if(container instanceof ArrayValue){ ((ArrayValue)container).set(lastIndex.asInteger(), value); return value; }
		else if(container instanceof MapValue){ ((MapValue)container).set(lastIndex, value); return value; }
		else if(container instanceof InstanceValue){ ((InstanceValue)container).set(lastIndex, value); return value; }
		else throw new TypeConsumingException("array", container.getClass().getSimpleName());
	}
	public Value getContainer() throws Exception{
		Value container = this.expr.eval();
		int last = this.path.size() - 1;
		for(int i = 0; i < last; i++){
			Value index = this.index(i);
			if(container instanceof ArrayValue){
				int arrIndex = index.asInteger();
				container = ((ArrayValue)container).get(arrIndex);
			} else if(container instanceof MapValue){
				container = ((MapValue)container).get(index);
			} else throw new TypeConsumingException("array");
		} return container;
	}
	/*public ArrayValue checkForArray(Value array) throws Exception{          //May be not needed anymore
		if(array instanceof ArrayValue){
			return (ArrayValue)array;
		} else throw new TypeConsumingException("array");
	} public MapValue checkForMap(Value map) throws Exception{
		if(map instanceof MapValue){
			return (MapValue)map;
		} else throw new TypeConsumingException("associative array");
	}*/
	public Value index(int index) throws Exception{
		return this.path.get(index).eval();
	} public Value lastIndex() throws Exception{
		return this.index(this.path.size() - 1);
	}
	public String toString(){
		return "ArrayAccess{name = " + this.expr.toString() + "; path = " + this.path + "CALLER!: "+caller+"}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
