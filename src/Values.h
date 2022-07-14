#ifndef AC_LIB_VALUES
#define AC_LIB_VALUES

#include <iostream>
#include <string>
#include <cmath>
#include <map>
#include <functional>
#include <stack>
#include "Token.h"

class Value {
	public:
	virtual TokenList getType(){ return TokenList::T_EOF; };
	virtual std::string asString() = 0;
	virtual int asInteger() = 0;
	virtual double asDouble() = 0;
	virtual bool asBoolean() = 0;
	virtual bool equals(Value* other) = 0;
};

class ArrayValue : public Value {
	public:
	std::map<std::string, Value*> container;
	TokenList getType(){ return TokenList::ARRAY; }
	ArrayValue(std::map<std::string, Value*> map) : container(map){}
	ArrayValue(){ this->container = std::map<std::string, Value*>(); }
	void set(std::string k, Value* v){
		this->container[k] = v;
	} Value* get(std::string k){
		try{ return this->container.at(k); }
		catch(const std::out_of_range& e){
			std::cerr<<"Element with key "<<k<<" is not found in array.";
			std::exit(EXIT_FAILURE);
		}
	}
	std::string asString(){
		std::string s = "["; if(this->container.empty()) return s+"]";
		for(std::pair<std::string, Value*> el : this->container)
			s += el.first+":"+el.second->asString()+", ";
		s.erase(s.length()-2); return s+"]";
	} int asInteger(){
		std::cerr<<"Cannot cast array \""<<this->asString()<<"\" to integer.";
		std::exit(EXIT_FAILURE);
	} double asDouble(){
		std::cerr<<"Cannot cast array \""<<this->asString()<<"\" to double.";
		std::exit(EXIT_FAILURE);
	} bool asBoolean(){
		return !this->container.empty();
	}
	bool equals(Value* other){
		TokenList t = other->getType();
		if(t == TokenList::ARRAY){
			auto v1 = this->container;
			auto v2 = static_cast<ArrayValue*>(other)->container;
			if(v1.size() != v2.size()) return false;
			for(std::pair<std::string, Value*> e1 : v1){
				if(v2.find(e1.first) == v2.end()) return false;
				if(!v2.at(e1.first)->equals(e1.second)) return false;
			} for(std::pair<std::string, Value*> e2 : v2){
				if(v1.find(e2.first) == v1.end()) return false;
				if(!v1.at(e2.first)->equals(e2.second)) return false;
			} return true;
		} else if(this->container.size() == 1)
			for(auto& el : this->container) return el.second->equals(other);
		else if(t == TokenList::BOOL) return other->equals(this);
		else return false;
	}
};
class StringValue : public Value {
	private:
	std::string value;
	public:
	TokenList getType(){ return TokenList::STRING; }
	StringValue(std::string value) : value(value){}
	std::string asString(){
		return this->value;
	} int asInteger(){
		try { return (this->value=="")? 0 : std::stoi(this->value); }
		catch(const std::exception& e){
			std::cerr<<"Cannot cast string \""<<this->value<<"\" to integer.";
			std::exit(EXIT_FAILURE); }
	} double asDouble(){
		try { return (this->value=="")? 0.0 : std::stod(this->value); }
		catch(const std::exception& e){
			std::cerr<<"Cannot cast string \""<<this->value<<"\" to double.";
			std::exit(EXIT_FAILURE); }
	} bool asBoolean(){
		return this->value != "" && this->value != "0";
	}
	bool equals(Value* other){
		TokenList t = other->getType();
		if(t==TokenList::STRING) return this->value == other->asString();
		else if(t==TokenList::INT || t==TokenList::DOUBLE){
			if(this->value=="") return 0.0 == other->asDouble();
			char* e = NULL; double v = std::strtod(this->value.c_str(), &e);
			if(*e) return false; else return v == other->asDouble();
		} else return other->equals(this);
	}
};
class IntegerValue : public Value {
	private:
	int value;
	public:
	TokenList getType(){ return TokenList::INT; }
	IntegerValue(int value) : value(value){}
	std::string asString(){
		return std::to_string(this->value);
	} int asInteger(){
		return this->value;
	} double asDouble(){
		return (double)this->value;
	} bool asBoolean(){
		return this->value != 0;
	}
	bool equals(Value* other){
		TokenList t = other->getType();
		if(t==TokenList::INT) return this->value == other->asInteger();
		else return other->equals(this);
	}
};
class DoubleValue : public Value {
	private:
	double value;
	public:
	TokenList getType(){ return TokenList::DOUBLE; }
	DoubleValue(double value) : value(value){}
	std::string asString(){
		return std::to_string(this->value);
	} int asInteger(){
		return (int)std::round(this->value);
	} double asDouble(){
		return this->value;
	} bool asBoolean(){
		return this->value != 0.0;
	}
	bool equals(Value* other){
		TokenList t = other->getType();
		if(t==TokenList::INT || t==TokenList::DOUBLE) return this->value == other->asDouble();
		else return other->equals(this);
	}
};
class BooleanValue : public Value {
	private:
	bool value;
	public:
	TokenList getType(){ return TokenList::BOOL; }
	BooleanValue(bool value) : value(value){}
	std::string asString(){
		return this->value?"true":"false";
	} int asInteger(){
		return this->value?1:0;
	} double asDouble(){
		return this->value?1.0:0.0;
	} bool asBoolean(){
		return this->value;
	}
	bool equals(Value* other){
		return this->value == other->asBoolean();
	}
};
class NullValue : public Value {
	public:
	TokenList getType(){ return TokenList::NUL; }
	NullValue(){}
	std::string asString(){
		return "null";
	} int asInteger(){
		std::cerr<<"Cannot cast null to integer.";
		std::exit(EXIT_FAILURE);
	} double asDouble(){
		std::cerr<<"Cannot cast null to double.";
		std::exit(EXIT_FAILURE);
	} bool asBoolean(){
		return false;
	}
	bool equals(Value* other){
		return other->getType() == TokenList::NUL;
	}
};
class Expression{
	public: virtual Value* eval() = 0;
}; //FIXME move
class Statement{
	public: virtual void execute() = 0;
}; //FIXME move
class Variables {
	static std::stack<std::map<std::string, Value*>> variables;
	public:
	static Value* get(std::string key){
		std::stack<std::map<std::string, Value*>> copy = variables;
		while(!copy.empty()){
			std::map<std::string, Value*>::iterator search = copy.top().find(key);
			if(search != copy.top().end()) return search->second;
			copy.pop();
		} std::cerr<<"Variable "<<key<<" is not defined."; exit(EXIT_FAILURE);
	} static void set(std::string key, Value* value){
		variables.top()[key] = value;
	}
	static void push(){ variables.push({}); }
	static void pop(){ variables.pop(); }
}; std::stack<std::map<std::string, Value*>> Variables::variables = {};

