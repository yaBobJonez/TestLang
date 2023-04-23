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
    		} else { std::cerr << "Invalid range values specified, allowed types are char and int."; std::exit(EXIT_FAILURE); }
            return new ArrayValue(arr);
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
		if(t == TokenList::MAP) return static_cast<MapValue*>(this->getContainer())->get(this->getKey()->asString());
		else if(t == TokenList::ARRAY){
            /*Expression* key = this->path.back();
            if(auto range = dynamic_cast<BinaryNode*>(key)) if(range->operator_ == ".."){
                Value *left = range->left->eval(), *right = range->right->eval();
                if(left->getType() != right->getType() || left->getType() != TokenList::INT)
                    { std::cerr<<"Cannot slice an array using non-integer range."; std::exit(EXIT_FAILURE); }
                ArrayValue* cont = static_cast<ArrayValue*>(this->getContainer());
                int l = left->asInteger(), r = right->asInteger();
                if(l<0) l=cont->container.size()-l; if(r<0) r=cont->container.size()-r;
                //TODO slicing?
            }*/ return static_cast<ArrayValue*>(this->getContainer())->get(this->getKey()->asInteger());
        } else { std::cerr << "Invalid operation: indexing a non-container value."; std::exit(EXIT_FAILURE); }
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
	}
    Value* getKey(){ return this->path.back()->eval(); }
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
	MapNode(std::vector<std::pair<Expression*, Expression*>> map) : map(map){}
	Value* eval(){
        ordered_map map;
        for(ordered_map::size_type i = 0; i < this->map.size(); i++)
            map.put(this->map[i].first->eval()->asString(), this->map[i].second->eval());
        return new MapValue(map);
	}
};
//MapNode toString

class ArrayNode : public Expression {
	public:
	std::vector<Expression*> arr;
	ArrayNode(std::vector<Expression*> arr) : arr(arr){}
	Value* eval(){
		std::vector<Value*> arr;
        for(Expression* expr : this->arr) arr.push_back(expr->eval());
        return new ArrayValue(arr);
	}
};
//ArrayNode toString

class UnaryNode : public Expression, public Statement {
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
        void execute(){ this->eval(); }
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

class FunctionDefinitionNode : public Expression {
private:
    std::vector<std::pair<std::string, Expression*>> params;
    std::string varargs;
    std::vector<Statement*> body;
    std::function<void(void)> bodyExecutor = [this]{ for(Statement* st : this->body) st->execute(); };
public:
    FunctionDefinitionNode(std::vector<std::pair<std::string, Expression*>> params,
                           std::string varargs, std::vector<Statement*> body)
        : params(params), varargs(varargs), body(body){}
    Value* eval(){
        std::unordered_map<std::string, Value*> context = Variables::variables.top();
        return new FunctionValue(this->params, this->varargs, this->body, context);
    }
};

//Patterns

class Pattern {
    public:
    virtual bool resolve(Value* value) = 0;
};
class ConstantPattern : public Pattern {
    public:
    Expression* expr;
    ConstantPattern(Expression* expr) : expr(expr){}
    bool resolve(Value* value){
        return value->equals(this->expr->eval());
    }
};
class RangePattern : public Pattern {
    private:
    Pattern *left, *right;
    public:
    RangePattern(Pattern* from, Pattern* to) : left(from), right(to){}
    bool resolve(Value* value){
        ConstantPattern* left = dynamic_cast<ConstantPattern*>(this->left);
        ConstantPattern* right = dynamic_cast<ConstantPattern*>(this->right);
        if(left == nullptr || right == nullptr){ std::cerr << "Illegal range operands specified, only constants allowed."; std::exit(EXIT_FAILURE); }
        Value* from = left->expr->eval(); Value* to = right->expr->eval();
        TokenList lt = from->getType();
        if(lt != to->getType()){ std::cerr<<"Types of range values don't match."; std::exit(EXIT_FAILURE); }
        if(lt == TokenList::INT || lt == TokenList::DOUBLE) return (value->asDouble() >= from->asDouble() && value->asDouble() < to->asDouble());
        else if(lt == TokenList::STRING){
            std::string vs = value->asString(), lv = from->asString(), rv = to->asString();
            if(vs.length() != 1 || lv.length() != 1 || rv.length() != 1)
                { std::cerr<<"Invalid range values specified."; std::exit(EXIT_FAILURE); }
            return (vs.at(0) >= lv.at(0) && vs.at(0) <= rv.at(0));
        } else { std::cerr<<"Invalid range values specified, allowed types are char and int."; std::exit(EXIT_FAILURE); }
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
    BinaryPattern(std::string op, Expression* expr) : op(op), expr(expr){}
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
        if(value->getType() == TokenList::ARRAY) value = new MapValue(static_cast<ArrayValue*>(value)->toMap());
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

//Match Node

class BreakStatement : public Statement {
public:
    unsigned char levels;
    BreakStatement(unsigned char levels) : levels(levels){}
    void execute(){ throw BreakStatement(this->levels-1); }
};
std::ostream& operator<<(std::ostream& stream, const BreakStatement& that){ return stream << "Break{}"; }
class ContinueStatement : public Statement {
public:
    unsigned char levels;
    ContinueStatement(unsigned char levels) : levels(levels){}
    void execute(){ throw ContinueStatement(this->levels-1); }
};
std::ostream& operator<<(std::ostream& stream, const ContinueStatement& that){ return stream << "Continue{}"; }

class MatchNode : public Expression, public Statement {
    public:
        Expression* expr;
        std::vector<Pattern*> cases;
        std::vector<std::vector<Statement*>> statements;
        MatchNode(Expression* expr, std::vector<Pattern*> cases, std::vector<std::vector<Statement*>> statements)
            : expr(expr), cases(cases), statements(statements){}
        Value* eval(){
            Value* val = this->expr->eval();
            for(std::size_t i = 0; i < this->cases.size(); i++)
                if(this->cases[i]->resolve(val)){
                    try{ for(Statement* st : this->statements[i]) st->execute(); }
                    catch(ContinueStatement e){ if(e.levels) e.execute(); continue; }
                    catch(BreakStatement e){} //TODO DECIDE: should allow break?
                    catch(ReturnStatement* e){ return e->expr->eval(); }
                    break;
                }
            return new NullValue();
        }
        void execute(){ this->eval(); }
};

class DestructureArrayNode : public Expression, public Statement {
public:
    ArrayPattern* left;
    Expression* right;
    DestructureArrayNode(ArrayPattern* left, Expression* right) : left(left), right(right){}
    Value* eval(){
        Value* arr = this->right->eval();
        if(arr->getType() != TokenList::ARRAY){ std::cerr<<"Cannot array destructure a non-array value."; std::exit(EXIT_FAILURE); }
        return new BooleanValue(this->left->resolve(arr));
    }
    void execute(){ this->eval(); }
};
//TODO destructure array right string

class DestructureMapNode : public Expression, public Statement {
public:
    MapPattern* left;
    Expression* right;
    DestructureMapNode(MapPattern* left, Expression* right) : left(left), right(right){}
    Value* eval(){
        Value* map = this->right->eval();
        if(map->getType() != TokenList::MAP && map->getType() != TokenList::ARRAY)
            { std::cerr << "Cannot map destructure a non-map value."; std::exit(EXIT_FAILURE); }
        return new BooleanValue(this->left->resolve(map));
    }
    void execute(){ this->eval(); }
};
//TODO map destructuring right string

#endif