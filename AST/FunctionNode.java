package AST;

import java.util.*;
import lib.*;

public class FunctionNode implements Expression {
	public String name;
	public List<Expression> args;
	public FunctionNode(String name, List<Expression> args) {
		this.name = name;
		this.args = args;
	}
	public FunctionNode(String name) {
		this.name = name;
		args = new ArrayList<>();
	}
	@Override
	public Value eval() throws Exception {
		int size = this.args.size();
		Value[] argsValues = new Value[size];
		for(int i = 0; i < size; i++){ argsValues[i] = this.args.get(i).eval(); }
		Function func = this.getFunction(name);
		if(func instanceof UserFunction){
			UserFunction uFunc = (UserFunction)func;
			if(size != uFunc.args.size()){ throw new Exception("Arguments count mismatch."); }
			Variables.push();
			for(int i = 0; i < size; i++){ Variables.set(uFunc.getArg(i), argsValues[i]); }
			Value result = uFunc.execute(argsValues);
			Variables.pop();
			return result;
		} else { return func.execute(argsValues); }
	}
	public void addArg(Expression argument){
		this.args.add(argument);
	} public Function getFunction(String name) throws Exception {
		if(Functions.exists(name)) return Functions.get(name);
		else if(Variables.variables.containsKey(name)){
			Value func = Variables.get(name);
			if(func instanceof FunctionValue) return ((FunctionValue)func).value;
		} throw new Exception("Function doesn't exist.");
	}
	public String toString(){
		return "Function{name = " + this.name + "; args = " + this.args.toString() + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
