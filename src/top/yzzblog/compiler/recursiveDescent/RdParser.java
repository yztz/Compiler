package top.yzzblog.compiler.recursiveDescent;

import com.sun.istack.internal.Nullable;
import top.yzzblog.compiler.Lex.Tag;
import top.yzzblog.compiler.Lex.Token;
import top.yzzblog.compiler.Lex.Tokenizer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;

public class RdParser {
    private Tokenizer tokenizer;
    private Token t;
    private boolean verbose;

    private Deque<String> deque = new ArrayDeque<>();

    public RdParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;

    }

    private void invoke(String name) {
        try {
            deque.push(name);
            if (verbose) {
                System.out.println(name + " => " + deque.toString());
                this.getClass().getDeclaredMethod(name).invoke(this);
                System.out.println(name + " <= " + deque.toString());
            } else {
                this.getClass().getDeclaredMethod(name).invoke(this);
            }
            deque.pop();

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public void analyze(boolean verbose) {
        this.deque.clear();
        this.verbose = verbose;

        move();
        invoke("E");
        if (t.tag == Tag.END)
            System.out.println("语法分析成功");
        else
            syntaxError("不支持的表达式");
    }

    public void analyze() {
        analyze(false);
    }

    private void move() {
        this.t = tokenizer.getToken();
    }

    private boolean match(Tag tag) {
        if (tag == t.tag) {
            if (verbose) System.out.println(t + " matched!");
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
        int a = (1 + 2);
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
        System.out.printf("Syntax Error at %d:%d: %s\n",
                tokenizer.getLineNo(),
                tokenizer.getColNo(),
                msg);
        System.exit(-1);
    }

}
