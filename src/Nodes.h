#ifndef AC_AST_NODES
#define AC_AST_NODES

#include <iostream>
#include <cmath>
#include <string>
#include <vector>
#include <map>
#include <sstream>
#include <iterator>
#include <algorithm>
#include <unordered_set>
#include "Token.h"
#include "Values.h"

/*class Expression {
    public:
    virtual Value* eval() = 0;
};*/

//Expressions

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
    		if(t == TokenList::MAP){
    			ordered_map r = static_cast<MapValue*>(right)->container;
    			return new BooleanValue(r.contains(left->asString()));
    		} else if(t == TokenList::ARRAY){
				std::vector<Value*> r = static_cast<ArrayValue*>(right)->container;
                for(Value* el : r) if(el->equals(left)) return new BooleanValue(true);
                return new BooleanValue(false);
			} else if(t == TokenList::STRING){
    			std::string r = right->asString();
    			if(r.find(left->asString()) != std::string::npos) return new BooleanValue(true);
    			else return new BooleanValue(false);
    		} std::cerr<<"Value "<<right->asString()<<" is not a container.";
    		std::exit(EXIT_FAILURE);
    	} Value* range(Value* left, Value* right){
    		TokenList t = left->getType();
    		if(t != right->getType()){ std::cerr<<"Types of range values don't match."; std::exit(EXIT_FAILURE); }
			std::vector<Value*> arr;
    		if(t == TokenList::INT){
    			int l = left->asInteger(), r = right->asInteger();
    			if(l<r) for(int i = l; i <= r; i++) arr.push_back(new IntegerValue(i));
    			else for(int i = r; i >= l; i--) arr.push_back(new IntegerValue(i));
    		} else if(t == TokenList::STRING){
    			std::string ll = left->asString(), rr = right->asString();
    			if(ll.length() != 1 || rr.length() != 1){ std::cerr<<"Invalid range values specified."; std::exit(EXIT_FAILURE); }
    			char l = ll.at(0), r = rr.at(0);
    			if(l<r) for(char i = l; i <= r; i++) arr.push_back(new StringValue(std::string(1, i)));
    			else for(char i = r; i >= l; i--) arr.push_back(new StringValue(std::string(1, i)));
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
		TokenList t = Variables::get(this->var)->getType();
		if(t == TokenList::MAP) return static_cast<MapValue*>(this->getContainer())->get(getKey()->asString());
		else if(t == TokenList::ARRAY) return static_cast<ArrayValue*>(this->getContainer())->get(getKey()->asInteger());
	}
	Value* getContainer(){
		Value* cont = Variables::get(this->var);
		for(int i = 0; i < this->path.size()-1; i++){
			if(cont->getType() == TokenList::MAP) cont = static_cast<MapValue*>(cont)->get(this->path.at(i)->eval()->asString());
			else if(cont->getType() == TokenList::ARRAY) cont = static_cast<ArrayValue*>(cont)->get(this->path.at(i)->eval()->asInteger());
			else{
				std::cerr<<cont->asString()<<" is not a container value.";
				std::exit(EXIT_FAILURE);
			}
		} return cont;
	} Value* getKey(){
		return this->path.back()->eval();
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
		if(op== "" && this->left->path.empty()) Variables::set(this->left->var, this->right->eval());
		else if(op== ""){
			Value* c = this->left->getContainer();
			if(c->getType()==TokenList::MAP) static_cast<MapValue*>(c)->set(this->left->getKey()->asString(), this->right->eval());
			else static_cast<ArrayValue*>(c)->set(this->left->getKey()->asInteger(), this->right->eval());
		} else if(this->left->path.empty()) Variables::set(this->left->var, BinaryNode(this->left, this->op, this->right).eval());
		else {
			Value* c = this->left->getContainer();
			if(c->getType()==TokenList::MAP) static_cast<MapValue*>(c)->set(this->left->getKey()->asString(), BinaryNode(this->left, this->op, this->right).eval());
			else static_cast<ArrayValue*>(c)->set(this->left->getKey()->asInteger(), BinaryNode(this->left, this->op, this->right).eval());
		} return this->left->eval();
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

class MapNode : public Expression {
	public:
	std::vector<std::pair<Expression*, Expression*>> map;
	bool hasRest; ContainerAccessNode* rest;
	MapNode(std::vector<std::pair<Expression*, Expression*>> map, bool hasRest, ContainerAccessNode* rest)
		: map(map), hasRest(hasRest), rest(rest){}
	Value* eval(){
        if(this->hasRest || std::find_if(this->map.begin(), this->map.end(),
         [](std::pair<Expression*, Expression*> el){ return el.second == nullptr; }) != this->map.end())
            { std::cerr<<"Cannot skip map pairs in a non-destructuring expression."; std::exit(EXIT_FAILURE); }
        ordered_map map;
        for(ordered_map::size_type i = 0; i < this->map.size(); i++)
            map.put(this->map[i].first->eval()->asString(), this->map[i].second->eval());
        return new MapValue(map);
	}
};
std::ostream& operator<<(std::ostream& stream, const MapNode& that){
	//TODO
}

class ArrayNode : public Expression {
	public:
	std::vector<Expression*> left, right;
	bool hasRest; ContainerAccessNode* rest;
	ArrayNode(std::vector<Expression*> left, std::vector<Expression*> right, bool hasRest, ContainerAccessNode* rest)
		: left(left), right(right), hasRest(hasRest), rest(rest){}
	Value* eval(){
		if(this->hasRest || std::find(this->left.begin(), this->left.end(), nullptr) != this->left.end())
			{ std::cerr<<"Cannot skip array elements in a non-destructuring expression."; std::exit(EXIT_FAILURE); }
		std::vector<Value*> arr; for(Expression* expr : this->left) arr.push_back(expr->eval()); return new ArrayValue(arr);
	}
};
std::ostream& operator<<(std::ostream& stream, const ArrayNode& that){
	std::ostringstream oss1; std::copy(that.left.begin(), that.left.end(), std::ostream_iterator<Expression*>(oss1, ", "));
	std::ostringstream oss2; std::copy(that.right.begin(), that.right.end(), std::ostream_iterator<Expression*>(oss2, ", "));
	return stream << "["+oss1.str()+", "<<that.rest<<(that.hasRest?"..., ":"")+oss2.str()+"]";
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

class FunctionNode : public Expression, public Statement {
	public:
		Expression* func;
		std::vector<Expression*> args;
		FunctionNode(Expression* func, std::vector<Expression*> args) : func(func), args(args){}
		Value* eval(){
			Value* func = this->func->eval();
			if(func->getType() != TokenList::FUNCTION){ std::cerr<<"Cannot call a non-function value."; std::exit(EXIT_FAILURE); }
			return static_cast<FunctionValue*>(func)->execute(this->args);
		}
		void execute(){ this->eval(); }
};
std::ostream& operator<<(std::ostream& stream, const FunctionNode& that){
	std::ostringstream oss1; std::copy(that.args.begin(), that.args.end(), std::ostream_iterator<Expression*>(oss1, ", "));
	return stream << "Call{func = "<<that.func<<"; args = "<<oss1.str()<<"}";
}

class DestructureArrayNode : public Expression, public Statement {
	public:
		ArrayNode* left;
		Expression* right;
		DestructureArrayNode(ArrayNode* left, Expression* right) : left(left), right(right){}
		Value* eval(){
			Value* contRaw = this->right->eval();
			if(contRaw->getType() != TokenList::ARRAY){ std::cerr<<"Cannot array destructure a non-array value."; std::exit(EXIT_FAILURE); }
			std::vector<Value*> array = static_cast<ArrayValue*>(contRaw)->container;
			int minSize = this->left->left.size() + this->left->right.size();
			if(!this->left->hasRest && array.size() != minSize) return new BooleanValue(false);
			else if(array.size() < minSize) return new BooleanValue(false);
			for(int i = 0; i < this->left->left.size(); i++){
				if(this->left->left[i] == NULL) continue; if(dynamic_cast<ContainerAccessNode*>(this->left->left[i])) continue;
				if(!this->left->left[i]->eval()->equals(array[i])) return new BooleanValue(false);
			} minSize = array.size()-this->left->right.size();
			for(int i = 0; i < this->left->right.size(); i++){
				if(this->left->right[i] == NULL) continue; if(dynamic_cast<ContainerAccessNode*>(this->left->right[i])) continue;
				if(!this->left->right[i]->eval()->equals(array[minSize+i])) return new BooleanValue(false);
			} for(int i = 0; i < this->left->left.size(); i++) if(ContainerAccessNode* l = dynamic_cast<ContainerAccessNode*>(this->left->left[i]))
				AssignmentNode(l, new ValueNode(array[i])).execute();
			for(int i = 0; i < this->left->right.size(); i++) if(ContainerAccessNode* r = dynamic_cast<ContainerAccessNode*>(this->left->right[i]))
				AssignmentNode(r, new ValueNode(array[minSize+i])).execute();
			if(this->left->rest != NULL){
				std::vector<Value*> slice(array.cbegin()+this->left->left.size(), array.cend()-this->left->right.size());
				AssignmentNode(this->left->rest, new ValueNode(new ArrayValue(slice))).execute();
			} return new BooleanValue(true);
		}
		void execute(){ this->eval(); }
};
//TODO destructure array to string

class DestructureMapNode : public Expression, public Statement {
    public:
        MapNode* left;
        Expression* right;
        DestructureMapNode(MapNode* left, Expression* right) : left(left), right(right){}
        Value* eval(){
            Value* contRaw = this->right->eval();
            if(contRaw->getType() != TokenList::MAP){ std::cerr << "Cannot map destructure a non-map value."; std::exit(EXIT_FAILURE); }
            ordered_map map = static_cast<MapValue*>(contRaw)->container;
            int minSize = this->left->map.size();
            if(!this->left->hasRest && map.size() != minSize) return new BooleanValue(false);
            else if(map.size() < minSize) return new BooleanValue(false);
            std::vector<std::pair<std::string, Expression*>> evalMap;
            std::unordered_set<std::string> requestedKeys;
            for(std::pair<Expression*, Expression*> pair : this->left->map) {
                std::string key = pair.first->eval()->asString();
                evalMap.push_back(std::make_pair(key, pair.second));
                requestedKeys.insert(key);
            }
            for(ordered_map::size_type i = 0; i < minSize; i++){
                std::string key = evalMap[i].first; Expression* value = evalMap[i].second;
                if(!map.contains(key)) return new BooleanValue(false);
                if(value == nullptr || dynamic_cast<ContainerAccessNode*>(value)) continue;
                if(!value->eval()->equals(map[key])) return new BooleanValue(false);
            } for(ordered_map::size_type i = 0; i < minSize; i++){
                std::string key = evalMap[i].first; Expression* value = evalMap[i].second;
                if(ContainerAccessNode* r = dynamic_cast<ContainerAccessNode*>(value))
                    AssignmentNode(r, new ValueNode(map[key])).execute();
            } if(this->left->rest != NULL){
                ordered_map restMap;
                for(ordered_map::size_type i = 0; i < map.size(); i++) if(requestedKeys.find(map[i]) == requestedKeys.end())
                    restMap.put(map[i], map[map[i]]);
                AssignmentNode(this->left->rest, new ValueNode(new MapValue(restMap))).execute();
            } return new BooleanValue(true);
        }
        void execute(){ this->eval(); }
};
//TODO map destructuring to string

//Patterns

class Pattern {
    public:
    virtual bool resolve(Value* value);
};
class ConstantPattern : public Pattern {
    private:
    ValueNode* expr;
    public:
    ConstantPattern(ValueNode* expr) : expr(expr){}
    bool resolve(Value* value){
        return value->equals(this->expr->eval());
    }
};
class RangePattern : public Pattern {
    private:
    BinaryNode* expr;
    public:
    RangePattern(BinaryNode* expr) : expr(expr){}
    bool resolve(Value* value){
        return BinaryNode(new ValueNode(value), "in", this->expr).eval()->asBoolean();
    }
};
class BindingPattern : public Pattern {
    private:
    ContainerAccessNode* expr;
    public:
    BindingPattern(ContainerAccessNode* expr) : expr(expr){}
    bool resolve(Value* value){
        AssignmentNode(this->expr, new ValueNode(value)).execute();
        return true;
    }
};
class BinaryPattern : public Pattern {
    private:
    std::string op;
    Expression* expr;
    public:
    BinaryPattern(Expression* expr) : expr(expr){}
    bool resolve(Value* value){
        if(this->op == "%") return ! BinaryNode(new ValueNode(value), "%", this->expr).eval()->asBoolean();
        else return BinaryNode(new ValueNode(value), this->op, this->expr).eval()->asBoolean();
    }
};
class NegationPattern : public Pattern {
    private:
    Pattern* pat;
    public:
    NegationPattern(Pattern* pat) : pat(pat){}
    bool resolve(Value* value){
        return ! this->pat->resolve(value);
    }
};
class ConjuctionPattern : public Pattern {
    private:
    Pattern *pat1, *pat2;
    public:
    ConjuctionPattern(Pattern* pat1, Pattern* pat2) : pat1(pat1), pat2(pat2){}
    bool resolve(Value* value){
        return this->pat1->resolve(value) & this->pat2->resolve(value);
    }
};
class DisjuctionPattern : public Pattern {
    private:
    Pattern *pat1, *pat2;
    public:
    DisjuctionPattern(Pattern* pat1, Pattern* pat2) : pat1(pat1), pat2(pat2){}
    bool resolve(Value* value){
        return this->pat1->resolve(value) | this->pat2->resolve(value);
    }
};
class BlankPattern : public Pattern {
    public: BlankPattern(){}
    bool resolve(Value* value){ return true; }
};
class ExpressionPattern : public Pattern {
    private:
    Expression* expr;
    public:
    ExpressionPattern(Expression* expr) : expr(expr){}
    bool resolve(Value* value){
        return this->expr->eval()->asBoolean();
    }
};
class ArrayPattern : public Pattern {
    private:
    std::vector<Pattern*> left, right;
    bool hasRest; ContainerAccessNode* rest;
    public:
    ArrayPattern(std::vector<Pattern*> left, std::vector<Pattern*> right, bool hasRest, ContainerAccessNode* rest)
        : left(left), right(right), hasRest(hasRest), rest(rest){}
    bool resolve(Value* value){
        if(value->getType() != TokenList::ARRAY){ std::cerr << "Cannot array destructure a non-array value."; std::exit(EXIT_FAILURE); }
        std::vector<Value*> array = static_cast<ArrayValue*>(value)->container;
        int minSize = this->left.size() + this->right.size();
        if(!this->hasRest && array.size() != minSize) return false;
        if(array.size() < minSize) return false;
        for(std::vector<Value*>::size_type i = 0; i < this->left.size(); i++){
            if(dynamic_cast<BindingPattern*>(this->left[i])) continue;
            if(!this->left[i]->resolve(array[i])) return false;
        } minSize = array.size() - this->right.size();
        for(std::vector<Value*>::size_type i = 0; i < this->right.size(); i++){
            if(dynamic_cast<BindingPattern*>(this->right[i])) continue;
            if(!this->right[i]->resolve(array[minSize+i])) return false;
        } for(std::vector<Value*>::size_type i = 0; i < this->left.size(); i++)
            if(BindingPattern* pat = dynamic_cast<BindingPattern*>(this->left[i])) pat->resolve(array[i]);
        for(std::vector<Value*>::size_type i = 0; i < this->right.size(); i++)
            if(BindingPattern* pat = dynamic_cast<BindingPattern*>(this->right[i])) pat->resolve(array[minSize+i]);
        if(this->rest != nullptr){
            std::vector<Value*> slice(array.begin()+this->left.size(), array.end()-this->right.size());
            AssignmentNode(this->rest, new ValueNode(new ArrayValue(slice))).execute();
        } return true;
    }
};
class MapPattern : public Pattern {
    private:
    std::vector<std::pair<Expression*, Pattern*>> pats;
    bool hasRest; ContainerAccessNode* rest;
    public:
    MapPattern(std::vector<std::pair<Expression*, Pattern*>> patterns, bool hasRest, ContainerAccessNode* rest)
        : pats(patterns), hasRest(hasRest), rest(rest){}
    bool resolve(Value* value){
        if(value->getType() != TokenList::MAP){ std::cerr << "Cannot map destructure a non-map value."; std::exit(EXIT_FAILURE); }
        ordered_map map = static_cast<MapValue*>(value)->container;
        if(!this->hasRest && map.size() != this->pats.size()) return false;
        if(map.size() < this->pats.size()) return false;
        std::vector<std::pair<std::string, Pattern*>> evalPats;
        std::unordered_set<std::string> requestedKeys;
        for(std::pair<Expression*, Pattern*> pair : this->pats){
            std::string key = pair.first->eval()->asString();
            if(!map.contains(key)) return false;
            evalPats.push_back(std::make_pair(key, pair.second));
            requestedKeys.insert(key);
        } for(std::pair<std::string, Pattern*> pair : evalPats){
            if(dynamic_cast<BindingPattern*>(pair.second)) continue;
            if(!pair.second->resolve(map[pair.first])) return false;
        } for(std::pair<std::string, Pattern*> pair : evalPats)
            if(BindingPattern* pat = dynamic_cast<BindingPattern*>(pair.second)) pat->resolve(map[pair.first]);
        if(this->rest != nullptr){
            ordered_map restMap;
            for(ordered_map::size_type i = 0; i < map.size(); i++) if(requestedKeys.find(map[i]) == requestedKeys.end())
                restMap.put(map[i], map[map[i]]);
            AssignmentNode(this->rest, new ValueNode(new MapValue(restMap))).execute();
        } return true;
    }
};

#endif