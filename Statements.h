#ifndef AC_AST_STATEMENTS
#define AC_AST_STATEMENTS

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
			std::cout << expression->eval() << std::endl;
		}
};
std::ostream& operator<<(std::ostream& stream, const OutputStatement& that){
    return stream << "Output[" << that.expression << "]";
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

#endif