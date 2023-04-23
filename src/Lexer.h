#ifndef AC_LEXER
#define AC_LEXER

#include <iostream>
#include <vector>
#include <locale>
#include <codecvt>
#include "Token.h"

using namespace std;

class Lexer {
    private:
        int position;
        char curr_char;
        string input;
        int size;
		std::vector<Token> tokens;
    protected:
        void advance(){
            this->position += 1;
            if(this->position >= this->size) this->curr_char = (char)0;
            else this->curr_char = this->input.at(this->position);
        }
        char at(int rel){
            return (this->position+rel >= this->size)? 0 : this->input.at(this->position+rel);
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
        	} if(word == "true") return Token(TokenList::BOOL, "1");
        	else if(word == "false") return Token(TokenList::BOOL, "0");
			else if(word == "null") return Token(TokenList::NUL, "");
        	else if(word == "if") return Token(TokenList::IF, "");
        	else if(word == "unless") return Token(TokenList::UNLESS, "");
            else if(word == "elif"){
                tokens.push_back(Token(TokenList::ELSE, ""));
                return Token(TokenList::IF, "");
            } else if(word == "else") return Token(TokenList::ELSE, "");
            else if(word == "loop"){
                tokens.insert(tokens.end(), {
                    Token(TokenList::WHILE, ""),
                    Token(TokenList::LPAR, ""),
                    Token(TokenList::BOOL, "1") });
                return Token(TokenList::RPAR, "");
            } else if(word == "for") return Token(TokenList::FOR, "");
			else if(word == "foreach") return Token(TokenList::FOREACH, "");
			else if(word == "while") return Token(TokenList::WHILE, "");
			else if(word == "until") return Token(TokenList::UNTIL, "");
			else if(word == "do") return Token(TokenList::DO, "");
        	else if(word == "match") return Token(TokenList::MATCH, "");
			else if(word == "function") return Token(TokenList::FUNCTION, "");
			else if(word == "break") return Token(TokenList::BREAK, "");
        	else if(word == "continue") return Token(TokenList::CONTINUE, "");
			else if(word == "return") return Token(TokenList::RETURN, "");
        	else if(word == "in") return Token(TokenList::IN, "");
        	else if(word == "_") return Token(TokenList::BLANK, "");
        	else return Token(TokenList::ID, word);
        }
        Token buildRawString(){
            std::string str; this->advance();
            while(this->curr_char != '\''){
                switch(this->curr_char){
                    case 0:
                    case '\n':
                        std::cerr<<"Unexpected end of string at "<<this->position<<".";
                        std::exit(EXIT_FAILURE);
                    case '\\':
                        this->advance();
                        if(this->curr_char == '\n'); //TOOD line tracking
                        else if(this->curr_char == '\'');
                        else str += '\\';
                    default: str += this->curr_char;
                } this->advance();
            } this->advance(); return Token(TokenList::STRING_RAW, str);
        }
        Token buildString(){
            std::wstring_convert<std::codecvt_utf8<wchar_t>, wchar_t> toUTF8;
            std::string str; this->advance();
            while(this->curr_char != '"'){
                switch(this->curr_char){
                    case 0:
                    case '\n':
                        std::cerr<<"Unexpected end of string at "<<this->position<<".";
                        std::exit(EXIT_FAILURE);
                    case '\\':
                        this->advance();
                        switch(this->curr_char){
                            case '\n': break; //TOOD line tracking
                            case '"': str += '"'; break;
                            case '\\': str += '\\'; break;
                            case '$': str += "\\$"; break;
                            case 'a': str += '\a'; break;
                            case 'b': str += '\b'; break;
                            case 'f': str += '\f'; break;
                            case 'n': str += '\n'; break;
                            case 'r': str += '\r'; break;
                            case 't': str += '\t'; break;
                            case 'v': str += '\v'; break;
                            case 'u': {
                                std::string codepoint = ""; for(int i=0;i<4;i++){ this->advance(); codepoint += this->curr_char; }
                                if(codepoint.find_first_not_of("0123456789abcdefABCDEF") != std::string::npos){
                                    std::cerr << "Non-hexadecimal Unicode codepoint \""<<codepoint<<"\" at "<<this->position<<".";
                                    std::exit(EXIT_FAILURE);
                                } str += toUTF8.to_bytes(std::stoi(codepoint, nullptr, 16));
                                break; }
                            default:
                                std::cerr << "Unsupported escape sequence '\\"<<this->curr_char<<"' at "<<this->position<<".";
                                std::exit(EXIT_FAILURE);
                        } break;
                    default: str += this->curr_char;
                } this->advance();
            } this->advance(); return Token(TokenList::STRING, str);
        }
        Token buildOperator(){
        	std::string op; op += this->curr_char;
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
			else if(op == "//"){ //TODO TEST comments single-line
                while(this->curr_char != '\n') this->advance();
                this->advance(); return Token(TokenList::T_EOF, ""); }
			else if(op == "/*"){
                while(this->curr_char != 0){
                    if(this->curr_char == '*' && this->at(1) == '/'){ this->advance(); this->advance(); return Token(TokenList::T_EOF, ""); }
                    this->advance();
                } std::cerr << "Unexpected end of file at "<<this->position<<".";
                std::exit(EXIT_FAILURE);
            } else if(op == "+") return Token(TokenList::ADD, "");
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
			else if(op == "...") return Token(TokenList::ELLIPSIS, "");
        	else if(op == "..") return Token(TokenList::RANGE, "");
			else if(op == ".") return Token(TokenList::DOT, "");
        	else if(op == "?") return Token(TokenList::QUESTION, "");
        	else if(op == ":") return Token(TokenList::COLON, "");
            else if(op == "=>") return Token(TokenList::RETEXPR, "");
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
            while(this->curr_char != (char)0){
                if(this->curr_char == ' ') this->advance();
                else if(string("0123456789").find(this->curr_char) != string::npos) tokens.push_back(this->buildNumber());
                else if(this->curr_char=='"') tokens.push_back(this->buildString());
                else if(this->curr_char=='\'') tokens.push_back(this->buildRawString());
                else if(std::string("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789").find(this->curr_char) != std::string::npos) tokens.push_back(this->buildWord());
                else if(std::string(";=+-*/^%()[]{}.<>!~&|,?:").find(this->curr_char) != std::string::npos){
                    Token tk = this->buildOperator(); if(tk.type != TokenList::T_EOF) tokens.push_back(tk);
                } else{ std::cerr<<"Illegal character "<<this->curr_char<<"."; exit(EXIT_FAILURE); }
            } tokens.push_back(Token(TokenList::T_EOF, ""));
            return tokens;
        }
};

#endif