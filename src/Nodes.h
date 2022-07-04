#ifndef AC_AST_NODES
#define AC_AST_NODES

#include <iostream>
#include <cmath>
#include <string>
#include <vector>
#include <map>
#include <sstream>
#include <iterator>
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
            else if(o== "in") return this->contains(left, right);
            else if(o== "..") return this->range(left, right);
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
    	} Value* contains(Value* left, Value* right){
    		TokenList t = right->getType();
    		if(t == TokenList::ARRAY){
    			std::map<std::string, Value*> r = static_cast<ArrayValue*>(right)->container;
    			for(std::pair<std::string, Value*> el : r){
    				if(el.first.find_first_not_of("0123456789")==std::string::npos){
    					if(el.second->asString() == left->asString()) return new BooleanValue(true);
    				} else{
    					if(el.first == left->asString()) return new BooleanValue(true);
    				}
    			} return new BooleanValue(false);
    		} else if(t == TokenList::STRING){
    			std::string r = right->asString();
    			if(r.find(left->asString()) != std::string::npos) return new BooleanValue(true);
    			else return new BooleanValue(false);
    		} std::cerr<<"Value "<<right->asString()<<" is not a container.";
    		std::exit(EXIT_FAILURE);
    	} Value* range(Value* left, Value* right){
    		std::map<std::string, Value*> arr;
    		TokenList t = left->getType();
    		if(t != right->getType()){ std::cerr<<"Types of range values don't match."; std::exit(EXIT_FAILURE); }
    		if(t == TokenList::INT){
    			int l = left->asInteger(), r = right->asInteger();
    			if(l<r) for(int i = 0; i <= r-l; i++) arr[std::to_string(i)] = new IntegerValue(l+i);
    			else for(int i = 0; i <= l-r; i++) arr[std::to_string(i)] = new IntegerValue(l-i);
    		} else if(t == TokenList::STRING){
    			std::string ll = left->asString(), rr = right->asString();
    			if(ll.length() != 1 || rr.length() != 1){ std::cerr<<"Invalid range values specified."; std::exit(EXIT_FAILURE); }
    			char l = ll.at(0), r = rr.at(0);
    			if(l<r) for(int i = 0; i <= r-l; i++) arr[std::to_string(i)] = new StringValue(std::string(1, l+i));
    			else for(int i = 0; i <= l-r; i++) arr[std::to_string(i)] = new StringValue(std::string(1, l-i));
    		} return new ArrayValue(arr);
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

class AssignmentNode : public Expression, public Statement {
	public:
	ContainerAccessNode* left;
	std::string op;
	Expression* right;
	AssignmentNode(ContainerAccessNode* left, std::string op, Expression* right) : left(left), op(op), right(right){}
	AssignmentNode(ContainerAccessNode* left, Expression* right) : left(left), op(""), right(right){}
	Value* eval(){
		if(op== "") this->left->path.empty()?
			Variables::set(this->left->var, this->right->eval())
			: static_cast<ArrayValue*>(this->left->getContainer())->set(this->left->getKey(), this->right->eval());
		else this->left->path.empty()?
			Variables::set(this->left->var, BinaryNode(this->left, this->op, this->right).eval())
			: static_cast<ArrayValue*>(this->left->getContainer())->set(this->left->getKey(), BinaryNode(this->left, this->op, this->right).eval());
		return this->left->eval();
	}
	void execute(){ this->eval(); }
};
std::ostream& operator<<(std::ostream& stream, const AssignmentNode& that){
	std::ostringstream oss1; std::copy(that.left->path.begin(), that.left->path.end(), std::ostream_iterator<Expression*>(oss1, "]["));
	return stream << "Assign{cont = "<<that.left->var<<"["<<oss1.str()<<"]; op = '"<<that.op<<"'; expr = "<<that.right<<"}";
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

class UnaryNode : public Expression {
    public:
        std::string op;
        Expression* right;
        UnaryNode(std::string op, Expression* right) : op(op), right(right){}
        Value* eval(){
            Value* r = this->right->eval();
            if(op== "-") return this->negate(r);
            else if(op== "!") return this->lNot(r);
            else if(op== "~") return this->bNot(r);
			ContainerAccessNode* l = dynamic_cast<ContainerAccessNode*>(this->right);
			if(l == nullptr){ std::cerr<<"Unary in/decrement expected a container."; exit(EXIT_FAILURE); }
			else if(op== "+_") return incr(l);
			else if(op== "-_") return decr(l);
			else if(op== "_+"){ incr(l); return r; }
			else if(op== "_-"){ decr(l); return r; }
            std::cerr<<"Unknown operator "<<this->op<<"."; exit(EXIT_FAILURE);
        }
    private:
    	Value* negate(Value* right){
    		return new DoubleValue(- right->asDouble());
    	} Value* lNot(Value* right){
    		return new BooleanValue(! right->asBoolean());
    	} Value* bNot(Value* right){
    		return new IntegerValue(~ right->asInteger());
    	} Value* incr(ContainerAccessNode* left){
			return AssignmentNode(left, "+", new ValueNode(new DoubleValue(1))).eval();
		} Value* decr(ContainerAccessNode* left){
			return AssignmentNode(left, "-", new ValueNode(new DoubleValue(1))).eval();
		}
};
std::ostream& operator<<(std::ostream& stream, const UnaryNode& that){
	return stream << "("<<that.op<<", "<<that.right<<")";
}

#endif