#ifndef AC_PARSER
#define AC_PARSER

#include <iostream>
#include <vector>
#include "Token.h"
#include "Statements.h"
#include "Nodes.h"

using namespace std;

/* Happy 20th Anniversary, WarriorS! */

class Parser {
    private:
        vector<Token> tokens;
        int position;
        int size;
        Statement* statement(){
        	Statement* res;
        	if(matches(TokenList::IF)) res = this->ifState(false);
            else if(matches(TokenList::UNLESS)) res = this->ifState(true);
			else if(matches(TokenList::FOR)) res = this->forState();
			else if(matches(TokenList::FOREACH)) res = this->foreachState();
			else if(matches(TokenList::WHILE)) res = this->whileState(false);
			else if(matches(TokenList::UNTIL)) res = this->whileState(true);
			else if(matches(TokenList::DO)) res = this->doWhileState();
			else if(matches(TokenList::FUNCTION)) res = this->funcState();
			else if(matches(TokenList::BREAK)) res = new BreakStatement();
        	else if(matches(TokenList::CONTINUE)) res = new ContinueStatement();
			else if(matches(TokenList::RETURN)) res = new ReturnStatement(this->expression());
			else if(matches(TokenList::SEMICOLON)) return new NullStatement();
        	else {
				Statement* exprState = dynamic_cast<Statement*>(this->expression());
				if(exprState != nullptr) res = exprState;
				else { std::cerr<<"Unsupported statement: "<<this->getToken(0); exit(EXIT_FAILURE); }
			} this->matches(TokenList::SEMICOLON);
        	return res;
        }
        Statement* ifState(bool negated){
            if(matches(TokenList::LBRACE))
                return this->ifStateSweet(negated);
            this->consume(TokenList::LPAR);
            Expression *condition = this->expression();
            if(negated) condition = new UnaryNode("!", condition);
            this->consume(TokenList::RPAR);
            std::vector<Statement *> then, otherwise;
            if (matches(TokenList::LBRACE))
                while (!matches(TokenList::RBRACE)) then.push_back(this->statement());
            else then.push_back(this->statement());
            if (matches(TokenList::ELSE))
                if (matches(TokenList::LBRACE))
                    while (!matches(TokenList::RBRACE)) otherwise.push_back(this->statement());
                else otherwise.push_back(this->statement());
            return new ConditionalStatement(condition, then, otherwise);
        }
        Statement* ifStateSweet(bool negated){
            std::vector<std::pair<Expression*, std::vector<Statement*>>> clauses;
            std::vector<Statement*> defaultClause;
            do{
                if(matches(TokenList::ELSE)){
                    if(clauses.empty()){ std::cerr << "Cannot use <else> without at least one <if> provided."; std::exit(EXIT_FAILURE); }
                    this->consume(TokenList::COLON);
                    if(matches(TokenList::LBRACE))
                        while(!matches(TokenList::RBRACE)) defaultClause.push_back(this->statement());
                    else defaultClause.push_back(this->statement());
                    this->consume(TokenList::RBRACE);
                    break;
                } Expression* cond = this->expression();
                if(negated) cond = new UnaryNode("!", cond);
                this->consume(TokenList::COLON);
                std::vector<Statement*> then;
                if(matches(TokenList::LBRACE))
                    while(!matches(TokenList::RBRACE)) then.push_back(this->statement());
                else then.push_back(this->statement());
                clauses.push_back(std::make_pair(cond, then));
            } while(!matches(TokenList::RBRACE));
            ConditionalStatement* state = new ConditionalStatement(clauses.back().first, clauses.back().second, defaultClause);
            for(int i = clauses.size()-2; i >= 0; i--)
                state = new ConditionalStatement(clauses[i].first, clauses[i].second, {state});
            return state;
        }
		Statement* forState(){
			consume(TokenList::LPAR);
			Statement* init = this->statement();
			Expression* cond;
			if(getToken(0).type == TokenList::SEMICOLON) cond = new ValueNode(new BooleanValue(true));
			else cond = this->expression();
			consume(TokenList::SEMICOLON);
			Statement* incr;
			if(getToken(0).type == TokenList::RPAR) incr = new NullStatement();
			else incr = this->statement();
			consume(TokenList::RPAR);
			std::vector<Statement*> states;
			if(matches(TokenList::LBRACE))
        		while(!matches(TokenList::RBRACE)) states.push_back(this->statement());
        	else states.push_back(this->statement());
			return new ForStatement(init, cond, incr, states);
		}
		Statement* foreachState(){
			consume(TokenList::LPAR);
			ContainerAccessNode* first = static_cast<ContainerAccessNode*>(this->qualifiedName());
			ContainerAccessNode* second = NULL;
			if(matches(TokenList::COLON)) second = static_cast<ContainerAccessNode*>(this->qualifiedName());
			consume(TokenList::IN);
			Expression* cont = this->expression();
			consume(TokenList::RPAR);
			std::vector<Statement*> states;
			if(matches(TokenList::LBRACE))
        		while(!matches(TokenList::RBRACE)) states.push_back(this->statement());
        	else states.push_back(this->statement());
			if(second == NULL) return new ForeachStatement(first, cont, states);
			else return new ForeachStatement(first, second, cont, states);
		}
		Statement* whileState(bool negated){
			consume(TokenList::LPAR);
			Expression* cond = this->expression();
            if(negated) cond = new UnaryNode("!", cond);
			consume(TokenList::RPAR);
			std::vector<Statement*> states;
			if(matches(TokenList::LBRACE))
        		while(!matches(TokenList::RBRACE)) states.push_back(this->statement());
        	else states.push_back(this->statement());
			return new WhileStatement(cond, states);
		}
		Statement* doWhileState(){
			std::vector<Statement*> states;
			if(matches(TokenList::LBRACE))
        		while(!matches(TokenList::RBRACE)) states.push_back(this->statement());
        	else states.push_back(this->statement());
            bool negated = false;
			if(this->matches(TokenList::UNTIL)) negated = true;
            else this->consume(TokenList::WHILE);
			consume(TokenList::LPAR);
			Expression* cond = this->expression();
            if(negated) cond = new UnaryNode("!", cond);
			consume(TokenList::RPAR);
			return new DoWhileStatement(states, cond);
		}
		Statement* funcState(){
			Expression* target = this->qualifiedName();
			if(target == NULL){ std::cerr<<"Cannot create an unnamed function in a statement."; std::exit(EXIT_FAILURE); }
			std::pair<std::string, std::vector<std::pair<std::string, Expression*>>> params = this->parameters();
			std::vector<Statement*> states;
			if(matches(TokenList::LBRACE))
				while(!matches(TokenList::RBRACE)) states.push_back(this->statement());
			else states.push_back(this->statement());
			return new AssignmentNode(static_cast<ContainerAccessNode*>(target), new ValueNode(new FunctionValue(params.second, params.first, states)));
		}
		std::pair<std::string, std::vector<std::pair<std::string, Expression*>>> parameters(){
			consume(TokenList::LPAR);
			std::vector<std::pair<std::string, Expression*>> params;
			bool opt = false;
			std::string variadic = "";
			while(!matches(TokenList::RPAR)){
				if(matches(TokenList::ELLIPSIS)){
                    variadic = this->consume(TokenList::ID).value;
                    this->consume(TokenList::RPAR); break;
                } std::string name = this->consume(TokenList::ID).value;
				if(matches(TokenList::ASSIGN)){
                    params.push_back(std::pair<std::string, Expression*>(name, this->expression()));
                    opt = true;
                } else if(!opt) params.push_back(std::pair<std::string, Expression*>(name, NULL));
				else { std::cerr<<"Required parameters cannot be after optional ones."; std::exit(EXIT_FAILURE); }
				this->matches(TokenList::COMMA);
			} return std::pair<std::string, std::vector<std::pair<std::string, Expression*>>>(variadic, params);
		}
        Pattern* pattern(){
            return this->patternDisjunction();
        }
        Pattern* patternDisjunction(){
            Pattern* result = this->patternConjunction();
            while(true){
                if(matches(TokenList::BOR)){
                    result = new DisjuctionPattern(result, this->patternConjunction()); continue;
                } break;
            } return result;
        }
        Pattern* patternConjunction(){
            Pattern* result = this->patternUnary();
            while(true){
                if(matches(TokenList::BAND)){
                    result = new ConjuctionPattern(result, this->patternUnary()); continue;
                } break;
            } return result;
        }
        Pattern* patternUnary(){
            if(matches(TokenList::LNOT)) return new NegationPattern(this->patternRange());
            return this->patternRange();
        }
        Pattern* patternRange(){
            Pattern* result = this->patternFactor();
            if(matches(TokenList::RANGE)) return new RangePattern(result, this->patternFactor());
            return result;
        }
        Pattern* patternFactor(){
            Expression* fqn = this->qualifiedName();
            if(fqn != NULL) return new BindingPattern(static_cast<ContainerAccessNode*>(fqn));
            if(matches(TokenList::LESS)) return new BinaryPattern("<", this->factor());
            else if(matches(TokenList::LTOREQ)) return new BinaryPattern("<=", this->factor());
            else if(matches(TokenList::GREATER)) return new BinaryPattern(">", this->factor());
            else if(matches(TokenList::GTOREQ)) return new BinaryPattern(">=", this->factor());
            else if(matches(TokenList::MODULO)) return new BinaryPattern("%", this->factor());
            if(matches(TokenList::BLANK)) return new BlankPattern();
            if(matches(TokenList::LBRACK)){
                std::vector<Pattern*> left, right; bool hasRest = false; ContainerAccessNode* rest = nullptr;
                while(!matches(TokenList::RBRACK)){
                    if(hasRest) right.push_back(this->pattern());
                    else {
                        if(matches(TokenList::ELLIPSIS)) {
                            hasRest = true;
                            if(this->getToken(0).type == TokenList::ID)
                                rest = static_cast<ContainerAccessNode*>(this->qualifiedName());
                        } else left.push_back(this->pattern());
                    } this->matches(TokenList::COMMA);
                } return new ArrayPattern(left, right, hasRest, rest);
            } else if(matches(TokenList::LBRACE)){
                std::vector<std::pair<Expression*, Pattern*>> pats; bool hasRest = false; ContainerAccessNode* rest = nullptr;
                while(!matches(TokenList::RBRACE)){
                    if(!hasRest && matches(TokenList::ELLIPSIS)){
                        hasRest = true;
                        if(this->getToken(0).type == TokenList::ID)
                            rest = static_cast<ContainerAccessNode*>(this->qualifiedName());
                    } else {
                        Expression* key = this->expression();
                        this->consume(TokenList::COLON);
                        pats.push_back(std::make_pair(key, this->pattern()));
                    } matches(TokenList::COMMA);
                } return new MapPattern(pats, hasRest, rest);
            } Token curr = this->getToken(0);
            if(matches(TokenList::INT)) return new ConstantPattern(new ValueNode(new IntegerValue(std::stoi(curr.value))));
            else if(matches(TokenList::DOUBLE)) return new ConstantPattern(new ValueNode(new DoubleValue(std::stod(curr.value))));
            else if(matches(TokenList::STRING)) return new ConstantPattern(new ValueNode(new StringValue(curr.value)));
            else if(matches(TokenList::BOOL)) return new ConstantPattern(new ValueNode(new BooleanValue(curr.value == "1")));
            else if(matches(TokenList::FUNCTION)) {
                std::pair<std::string, std::vector<std::pair<std::string, Expression *>>> params = this->parameters();
                std::vector<Statement *> states;
                if (matches(TokenList::LBRACE))
                    while (!matches(TokenList::RBRACE)) states.push_back(this->statement());
                else states.push_back(this->statement());
                return new ConstantPattern(new ValueNode(new FunctionValue(params.second, params.first, states)));
            } else if(matches(TokenList::NUL)) return new ConstantPattern(new ValueNode(new NullValue()));
            if(matches(TokenList::LPAR)){
                Pattern* pat = this->pattern();
                this->consume(TokenList::RPAR);
                return pat;
            } std::cerr << "Unknown pattern specified."; std::exit(EXIT_FAILURE);
        }
        Expression* expression(){
        	return this->assignment();
        }
		Expression* assignment(){
			int pos = this->position;
			Expression* result = this->qualifiedName();
			if(result != NULL){
				ContainerAccessNode* cont = static_cast<ContainerAccessNode*>(result);
				if(matches(TokenList::ASSIGN)) return new AssignmentNode(cont, this->expression());
				else if(matches(TokenList::ASGNCONCAT)) return new AssignmentNode(cont, "><", this->expression());
				else if(matches(TokenList::ASGNADD)) return new AssignmentNode(cont, "+", this->expression());
				else if(matches(TokenList::ASGNSUBTRACT)) return new AssignmentNode(cont, "-", this->expression());
				else if(matches(TokenList::ASGNMULTIPLY)) return new AssignmentNode(cont, "*", this->expression());
				else if(matches(TokenList::ASGNDIVIDE)) return new AssignmentNode(cont, "/", this->expression());
				else if(matches(TokenList::ASGNMODULO)) return new AssignmentNode(cont, "%", this->expression());
				else if(matches(TokenList::ASGNPOWER)) return new AssignmentNode(cont, "^", this->expression());
				else if(matches(TokenList::ASGNBAND)) return new AssignmentNode(cont, "&", this->expression());
				else if(matches(TokenList::ASGNBOR)) return new AssignmentNode(cont, "|", this->expression());
				else if(matches(TokenList::ASGNBLSH)) return new AssignmentNode(cont, "<<", this->expression());
				else if(matches(TokenList::ASGNBRSH)) return new AssignmentNode(cont, ">>", this->expression());
			} this->position = pos;
			return this->ternary();
		}
        Expression* ternary(){
        	Expression* result = this->lgdisjunct();
        	while(true){
        		if(matches(TokenList::QUESTION)){
        			Expression* ifExpr = this->expression();
        			this->consume(TokenList::COLON);
        			result = new TernaryNode(result, ifExpr, this->expression());
        		} break;
        	} return result;
        }
        Expression* lgdisjunct(){
        	Expression* result = this->lgconjunct();
        	while(true){
        		if(matches(TokenList::LOR)){
        			result = new BinaryNode(result, "||", this->lgconjunct()); continue;
        		} break;
        	} return result;
        }
        Expression* lgconjunct(){
        	Expression* result = this->equality();
        	while(true){
        		if(matches(TokenList::LAND)){
        			result = new BinaryNode(result, "&&", this->equality()); continue;
        		} break;
        	} return result;
        }
        Expression* equality(){
        	Expression* result = this->comparison();
        	while(true){
        		if(matches(TokenList::EQUALS)){
        			result = new BinaryNode(result, "==", this->comparison()); continue;
        		} else if(matches(TokenList::NOTEQ)){
        			result = new BinaryNode(result, "!=", this->comparison()); continue;
        		} break;
        	} return result;
        }
        Expression* comparison(){
        	Expression* result = this->bitdisjunct();
        	while(true){
        		if(matches(TokenList::LESS)){
        			result = new BinaryNode(result, "<", this->bitdisjunct()); continue;
        		} else if(matches(TokenList::LTOREQ)){
        			result = new BinaryNode(result, "<=", this->bitdisjunct()); continue;
        		} else if(matches(TokenList::GREATER)){
        			result = new BinaryNode(result, ">", this->bitdisjunct()); continue;
        		} else if(matches(TokenList::GTOREQ)){
        			result = new BinaryNode(result, ">=", this->bitdisjunct()); continue;
        		} else if(matches(TokenList::IN)){
        			result = new BinaryNode(result, "in", this->bitdisjunct()); continue;
        		} break;
        	} return result;
        }
        Expression* bitdisjunct(){
        	Expression* result = this->bitdisjunctexcl();
        	while(true){
        		if(matches(TokenList::BOR)){
        			result = new BinaryNode(result, "|", this->bitdisjunctexcl()); continue;
        		} break;
        	} return result;
        }
        Expression* bitdisjunctexcl(){
        	Expression* result = this->bitconjunct();
        	while(true){
        		if(matches(TokenList::BXOR)){
        			result = new BinaryNode(result, "~=", this->bitconjunct()); continue;
        		} break;
        	} return result;
        }
        Expression* bitconjunct(){
        	Expression* result = this->concatenation();
        	while(true){
        		if(matches(TokenList::BAND)){
        			result = new BinaryNode(result, "&", this->concatenation()); continue;
        		} break;
        	} return result;
        }
        Expression* concatenation(){
        	Expression* result = this->ranging();
        	while(true){
        		if(matches(TokenList::CONCAT)){
        			result = new BinaryNode(result, "<>", this->ranging()); continue;
        		} break;
        	} return result;
        }
        Expression* ranging(){
        	Expression* result = this->shifting();
        	while(true){
        		if(matches(TokenList::RANGE)){
        			result = new BinaryNode(result, "..", this->shifting()); continue;
        		} break;
        	} return result;
        }
        Expression* shifting(){
        	Expression* result = this->addition();
        	while(true){
        		if(matches(TokenList::BLSH)){
        			result = new BinaryNode(result, "<<", this->addition()); continue;
        		} else if(matches(TokenList::BRSH)){
        			result = new BinaryNode(result, ">>", this->addition()); continue;
        		} break;
        	} return result;
        }
        Expression* addition(){
            Expression* result = this->multiplication();
            while(true){
                if(matches(TokenList::ADD)){
                    result = new BinaryNode(result, "+", this->multiplication()); continue;
                } else if(matches(TokenList::SUBTRACT)){
                    result = new BinaryNode(result, "-", this->multiplication()); continue;
                } break;
            } return result;
        }
        Expression* multiplication(){
            Expression* result = this->unary();
            while(true){
                if(matches(TokenList::MULTIPLY)){
                    result = new BinaryNode(result, "*", this->unary()); continue;
                } else if(matches(TokenList::DIVIDE)){
                    result = new BinaryNode(result, "/", this->unary()); continue;
                } else if(matches(TokenList::MODULO)){
                    result = new BinaryNode(result, "%", this->unary()); continue;
                } break;
            } return result;
        }
        Expression* unary(){
            if(matches(TokenList::SUBTRACT)){
                return new UnaryNode("-", this->exponentiation());
            } else if(matches(TokenList::LNOT)){
            	return new UnaryNode("!", this->exponentiation());
            } else if(matches(TokenList::INCR)){
				return new UnaryNode("+_", this->exponentiation());
			} else if(matches(TokenList::DECR)){
				return new UnaryNode("-_", this->exponentiation());
			} else if(matches(TokenList::BNOT)){
            	return new UnaryNode("~", this->exponentiation());
            } return this->exponentiation();
        }
        Expression* exponentiation(){
        	Expression* result = this->factor();
        	while(true){
        		if(matches(TokenList::POWER)){
        			result = new BinaryNode(result, "^", this->factor()); continue;
                } break;
        	} return result;
        }
		Expression* qualifiedName(){
			Token curr = this->getToken(0);
			if(matches(TokenList::ID)){
            	std::vector<Expression*> indices;
            	while(matches(TokenList::LBRACK)){
            		indices.push_back(this->expression());
            		consume(TokenList::RBRACK);
            	} return new ContainerAccessNode(curr.value, indices);
        	} return NULL;
		}
        Expression* factor() {
            Expression *fqn = qualifiedName();
            if (fqn != NULL) {
                if (matches(TokenList::INCR)) return new UnaryNode("_+", fqn);
                else if (matches(TokenList::DECR)) return new UnaryNode("_-", fqn);
                else if (this->getToken(0).type == TokenList::LPAR) {
                    Expression *func = fqn;
                    while (matches(TokenList::LPAR)) {
                        std::vector<Expression *> args;
                        while (!matches(TokenList::RPAR))
                            args.push_back(this->expression()), this->matches(TokenList::COMMA);
                        func = new FunctionNode(func, args);
                    }
                    return func;
                } else return fqn;
            }
            Token curr = this->getToken(0);
            if(matches(TokenList::INT)){
                return new ValueNode(new IntegerValue(std::stoi(curr.value)));
            } else if(matches(TokenList::DOUBLE)){
                return new ValueNode(new DoubleValue(std::stod(curr.value)));
            } else if(matches(TokenList::STRING)){
                return new ValueNode(new StringValue(curr.value));
            } else if(matches(TokenList::BOOL)){
                return new ValueNode(new BooleanValue(curr.value == "1" ? true : false));
            } else if(matches(TokenList::FUNCTION)){
                std::pair<std::string, std::vector<std::pair<std::string, Expression *>>> params = this->parameters();
                std::vector<Statement *> states;
                if (matches(TokenList::LBRACE))
                    while (!matches(TokenList::RBRACE)) states.push_back(this->statement());
                else states.push_back(this->statement());
                return new ValueNode(new FunctionValue(params.second, params.first, states));
            } else if(matches(TokenList::NUL)){
                return new ValueNode(new NullValue());
            } else if(curr.type == TokenList::LBRACK){
                std::size_t i = 1; while(this->getToken(i++).type != TokenList::RBRACK);
                if(this->getToken(i).type == TokenList::ASSIGN){
                    ArrayPattern* pat = static_cast<ArrayPattern*>(this->pattern());
                    this->consume(TokenList::ASSIGN);
                    return new DestructureArrayNode(pat, this->expression());
                } this->consume(TokenList::LBRACK);
                std::vector<Expression*> arr;
                while(!matches(TokenList::RBRACK)){
                    arr.push_back(this->expression());
                    this->matches(TokenList::COMMA);
                } return new ArrayNode(arr);
            } else if(curr.type == TokenList::LBRACE){
                std::size_t i = 1; while(this->getToken(i++).type != TokenList::RBRACE);
                if(this->getToken(i).type == TokenList::ASSIGN){
                    MapPattern* pat = static_cast<MapPattern*>(this->pattern());
                    this->consume(TokenList::ASSIGN);
                    return new DestructureMapNode(pat, this->expression());
                } this->consume(TokenList::LBRACE);
                std::vector<std::pair<Expression*, Expression*>> map;
                while(!matches(TokenList::RBRACE)){
                    Expression* key = this->expression();
                    this->consume(TokenList::COLON);
                    map.push_back(std::make_pair(key, this->expression()));
                    this->matches(TokenList::COMMA);
                } return new MapNode(map);
            } else if(matches(TokenList::MATCH)){
                this->consume(TokenList::LPAR);
                Expression* expr = this->expression();
                this->consume(TokenList::RPAR);
                this->consume(TokenList::LBRACE);
                std::vector<Pattern*> cases; std::vector<std::vector<Statement*>> states;
                while(!matches(TokenList::RBRACE)){
                    cases.push_back(this->pattern());
                    this->consume(TokenList::COLON);
                    std::vector<Statement*> state;
                    if(matches(TokenList::LBRACE))
                        while(!matches(TokenList::RBRACE)) state.push_back(this->statement());
                    else state.push_back(this->statement());
                    states.push_back(state);
                } return new MatchNode(expr, cases, states);
            } else if(matches(TokenList::LPAR)){
            	Expression* expr = this->expression();
            	this->consume(TokenList::RPAR);
            	return expr;
            } std::cerr<<"Unrecognized token "<<std::to_string(curr.type)<<": "<<curr.value<<"."; exit(EXIT_FAILURE);
        }
    public:
        Parser(vector<Token> tokens) : tokens(tokens), position(0), size(tokens.size()){}
        std::vector<Statement*> parse(){
        	std::vector<Statement*> result;
        	while(!matches(TokenList::T_EOF)) result.push_back(this->statement());
        	return result;
        }
        bool matches(TokenList type){
            Token curr_token = this->getToken(0);
            if(curr_token.type != type) return false;
            this->position += 1;
            return true;
        }
        Token getToken(int rel_pos){
            int curr_position = this->position + rel_pos;
            if(curr_position >= this->size) return Token(TokenList::T_EOF, 0);
            return this->tokens.at(curr_position);
        }
        Token consume(TokenList type){
            Token curr_token = this->getToken(0);
            if(curr_token.type != type){ cerr<<"Unexpected token "<<curr_token.value<<"."; exit(EXIT_FAILURE); }
            this->position += 1;
            return curr_token;
        }
};

#endif