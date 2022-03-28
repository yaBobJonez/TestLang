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
		return this->value != "";
	}
	bool equals(Value* other){
		TokenList t = other->getType();
		if(t==TokenList::STRING) return this->value == other->asString();
		else if(t==TokenList::BOOL) return this->asBoolean() == other->asBoolean();
		else return this->asDouble() == other->asDouble();
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
		if(t==TokenList::BOOL) return this->asBoolean() == other->asBoolean();
		else return this->value== other->asDouble();
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
		return (int)this->value==this->value? (int)this->value : std::round(this->value);
	} double asDouble(){
		return this->value;
	} bool asBoolean(){
		return this->value != 0.0;
	}
	bool equals(Value* other){
		TokenList t = other->getType();
		if(t==TokenList::BOOL) return this->asBoolean() == other->asBoolean();
		else return this->value == other->asDouble();
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
		return this->asBoolean() == other->asBoolean();
	}
};

class Variables {
	static std::map<std::string, Value*> variables;
	public:
	static Value* get(std::string key){
		std::map<std::string, Value*>::iterator search = variables.find(key);
		if(search != variables.end()) return search->second;
		else{ std::cerr<<"Variable "<<key<<"is not defined."; exit(EXIT_FAILURE); }
	} static void set(std::string key, Value* value){
		variables[key] = value;
	}
}; std::map<std::string, Value*> Variables::variables = {};

#endif 