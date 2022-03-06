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
                    if(point == true) break;
                    else { point = true; number += "."; }
                } else number += this->curr_char;
                this->advance();
            } if(point == true) return Token(TokenList::DOUBLE, number);
            else return Token(TokenList::INT, number);
        }
        Token buildWord(){
        	std::string word;
        	while(std::string("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789").find(this->curr_char) != std::string::npos){
        		word += this->curr_char;
        		this->advance();
        	} if(word == "print") return Token(TokenList::OUTPUT, "");
        	else return Token(TokenList::ID, word);
        }
        Token buildOperator(){
        	std::string operator_;
        	while(std::string("=+-*/").find(this->curr_char) != std::string::npos){
        		operator_ += this->curr_char;
        		this->advance();
        	} if(operator_ == "=") return Token(TokenList::ASSIGN, "");
        	else if(operator_ == "+") return Token(TokenList::ADD, "");
        	else if(operator_ == "-") return Token(TokenList::SUBTRACT, "");
        	else if(operator_ == "*") return Token(TokenList::MULTIPLY, "");
        	else if(operator_ == "/") return Token(TokenList::DIVIDE, "");
        	else{ std::cerr<<"Unrecognized operator "<<operator_<<"."; exit(EXIT_FAILURE); }
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
                else if(std::string("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789").find(this->curr_char) != std::string::npos) tokens.push_back(this->buildWord());
                else if(std::string("=+-*/").find(this->curr_char) != std::string::npos) tokens.push_back(this->buildOperator());
                else{ std::cerr<<"Illegal character "<<this->curr_char<<"."; exit(EXIT_FAILURE); }
            } tokens.push_back(Token(TokenList::T_EOF, ""));
            return tokens;
        }
};

#endif