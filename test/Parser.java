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
		if(matches(TokenList.TS_SEMICOLON)){ return new SemicolonStatement(); }
		else if(matches(TokenList.TI_OUT)){
			return new OutputStatement(this.expression());
		} else if(matches(TokenList.TS_IF)){
			return this.ifElseState();
		} else if(matches(TokenList.TS_FOR)){
			return this.forState();
		} else if(matches(TokenList.TS_WHILE)){
			return this.whileState();
		} else if(matches(TokenList.TS_DOWHILE)){
			return this.doWhileState();
		} else if(matches(TokenList.TA_BREAK)){ return new BreakStatement(); }
		else if(matches(TokenList.TA_CONTINUE)){ return new ContinueStatement(); }
		return this.assignmentState();
	}
	public Statement assignmentState() throws Exception{
		Token curr_token = this.getToken(0);
		if(matches(TokenList.TS_ID) && this.getToken(0).type == TokenList.TS_ASSIGN){
			String varName = curr_token.value;
			consume(TokenList.TS_ASSIGN);
			return new AssignmentStatement(varName, this.expression());
		} else { throw new Exception("Unsupported statement."); }
	} public Statement ifElseState() throws Exception{
		Expression condition = this.expression();
		Statement ifState = this.StateOrBlock();
		Statement elseState;
		if(matches(TokenList.TS_ELSE)){
			elseState = this.StateOrBlock();
		} else {
			elseState = null;
		} return new IfElseStatement(condition, ifState, elseState);
	} public Statement forState() throws Exception{
		this.consume(TokenList.TO_LPAR);
		Statement init = this.assignmentState();
		this.consume(TokenList.TO_COMMA);
		Expression cond = this.expression();
		this.consume(TokenList.TO_COMMA);
		Statement incr = this.assignmentState();
		this.consume(TokenList.TO_RPAR);
		Statement actions = this.StateOrBlock();
		return new ForStatement(init, cond, incr, actions);
	} public Statement whileState() throws Exception{
		Expression cond = this.expression();
		Statement actions = this.StateOrBlock();
		return new WhileStatement(cond, actions);
	} public Statement doWhileState() throws Exception{
		Statement actions = this.StateOrBlock();
		this.consume(TokenList.TS_WHILE);
		Expression cond = this.expression();
		return new DoWhileStatement(actions, cond);
	}
	public Statement StateOrBlock() throws Exception{
		if(this.getToken(0).type == TokenList.TO_LCURL){ return this.blockState(); }
		return this.statement();
	} public Statement blockState() throws Exception{
		BlockStatement block = new BlockStatement();
		this.consume(TokenList.TO_LCURL);
		while(!this.matches(TokenList.TO_RCURL)){
			block.add(this.statement());
		} return block;
	}
	public Expression expression() throws Exception{
		return this.logicDisjunction();
	}
	public Expression logicDisjunction() throws Exception{
		Expression result = this.logicConjuction();
		while(true){
			if(this.matches(TokenList.TL_OR)){
				result = new ConditionNode(result, "|", this.logicConjuction());
			} break;
		} return result;
	} public Expression logicConjuction() throws Exception{
		Expression result = this.logicEquality();
		while(true){
			if(this.matches(TokenList.TL_AND)){
				result = new ConditionNode(result, "&", this.logicEquality());
			} break;
		} return result;
	} public Expression logicEquality() throws Exception{
		Expression result = this.condition();
		while(true){
			if(this.matches(TokenList.TL_EQUALS)){
				result = new ConditionNode(result, "==", this.condition());
			} else if(this.matches(TokenList.TL_NEQUALS)){
				result = new ConditionNode(result, "!=", this.condition());
			} break;
		} return result;
	} public Expression condition() throws Exception{
		Expression result = this.addition();
		while(true){
			if(this.matches(TokenList.TL_LESS)){
				result = new ConditionNode(result, "<", this.addition());
			} else if(this.matches(TokenList.TL_GREATER)){
				result = new ConditionNode(result, ">", this.addition());
			} else if(this.matches(TokenList.TL_EQLESS)){
				result = new ConditionNode(result, "<=", this.addition());
			} else if(this.matches(TokenList.TL_EQGREATER)){
				result = new ConditionNode(result, ">=", this.addition());
			} break;
		} return result;
	} public Expression addition() throws Exception{
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
