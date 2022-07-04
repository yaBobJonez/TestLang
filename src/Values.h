#ifndef AC_LIB_VALUES
#define AC_LIB_VALUES

#include <iostream>
#include <string>
#include <cmath>
#include <map>
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

class Variables {
	static std::map<std::string, Value*> variables;
	public:
	static Value* get(std::string key){
		std::map<std::string, Value*>::iterator search = variables.find(key);
		if(search != variables.end()) return search->second;
		else{ std::cerr<<"Variable "<<key<<" is not defined."; exit(EXIT_FAILURE); }
	} static void set(std::string key, Value* value){
		variables[key] = value;
	}
}; std::map<std::string, Value*> Variables::variables = {};

#endif 