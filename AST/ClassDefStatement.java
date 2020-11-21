package AST;

import java.util.ArrayList;
import java.util.List;

import lib.Classes;
import lib.ClassValue;

public class ClassDefStatement implements Statement { //TODO separate => Class, ClassDefStatement
	public String name;
	public List<AssignmentNode> fields = new ArrayList<>();
	public List<FuncDefStatement> methods = new ArrayList<>();
	public ClassDefStatement(String name) {
		this.name = name;
	}
	@Override
	public void execute() throws Exception {
		ClassValue classValue = new ClassValue(this.name);
		classValue.fields = this.fields;
		classValue.methods = this.methods;
		Classes.set(this.name, classValue);
	}
	public void addField(AssignmentNode field){
		this.fields.add(field);
	} public void addMethod(FuncDefStatement field){
		this.methods.add(field);
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "ClassStatement{name = "+this.name + "; fields = "+this.fields + "; methods = "+this.methods + "}";
	}
}
