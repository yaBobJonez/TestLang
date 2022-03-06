#ifndef AC_LIB_VALUES
#define AC_LIB_VALUES

#include <iostream>
#include <string>
#include <cmath>
#include <map>

class Value {
	public:
	virtual std::string asString() = 0;
	virtual int asInteger() = 0;
	virtual double asDouble() = 0;
	virtual bool asBoolean() = 0;
};

class StringValue : public Value {
	private:
	std::string value;
	public:
	StringValue(std::string value) : value(value){}
	std::string asString(){
		return this->value;
	} int asInteger(){
		try { return std::stoi(this->value); }
		catch(const std::exception& e){ return 0; }
	} double asDouble(){
		try { return std::stod(this->value); }
		catch(const std::exception& e){ return 0.0; }
	} bool asBoolean(){
		return this->value == "1" || this->value == "true";
	}
};
class IntegerValue : public Value {
	private:
	int value;
	public:
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
};
class DoubleValue : public Value {
	private:
	double value;
	public:
	DoubleValue(double value) : value(value){}
	std::string asString(){
		return std::to_string(this->value);
	} int asInteger(){
		return std::round(this->value);
	} double asDouble(){
		return this->value;
	} bool asBoolean(){
		return this->value != 0.0;
	}
};
class BooleanValue : public Value {
	private:
	bool value;
	public:
	BooleanValue(bool value) : value(value){}
	std::string asString(){
		return value?"true":"false";
	} int asInteger(){
		return value;
	} double asDouble(){
		return value;
	} bool asBoolean(){
		return value;
	}
};

class Variables {
	static std::map<std::string, double> variables;
	public:
	static double get(std::string key){
		std::map<std::string, double>::iterator search = variables.find(key);
		if(search != variables.end()) return search->second;
		else{ std::cerr<<"Variable "<<key<<"is not defined."; exit(EXIT_FAILURE); }
	} static void set(std::string key, double value){
		variables[key] = value;
	}
}; std::map<std::string, double> Variables::variables = {};

#endif 