package top.yzzblog.compiler.recursiveDescent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yzzblog.compiler.Lex.Tag;
import top.yzzblog.compiler.Lex.Token;
import top.yzzblog.compiler.util.Parser;
import top.yzzblog.compiler.util.Tokenizer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;

public class RdParser {
    private static final Logger logger = LoggerFactory.getLogger(RdParser.class);

    private Tokenizer tokenizer;
    private Token t;
    private Deque<String> deque = new ArrayDeque<>();

    public RdParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;

    }

    private void invoke(String name) {
        try {
            deque.push(name);
            logger.debug("{}入栈，当前：{}", name, deque);
            this.getClass().getDeclaredMethod(name).invoke(this);
            logger.debug("{}出栈，当前：{}", deque.pop(), deque);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public void analyze() {
        this.deque.clear();

        move();
        invoke("E");
        if (t.tag == Tag.END)
            logger.info("语法分析成功");
        else
            logger.error("不支持的表达式");
    }


    private void move() {
        this.t = tokenizer.getToken();
    }

    private boolean match(Tag tag) {
        if (tag == t.tag) {
            logger.debug("匹配成功【{}】", t);
            move();
            return true;
        } else {
            return false;
        }
    }

    private void E() {
        invoke("T");
        invoke("E_");

    }

    private void T() {
        invoke("F");
        invoke("T_");
    }


    private void E_() {
        if (t.tag == Tag.END || t.tag == Tag.RPAREN) {
            return;
        }

        invoke("A");
        invoke("T");
        invoke("E_");

    }

    private void A() {
        if (!match(Tag.ADD) && !match(Tag.SUB)) {
            syntaxError("缺失运算符");
        }
    }

    private void F() {

        switch (t.tag) {
            case LPAREN:
                match(Tag.LPAREN);
                invoke("E");
                if (!match(Tag.RPAREN)) {
                    syntaxError("缺少\")\"");
                }
                break;
            case ID:
                match(Tag.ID);
                break;
            default:
                syntaxError("缺少标识符或\"(\"");
        }

    }

    private void T_() {

        if (t.tag == Tag.ADD || t.tag == Tag.SUB || t.tag == Tag.END || t.tag == Tag.RPAREN) {
            return;
        }

        invoke("M");
        invoke("F");
        invoke("T_");

    }

    private void M() {

        if (!match(Tag.MUL) && !match(Tag.DIV)) {
            syntaxError("缺失运算符");
        }

    }


    private void syntaxError(String msg) {
        logger.error("语法错误 at {}:{} {}", tokenizer.getLineNo(), tokenizer.getColNo(), msg);
        System.exit(-1);
    }

}
