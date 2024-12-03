#ifndef AC_PRESETS
#define AC_PRESETS

#include <vector>
#include "Nodes.h"
#include "Values.h"

void init(){
    Variables::set("print", {new FunctionValue({
        {"intlfprintval", NULL}
    }, "", []{
        std::string val = Variables::get("intlfprintval")->asString();
        std::cout << val << std::endl;
        throw new ReturnStatement(new ValueNode(new StringValue(val)));
    }), true});
    Variables::set("read", {new FunctionValue({
        {"intlfreadprt", new ValueNode(new StringValue(""))}
    }, "", []{
        std::string prompt = Variables::get("intlfreadprt")->asString();
        if(prompt != "") std::cout << prompt;
        std::string input; std::getline(std::cin, input);
        throw new ReturnStatement(new ValueNode(new StringValue(input)));
    }), true});
}

#endif