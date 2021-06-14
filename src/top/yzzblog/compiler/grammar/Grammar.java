package top.yzzblog.compiler.grammar;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Grammar {
    public final List<String> V, T;
    public final List<Rule> rules;
    public final String S;

    private Grammar(List<String> V, List<String> T, List<String> P, String S) {
        this.V = new ArrayList<>(V);
        this.T = new ArrayList<>(T);
        this.S = S;
        //初始化规则
        rules = new ArrayList<>();
        for (String rule : P) {
            this.rules.add(new Rule(rule));
        }
    }

    private Grammar(Grammar grammar, List<Rule> rules) {
        this.V = new ArrayList<>(grammar.V);
        this.T = new ArrayList<>(grammar.T);

        this.S = grammar.S;
        this.rules = rules;
    }

    public static Grammar extend(Grammar old, List<Rule> rules) {
        return new Grammar(old, rules);
    }

    public static Grammar extend(Grammar old) {
        return new Grammar(old, new ArrayList<>());
    }

    public void addRules(List<Rule> rules) {
        this.rules.addAll(rules);
    }

    public static Grammar parse(String filename) throws IOException {
        // 读取文件
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String tmp;
        while ((tmp = reader.readLine()) != null) sb.append(tmp);
        reader.close();
        tmp = sb.toString();
        // 解析
        JSONObject raw = JSONObject.parseObject(tmp);

        List<String> V, T, P;
        String S;

        V = raw.getJSONArray("V").toJavaList(String.class);
        T = raw.getJSONArray("T").toJavaList(String.class);
        P = raw.getJSONArray("P").toJavaList(String.class);
        S = raw.getString("S");

        return new Grammar(V, T, P, S);
    }

    public int getRuleNum() {
        return this.rules.size();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Rule rule : rules) {
            sb.append(rule.L).append(" -> ").append(rule.R_str).append("\n");
        }

        return sb.toString();
    }

}
