package test;

import java.util.*;
import AST.*;

public class Parser {
	public List<Token> tokens;
	public int size;
	public int position;
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.size = tokens.size();
	}
	public List<Statement> parse() throws Exception{
		List<Statement> result = new ArrayList<>();
		while(!this.matches(TokenList.TS_EOF)){
			result.add(this.statement());
		} return result;
	}
	public Statement statement() throws Exception{
		if(matches(TokenList.TS_SEMICOLON)){ return this.statement(); }
		else if(matches(TokenList.TI_OUT)){
			return new OutputStatement(this.expression());
		}
		return this.assignmentState();
	}
	public Statement assignmentState() throws Exception{
		Token curr_token = this.getToken(0);
		if(matches(TokenList.TS_ID) && this.getToken(0).type == TokenList.TS_ASSIGN){
			String varName = curr_token.value;
			consume(TokenList.TS_ASSIGN);
			return new AssignmentStatement(varName, this.expression());
		} else { throw new Exception("Unsupported statement."); }
	}
	public Expression expression() throws Exception{
		return this.addition();
	} 
	public Expression addition() throws Exception{
		Expression result = this.multiplication();
		while(true){
			if(this.matches(TokenList.TO_PLUS)){
				result = new BinOpNode(result, '+', this.multiplication()); continue;
			} else if(this.matches(TokenList.TO_MINUS)){
				result = new BinOpNode(result, '-', this.multiplication()); continue;
			} break;
		} return result;
	} public Expression multiplication() throws Exception{
		Expression result = this.unary();
		while(true){
			if(this.matches(TokenList.TO_MULTIPLY)){
				result = new BinOpNode(result, '*', this.unary()); continue;
			} else if(this.matches(TokenList.TO_DIVIDE)){
				result = new BinOpNode(result, '/', this.unary()); continue;
			} break;
		} return result;
	} public Expression unary() throws Exception{
		if(this.matches(TokenList.TO_MINUS)){
			return new UnOpNode('-', this.factor());
		} else { return this.factor(); }
	}
	public Expression factor() throws Exception{
		Token curr_token = this.getToken(0);
		if(matches(TokenList.TT_INT)){
			return new ValueNode(curr_token);
		} else if(matches(TokenList.TT_DOUBLE)){
			return new ValueNode(curr_token);
		} else if(matches(TokenList.TS_ID)){
			return new VariableNode(curr_token);
		} else if(matches(TokenList.TT_STRING)){
			return new ValueNode(curr_token);
		} else if(matches(TokenList.TO_LPAR)){
			Expression result = this.expression();
			this.matches(TokenList.TO_RPAR);
			return result;
		} else if(matches(TokenList.TT_CONST)){
			return new ConstantNode(curr_token);
		} else { throw new Exception("Unregistered/invalid expression."); }
	}
	public boolean matches(String type){
		Token curr_token = this.getToken(0);
		if(curr_token.type != type){ return false; }
		this.position += 1;
		return true;
	} public Token getToken(int rel_pos){
		int curr_position = this.position + rel_pos;
		if(curr_position >= this.size){ return new Token(TokenList.TS_EOF, null); }
		return this.tokens.get(curr_position);
	} public Token consume(String type) throws Exception{
		Token curr_token = this.getToken(0);
		if(curr_token.type != type){ throw new Exception("Unexpected token type."); }
		this.position += 1;
		return curr_token;
	}
}
