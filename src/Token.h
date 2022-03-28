#ifndef AC_TOKENING
#define AC_TOKENING

#include <iostream>

enum TokenList {
    SEMICOLON,
    ID,
    OUTPUT,
    INT,
    DOUBLE,
    BOOL,
    STRING,
    LPAR,
    RPAR,
    LBRACE,
    RBRACE,
    ASSIGN,
    CONCAT,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MODULO,
    POWER,
    QUESTION,
    COLON,
    LESS,
    LTOREQ,
    EQUALS,
    NOTEQ,
    LNOT,
    GREATER,
    GTOREQ,
    LAND,
    LOR,
    BNOT,
    BAND,
    BOR,
    BXOR,
    BLSH,
    BRSH,
    IF,
    ELSE,
    T_EOF
};

class Token {
    public:
        TokenList type;
        std::string value;
        Token(TokenList type, std::string value) : type(type), value(value) {}
};
std::ostream& operator<<(std::ostream& stream, const Token& that){
    return stream << "Token[" << that.type << ": " << that.value << "]";
}

#endif