package top.yzzblog.compiler.Lex;

import top.yzzblog.compiler.Util;

import java.io.InputStream;

public class Lexer {
    private final Scanner scanner;
    private char ch;

    public Lexer(String filename) {
        scanner = new Scanner(filename);
        scan();
    }

    public Lexer(InputStream is) {
        scanner = new Scanner(is);
        scan();
    }

    public void parse() {
        Token token = getToken();
        while (token.tag != Tag.END) {
            if (token.tag != Tag.ERR) {
                System.out.println(token);
            }
            token = getToken();
        }


    }

    private boolean scan(Character need) {
        ch = scanner.scan();
        if (null == need) return true;
        if (ch != need) {   // 如果读取的下个字符不符合需要
            return false;
        } else {            // 符合需要，返回true，并且继续读入下个字符
            ch = scanner.scan();
            return true;
        }

    }

    private void scan() {
        scan(null);
    }
    private void printError(String msg) {
        System.out.printf("Error: 词法错误 at %d:%d: %s\n",
                scanner.getLineNum(),
                scanner.getColNum(),
                msg);
    }

    private void lexError(LexError code) {

        switch (code) {
            case NUM_BIN_TYPE:
                printError("二进制数字必须包含至少一位二进制数");
                break;
            case NUM_HEX_TYPE:
                printError("十六进制数字必须包含至少一位十六进制数");
                break;
            case OR_NO_PAIR:
                printError("错误的\"或\"运算符");
                break;
            case AND_NO_PAIR:
                printError( "错误的\"与\"运算符");
                break;
            case COMMENT_NO_END:
                printError("多行注释未正常结束");
                break;
            case TOKEN_NO_EXIST:
                printError("不支持的词法记号");
                break;
        }
    }

    public Token getToken() {
        Token token;
        // 排除空白字符
        while (Util.isWhite(ch)) scan();

        if (ch == '$') return new Token(Tag.END);

        /* 标识符 */
        if (Util.isLetter(ch) || ch == '_') {    // 状态1
            StringBuilder sb = new StringBuilder();
            do {    //状态2
                sb.append(ch);
                scan();
            } while (Util.isLetter(ch) ||
                    Util.isDigit(ch) ||
                    ch == '_');
            String name = sb.toString();
            Tag tag = KeyWords.getTag(name);
            // 当不为标识符
            if (tag == Tag.ID)
                token = new Token.ID(name);
            else
                token = new Token(tag);
        }
        /* 数值常量 */
        else if (Util.isDigit(ch)) {
            int val = 0;
            if (ch != '0') {    //十进制
                do {
                    val = val * 10 + ch - '0';
                    scan();
                } while (Util.isDigit(ch));
            } else {
                scan();
                if (ch == 'x' || ch == 'X') {   //十六进制
                    scan();
                    if (Util.isHexDigit(ch)) {
                        do {
                            val = val * 16 + ch;
                            if (Util.isDigit(ch)) val -= '0';
                            else if (ch >= 'A' && ch <= 'F') val = val - 'A' + 10;
                            else if (ch >= 'a' && ch <= 'f') val = val - 'a' + 10;
                            scan();
                        } while (Util.isHexDigit(ch));
                    } else {    //0x 后无数据
                        lexError(LexError.NUM_HEX_TYPE);
                        token = new Token(Tag.ERR);
                    }
                } else if (ch == 'b' || ch == 'B') {    //二进制
                    scan();
                    if (Util.isBinDigit(ch)) {
                        do {
                            val = val * 2 + ch - '0';
                            scan();
                        } while (Util.isBinDigit(ch));
                    } else {    //0b 后无数据
                        lexError(LexError.NUM_BIN_TYPE);
                        token = new Token(Tag.ERR);
                    }
                } else if (Util.isOctDigit(ch)) {   //八进制
                    do {
                        val = val * 8 + ch - '0';
                        scan();
                    } while (Util.isOctDigit(ch));
                }
            }
            token = new Token.Num(val);
        } else
            switch (ch) {
                case '+':
                    token = new Token(Tag.ADD);
                    scan();
                    break;
                case '-':
                    token = new Token(Tag.SUB);
                    scan();
                    break;
                case '*':
                    token = new Token(Tag.MUL);
                    scan();
                    break;
                case '/':   // 包括注释处理
                    scan();
                    if (ch == '/') {    // 单行注释
                        while (ch != '\n' && ch != '$') {
                            scan();
                        }
                        token = new Token(Tag.ERR);
                    } else if (ch == '*') { // 多行注释
                        while (true) {
                            scan();
                            if (ch == '*') {
                                if (scan('/')) {
                                    token = new Token(Tag.ERR);
                                    break;
                                }
                            }
                            if (ch == '$') {
                                lexError(LexError.COMMENT_NO_END);
                                token = new Token(Tag.ERR);
                                break;
                            }
                        }
                    } else {
                        token = new Token(Tag.DIV);
                    }
                    break;
                case '%':
                    token = new Token(Tag.MOD);
                    scan();
                    break;
                case '>':
                    token = new Token(scan('=') ? Tag.GE : Tag.GT);
                    break;
                case '<':
                    token = new Token(scan('=') ? Tag.LE : Tag.LT);
                    break;
                case '=':
                    token = new Token(scan('=') ? Tag.EQ : Tag.ASSIGN);
                    break;
                case '|':
                    token = new Token(scan('|') ? Tag.OR : Tag.ERR);
                    if (token.tag == Tag.ERR) lexError(LexError.OR_NO_PAIR);
                    break;
                case '&':
                    token = new Token(scan('&') ? Tag.AND : Tag.ERR);
                    if (token.tag == Tag.ERR) lexError(LexError.AND_NO_PAIR);
                    break;
                case '!':
                    token = new Token(scan('=') ? Tag.NEQ : Tag.NOT);
                    scan();
                    break;
                case ',':
                    token = new Token(Tag.COMMA);
                    scan();
                    break;
                case ':':
                    token = new Token(Tag.COLON);
                    scan();
                    break;
                case ';':
                    token = new Token(Tag.SEMICOLON);
                    scan();
                    break;
                case '(':
                    token = new Token(Tag.LPAREN);
                    scan();
                    break;
                case ')':
                    token = new Token(Tag.RPAREN);
                    scan();
                    break;
                case '[':
                    token = new Token(Tag.LBRACKET);
                    scan();
                    break;
                case ']':
                    token = new Token(Tag.RBRACKET);
                    scan();
                    break;
                case '{':
                    token = new Token(Tag.LBRACE);
                    scan();
                    break;
                case '}':
                    token = new Token(Tag.RBRACE);
                    scan();
                    break;

                default:
                    lexError(LexError.TOKEN_NO_EXIST);
                    scan();
                    token = new Token(Tag.ERR);
            }


        return token;
    }
}
