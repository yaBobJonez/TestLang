#ifndef AC_AST_NODES
#define AC_AST_NODES

#include <iostream>
#include <cmath>
#include <vector>
#include <sstream>
#include "Token.h"
#include "Values.h"

class Expression {
    public:
    virtual Value* eval() = 0;
};

class TernaryNode : public Expression {
	public:
		Expression* condition;
		Expression* trueExpr;
		Expression* falseExpr;
		TernaryNode(Expression* cond, Expression* t, Expression* f) : condition(cond), trueExpr(t), falseExpr(f){}
		Value* eval(){
			if(this->condition->eval()->asBoolean()) return this->trueExpr->eval();
			else return this->falseExpr->eval();
		}
};
std::ostream& operator<<(std::ostream& stream, const TernaryNode& that){
	return stream << "("<<that.condition<<"? "<<that.trueExpr<<" : "<<that.falseExpr<<")";
}

class BinaryNode : public Expression {
    public:
        Expression* left;
        std::string operator_;
        Expression* right;
        BinaryNode(Expression* left, std::string operator_, Expression* right) : left(left), operator_(operator_), right(right){}
        Value* eval(){
            Value* left = (*this->left).eval();
            Value* right = (*this->right).eval();
            std::string o = this->operator_;
            if(o== "+") return this->add(left, right);
            else if(o== "-") return this->subtract(left, right);
            else if(o== "*") return this->multiply(left, right);
            else if(o== "/") return this->divide(left, right);
            else if(o== "^") return this->power(left, right);
            else if(o== "%") return this->modulo(left, right);
            else if(o== "<>") return this->concatenate(left, right);
            else if(o== "<") return this->lt(left, right);
            else if(o== "<=") return this->ltOrEq(left, right);
            else if(o== "==") return this->eq(left, right);
            else if(o== "!=") return this->notEq(left, right);
            else if(o== ">") return this->gt(left, right);
            else if(o== ">=") return this->gtOrEq(left, right);
            else if(o== "&&") return this->lAnd(left, right);
            else if(o== "||") return this->lOr(left, right);
            else if(o== "&") return this->bAnd(left, right);
            else if(o== "|") return this->bOr(left, right);
            else if(o== "~=") return this->bXor(left, right);
            else if(o== "<<") return this->bLSh(left, right);
            else if(o== ">>") return this->bRSh(left, right);
            std::cerr << "Unknown operator "<<this->operator_<<"."; std::exit(EXIT_FAILURE);
        }
    private:
    	Value* add(Value* left, Value* right){
    		return new DoubleValue(left->asDouble() + right->asDouble());
    	} Value* subtract(Value* left, Value* right){
    		return new DoubleValue(left->asDouble() - right->asDouble());
    	} Value* multiply(Value* left, Value* right){
    		return new DoubleValue(left->asDouble() * right->asDouble());
    	} Value* divide(Value* left, Value* right){
    		return new DoubleValue(left->asDouble() / right->asDouble());
    	} Value* modulo(Value* left, Value* right){
    		return new DoubleValue(std::fmod(left->asDouble(), right->asDouble()));
    	} Value* power(Value* left, Value* right){
    		return new DoubleValue(std::pow(left->asDouble(), right->asDouble()));
    	} Value* concatenate(Value* left, Value* right){
    		return new StringValue(left->asString() + right->asString());
    	} Value* lt(Value* left, Value* right){
    		return new BooleanValue(left->asDouble() < right->asDouble());
    	} Value* ltOrEq(Value* left, Value* right){
    		return new BooleanValue(left->asDouble() <= right->asDouble());
    	} Value* eq(Value* left, Value* right){
    		return new BooleanValue(left->equals(right));
    	} Value* notEq(Value* left, Value* right){
    		return new BooleanValue(! left->equals(right));
    	} Value* gt(Value* left, Value* right){
    		return new BooleanValue(left->asDouble() > right->asDouble());
    	} Value* gtOrEq(Value* left, Value* right){
    		return new BooleanValue(left->asDouble() >= right->asDouble());
    	} Value* lAnd(Value* left, Value* right){
    		return left->asBoolean()? right : left;
    	} Value* lOr(Value* left, Value* right){
    		return left->asBoolean()? left : right;
    	} Value* bAnd(Value* left, Value* right){
    		return new IntegerValue(left->asInteger() & right->asInteger());
    	} Value* bOr(Value* left, Value* right){
    		return new IntegerValue(left->asInteger() | right->asInteger());
    	} Value* bXor(Value* left, Value* right){
    		return new IntegerValue(left->asInteger() ^ right->asInteger());
    	} Value* bLSh(Value* left, Value* right){
    		return new IntegerValue(left->asInteger() << right->asInteger());
    	} Value* bRSh(Value* left, Value* right){
    		return new IntegerValue(left->asInteger() >> right->asInteger());
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
        Value* eval(){
            Value* right = (*this->right).eval();
            char o = this->operator_;
            if(o== '-') return this->negate(right);
            else if(o== '!') return this->lNot(right);
            else if(o== '~') return this->bNot(right);
            std::cerr<<"Unknown operator "<<this->operator_<<"."; exit(EXIT_FAILURE);
        }
    private:
    	Value* negate(Value* right){
    		return new DoubleValue(- right->asDouble());
    	} Value* lNot(Value* right){
    		return new BooleanValue(! right->asBoolean());
    	} Value* bNot(Value* right){
    		return new IntegerValue(~ right->asInteger());
    	}
};
std::ostream& operator<<(std::ostream& stream, const UnaryNode& that){
	return stream << "("<<that.operator_<<", "<<that.right<<")";
}

class ContainerAccessNode : public Expression {
	public:
	std::string var;
	std::vector<Expression*> path;
	ContainerAccessNode(std::string var, std::vector<Expression*> path) : var(var), path(path){}
	Value* eval(){
		if(this->path.empty()) return Variables::get(this->var);
		else return static_cast<ArrayValue*>(this->getContainer())->get(this->getKey());
	}
	Value* getContainer(){
		Value* cont = Variables::get(this->var);
		for(int i = 0; i < this->path.size()-1; i++){
			if(cont->getType() != TokenList::ARRAY){
				std::cerr<<cont->asString()<<" is not an array value.";
				std::exit(EXIT_FAILURE);
			} cont = static_cast<ArrayValue*>(cont)->get(this->path.at(i)->eval()->asString());
		} return cont;
	} std::string getKey(){
		return this->path.back()->eval()->asString();
	}
};
std::ostream& operator<<(std::ostream& stream, const ContainerAccessNode& that){
    std::ostringstream oss1; std::copy(that.path.begin(), that.path.end(), std::ostream_iterator<Expression*>(oss1, "]["));
    return stream << that.var<<"["<<oss1.str()<<"]";
}

class ValueNode : public Expression {
	public:
	Value* value;
	ValueNode(Value* rvalue) : value(rvalue){}
	Value* eval(){
		return this->value;
	}
};
std::ostream& operator<<(std::ostream& stream, const ValueNode& that){
	return stream << that.value;
}

#endif