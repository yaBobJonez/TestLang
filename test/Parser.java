package test;

import java.util.*;
import AST.*;
import lib.CaseValue;
import lib.UserFunction;

public class Parser {
	public List<Token> tokens;
	public int size;
	public int position;
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.size = tokens.size();
	}
	public Statement parse() throws Exception{
		BlockStatement result = new BlockStatement();
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
		} else if(matches(TokenList.TS_FOREACH)){
			return this.foreachState();
		} else if(matches(TokenList.TS_WHILE)){
			return this.whileState();
		} else if(matches(TokenList.TS_DOWHILE)){
			return this.doWhileState();
		} else if(matches(TokenList.TS_SWITCH)){
			return this.switchState();
		} else if(matches(TokenList.TA_BREAK)){ return new BreakStatement(); }
		else if(matches(TokenList.TA_CONTINUE)){ return new ContinueStatement(); }
		else if(matches(TokenList.TA_RETURN)){ return new ReturnStatement(this.expression()); }
		else if(matches(TokenList.TA_USE)){ return new UseStatement(this.expression()); }
		else if(matches(TokenList.TS_FUNCTION)){
			return this.defineFunction();
		}
		else if(this.getToken(0).type == TokenList.TS_ID && this.getToken(1).type == TokenList.TO_LPAR){
			return new FunctionStatement(this.function());
		}
		return this.assignmentState();
	}
	public Statement assignmentState() throws Exception{
		if(this.getToken(0).type == TokenList.TS_ID && this.getToken(1).type == TokenList.TS_ASSIGN){
			String varName = this.consume(TokenList.TS_ID).value;
			consume(TokenList.TS_ASSIGN);
			return new AssignmentStatement(varName, this.expression());
		} if(this.getToken(0).type == TokenList.TS_ID && this.getToken(1).type == TokenList.TO_LBRA){
			ArrayAccessNode array = this.arrayElement();
			consume(TokenList.TS_ASSIGN);
			return new ArrayAssignmentStatement(array, this.expression());
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
	} public Statement foreachState() throws Exception{
		this.consume(TokenList.TO_LPAR);
		String key = this.consume(TokenList.TS_ID).value;
		String value = null;
		if(this.matches(TokenList.TS_COLON)){
			value = this.consume(TokenList.TS_ID).value;
		} this.consume(TokenList.TA_IN);
		Expression array = this.expression();
		this.consume(TokenList.TO_RPAR);
		Statement body = this.StateOrBlock();
		return new ForeachStatement(key, value, array, body);
	} public Statement whileState() throws Exception{
		Expression cond = this.expression();
		Statement actions = this.StateOrBlock();
		return new WhileStatement(cond, actions);
	} public Statement doWhileState() throws Exception{
		Statement actions = this.StateOrBlock();
		this.consume(TokenList.TS_WHILE);
		Expression cond = this.expression();
		return new DoWhileStatement(actions, cond);
	} public Statement switchState() throws Exception{
		this.consume(TokenList.TO_LPAR);
		Expression expr = this.expression();
		this.consume(TokenList.TO_RPAR);
		this.consume(TokenList.TO_LCURL);
		List<CaseValue> cases = new ArrayList<>();
		while(!this.matches(TokenList.TO_RCURL)){
			cases.add(this.casePattern());
			this.matches(TokenList.TS_SEMICOLON);
		} return new SwitchStatement(expr, cases);
	} public FunctionNode function() throws Exception{
		String name = this.consume(TokenList.TS_ID).value;
		this.consume(TokenList.TO_LPAR);
		FunctionNode func = new FunctionNode(name);
		while(!this.matches(TokenList.TO_RPAR)){
			func.addArg(this.expression());
			this.matches(TokenList.TO_COMMA);
		} return func;
	} public FuncDefStatement defineFunction() throws Exception{
		String name = this.consume(TokenList.TS_ID).value;
		this.consume(TokenList.TO_LPAR);
		List<String> args = new ArrayList<>();
		while(!this.matches(TokenList.TO_RPAR)){
			args.add(this.consume(TokenList.TS_ID).value);
			this.matches(TokenList.TO_COMMA);
		} Statement body = this.StateOrBlock();
		return new FuncDefStatement(name, args, body);
	} public ValueNode functionVariable() throws Exception{
		this.consume(TokenList.TO_LPAR);
		List<String> args = new ArrayList<>();
		while(!this.matches(TokenList.TO_RPAR)){
			args.add(this.consume(TokenList.TS_ID).value);
			this.matches(TokenList.TO_COMMA);
		} Statement body = this.StateOrBlock();
		return new ValueNode(new UserFunction(args, body));
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
	public ArrayAccessNode arrayElement() throws Exception{
		String varName = this.consume(TokenList.TS_ID).value;
		List<Expression> path = new ArrayList<>();
		do {
			consume(TokenList.TO_LBRA);
			path.add(this.expression());
			consume(TokenList.TO_RBRA);
		} while(this.getToken(0).type == TokenList.TO_LBRA);
		return new ArrayAccessNode(varName, path);
	} public Expression array() throws Exception{
		consume(TokenList.TO_LBRA);
		List<Expression> elements = new ArrayList<>();
		while(!this.matches(TokenList.TO_RBRA)){
			elements.add(this.expression());
			this.matches(TokenList.TO_COMMA);
		} return new ArrayNode(elements);
	} public Expression associativeArray() throws Exception{
		consume(TokenList.TO_LCURL);
		Map<Expression, Expression> elements = new HashMap<>();
		while(!this.matches(TokenList.TO_RCURL)){
			Expression key = this.factor();
			consume(TokenList.TS_COLON);
			Expression value = this.expression();
			elements.put(key, value);
			matches(TokenList.TO_COMMA);
		} return new MapNode(elements);
	} public CaseValue casePattern() throws Exception{
		this.consume(TokenList.TA_CASE);
		Expression match = this.expression();
		this.consume(TokenList.TS_COLON);
		Statement body = this.StateOrBlock();
		return new CaseValue(match, body);
	}
	public Expression expression() throws Exception{
		return this.ternary();
	} public Expression ternary() throws Exception{
		Expression result = this.logicDisjunction();
		if(this.matches(TokenList.TS_QUESTION)){
			Expression trueExpr = this.expression();
			this.consume(TokenList.TS_COLON);
			Expression falseExpr = this.expression();
			return new TernaryNode(result, trueExpr, falseExpr);
		} return result;
	} public Expression logicDisjunction() throws Exception{
		Expression result = this.logicConjuction();
		while(true){ //TODO single if statements without while/break where possible (after everything else). 
			if(this.matches(TokenList.TL_OR)){
				result = new LogicNode(result, "|", this.logicConjuction());
			} break;
		} return result;
	} public Expression logicConjuction() throws Exception{
		Expression result = this.logicEquality();
		while(true){
			if(this.matches(TokenList.TL_AND)){
				result = new LogicNode(result, "&", this.logicEquality());
			} break;
		} return result;
	} public Expression logicEquality() throws Exception{
		Expression result = this.condition();
		while(true){
			if(this.matches(TokenList.TL_EQUALS)){
				result = new LogicNode(result, "==", this.condition());
			} else if(this.matches(TokenList.TL_NEQUALS)){
				result = new LogicNode(result, "!=", this.condition());
			} break;
		} return result;
	} public Expression condition() throws Exception{
		Expression result = this.addition();
		while(true){
			if(this.matches(TokenList.TL_LESS)){
				result = new LogicNode(result, "<", this.addition());
			} else if(this.matches(TokenList.TL_GREATER)){
				result = new LogicNode(result, ">", this.addition());
			} else if(this.matches(TokenList.TL_EQLESS)){
				result = new LogicNode(result, "<=", this.addition());
			} else if(this.matches(TokenList.TL_EQGREATER)){
				result = new LogicNode(result, ">=", this.addition());
			} break;
		} return result;
	} public Expression addition() throws Exception{
		Expression result = this.multiplication();
		while(true){
			if(this.matches(TokenList.TO_PLUS)){
				result = new BinOpNode(result, "+", this.multiplication()); continue;
			} else if(this.matches(TokenList.TO_MINUS)){
				result = new BinOpNode(result, "-", this.multiplication()); continue;
			} break;
		} return result;
	} public Expression multiplication() throws Exception{
		Expression result = this.unary();
		while(true){
			if(this.matches(TokenList.TO_MULTIPLY)){
				result = new BinOpNode(result, "*", this.unary()); continue;
			} else if(this.matches(TokenList.TO_DIVIDE)){
				result = new BinOpNode(result, "/", this.unary()); continue;
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
			return new ValueNode(Double.parseDouble(curr_token.value));
		} else if(matches(TokenList.TT_DOUBLE)){
			return new ValueNode(Double.parseDouble(curr_token.value));
		} else if(this.getToken(0).type == TokenList.TS_ID && this.getToken(1).type == TokenList.TO_LBRA){
			return this.arrayElement();
		} else if(this.getToken(0).type == TokenList.TS_ID && this.getToken(1).type == TokenList.TO_LPAR){
			return this.function();
		} else if(this.getToken(0).type == TokenList.TO_LBRA){
			return this.array();
		} else if(this.getToken(0).type == TokenList.TO_LCURL){
			return this.associativeArray();
		} else if(matches(TokenList.TS_ID)){
			return new VariableNode(curr_token);
		} else if(matches(TokenList.TT_STRING)){
			return new ValueNode(curr_token.value);
		} else if(matches(TokenList.TT_BOOL)){
			return new ValueNode(Boolean.parseBoolean(curr_token.value));
		} else if(matches(TokenList.TS_FUNCTION)){
			return this.functionVariable();
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
