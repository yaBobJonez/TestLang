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
		return Functions.get(this.name).execute(argsValues);
	}
	public void addArg(Expression argument){
		this.args.add(argument);
	} public String toString(){
		return "Function{name = " + this.name + "; args = " + this.args.toString() + "}";
	}
}
