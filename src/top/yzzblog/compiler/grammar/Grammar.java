package top.yzzblog.compiler.grammar;

import com.alibaba.fastjson.JSONObject;
import top.yzzblog.compiler.Util;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

public class Grammar {
    private final List<String> V, T;
    private final List<Rule> rules;
    private final String S;

    private HashMap<String, Set<String>> first = new HashMap<>();
    private HashMap<String, Set<String>> follow = new HashMap<>();

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
        clearCache();
        this.rules.addAll(rules);
    }

    public void addRule(Rule rule) {
        clearCache();
        this.rules.add(rule);
    }

    public Rule getRule(int i) {
        return this.rules.get(i);
    }

    public void removeRuleIf(Predicate<Rule> filter) {
        clearCache();
        this.rules.removeIf(filter);
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

    private void clearCache() {
//        this.firstCache.clear();
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<String> getT() {
        return T;
    }

    public List<String> getV() {
        return V;
    }

    public String getS() {
        return S;
    }

    public int getRuleNum() {
        return this.rules.size();
    }

    public void compile() {
        genFirst();
        genFollow();
    }

    private void genFirst() {
        for (String v : V) first.put(v, new HashSet<>());
        for (String t : T) first.put(t, Collections.singleton(t));
        while (true) {
            boolean flag = false;
            for (Rule rule : rules) {
                Set<String> X = first(rule.L);
                if (rule.isEpsilon()) { //空推导
                    flag |= X.add("ε");
                } else {
                    int len = rule.R.length;
                    boolean epsilonExist = true;
                    int idx = 0;
                    while (epsilonExist && idx < len) {
                        Set<String> Y_idx = first(rule.R[idx]);
                        Set<String> tmp = new HashSet<>(Y_idx);
                        epsilonExist = tmp.remove("ε");
                        flag |= X.addAll(tmp);
                        idx++;
                    }
                    if (idx == len && epsilonExist) flag |= X.add("ε");
                }
            }
            if (!flag) break;
        }

    }

    private void genFollow() {
        for (String v : V) follow.put(v, new HashSet<>());
        follow.get(S).add("#");
        while (true) {
            boolean flag = false;
            for (Rule rule : rules) {
                int len = rule.R.length;
                for (int i = 0; i < len; i++) {
                    String v = rule.R[i];
                    if (V.contains(v)) {
                        if (i + 1 == len) {
                            flag |= follow(v).addAll(follow(rule.L));
                        } else {
                            Set<String> rst_first = first(Arrays.copyOfRange(rule.R, i + 1, len));
                            if (rst_first.remove("ε")) {    // ε存在
                                flag |= follow(v).addAll(follow(rule.L));
                            }
                            flag |= follow(v).addAll(rst_first);
                        }
                    }
                }
            }
            if (!flag) break;
        }
    }

    public Set<String> first(String X) {
        return first.get(X);
    }

    public Set<String> first(String[] X) {
        Set<String> ret = new HashSet<>();
        boolean epsilonExist = true;
        int idx = 0;
        int len = X.length;
        while (idx < len && epsilonExist) {
            Set<String> Xi = first(X[idx]);
            Set<String> tmp = new HashSet<>(Xi);
            epsilonExist = tmp.remove("ε");
            ret.addAll(tmp);
            idx++;
        }
        if (idx == len && epsilonExist) ret.add("ε");

        return ret;
    }


    public Set<String> follow(String X) {
        return follow.get(X);
    }


    public List<Rule> getRulesOf(String X) {
        List<Rule> ret = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.L.equals(X)) ret.add(rule);
        }

        return ret;
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
