#ifndef AC_AST_STATEMENTS
#define AC_AST_STATEMENTS

#include <vector>
#include <sstream>
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
	ContainerAccessNode* container;
	Expression* expression;
	AssignmentStatement(ContainerAccessNode* cont, Expression* expr) : container(cont), expression(expr){}
	void execute(){
		if(this->container->path.empty()) return Variables::set(this->container->var, this->expression->eval());
		else static_cast<ArrayValue*>(this->container->getContainer())->set(this->container->getKey(), this->expression->eval());
	}
};
std::ostream& operator<<(std::ostream& stream, const AssignmentStatement& that){
	std::ostringstream oss1; std::copy(that.container->path.begin(), that.container->path.end(), std::ostream_iterator<Expression*>(oss1, "]["));
	return stream << "Assign{"<<that.container->var<<"["<<oss1.str()<<"] = "<<that.expression<<"}";
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

class ContinueStatement : public Statement {
	public:
	ContinueStatement(){}
	void execute(){ throw this; }
};

class SwitchStatement : public Statement {
	public:
	Expression* condition;
	std::map<std::vector<Expression*>, std::vector<Statement*>> cases;
	std::vector<Statement*> defcase;
	SwitchStatement(
		Expression* cond,
		std::map<std::vector<Expression*>, std::vector<Statement*>> cases,
		std::vector<Statement*> def
	) : condition(cond), cases(cases), defcase(def){}
	void execute(){
		Value* cond = this->condition->eval();
		bool matched = false;
		for(auto& c : this->cases){
			if(!matched){
				for(Expression* pattern : c.first)
					if(cond->equals(pattern->eval())){ matched = true; break; }
				if(!matched) continue;
			} try{ for(Statement* st : c.second) st->execute(); }
			catch(ContinueStatement* e){ continue; }
			return;
		} for(Statement* st : this->defcase) st->execute();
	}
};

#endif