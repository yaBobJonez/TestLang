package AST;

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
		try{
			String additPath = this.name.eval().asString();
			Module module = (Module)Class.forName(this.basePath + additPath).getDeclaredConstructor().newInstance();
			module.initialize();
		} catch(Exception e) {}
	}
	@Override
	public String toString() {
		return "Use{basePath = " + this.basePath + "; name = " + this.name + "}";
	}
}
