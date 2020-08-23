package AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import exceptions.TypeConsumingException;
import lib.*;

public class DestructuringAssignmentStatement implements Statement {
	public List<String> variables;
	public Expression expr;
	public DestructuringAssignmentStatement(List<String> variables, Expression expr) {
		this.variables = variables;
		this.expr = expr;
	}
	@Override
	public void execute() throws Exception {
		Value value = this.expr.eval();
		if(value instanceof ArrayValue) this.executeArray((ArrayValue)value);
		else if(value instanceof MapValue) this.executeMap((MapValue)value);
		else throw new TypeConsumingException("array");
	}
	public void executeArray(ArrayValue array){
		for(int i = 0; i < this.variables.size(); i++){
			String var = this.variables.get(i);
			if(var != null) Variables.set(var, array.get(i));
		}
	} public void executeMap(MapValue map){
		int i = 0;
		for(Map.Entry<Value, Value> entry : map){
			String var = this.variables.get(i);
			if(var != null){
				List<Value> list = new ArrayList<>();
				list.add(entry.getKey()); list.add(entry.getValue());
				Variables.set(var, new ArrayValue(list));
			} i += 1;
		}
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
