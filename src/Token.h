#ifndef AC_TOKENING
#define AC_TOKENING

#include <iostream>

enum TokenList {
    SEMICOLON,
    COMMA,
    ID,
    NUL,
    INT,
    DOUBLE,
    BOOL,
    STRING,
    ARRAY,
    LPAR,
    RPAR,
    LBRACK,
    RBRACK,
    LBRACE,
    RBRACE,
    ASSIGN,
    ASGNCONCAT,
    ASGNADD,
    ASGNSUBTRACT,
    ASGNMULTIPLY,
    ASGNDIVIDE,
    ASGNMODULO,
    ASGNPOWER,
    ASGNBAND,
    ASGNBOR,
    ASGNBLSH,
    ASGNBRSH,
    INCR,
    DECR,
    CONCAT,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MODULO,
    POWER,
    QUESTION,
    COLON,
    RANGE,
    ELLIPSIS,
    IN,
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
    SWITCH,
    CASE,
    DEFAULT,
    FOR,
    FOREACH,
    WHILE,
    DO,
    FUNCTION,
    BREAK,
    CONTINUE,
    RETURN,
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