package top.yzzblog.compiler;

public enum Tag {
    ERR,    // 错误
    END,    // 文件结束
    ID,     // 标识符
    KW_INT, // 数据类型
    NUM,    // 常量
    NOT,    // 单目运算符 !
    ADD, SUB, MUL, DIV, MOD, // 算术运算符
    GT, GE, LT, LE, EQ, NEQ, //比较运算法
    AND, OR,                 // 逻辑运算符
    LPAREN, RPAREN,          // ()
    LBRACKET, RBRACKET,      // []
    LBRACE, RBRACE,          // {}
    COMMA, COLON, SEMICOLON, // , : ;
    ASSIGN,                  // 赋值
    KW_IF, KW_ELSE,          // if-else
    KW_WHILE,                // while
    KW_CONTINUE,             // continue
    KW_BREAK,                // break
    KW_RETURN,               // return

}
