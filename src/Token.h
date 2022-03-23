#ifndef AC_TOKENING
#define AC_TOKENING

#include <iostream>

enum TokenList {
    ID,
    OUTPUT,
    INT,
    DOUBLE,
    BOOL,
    STRING,
    ASSIGN,
    CONCAT,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MODULO,
    POWER,
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