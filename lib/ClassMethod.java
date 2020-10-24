package lib;

import AST.Arguments;
import AST.Statement;

public class ClassMethod extends UserFunction{
	public InstanceValue object;
	public ClassMethod(Arguments args, Statement body, InstanceValue inst){
		super(args, body);
		this.object = inst;
	}
	@Override
	public Value execute(Value... args) throws Exception{
		Variables.push();
		Variables.define("this", this.object.container);
		try{
			return super.execute(args);
		} finally{
			Variables.pop();
		}
	}
}
