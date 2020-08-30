package AST;

import lib.*;

public class FuncDefStatement implements Statement {
	public String name;
	public Arguments args;
	public Statement body;
	public FuncDefStatement(String name, Arguments args, Statement body) {
		this.name = name;
		this.args = args;
		this.body = body;
	}
	@Override
	public void execute() throws Exception {
		Functions.set(name, new UserFunction(args, body));
	} public String toString(){
		return "FuncDef{name = " + this.name + "; args = " + this.args.toString() + "; body = " + this.body + "}";
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
