package visitors;

import AST.*;

public abstract class AbstractVisitor implements Visitor {
	@Override
	public void visit(ArrayNode s) throws Exception {
		for(Expression index : s.elements){ index.accept(this); }
	}
	@Override
	public void visit(AssignmentNode s) throws Exception {
		s.expression.accept(this);
	}
	@Override
	public void visit(BinOpNode s) throws Exception {
		s.left.accept(this);
		s.right.accept(this);
	}
	@Override
	public void visit(BlockStatement s) throws Exception {
		for(Statement statement : s.statements){ statement.accept(this); }
	}
	@Override
	public void visit(BreakStatement s) {}
	@Override
	public void visit(ClassStatement s) throws Exception {
		for(AssignmentNode a : s.fields) a.accept(this); //TODO check class declarations visitor
		for(FuncDefStatement f : s.methods) f.body.accept(this);
	}
	@Override
	public void visit(ContainerAccessNode s) throws Exception {
		s.expr.accept(this);
		for(Expression index : s.path){ index.accept(this); }
	}
	@Override
	public void visit(ConstantNode s) {}
	@Override
	public void visit(ContinueStatement s) {}
	@Override
	public void visit(DestructuringAssignmentStatement s) throws Exception {
		s.expr.accept(this);
	}
	@Override
	public void visit(DoWhileStatement s) throws Exception {
		s.condition.accept(this);
		s.statement.accept(this);
	}
	@Override
	public void visit(ForStatement s) throws Exception {
		s.initial.accept(this);
		s.condition.accept(this);
		s.increment.accept(this);
		s.statement.accept(this);
	}
	@Override
	public void visit(ForeachStatement s) throws Exception{
		s.array.accept(this);
		s.body.accept(this);
	}
	@Override
	public void visit(FuncDefStatement s) throws Exception {
		s.body.accept(this);
	}
	@Override
	public void visit(FunctionNode s) throws Exception {
		s.name.accept(this);
		for(Expression arg : s.args){ arg.accept(this); }
	}
	@Override
	public void visit(IfElseStatement s) throws Exception {
		s.condition.accept(this);
		s.ifStatement.accept(this);
		if(s.elseStatement != null) s.elseStatement.accept(this);
	}
	@Override
	public void visit(IncludeStatement s) throws Exception {
		s.expr.accept(this);
	}
	@Override
	public void visit(InputStatement s) throws Exception{
		//TODO fast "msg" check
	}
	@Override
	public void visit(LogicNode s) throws Exception {
		s.left.accept(this);
		s.right.accept(this);
	}
	@Override
	public void visit(MapNode s) throws Exception {
		for(Expression key : s.elements.keySet()){
			key.accept(this);
			s.elements.get(key).accept(this);
		}
	}
	@Override
	public void visit(NodeStatement s) throws Exception{
		s.expr.accept(this);
	}
	@Override
	public void visit(ObjectNode s) throws Exception{
		for(Expression arg : s.consArgs) arg.accept(this);
	}
	@Override
	public void visit(OutputStatement s) throws Exception {
		s.expression.accept(this);
	}
	@Override
	public void visit(RaiseStatement s) throws Exception {}
	@Override
	public void visit(ReturnStatement s) throws Exception {
		s.expression.accept(this);
	}
	@Override
	public void visit(SemicolonStatement s) {}
	@Override
	public void visit(SwitchStatement s) throws Exception {
		s.search.accept(this);
	}
	@Override
	public void visit(TernaryNode s) throws Exception {
		s.condition.accept(this);
		s.trueActions.accept(this);
		s.falseActions.accept(this);
	}
	@Override
	public void visit(TryCatchStatement s) throws Exception {
		s.body.accept(this);
		for(Statement entry : s.catches.values()){
			entry.accept(this);
		}
	}
	@Override
	public void visit(UnOpNode s) throws Exception {
		s.right.accept(this);
	}
	@Override
	public void visit(UseStatement s) throws Exception {
		s.name.accept(this);
	}
	@Override
	public void visit(ValueNode s) {}
	@Override
	public void visit(VariableNode s) {}
	@Override
	public void visit(WhileStatement s) throws Exception {
		s.condition.accept(this);
		s.statement.accept(this);
	}
	
}
