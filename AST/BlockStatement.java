package AST;

import java.util.*;

public class BlockStatement implements Statement {
	public List<Statement> statements;
	public BlockStatement() {
		this.statements = new ArrayList<>();
	} public void add(Statement statement){
		this.statements.add(statement);
	}
	@Override
	public void execute() throws Exception {
		for(Statement state : this.statements){
			state.execute();
		}
	}
	@Override
	public String toString() {
		String str = "{";
		for(Statement state : this.statements){
			str += state.toString() + ";";
		} str += "}";
		return str;
	}
}
