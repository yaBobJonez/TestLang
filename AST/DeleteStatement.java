package AST;

import exceptions.InvalidExpressionException;
import lib.ArrayValue;
import lib.Constants;
import lib.MapValue;
import lib.Value;
import lib.Variables;

public class DeleteStatement implements Statement {
	public Expression expression;
	public DeleteStatement(Expression expression) {
		this.expression = expression;
	}
	@Override
	public void execute() throws Exception {
		if(this.expression == null) throw new InvalidExpressionException("identificator expected after delete."); //TODO comma-separated
		if(this.expression instanceof ContainerAccessNode){
			ContainerAccessNode contAccessExpr = (ContainerAccessNode)this.expression;
			Value container = contAccessExpr.getContainer();
			Value index = contAccessExpr.lastIndex();
			if(container instanceof ArrayValue) ((ArrayValue)container).elements.remove(index.asInteger());
			else if(container instanceof MapValue) ((MapValue)container).array.remove(index);
			else System.out.println("Failure."); //TODO HERE
		} else if(this.expression instanceof ConstantNode){
			Constants.constants.remove( ((ConstantNode)this.expression).token.value );
		} else if(this.expression instanceof VariableNode){
			Variables.remove( ((VariableNode)this.expression).token.value );
		} else throw new InvalidExpressionException("deleting operation could not be performed.");
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		//visitor.visit(this);
	}
}
