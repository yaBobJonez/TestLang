cmake_minimum_required(VERSION 3.24)
project(ChimeCmob)

set(CMAKE_CXX_STANDARD 11)

add_executable(ChimeCmob
    src/Dispatcher.cpp
    src/Lexer.h
    src/Nodes.h
    src/Parser.h
    src/Presets.h
    src/Statements.h
    src/Token.h
    src/Values.h
)
