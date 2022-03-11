#ifndef AC_TOKENING
#define AC_TOKENING

#include <iostream>

enum TokenList {
    INT,
    DOUBLE,
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
        double value;
        Token(TokenList type, double value) : type(type), value(value) {}
};
std::ostream& operator<<(std::ostream& stream, const Token& that){
    return stream << "Token[" << that.type << ": " << that.value << "]";
}

#endif