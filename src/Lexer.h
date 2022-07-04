#ifndef AC_LEXER
#define AC_LEXER

#include <iostream>
#include <vector>
#include "Token.h"

using namespace std;

class Lexer {
    private:
        int position;
        char curr_char;
        string input;
        int size;
    protected:
        void advance(){
            this->position += 1;
            if(this->position >= this->size) this->curr_char = (char)0;
            else this->curr_char = this->input.at(this->position);
        }
        Token buildNumber(){
            std::string number;
            bool point = false;
            while(string("0123456789.").find(this->curr_char) != string::npos){
                if(this->curr_char == '.'){
                    if(point || (this->position+1<this->size?this->input.at(this->position+1)=='.':true)) break;
                    else point = true;
                } number += this->curr_char;
                this->advance();
            } if(point) return Token(TokenList::DOUBLE, number);
            else return Token(TokenList::INT, number);
        }
        Token buildWord(){
        	std::string word;
        	while(std::string("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789").find(this->curr_char) != std::string::npos){
        		word += this->curr_char;
        		this->advance();
        	} if(word == "print") return Token(TokenList::OUTPUT, "");
        	else if(word == "true") return Token(TokenList::BOOL, "1");
        	else if(word == "false") return Token(TokenList::BOOL, "0");
			else if(word == "null") return Token(TokenList::NUL, "");
        	else if(word == "if") return Token(TokenList::IF, "");
        	else if(word == "else") return Token(TokenList::ELSE, "");
			else if(word == "for") return Token(TokenList::FOR, "");
			else if(word == "foreach") return Token(TokenList::FOREACH, "");
			else if(word == "while") return Token(TokenList::WHILE, "");
			else if(word == "do") return Token(TokenList::DO, "");
        	else if(word == "switch") return Token(TokenList::SWITCH, "");
        	else if(word == "case") return Token(TokenList::CASE, "");
        	else if(word == "default") return Token(TokenList::DEFAULT, "");
			else if(word == "break") return Token(TokenList::BREAK, "");
        	else if(word == "continue") return Token(TokenList::CONTINUE, "");
        	else if(word == "in") return Token(TokenList::IN, "");
        	else return Token(TokenList::ID, word);
        }
        Token buildString(char start){
        	std::string str;
        	this->advance();
        	while(this->curr_char != 0)
        		if(this->curr_char == start){ this->advance(); return Token(TokenList::STRING, str); }
        		else{ str += this->curr_char; this->advance(); }
        	std::cerr<<"Unexpected end of string at "<<this->position<<".";
        	std::exit(EXIT_FAILURE);
        }
        Token buildOperator(){
        	std::string op; op+=this->curr_char;
        	this->advance();
        	if(op == ";") return Token(TokenList::SEMICOLON, "");
        	else if(op == "(") return Token(TokenList::LPAR, "");
        	else if(op == ")") return Token(TokenList::RPAR, "");
        	else if(op == "[") return Token(TokenList::LBRACK, "");
        	else if(op == "]") return Token(TokenList::RBRACK, "");
        	else if(op == "{") return Token(TokenList::LBRACE, "");
        	else if(op == "}") return Token(TokenList::RBRACE, "");
        	else if(op == ",") return Token(TokenList::COMMA, "");
        	while(std::string("=+-*/%^.<>!~&|?:").find(this->curr_char) != std::string::npos){
        		op += this->curr_char;
        		this->advance();
        	} if(op == "=") return Token(TokenList::ASSIGN, "");
			else if(op == "><=") return Token(TokenList::ASGNCONCAT, "");
			else if(op == "+=") return Token(TokenList::ASGNADD, "");
			else if(op == "-=") return Token(TokenList::ASGNSUBTRACT, "");
			else if(op == "*=") return Token(TokenList::ASGNMULTIPLY, "");
			else if(op == "/=") return Token(TokenList::ASGNDIVIDE, "");
			else if(op == "%=") return Token(TokenList::ASGNMODULO, "");
			else if(op == "^=") return Token(TokenList::ASGNPOWER, "");
			else if(op == "&=") return Token(TokenList::ASGNBAND, "");
			else if(op == "|=") return Token(TokenList::ASGNBOR, "");
			else if(op == "<<=") return Token(TokenList::ASGNBLSH, "");
			else if(op == ">>=") return Token(TokenList::ASGNBRSH, "");
			else if(op == "++") return Token(TokenList::INCR, "");
			else if(op == "--") return Token(TokenList::DECR, "");
        	else if(op == "+") return Token(TokenList::ADD, "");
        	else if(op == "-") return Token(TokenList::SUBTRACT, "");
        	else if(op == "*") return Token(TokenList::MULTIPLY, "");
        	else if(op == "/") return Token(TokenList::DIVIDE, "");
			else if(op == "^") return Token(TokenList::POWER, "");
        	else if(op == "%") return Token(TokenList::MODULO, "");
        	else if(op == "<>") return Token(TokenList::CONCAT, "");
        	else if(op == "<") return Token(TokenList::LESS, "");
        	else if(op == "<=") return Token(TokenList::LTOREQ, "");
        	else if(op == "!") return Token(TokenList::LNOT, "");
        	else if(op == "==") return Token(TokenList::EQUALS, "");
        	else if(op == "!=") return Token(TokenList::NOTEQ, "");
        	else if(op == ">") return Token(TokenList::GREATER, "");
        	else if(op == ">=") return Token(TokenList::GTOREQ, "");
        	else if(op == "&&") return Token(TokenList::LAND, "");
        	else if(op == "||") return Token(TokenList::LOR, "");
        	else if(op == "..") return Token(TokenList::RANGE, "");
        	else if(op == "?") return Token(TokenList::QUESTION, "");
        	else if(op == ":") return Token(TokenList::COLON, "");
        	else if(op == "&") return Token(TokenList::BAND, "");
        	else if(op == "|") return Token(TokenList::BOR, "");
        	else if(op == "~=") return Token(TokenList::BXOR, "");
        	else if(op == "~") return Token(TokenList::BNOT, "");
        	else if(op == "<<") return Token(TokenList::BLSH, "");
        	else if(op == ">>") return Token(TokenList::BRSH, "");
        	std::cerr<<"Unrecognized operator: "<<op; exit(EXIT_FAILURE);
        }
    public:
        Lexer(string input){
            this->input = input;
            this->size = input.length();
            this->position = -1;
            this->advance();
        }
        std::vector<Token> tokenize(){
            std::vector<Token> tokens;
            while(this->curr_char != (char)0){
                if(this->curr_char == ' ') this->advance();
                else if(string("0123456789").find(this->curr_char) != string::npos) tokens.push_back(this->buildNumber());
                else if(this->curr_char=='"'||this->curr_char=='\'') tokens.push_back(this->buildString(this->curr_char));
                else if(std::string("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789").find(this->curr_char) != std::string::npos) tokens.push_back(this->buildWord());
                else if(std::string(";=+-*/^%()[]{}.<>!~&|,?:").find(this->curr_char) != std::string::npos) tokens.push_back(this->buildOperator());
                else{ std::cerr<<"Illegal character "<<this->curr_char<<"."; exit(EXIT_FAILURE); }
            } tokens.push_back(Token(TokenList::T_EOF, ""));
            return tokens;
        }
};

#endif