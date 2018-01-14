package com.vpetrosyan.app.testo.parser

enum class Token  {
    UNKNOWN,

    IDENTIFIER,
    INTEGER,

    KEYWORD_MODULE,
    KEYWORD_TEST,
    KEYWORD_MEMORY,
    KEYWORD_LET,
    KEYWORD_RUN,
    KEYWORD_STRICT,
    KEYWORD_ON,

    DIR_UP,
    DIR_DOWN,

    OP_W_0,
    OP_W_1,
    OP_R_0,
    OP_R_1,

    FAILURE_ST_0,
    FAILURE_ST_1,

    PAR_LEFT,    // (
    PAR_RIGHT,   // )
    BRCK_LEFT,   // [
    BRCK_RIGHT,  // ]
    BR_LEFT,     // {
    BT_RIGHT,    // }

    NEWLINE,

    COMMA,

    EOF
}