class ReturnStatement : public Statement { //FIXME move
	public:
	Expression* expr;
	ReturnStatement(Expression* expr) : expr(expr){}
	void execute(){ throw this; }
};
std::ostream& operator<<(std::ostream& stream, const ReturnStatement& that){ return stream << "Return{"<<that.expr<<"}"; }
class FunctionValue : public Value {
	private:
	std::vector<std::pair<std::string, Expression*>> params;
	int rqParams = 0;
	std::string varargs;
	std::vector<Statement*> body;
	std::function<void(void)> bodyExecutor = [this]{ for(Statement* st : this->body) st->execute(); };
	public:
	TokenList getType(){ return TokenList::FUNCTION; }
	FunctionValue(std::vector<std::pair<std::string, Expression*>> params, std::string varargs, std::vector<Statement*> body)
		: params(params), varargs(varargs), body(body){ for(std::pair<std::string, Expression*> param : params) if(param.second == NULL) this->rqParams++; }
	FunctionValue(std::vector<std::pair<std::string, Expression*>> params, std::string varargs, std::function<void()> intl)
		: params(params), varargs(varargs), bodyExecutor(intl){ for(std::pair<std::string, Expression*> param : params) if(param.second == NULL) this->rqParams++; }
	Value* execute(std::vector<Expression*> args){
		if(args.size() < this->rqParams){ std::cout<<"Too few arguments provided; required at least "<<this->rqParams<<", got "<<args.size()<<"."; exit(EXIT_FAILURE); }
		if(this->varargs == "" && args.size() > this->params.size())
			{ std::cout<<"Too many arguments provided; expected "<<this->params.size()<<", got "<<args.size()<<"."; exit(EXIT_FAILURE); }
		int total = args.size() > this->params.size()? this->params.size() : args.size();
		try {
			Variables::push();
			for(int i = 0; i < total; i++) Variables::set(this->params[i].first, args[i]->eval());
			for(int i = total; i < this->params.size(); i++) Variables::set(this->params[i].first, this->params[i].second->eval());
			if(this->varargs != ""){
				std::map<std::string, Value*> others;
				for(int i = this->params.size(); i < args.size(); i++) others[IntegerValue(i-this->params.size()).asString()] = args[i]->eval();
				Variables::set(this->varargs, new ArrayValue(others));
			} this->bodyExecutor();
			Variables::pop();
			return new NullValue();
		} catch(ReturnStatement* e){
			Value* ret = e->expr->eval();
			Variables::pop();
			return ret;
		}
	}
	std::string asString(){
		std::string parameters = "";
		for(std::pair<std::string, Expression*> par : this->params) parameters += par.second!=NULL? par.first+"="+par.second->eval()->asString()+"," : par.first+",";
		std::ostringstream oss1; std::copy(this->body.begin(), this->body.end(), std::ostream_iterator<Statement*>(oss1, ", "));
		return "function("+parameters+"){"+oss1.str()+"}";
	} int asInteger(){
		std::cerr<<"Cannot cast function to integer.";
		std::exit(EXIT_FAILURE);
	} double asDouble(){
		std::cerr<<"Cannot cast function to double.";
		std::exit(EXIT_FAILURE);
	} bool asBoolean(){
		return true;
	}
	bool equals(Value* other){
		if(other->getType() == TokenList::FUNCTION) return this->asString() == other->asString();
		else if(other->getType() == TokenList::BOOL) return other->asBoolean();
		else return false;
	}
};

#endif 