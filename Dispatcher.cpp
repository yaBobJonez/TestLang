#include <iostream>
#include "Lexer.h"
#include "Parser.h"

using namespace std;

int main(int argc, char const *argv[])
{
    while(true){
        cout << "> ";
        string input;
        getline(cin, input);
        if(input.empty() || input == "exit") return 0;
        Lexer lx(input);
        Parser pr(lx.tokenize());
        for(Statement* st : pr.parse()) st->execute();
    }
    return 0;
}