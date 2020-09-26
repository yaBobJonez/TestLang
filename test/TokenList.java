package test;

public final class TokenList {
	public static final String TT_INT = "INTEGER";
	public static final String TT_DOUBLE = "DOUBLE";
	public static final String TT_STRING = "STRING";
	public static final String TT_BOOL = "BOOLEAN";
	public static final String TT_CONST = "CONSTANT";
	public static final String TT_NULL = "NULL"; //TODO null, inf, nan
	public static final String TT_INF = "INFINITY";
	public static final String TT_NAN = "NAN";
	
	public static final String TS_EOF = "EOF";
	public static final String TS_ID = "IDENTIFICATOR";
	public static final String TS_ASSIGN = "ASSIGN";
	public static final String TS_PLUSASSIGN = "ASSIGNPLUS";
	public static final String TS_MINUSASSING = "ASSIGNMINUS";
	public static final String TS_MULTIPLYASSIGN = "ASSIGNMULTIPLY";
	public static final String TS_DIVIDEASSIGN = "ASSIGNDIVIDE";
	public static final String TS_ACCESS = "ACCESSOR";
	public static final String TS_IF = "IF";
	public static final String TS_ELSE = "ELSE";
	public static final String TS_WHILE = "WHILE";
	public static final String TS_DOWHILE = "DO";
	public static final String TS_FOR = "FOR";
	public static final String TS_FOREACH = "FOREACH";
	public static final String TS_QUESTION = "QUESTIONMARK";
	public static final String TS_COLON = "COLON";
	public static final String TS_SEMICOLON = "SEMICOLON";
	public static final String TS_FUNCTION = "FUNCTION";
	public static final String TS_SWITCH = "SWITCH";
	public static final String TS_IMPORT = "IMPORT";
	public static final String TS_CLASS = "CLASS";
	public static final String TS_TRY = "TRY";
	public static final String TS_CATCH = "CATCH";
	
	public static final String TA_BREAK = "BREAK";
	public static final String TA_CONTINUE = "CONTINUE";
	public static final String TA_RETURN = "RETURN";
	public static final String TA_USE = "USE";
	public static final String TA_CASE = "CASE";
	public static final String TA_NEW = "NEW";
	public static final String TA_IN = "IN"; //Formerly: TA_AS = "AS"
	
	public static final String TI_OUT = "OUTPUT";
	public static final String TI_IN = "INPUT";
	
	public static final String TO_PLUS = "PLUS";
	public static final String TO_MINUS = "MINUS";
	public static final String TO_MULTIPLY = "MULTIPLY";
	public static final String TO_DIVIDE = "DIVIDE";
	public static final String TO_POWER = "EXPONENT";
	public static final String TO_MODULO = "MODULO";
	public static final String TO_LPAR = "LEFTPARENTHESIS";
	public static final String TO_RPAR = "RIGHTPARENTHESIS";
	public static final String TO_LBRA = "LEFTBRACKET";
	public static final String TO_RBRA = "RIGHTBRACKET";
	public static final String TO_LCURL = "LEFTCURLYBRACKET";
	public static final String TO_RCURL = "RIGHTCURLYBRACKET";
	public static final String TO_COMMA = "COMMA";
	public static final String TO_PERIOD = "PERIOD";
	
	public static final String TL_EQUALS = "EQUALS";
	public static final String TL_NEQUALS = "NOTEQUALS";
	public static final String TL_NOT = "NOT";
	public static final String TL_EQGREATER = "GREATEREQUALS";
	public static final String TL_EQLESS = "LESSEQUALS";
	public static final String TL_GREATER = "GREATER";
	public static final String TL_LESS = "LESS";
	public static final String TL_AND = "AND";
	public static final String TL_OR = "OR";
}
