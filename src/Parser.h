#ifndef AC_PARSER
#define AC_PARSER

#include <iostream>
#include <vector>
#include "Token.h"
#include "Statement.h"
#include "Nodes.h"

using namespace std;

class Parser {
    private:
        vector<Token> tokens;
        int position;
        int size;
        Statement* addition(){
            Statement* result = this->multiplication();
            while(true){
                if(matches(TokenList::ADD)){
                    result = new BinaryNode(result, '+', this->multiplication()); continue; //TODO verify each
                } else if(matches(TokenList::SUBTRACT)){
                    result = new BinaryNode(result, '-', this->multiplication()); continue;
                } break;
            } return result;
        }
        Statement* multiplication(){
            Statement* result = this->unary();
            while(true){
                if(matches(TokenList::MULTIPLY)){
                    result = new BinaryNode(result, '*', this->unary()); continue; //TODO verify each
                } else if(matches(TokenList::DIVIDE)){
                    result = new BinaryNode(result, '/', this->unary()); continue;
                } else if(matches(TokenList::POWER)){
                    result = new BinaryNode(result, '^', this->unary()); continue;
                } else if(matches(TokenList::MODULO)){
                    result = new BinaryNode(result, '%', this->unary()); continue;
                } break;
            } return result;
        }
        Statement* unary(){
            if(matches(TokenList::SUBTRACT)){
                Statement* intlValue = this->value();
                return new UnaryNode('-', intlValue); //TODO verify
            }
            return this->value();
        }
        Statement* value(){
            Token curr = this->getToken(0);
            if(matches(TokenList::INT)){
                Statement* intlValueNode = new ValueNode(curr.value); //TODO clear
                return intlValueNode;
            } else if(matches(TokenList::DOUBLE)){
                return new ValueNode(curr.value); //TODO verify this is okay
            } else { cerr<<endl<<"Unrecognized token "+std::to_string(this->getToken(0).type)+": "+std::to_string(this->getToken(0).value); exit(EXIT_FAILURE); }
        }
    public:
        Parser(vector<Token> tokens) : tokens(tokens), position(0), size(tokens.size()){}
        Statement* parse(){ return this->addition(); }
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
            if(curr_token.type != type){ cerr<<endl<<"Unexpected token "+std::to_string(curr_token.value); exit(EXIT_FAILURE); }
            this->position += 1;
            return curr_token;
        }
};

#endif