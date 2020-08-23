package AST;

import java.util.*;
import exceptions.FunctionDoesNotExistException;
import exceptions.VariableDoesNotExistException;
import lib.*;

public class FunctionNode implements Expression {
	public Expression name;
	public List<Expression> args;
	public FunctionNode(Expression name, List<Expression> args) { //May be not used.
		this.name = name;
		this.args = args;
	}
	public FunctionNode(Expression name) {
		this.name = name;
		args = new ArrayList<>();
	}
	@Override
	public Value eval() throws Exception {
		int size = this.args.size();
		Value[] argsValues = new Value[size];
		for(int i = 0; i < size; i++){ argsValues[i] = this.args.get(i).eval(); }
		Function func = this.consumeFunction(this.name);
		CallStack.entry(this.name.toString(), func);
		Value res = func.execute(argsValues);
		CallStack.exit();
		return res;
	}
	public void addArg(Expression argument){
		this.args.add(argument);
	} public Function consumeFunction(Expression expr) throws Exception {
		try {
			Value value = expr.eval();
			if(value instanceof FunctionValue) return ((FunctionValue)value).value;
			else return this.getFunction(value.asString());
		} catch(VariableDoesNotExistException e) {
			return this.getFunction(e.name);
		}
	} public Function getFunction(String name) throws Exception {
		if(Functions.exists(name)) return Functions.get(name);
		else if(Variables.variables.containsKey(name)){
			Value func = Variables.get(name);
			if(func instanceof FunctionValue) return ((FunctionValue)func).value;
		} throw new FunctionDoesNotExistException(name);
	}
	public String toString(){
		return "Function{name = " + this.name + "; args = " + this.args.toString() + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
