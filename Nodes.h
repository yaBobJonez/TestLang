#ifndef AC_AST_NODES
#define AC_AST_NODES

#include <iostream>
#include <cmath>
#include "Token.h"
#include "Values.h"

class Expression {
    public:
    virtual double eval() = 0;
};

class BinaryNode : public Expression {
    public:
        Expression* left;
        char operator_;
        Expression* right;
        BinaryNode(Expression* left, char operator_, Expression* right) : left(left), operator_(operator_), right(right){}
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
std::ostream& operator<<(std::ostream& stream, const BinaryNode& that){
	return stream << "("<<that.left<<", "<<that.operator_<<", "<<that.right<<")";
}

class UnaryNode : public Expression {
    public:
        char operator_;
        Expression* right;
        UnaryNode(char operator_, Expression* right) : operator_(operator_), right(right){}
        double eval(){
            if(this->operator_ == '-') return -(*this->right).eval();
            else{ std::cerr<<"Unknown operator "<<this->operator_<<"."; exit(EXIT_FAILURE); }
        }
};
std::ostream& operator<<(std::ostream& stream, const UnaryNode& that){
	return stream << "("<<that.operator_<<", "<<that.right<<")";
}

class VariableNode : public Expression {
	public:
	Token token;
	VariableNode(Token rvalue) : token(rvalue){}
	double eval(){
		return Variables::get(this->token.value);
	}
};
std::ostream& operator<<(std::ostream& stream, const VariableNode& that){
    return stream << that.token;
}
class NumberNode : public Expression {
	public:
	Token token;
	NumberNode(Token rvalue) : token(rvalue){}
	double eval(){
		return std::stod(this->token.value);
	}
};
std::ostream& operator<<(std::ostream& stream, const NumberNode& that){
	return stream << that.token;
}

#endif