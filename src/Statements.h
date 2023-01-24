#ifndef AC_AST_STATEMENTS
#define AC_AST_STATEMENTS

#include <vector>
#include <sstream>
#include <iterator>
#include "Values.h"

/*class Statement {
	public:
	virtual void execute() = 0;
};*/

#include "Nodes.h"

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

//BREAK, CONT
class NullStatement : public Statement {
	public:
	NullStatement(){}
	void execute(){}
};
std::ostream& operator<<(std::ostream& stream, const NullStatement& that){ return stream << "Pass{}"; }

class ForStatement : public Statement {
	public:
	Statement* init;
	Expression* cond;
	Statement* incr;
	std::vector<Statement*> statements;
	ForStatement(Statement* init, Expression* cond, Statement* incr, std::vector<Statement*> statements)
		: init(init), cond(cond), incr(incr), statements(statements){}
	void execute(){
		for(this->init->execute(); this->cond->eval()->asBoolean(); this->incr->execute()){
			try{ for(Statement* st : this->statements) st->execute(); }
			catch(BreakStatement* e){ break; }
			catch(ContinueStatement* e){ continue; }
		}
	}
};
std::ostream& operator<<(std::ostream& stream, const ForStatement& that){
	std::ostringstream oss1; std::copy(that.statements.begin(), that.statements.end(), std::ostream_iterator<Statement*>(oss1, ", "));
	return stream << "For{init = "<<that.init<<"; cond = "<<that.cond<<"; incr = "<<that.incr<<"; states = ["<<oss1.str()<<"]}";
}

class ForeachStatement : public Statement {
	public:
	ContainerAccessNode *key, *val;
	Expression* cont;
	std::vector<Statement*> statements;
	ForeachStatement(ContainerAccessNode* k, ContainerAccessNode* v, Expression* c, std::vector<Statement*> st)
		: key(k), val(v), cont(c), statements(st){}
	ForeachStatement(ContainerAccessNode* v, Expression* c, std::vector<Statement*> st) : key(NULL), val(v), cont(c), statements(st){}
	void execute(){
		Value* contraw = this->cont->eval();
		ordered_map iter;
		if(contraw->getType() == TokenList::ARRAY) iter = static_cast<ArrayValue*>(contraw)->toMap();
		else if(contraw->getType() == TokenList::MAP) iter = static_cast<MapValue*>(contraw)->container;
		else if(contraw->getType() == TokenList::STRING) iter = static_cast<StringValue*>(contraw)->toMap();
        else { std::cerr<<"Foreach loop expected an iterable value."; std::exit(EXIT_FAILURE); }
		for(ordered_map::size_type i = 0; i < iter.size(); i++){
			if(this->key != NULL) AssignmentNode(this->key, new ValueNode(new StringValue(iter[i]))).execute();
			AssignmentNode(this->val, new ValueNode(iter[iter[i]])).execute();
			try{ for(Statement* st : this->statements) st->execute(); }
			catch(BreakStatement* e){ break; }
			catch(ContinueStatement* e){ continue; }
		}
	}
};
std::ostream& operator<<(std::ostream& stream, const ForeachStatement& that){
	std::ostringstream oss1; std::copy(that.statements.begin(), that.statements.end(), std::ostream_iterator<Statement*>(oss1, ", "));
	std::ostringstream oss2; if(that.key!=NULL){ std::copy(that.key->path.begin(), that.key->path.end(), std::ostream_iterator<Expression*>(oss2, "][")); }
	std::ostringstream oss3; std::copy(that.val->path.begin(), that.val->path.end(), std::ostream_iterator<Expression*>(oss3, "]["));
	return stream << "Foreach{"<<(that.key!=NULL?"key = ["+oss2.str()+"]; ":"")<<"value = ["<<oss3.str()<<"]; cont = "<<that.cont<<"; states = ["<<oss1.str()<<"]}";
}

class WhileStatement : public Statement {
	public:
	Expression* cond;
	std::vector<Statement*> statements;
	WhileStatement(Expression* cond, std::vector<Statement*> st) : cond(cond), statements(st){}
	void execute(){
		while(this->cond->eval()->asBoolean()){
			try{ for(Statement* st : this->statements) st->execute(); }
			catch(BreakStatement* e){ break; }
			catch(ContinueStatement* e){ continue; }
		}
	}
};
std::ostream& operator<<(std::ostream& stream, const WhileStatement& that){
	std::ostringstream oss1; std::copy(that.statements.begin(), that.statements.end(), std::ostream_iterator<Statement*>(oss1, ", "));
	return stream << "While{cond = "<<that.cond<<"; states = ["<<oss1.str()<<"]}";
}

class DoWhileStatement : public Statement {
	public:
	Expression* cond;
	std::vector<Statement*> statements;
	DoWhileStatement(std::vector<Statement*> st, Expression* cond) : statements(st), cond(cond){}
	void execute(){
		do {
			try{ for(Statement* st : this->statements) st->execute(); }
			catch(BreakStatement* e){ break; }
			catch(ContinueStatement* e){ continue; }
		} while(this->cond->eval()->asBoolean());
	}
};
std::ostream& operator<<(std::ostream& stream, const DoWhileStatement& that){
	std::ostringstream oss1; std::copy(that.statements.begin(), that.statements.end(), std::ostream_iterator<Statement*>(oss1, ", "));
	return stream << "DoWhile{cond = "<<that.cond<<"; states = ["<<oss1.str()<<"]}";
}

#endif