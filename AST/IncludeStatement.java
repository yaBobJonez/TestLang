package AST;

import java.util.List;
import test.*;
import visitors.FunctionsPresetter;

public class IncludeStatement implements Statement {
	public Expression expr;
	public IncludeStatement(Expression expr) {
		this.expr = expr;
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public void execute() throws Exception {
		try {
			String input = Loader.readSource(this.expr.eval().asString());
			Lexer lexer = new Lexer(input);
			List<Token> tokens = lexer.tokenize();
			Parser parser = new Parser(tokens);
			Statement ast = parser.parse();
			if(!parser.parseErrors.hasErrors()){
				ast.accept(new FunctionsPresetter());
				ast.execute();
			}
		} catch(Exception e){
			throw new Exception(e);
		}
	}
	@Override
	public String toString() {
		return "Include{" + expr + "}";
	}
}
