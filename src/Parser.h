#ifndef AC_PARSER
#define AC_PARSER

#include <iostream>
#include <vector>
#include "Token.h"
#include "Statements.h"
#include "Nodes.h"

using namespace std;

class Parser {
    private:
        vector<Token> tokens;
        int position;
        int size;
        Statement* statement(){
        	Statement* res;
        	if(matches(TokenList::IF)) res = this->ifState();
        	//else if(matches(TokenList::SWITCH)) res = this->switchState();
			else if(matches(TokenList::FOR)) res = this->forState();
			else if(matches(TokenList::FOREACH)) res = this->foreachState();
			else if(matches(TokenList::WHILE)) res = this->whileState();
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
        Statement* ifState(){
        	this->consume(TokenList::LPAR);
        	Expression* condition = this->expression();
        	this->consume(TokenList::RPAR);
        	std::vector<Statement*> then, otherwise;
        	if(matches(TokenList::LBRACE))
        		while(!matches(TokenList::RBRACE)) then.push_back(this->statement());
        	else then.push_back(this->statement());
        	if(matches(TokenList::ELSE))
        		if(matches(TokenList::LBRACE))
        			while(!matches(TokenList::RBRACE)) otherwise.push_back(this->statement());
        		else otherwise.push_back(this->statement());
        	return new ConditionalStatement(condition, then, otherwise);
        }
        /*Statement* switchState(){
        	consume(TokenList::LPAR);
        	Expression* condition = this->expression();
        	consume(TokenList::RPAR);
        	std::map<std::vector<Expression*>, std::vector<Statement*>> cases;
        	std::vector<Statement*> defcase;
        	consume(TokenList::LBRACE);
        	while(matches(TokenList::CASE)){
        		std::vector<Expression*> matchs; do{
        			matchs.push_back(this->expression());
        			matches(TokenList::COMMA);
        		} while(!matches(TokenList::COLON));
        		std::vector<Statement*> statements;
        		TokenList t; do{ statements.push_back(this->statement());
        		t = this->getToken(0).type; } while(!(t == TokenList::CASE || t == TokenList::DEFAULT || t == TokenList::RBRACE));
        		cases[matchs] = statements;
        	} if(matches(TokenList::DEFAULT)){
        		consume(TokenList::COLON);
        		do{ defcase.push_back(this->statement()); }
        		while(this->getToken(0).type != TokenList::RBRACE);
        	} consume(TokenList::RBRACE);
        	return new SwitchStatement(condition, cases, defcase);
        }*/
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
		Statement* whileState(){
			consume(TokenList::LPAR);
			Expression* cond = this->expression();
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
			consume(TokenList::WHILE);
			consume(TokenList::LPAR);
			Expression* cond = this->expression();
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
				std::string name = this->consume(TokenList::ID).value;
				if(matches(TokenList::ASSIGN)){ params.push_back(std::pair<std::string, Expression*>(name, this->expression())); opt = true; }
				else if(matches(TokenList::ELLIPSIS)){ variadic = name; this->consume(TokenList::RPAR); break; }
				else if(!opt) params.push_back(std::pair<std::string, Expression*>(name, NULL));
				else { std::cerr<<"Required parameters cannot be after optional ones."; std::exit(EXIT_FAILURE); }
				this->matches(TokenList::COMMA);
			} return std::pair<std::string, std::vector<std::pair<std::string, Expression*>>>(variadic, params);
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
        Expression* factor(){
            Expression* fqn = qualifiedName();
			if(fqn != NULL){
				if(matches(TokenList::INCR)) return new UnaryNode("_+", fqn);
				else if(matches(TokenList::DECR)) return new UnaryNode("_-", fqn);
				else if(this->getToken(0).type == TokenList::LPAR){
					Expression* func = fqn;
					while(matches(TokenList::LPAR)){
						std::vector<Expression*> args;
						while(!matches(TokenList::RPAR)) args.push_back(this->expression()), this->matches(TokenList::COMMA);
						func = new FunctionNode(func, args);
					} return func;
				} else return fqn;
			} Token curr = this->getToken(0);
            if(matches(TokenList::INT)){
                return new ValueNode(new IntegerValue(std::stoi(curr.value)));
            } else if(matches(TokenList::DOUBLE)){
                return new ValueNode(new DoubleValue(std::stod(curr.value)));
            } else if(matches(TokenList::STRING)){
            	return new ValueNode(new StringValue(curr.value));
            } else if(matches(TokenList::BOOL)){
            	return new ValueNode(new BooleanValue(curr.value=="1"?true:false));
            } else if(matches(TokenList::FUNCTION)){
				std::pair<std::string, std::vector<std::pair<std::string, Expression*>>> params = this->parameters();
				std::vector<Statement*> states;
				if(matches(TokenList::LBRACE))
        			while(!matches(TokenList::RBRACE)) states.push_back(this->statement());
        		else states.push_back(this->statement());
				return new ValueNode(new FunctionValue(params.second, params.first, states));
			} else if(matches(TokenList::NUL)){
            	return new ValueNode(new NullValue());
            } else if(matches(TokenList::LBRACK)){
				std::vector<Expression*> left, right;
				bool hasRest = false; ContainerAccessNode* rest = NULL;
				while(!matches(TokenList::RBRACK)){
					if(hasRest){
						if(matches(TokenList::DOT)) right.push_back(NULL);
						else right.push_back(this->expression());
					} else {
						if(matches(TokenList::ELLIPSIS)){
							hasRest = true;
							if(this->getToken(0).type == TokenList::ID) rest = static_cast<ContainerAccessNode*>(this->qualifiedName());
						} else if(matches(TokenList::DOT)) left.push_back(NULL);
						else left.push_back(this->expression());
					} matches(TokenList::COMMA);
				} ArrayNode* larr = new ArrayNode(left, right, hasRest, rest);
				if(this->matches(TokenList::ASSIGN)) return new DestructureArrayNode(larr, this->expression());
				return larr;
			} else if(matches(TokenList::LBRACE)){
            	std::vector<std::pair<Expression*, Expression*>> map;
                bool hasRest = false; ContainerAccessNode* rest = NULL;
            	while(!matches(TokenList::RBRACE)){
                    if(matches(TokenList::ELLIPSIS)){
                        hasRest = true;
                        if(this->getToken(0).type == TokenList::ID) rest = static_cast<ContainerAccessNode*>(this->qualifiedName());
                        this->consume(TokenList::RBRACE); break;
                    } else {
                        Expression* first = this->expression();
                        this->consume(TokenList::COLON);
                        if(matches(TokenList::DOT)) map.push_back(std::make_pair(first, nullptr));
                        else map.push_back(std::make_pair(first, this->expression()));
                    } matches(TokenList::COMMA);
            	} MapNode* lmap = new MapNode(map, hasRest, rest);
                if(this->matches(TokenList::ASSIGN)) return new DestructureMapNode(lmap, this->expression());
                return lmap;
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