#ifndef AC_PARSER
#define AC_PARSER

#include <iostream>
#include <vector>
#include "Token.h"
#include "Nodes.h"
#include "Statements.h"

using namespace std;

class Parser {
    private:
        vector<Token> tokens;
        int position;
        int size;
        Statement* statement(){
        	if(matches(TokenList::OUTPUT)) return new OutputStatement(this->expression());
        	else return this->assignmentState();
        }
        Statement* assignmentState(){
        	Token curr = this->getToken(0);
        	if(matches(TokenList::ID) && this->getToken(0).type == TokenList::ASSIGN){
        		std::string var = curr.value;
        		consume(TokenList::ASSIGN);
        		return new AssignmentStatement(var, this->expression());
        	} std::cerr<<"Unsupported statement."; exit(EXIT_FAILURE);
        }
        Expression* expression(){
        	return this->concatenation();
        }
        Expression* concatenation(){
        	Expression* result = this->addition();
        	while(true){
        		if(matches(TokenList::CONCAT)){
        			result = new BinaryNode(result, "<>", this->addition()); continue;
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
            Expression* result = this->exponentiation();
            while(true){
                if(matches(TokenList::MULTIPLY)){
                    result = new BinaryNode(result, "*", this->exponentiation()); continue;
                } else if(matches(TokenList::DIVIDE)){
                    result = new BinaryNode(result, "/", this->exponentiation()); continue;
                } break;
            } return result;
        }
        Expression* exponentiation(){
        	Expression* result = this->unary();
        	while(true){
        		if(matches(TokenList::POWER)){
        			result = new BinaryNode(result, "^", this->unary()); continue;
                } else if(matches(TokenList::MODULO)){
                    result = new BinaryNode(result, "%", this->unary()); continue;
                } break;
        	} return result;
        }
        Expression* unary(){
            if(matches(TokenList::SUBTRACT)){
                return new UnaryNode('-', this->factor());
            } return this->factor();
        }
        Expression* factor(){
            Token curr = this->getToken(0);
            if(matches(TokenList::INT)){
                return new ValueNode(curr);
            } else if(matches(TokenList::DOUBLE)){
                return new ValueNode(curr);
            } else if(matches(TokenList::STRING)){
            	return new ValueNode(curr);
            } else if(matches(TokenList::ID)){
            	return new VariableNode(curr);
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