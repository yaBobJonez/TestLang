package test;

import java.util.*;
import AST.*;
import exceptions.*;
import lib.CaseValue;
import lib.UserFunction;

/*
 * In Memory of my loved Grandmother,
 * who passed away 10/05/2020...
 * Rest Easy, It won't get worse...
 */

public class Parser {
	public List<Token> tokens;
	public int size;
	public int position;
	public ParseErrors parseErrors;
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.size = tokens.size();
		this.parseErrors = new ParseErrors();
	}
	public Statement parse() throws Exception{
		this.parseErrors.errors.clear();
		BlockStatement result = new BlockStatement();
		while(!this.matches(TokenList.TS_EOF)){
			try {
				result.add(this.statement());
			} catch(Exception e) {
				this.parseErrors.add(e, this.errorLine());
				this.recover();
			}
		} return result;
	} public int errorLine(){
		if(this.size == 0) return 0;
		else if(this.position >= this.size) return this.tokens.get(this.size - 1).line;
		else return this.tokens.get(this.position).line;
	} public void recover(){
		int prevPos = this.position;
		for(int i = prevPos; i <= this.size; i++){
			this.position = i;
			try {
				this.statement();
				this.position = i;
				return;
			} catch(Exception e){}
		}
	}
	public Statement statement() throws Exception{
		if(matches(TokenList.TS_SEMICOLON)){ return new SemicolonStatement(); }
		else if(matches(TokenList.TI_OUT)){
			return new OutputStatement(this.expression());
		} else if(matches(TokenList.TI_IN)){
			return this.input();
		}else if(matches(TokenList.TS_IF)){
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
		} else if(matches(TokenList.TS_TRY)){
			return this.tryCatch();
		} else if(matches(TokenList.TS_RAISE)){
			return this.raiseState();
		} else if(matches(TokenList.TA_BREAK)){ return new BreakStatement(); }
		else if(matches(TokenList.TA_CONTINUE)){ return new ContinueStatement(); }
		else if(matches(TokenList.TA_RETURN)){ return new ReturnStatement(this.expression()); }
		else if(matches(TokenList.TA_USE)){ return new UseStatement(this.expression()); }
		else if(matches(TokenList.TS_INCLUDE)){ return new IncludeStatement(this.expression()); }
		else if(matches(TokenList.TS_FUNCTION)){
			return this.defineFunction();
		} else if(matches(TokenList.TS_CLASS)){
			return this.declareClass();
		} else if(this.getToken(0).type == TokenList.TS_ID && this.getToken(1).type == TokenList.TO_LPAR){
			return new NodeStatement(this.functionChain(this.qualifiedName()));
		}
		return this.assignmentState();
	}
	public Statement assignmentState() throws Exception{
		if(this.getToken(0).type == TokenList.TO_LBRA){
			return this.destructuringAssignment();
		} Expression assignment = this.expression();
		if(assignment instanceof Statement) return (Statement)assignment;
		else { throw new UnsupportedStatementException(this.getToken(0).value, this.getToken(0).line, this.getToken(0).character); }
	} public DestructuringAssignmentStatement destructuringAssignment() throws Exception{
		this.consume(TokenList.TO_LBRA);
		List<String> vars = new ArrayList<>();
		do {
			if(this.getToken(0).type == TokenList.TS_ID) vars.add(this.consume(TokenList.TS_ID).value);
			else vars.add(null);
			this.matches(TokenList.TO_COMMA);
		} while(!this.matches(TokenList.TO_RBRA));
		this.consume(TokenList.TS_ASSIGN);
		return new DestructuringAssignmentStatement(vars, this.expression());
	} public Statement ifElseState() throws Exception{
		Expression condition = this.expression();
		Statement ifState = this.StateOrBlock();
		matches(TokenList.TS_SEMICOLON);
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
		Statement def = null;
		while(!this.matches(TokenList.TO_RCURL)){
			if(this.getToken(0).type == TokenList.TA_DEFAULT){
				def = this.defaultPattern();
				this.matches(TokenList.TS_SEMICOLON);
				this.consume(TokenList.TO_RCURL); break;
			} cases.add(this.casePattern());
			this.matches(TokenList.TS_SEMICOLON);
		} return new SwitchStatement(expr, cases, def);
	} public Statement tryCatch() throws Exception{
		Statement body = this.blockState();
		Map<String, Statement> catches = new HashMap<>();
		do {
			this.consume(TokenList.TS_CATCH);
			this.consume(TokenList.TO_LPAR);
			String type = this.consume(TokenList.TS_ID).value; //TODO caught throwable variable ($e)
			this.consume(TokenList.TO_RPAR);
			Statement exceptionBody = this.blockState();
			catches.put(type, exceptionBody);
		} while(this.getToken(0).type == TokenList.TS_CATCH);
		return new TryCatchStatement(body, catches);
	} public Statement raiseState() throws Exception{
		String type = this.consume(TokenList.TS_ID).value;
		String message = "";
		if(matches(TokenList.TO_LPAR)){
			message = this.consume(TokenList.TT_STRING).value;
			this.consume(TokenList.TO_RPAR);
		} return new RaiseStatement(type, message);
	}
	public FunctionNode function(Expression varName) throws Exception{
		this.consume(TokenList.TO_LPAR);
		FunctionNode func = new FunctionNode(varName);
		while(!this.matches(TokenList.TO_RPAR)){
			func.addArg(this.expression());
			this.matches(TokenList.TO_COMMA);
		} return func;
	} public Expression functionChain(Expression varName) throws Exception{
		Expression expr = this.function(varName);
		if(this.getToken(0).type == TokenList.TO_LPAR){
			return this.functionChain(expr);
		}
		else if(this.getToken(0).type == TokenList.TS_ACCESS){
			List<Expression> exponents = this.variableExponents();
			if(exponents != null | exponents.isEmpty()) return expr;
			if(this.getToken(0).type == TokenList.TO_LPAR) return this.functionChain(new ContainerAccessNode(expr, exponents));
			else return new ContainerAccessNode(expr, exponents);
		} return expr;
	} public FuncDefStatement defineFunction() throws Exception{
		String name = this.consume(TokenList.TS_ID).value;
		Arguments args = this.arguments();
		Statement body = this.StateOrBlock();
		return new FuncDefStatement(name, args, body);
	} public ValueNode functionVariable() throws Exception{ //TODO remove this method, insert directly.
		Arguments args = this.arguments();
		Statement body = this.StateOrBlock();
		return new ValueNode(new UserFunction(args, body));
	} public Statement declareClass() throws Exception{
		String className = this.consume(TokenList.TS_ID).value;
		ClassDefStatement classSt = new ClassDefStatement(className);
		this.consume(TokenList.TO_LCURL); //TODO extends/implements before this
		while(!matches(TokenList.TO_RCURL)){
			if(matches(TokenList.TS_FUNCTION)) classSt.addMethod(this.defineFunction());
			else if(this.getToken(0).type == TokenList.TS_ID){
				VariableNode target = new VariableNode(this.consume(TokenList.TS_ID));
				Expression expr = new ValueNode((Object)"null");
				if(matches(TokenList.TS_ASSIGN)) expr = this.expression();
				AssignmentNode field = new AssignmentNode((Accessible)target, null, expr);
				classSt.addField(field);
			} else throw new UnsupportedStatementException("invalid class declaration");
			this.matches(TokenList.TS_SEMICOLON);
		} return classSt;
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
	} public InputStatement input() throws Exception{
		String name = this.consume(TokenList.TS_ID).value;
		Expression expr = null;
		if(matches(TokenList.TA_IN)) expr = this.expression();
		return new InputStatement(name, expr);
	}
	public Expression array() throws Exception{
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
		List<Expression> matches = new ArrayList<>();
		do {
			this.consume(TokenList.TA_CASE);
			matches.add(this.expression());
			this.consume(TokenList.TS_COLON);
		} while(this.getToken(0).type == TokenList.TA_CASE);
		Statement body = this.StateOrBlock();
		return new CaseValue(matches, body);
	} public Statement defaultPattern() throws Exception{
		this.consume(TokenList.TA_DEFAULT);
		this.consume(TokenList.TS_COLON);
		return this.StateOrBlock();
	}
	public Arguments arguments() throws Exception{
		Arguments args = new Arguments();
		boolean startsOpt = false;
		this.consume(TokenList.TO_LPAR);
		while(!this.matches(TokenList.TO_RPAR)){
			String name = this.consume(TokenList.TS_ID).value;
			if(this.matches(TokenList.TS_ASSIGN)){
				startsOpt = true;
				args.addOptional(name, this.variable());
			} else if(!startsOpt){
				args.addRequired(name);
			} else throw new UnsupportedStatementException("a required argument cannot be added after an optional");
			this.matches(TokenList.TO_COMMA);
		} return args;
	}
	public Expression expression() throws Exception{
		return this.assignment();
	} public Expression assignment() throws Exception{
		Expression assignment = this.assign();
		if(assignment != null) return assignment;
		return this.ternary();
	} public AssignmentNode assign() throws Exception{
		int pos = this.position;
		Expression target = this.qualifiedName(); //Alters position by one
		if((target == null) || !(target instanceof Accessible)){ this.position = pos; return null; }
		String type = this.getToken(0).type;
		String operator;
		switch(type){
			case TokenList.TS_ASSIGN: operator = null; break;
			case TokenList.TS_PLUSASSIGN: operator = "+"; break;
			case TokenList.TS_MINUSASSING: operator = "-"; break;
			case TokenList.TS_MULTIPLYASSIGN: operator = "*"; break;
			case TokenList.TS_DIVIDEASSIGN: operator = "/"; break;
			default: this.position = pos; return null;
		} this.matches(type);
		Expression expr = this.expression();
		return new AssignmentNode((Accessible)target, operator, expr);
	}
	public Expression ternary() throws Exception{
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
			} else if(this.matches(TokenList.TO_PERIOD)){
				result = new BinOpNode(result, ".", this.multiplication()); continue;
			} break;
		} return result;
	} public Expression multiplication() throws Exception{
		Expression result = this.object();
		while(true){
			if(this.matches(TokenList.TO_MULTIPLY)){
				result = new BinOpNode(result, "*", this.object()); continue;
			} else if(this.matches(TokenList.TO_DIVIDE)){
				result = new BinOpNode(result, "/", this.object()); continue;
			} else if(this.matches(TokenList.TO_MODULO)){
				result = new BinOpNode(result, "%", this.object()); continue;
			} else if(this.matches(TokenList.TO_POWER)){
				result = new BinOpNode(result, "^", this.object()); continue;
			} break;
		} return result;
	} public Expression object() throws Exception{
		if(this.matches(TokenList.TA_NEW)){
			String className = this.consume(TokenList.TS_ID).value;
			this.consume(TokenList.TO_LPAR); //TODO no parentheses — default constructor
			List<Expression> args = new ArrayList<>();
			while(!this.matches(TokenList.TO_RPAR)){
				args.add(this.expression());
				this.matches(TokenList.TO_COMMA);
			} return new ObjectNode(className, args);
		} return this.unary();
	} public Expression unary() throws Exception{
		if(this.matches(TokenList.TO_MINUS)){
			return new UnOpNode('-', this.factor());
		} else if(this.matches(TokenList.TL_NOT)){
			return new UnOpNode('!', this.factor());
		} return this.factor();
	}
	public Expression factor() throws Exception{
		if(matches(TokenList.TO_LPAR)){
			Expression result = this.expression();
			this.matches(TokenList.TO_RPAR);
			return result;
		} else if(matches(TokenList.TS_FUNCTION)){
			return this.functionVariable();
		} else return this.variable();
	} public Expression variable() throws Exception{
		if(this.getToken(0).type == TokenList.TS_ID && this.getToken(1).type == TokenList.TO_LPAR){
			return this.functionChain(new ValueNode(this.consume(TokenList.TS_ID).value));
		}
		Expression varName = this.qualifiedName();
		if(varName != null){
			if(this.getToken(0).type == TokenList.TO_LPAR) return this.functionChain(varName);
			else return varName;
		}
		if(this.getToken(0).type == TokenList.TO_LBRA){
			return this.array();
		} else if(this.getToken(0).type == TokenList.TO_LCURL){
			return this.associativeArray();
		} else return this.value();
	} public Expression qualifiedName() throws Exception{
		Token curr_token = this.getToken(0);
		if(matches(TokenList.TS_ID)){
			List<Expression> exponents = this.variableExponents();
			if((exponents == null) || exponents.isEmpty()) return new VariableNode(curr_token);
			return new ContainerAccessNode(curr_token, exponents);
		} else if(matches(TokenList.TT_CONST)){
			List<Expression> exponents = this.variableExponents();
			if((exponents == null) || exponents.isEmpty()) return new ConstantNode(curr_token);
			return new ContainerAccessNode(curr_token, exponents);
		} else return null;
	} public List<Expression> variableExponents() throws Exception{
		if(!(this.getToken(0).type == TokenList.TO_LBRA) && !(this.getToken(0).type == TokenList.TS_ACCESS)) return Collections.emptyList();
		List<Expression> exponents = new ArrayList<>();
		while(this.getToken(0).type == TokenList.TO_LBRA || this.getToken(0).type == TokenList.TS_ACCESS){
			if(matches(TokenList.TO_LBRA)){ exponents.add(this.expression()); this.consume(TokenList.TO_RBRA); }
			else if(matches(TokenList.TS_ACCESS)){ Expression key = new ValueNode(this.consume(TokenList.TS_ID).value); exponents.add(key); }
		} return exponents;
	} public Expression value() throws Exception{
		Token curr_token = this.getToken(0);
		if(matches(TokenList.TT_INT)){
			Number num;
			try { num = Short.parseShort(curr_token.value); }
			catch(NumberFormatException e) {
				try { num = Integer.parseInt(curr_token.value); }
				catch(NumberFormatException e2) {
					num = Long.parseLong(curr_token.value); //TODO Out Of Bounds Exception
				}
			} return new ValueNode(num);
		} else if(matches(TokenList.TT_DOUBLE)){
			return new ValueNode(Double.parseDouble(curr_token.value));
		} else if(matches(TokenList.TT_STRING)){
			ValueNode expr = new ValueNode(curr_token.value);
			if(this.getToken(0).type == TokenList.TS_ACCESS){
				if(this.getToken(1).type == TokenList.TS_ID & this.getToken(2).type == TokenList.TO_LPAR){
					this.consume(TokenList.TS_ACCESS);
					ValueNode propName = new ValueNode(this.consume(TokenList.TS_ID).value);
					ContainerAccessNode prop = new ContainerAccessNode(expr, Collections.singletonList(propName));
					return this.functionChain(prop);
				} List<Expression> exponents = this.variableExponents();
				if(!exponents.isEmpty() | exponents != null) return new ContainerAccessNode(expr, exponents);
			} return expr;
		} else if(matches(TokenList.TT_BOOL)){
			return new ValueNode(Boolean.parseBoolean(curr_token.value));
		} else if(matches(TokenList.TT_NULL)){
			return new ValueNode((Object)"null");
		} else if(matches(TokenList.TT_INF)){
			return new ValueNode((Object)Double.POSITIVE_INFINITY);
		} else { throw new InvalidExpressionException(this.getToken(0).toString()); }
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
		if(curr_token.type != type){ throw new UnexpectedTokenException(type, curr_token); }
		this.position += 1;
		return curr_token;
	}
}
