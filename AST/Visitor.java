package AST;

public interface Visitor {
	void visit(ArrayNode s) throws Exception;
	void visit(AssignmentNode s) throws Exception;
	void visit(BinOpNode s) throws Exception;
	void visit(BlockStatement s) throws Exception;
	void visit(BreakStatement s) throws Exception;
	void visit(ClassStatement s) throws Exception;
	void visit(ConstantNode s);
	void visit(ContainerAccessNode s) throws Exception;
	void visit(ContinueStatement s) throws Exception;
	void visit(DestructuringAssignmentStatement s) throws Exception;
	void visit(DoWhileStatement s) throws Exception;
	void visit(ForStatement s) throws Exception;
	void visit(ForeachStatement s) throws Exception;
	void visit(FuncDefStatement s) throws Exception;
	void visit(FunctionNode s) throws Exception;
	void visit(IfElseStatement s) throws Exception;
	void visit(IncludeStatement s) throws Exception;
	void visit(InputStatement s) throws Exception;
	void visit(LogicNode s) throws Exception;
	void visit(MapNode s) throws Exception;
	void visit(NodeStatement s) throws Exception;
	void visit(ObjectNode s) throws Exception;
	void visit(OutputStatement s) throws Exception;
	void visit(ReturnStatement s) throws Exception;
	void visit(SemicolonStatement s);
	void visit(SwitchStatement s) throws Exception;
	void visit(TernaryNode s) throws Exception;
	void visit(UnOpNode s) throws Exception;
	void visit(UseStatement s) throws Exception;
	void visit(ValueNode s);
	void visit(VariableNode s);
	void visit(WhileStatement s) throws Exception;
}
