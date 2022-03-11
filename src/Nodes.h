#ifndef AC_AST_NODES
#define AC_AST_NODES

#include <iostream>
#include "Statement.h"
#include <cmath>

class BinaryNode : public Statement {
    protected:
        Statement* left;
        char operator_;
        Statement* right;
    public:
        BinaryNode(Statement* left, char operator_, Statement* right) : left(left), operator_(operator_), right(right){}
        double eval(){
            double left = (*this->left).eval();
            double right = (*this->right).eval();
            switch(this->operator_){
                case '+': return left + right;
                case '-': return left - right;
                case '*': return left * right;
                case '/': return left / right;
                case '%': return std::fmod(left, right);
                case '^': return std::pow(left, right);
                default: throw "Unknown operator "+this->operator_;
            }
        }
};

class UnaryNode : public Statement {
    protected:
        char operator_;
        Statement* right;
    public:
        UnaryNode(char operator_, Statement* right) : operator_(operator_), right(right){}
        double eval(){
            if(this->operator_ == '-') return -(*this->right).eval();
            else throw "Unknown operator "+this->operator_;
        }
};

class ValueNode : public Statement {
    protected:
        double value;
    public:
        ValueNode(double value) : value(value){}
        ValueNode(int value) : value(value){}
        double eval(){
            return this->value;
        }
};

#endif