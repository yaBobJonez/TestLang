package AST;

import exceptions.ClassDoesNotExistException;

public class RaiseStatement implements Statement {
	public String type;
	public String message;
	public RaiseStatement(String type, String message) {
		this.type = type;
		this.message = message;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute() throws Exception {
		try {
			Class exClass = Class.forName("exceptions."+this.type);
			Object exObj = exClass.getDeclaredConstructor().newInstance();
			Exception ex = (Exception)exObj;
			throw new Exception(this.message, ex);
		} catch (ClassNotFoundException e){
			if(this.type.equals("Exception")) throw new Exception(this.message);
			else if(this.type.equals("Error")) throw new Error(this.message);
			else throw new ClassDoesNotExistException("Throwable class "+this.type+" is not found");
		}
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
