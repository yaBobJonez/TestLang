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
    MAP,
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
    DOT,
    RANGE,
    ELLIPSIS,
    IN,
    BLANK,
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
    UNLESS,
    ELSE,
    MATCH,
    FOR,
    FOREACH,
    WHILE,
    UNTIL,
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