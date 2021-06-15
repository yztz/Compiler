package top.yzzblog.compiler.LL1;

import top.yzzblog.compiler.Lex.Tokenizer;
import top.yzzblog.compiler.grammar.Grammar;
import top.yzzblog.compiler.grammar.Rule;

import java.util.*;

public class LL1Parser {
    private HashMap<String, Integer> v2i, t2i;
    private boolean[][] table;
    private Tokenizer tokenizer;

    public LL1Parser(Grammar grammar, Tokenizer tokenizer) {
//        this.grammar = grammar;
        genTable(grammar);
        this.tokenizer = tokenizer;
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

        table = new boolean[rowNum][colNum];
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                table[i][j] = false;
            }
        }


        // 建立符号对下标的映射
        for (int i = 0; i < rowNum; i++) {
            String v = V.get(i);
            v2i.put(v, i);
        }
        for (int i = 0; i < colNum; i++) {
            String t = T.get(i);
            t2i.put(t, i);
        }
        // 遍历规则 查询select
        for (Rule rule : grammar.getRules()) {
            Set<String> selected = grammar.select(rule);
            int rowIdx = v2i.get(rule.L);
            for (String a : selected) {
                int colIdx = t2i.get(a);
                table[rowIdx][colIdx] = true;
            }
        }


    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // 表头
        for (String colName : t2i.keySet()) {
            sb.append("\t").append(colName);
        }
        // 每行
        for (String rowName : v2i.keySet()) {
            sb.append("\n").append(rowName);
            int rowIdx = v2i.get(rowName);
            for (String colName : t2i.keySet()) {
                int colIdx = t2i.get(colName);
                sb.append("\t").append(table[rowIdx][colIdx]);
            }
        }

        return sb.toString();
    }
}
