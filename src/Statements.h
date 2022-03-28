#ifndef AC_AST_STATEMENTS
#define AC_AST_STATEMENTS

#include <vector>
#include <algorithm>
#include <sstream>
#include <iterator>
#include "Nodes.h"
#include "Values.h"

class Statement {
	public:
	virtual void execute() = 0;
};

class OutputStatement : public Statement {
	public:
		Expression* expression;
		OutputStatement(Expression* expression) : expression(expression){}
		void execute(){
			std::cout << expression->eval()->asString() << std::endl;
		}
};
std::ostream& operator<<(std::ostream& stream, const OutputStatement& that){
    return stream << "Output{" << that.expression << "}";
}

class AssignmentStatement : public Statement {
	public:
	std::string variable;
	Expression* expression;
	AssignmentStatement(std::string var, Expression* expr) : variable(var), expression(expr){}
	void execute(){
		Variables::set(this->variable, this->expression->eval());
	}
};
std::ostream& operator<<(std::ostream& stream, const AssignmentStatement& that){
	return stream << "Assign{"<<that.variable<<" = "<<that.expression<<"}";
}

class ConditionalStatement : public Statement {
	public:
	Expression* condition;
	std::vector<Statement*> ifstate;
	std::vector<Statement*> elsestate;
	ConditionalStatement(Expression* cond, std::vector<Statement*> then, std::vector<Statement*> otherwise) : condition(cond), ifstate(then), elsestate(otherwise){}
	void execute(){
		if(this->condition->eval()->asBoolean())
			for(Statement* st : this->ifstate) st->execute();
		else
			for(Statement* st : this->elsestate) st->execute();
	}
};
std::ostream& operator<<(std::ostream& stream, const ConditionalStatement& that){
	std::ostringstream oss1; std::copy(that.ifstate.begin(), that.ifstate.end(), std::ostream_iterator<Statement*>(oss1, ", "));
	std::ostringstream oss2; std::copy(that.elsestate.begin(), that.elsestate.end(), std::ostream_iterator<Statement*>(oss2, ", "));
	return stream << "IfElse{cond = "<<that.condition<<"; if = ["<<oss1.str()<<"]; else = ["<<oss2.str()<<"]}";
}

#endif