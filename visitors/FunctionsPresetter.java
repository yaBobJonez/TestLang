package visitors;

import AST.*;

public class FunctionsPresetter extends AbstractVisitor{
	public void visit(FuncDefStatement s) throws Exception{
		super.visit(s);
		s.execute();
	}
}
