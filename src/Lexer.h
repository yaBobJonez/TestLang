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
            string number("");
            bool point = false;
            while(string("0123456789.").find(this->curr_char) != string::npos){
                if(this->curr_char == '.'){
                    if(point == true) break;
                    else { point = true; number += "."; }
                } else number += this->curr_char;
                this->advance();
            } if(point == true) return Token(TokenList::DOUBLE, std::stod(number));
            else return Token(TokenList::INT, std::stod(number));
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
                else if(this->curr_char == '+'){ tokens.push_back(Token(TokenList::ADD, 0)); this->advance(); }
                else if(this->curr_char == '-'){ tokens.push_back(Token(TokenList::SUBTRACT, 0)); this->advance(); }
                else if(this->curr_char == '*'){ tokens.push_back(Token(TokenList::MULTIPLY, 0)); this->advance(); }
                else if(this->curr_char == '/'){ tokens.push_back(Token(TokenList::DIVIDE, 0)); this->advance(); }
                else if(this->curr_char == '%'){ tokens.push_back(Token(TokenList::MODULO, 0)); this->advance(); }
                else if(this->curr_char == '^'){ tokens.push_back(Token(TokenList::POWER, 0)); this->advance(); }
                else { cerr<<endl<<"Invalid expression."; exit(EXIT_FAILURE); }
            } tokens.push_back(Token(TokenList::T_EOF, 0));
            return tokens;
        }
};

#endif