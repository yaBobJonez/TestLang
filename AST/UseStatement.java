package AST;

import exceptions.InvalidExpressionException;
import exceptions.TypeConsumingException;
import lib.ArrayValue;
import lib.StringValue;
import lib.Value;
import lib.modules.Module;

public class UseStatement implements Statement {
	private String basePath = "lib.modules."; 
	public Expression name;
	public UseStatement(Expression name) {
		this.name = name;
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public void execute() throws Exception {
		Value expr = this.name.eval();
		if(expr instanceof ArrayValue){
			try{
				for(Value el : (ArrayValue)expr){
					Module module = (Module)Class.forName(this.basePath + el.asString()).getDeclaredConstructor().newInstance();
					module.initialize();
				}
			} catch(Exception e){ throw new InvalidExpressionException("an unexistent module is specified"); }
		} else if(expr instanceof StringValue){
			try{
				Module module = (Module)Class.forName(this.basePath + expr.asString()).getDeclaredConstructor().newInstance();
				module.initialize();
			} catch(Exception e){ throw new InvalidExpressionException("an unexistent module (" + expr.asString() + ") is specified"); }
		} else throw new TypeConsumingException("string or array");
	}
	@Override
	public String toString() {
		return "Use{basePath = " + this.basePath + "; name = " + this.name + "}";
	}
}
