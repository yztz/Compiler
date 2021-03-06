package top.yzzblog.compiler.LL1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yzzblog.compiler.util.Adapter;
import top.yzzblog.compiler.Lex.Tag;
import top.yzzblog.compiler.Lex.Token;
import top.yzzblog.compiler.util.Tokenizer;
import top.yzzblog.compiler.Table;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.Rule;
import top.yzzblog.compiler.util.Parser;

import java.util.*;

public class LL1Parser implements Parser {
    private final static Logger logger = LoggerFactory.getLogger(LL1Parser.class);

    private HashMap<String, Integer> v2i, t2i;
    private final Grammar grammar;
    private Table<Rule> table;

    public LL1Parser(Grammar grammar) {
        this.grammar = grammar;
        genTable(grammar);
    }

    public void parse(Tokenizer tokenizer, Adapter adapter) {
        logger.info("开始LL(1)语法分析...");

        Deque<String> stack = new ArrayDeque<>();
        stack.push(Grammar.END);
        stack.push(grammar.getS());

        Token token = tokenizer.getToken();
        String X = stack.peek();
        while (!X.equals(adapter.getT(Tag.END))) {
            int row = tokenizer.getLineNo();
            int col = tokenizer.getColNo();
            logger.debug("栈顶[" + X + "]\t@" + stack);
            Tag tag = token.tag;
            if (X.equals(adapter.getT(tag))) {
                logger.debug("捕获【{}】", tag);
                stack.pop();
                token = tokenizer.getToken();
            } else if (grammar.getT().contains(X)) {
                error("{}:{} 需要'{}'", row, col, X);
            } else {
                Rule rule = table.getCell(v2i.get(X), t2i.get(adapter.getT(tag)));
                if (null == rule) {
                    error("{}:{} Unexpected '{}'", row, col, token);
                } else {
                    logger.debug("应用规则 " + rule);
                    stack.pop();
                    String[] R = rule.R;
                    for (int i = R.length - 1; i >= 0; i--) {
                        if (!R[i].equals(Grammar.EPSILON)) stack.push(R[i]);
                    }
                }
            }
            X = stack.peek();
        }
        logger.info("匹配成功");
    }

    private void error(String msg, Object ...objs) {
        logger.error("语法错误 " + msg ,objs);
        System.exit(-1);
    }

    private void genTable(Grammar grammar) {
        grammar.compile();

        List<String> V = grammar.getV();
        List<String> T = grammar.getT();

        // 初始化
        v2i = new HashMap<>();
        t2i = new HashMap<>();

        int colNum = T.size();
        int rowNum = V.size();

        table = new Table<>(
                V.toArray(new String[0]),
                T.toArray(new String[0]),
                new Rule[rowNum][colNum]);

        // 建立符号对下标的映射
        for (int i = 0; i < rowNum; i++) {
            String v = V.get(i);
            v2i.put(v, i);
        }
        for (int i = 0; i < colNum; i++) {
            String t = T.get(i);
            t2i.put(t, i);
        }

//        System.out.println(t2i);
        // 遍历规则 查询select
        for (Rule rule : grammar.getRules()) {
            Set<String> selected = grammar.select(rule);
            int rowIdx = v2i.get(rule.L);
            for (String a : selected) {
//                System.out.println(a);
                int colIdx = t2i.get(a);
                table.setCell(rowIdx, colIdx, rule);
            }
        }


    }

    @Override
    public String toString() {
        return "==========================================\n" +
                "Parser: LL1Parser\n" +
                grammar +
                '\n' + "select: " + '\n' + table.toString();
    }


}
