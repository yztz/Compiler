package top.yzzblog.compiler.grammar;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yzzblog.compiler.util.Util;

import java.util.*;

public class Grammar {
    private static final Logger logger = LoggerFactory.getLogger(Grammar.class);

    public static final String END = "#";
    public static final String EPSILON = "ε";

    private final List<String> V, T;
    private final List<Rule> rules;
    private final String S;

    private HashMap<String, Set<String>> first;
    private HashMap<String, Set<String>> follow;

    private String name;

    private Grammar(String name, List<String> V, List<String> T, List<String> P, String S) {
        if (null == name) {
            this.name = "(untitled)";
        } else {
            this.name = name;
        }

        this.V = new ArrayList<>(V);
        this.T = new ArrayList<>(T);
        this.S = S;
        //初始化规则
        rules = new ArrayList<>();
        for (String rule : P) {
            this.rules.add(new Rule(rule));
        }
        // 为了后续处理的方便，加入'#'做为文件结束符
        this.T.add(Grammar.END);
    }


    private Grammar(Grammar grammar, List<Rule> rules) {
        this.name = grammar.name;
        this.V = new ArrayList<>(grammar.V);
        this.T = new ArrayList<>(grammar.T);

        this.S = grammar.S;
        this.rules = rules;
    }

    /**
     * 检查文法正确性， 消除冗余
     * @return
     */
    private boolean validate() {
        if (!this.V.contains(S)) {
            logger.error("开始符号【" + S + "】不存在于非终结符号集中");
            return false;
        }
        /*
        相关规则检查
        1. 检查规则是否包含了所有非终结符号的产生式
        2. 检查规则中的符号是否都包含在了V/T中
         */
        Set<String> vanish = new HashSet<>();
        for (Rule rule : rules) { // 检查规则
            String L = rule.L;
            String[] R = rule.R;

            vanish.add(L);
            vanish.addAll(Arrays.asList(R));
            if (!V.contains(L)) {   // V中不存在，但是规则中左部存在的符号
                logger.warn("非终结符号【" + L + "】不存在");
                V.add(L);
            }
        }
        // V中存在，但是规则中不存在
        V.removeIf(v -> {
            if (!vanish.contains(v)) {
                logger.warn("非终结符号【" + v + "】没有被应用在规则中");
                return true;
            } else return false;
        });
        // T中存在，但是规则中不存在
        T.removeIf(t -> {
            if (!vanish.contains(t) && !t.equals(Grammar.END)) {
                logger.warn("终结符号【" + t + "】没有被应用在规则中");
                return true;
            } else return false;
        });

        // V中不存在，但是在规则右部存在
        vanish.removeAll(T);
        for (String v : vanish) {
            if (!V.contains(v) && !v.equals(Grammar.EPSILON)) {
                logger.error("无效的非终结符号【" + v + "】");
                return false;
            }
        }

        return true;
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


    public static Grammar parse(String filepath) {
        // 读取解析文件
        JSONObject raw = Util.readJSONObj(filepath);

        List<String> V, T, P;
        String S, name;

        V = raw.getJSONArray("V").toJavaList(String.class);
        T = raw.getJSONArray("T").toJavaList(String.class);
        P = raw.getJSONArray("P").toJavaList(String.class);
        S = raw.getString("S");
        name = raw.getString("name");

        Grammar grammar = new Grammar(name, V, T, P, S);
        if(!grammar.validate()) throw new GrammarParseException("无效的文法");

        return grammar;
    }

    public String getName() {
        return name;
    }

    private void clearCache() {
//        this.firstCache.clear();
    }

    public Set<String> select(Rule rule) {
        if (this.first == null || this.follow == null) return null;

        String L = rule.L;
        String[] R = rule.R;
        Set<String> ret = first(R);

        if (ret.contains(Grammar.EPSILON)) {    // 包含 ε
            ret.remove(Grammar.EPSILON);
            ret.addAll(follow(L));
        }

        return Collections.unmodifiableSet(ret);
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
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
        logger.info("开始生成集合...");

        this.first = new HashMap<>();
        this.follow = new HashMap<>();
        genFirst();
        genFollow();
    }

    private void genFirst() {
        logger.info("生成First集");

        for (String v : V) first.put(v, new HashSet<>());
        for (String t : T) first.put(t, Collections.singleton(t));
        first.put(Grammar.EPSILON, Collections.singleton(Grammar.EPSILON));
        while (true) {
            boolean flag = false;
            for (Rule rule : rules) {
                Set<String> X = first.get(rule.L);
                if (rule.isEpsilon()) { //空推导
                    flag |= X.add(Grammar.EPSILON);
                } else {
                    int len = rule.R.length;
                    boolean epsilonExist = true;
                    int idx = 0;
                    while (epsilonExist && idx < len) {
                        Set<String> Y_idx = first(rule.R[idx]);
                        Set<String> tmp = new HashSet<>(Y_idx);
                        epsilonExist = tmp.remove(Grammar.EPSILON);
                        flag |= X.addAll(tmp);
                        idx++;
                    }
                    if (idx == len && epsilonExist) flag |= X.add(Grammar.EPSILON);
                }
            }
            if (!flag) break;
        }

    }

    private void genFollow() {
        logger.info("生成Follow集");

        for (String v : V) follow.put(v, new HashSet<>());
        follow.get(S).add(Grammar.END);
        while (true) {
            boolean flag = false;
            for (Rule rule : rules) {
                int len = rule.R.length;
                for (int i = 0; i < len; i++) {
                    String v = rule.R[i];
                    if (V.contains(v)) {
                        if (i + 1 == len) {
                            flag |= follow.get(v).addAll(follow.get(rule.L));
                        } else {
                            Set<String> rst_first = first(Arrays.copyOfRange(rule.R, i + 1, len));
                            if (rst_first.remove(Grammar.EPSILON)) {    // ε存在
                                flag |= follow.get(v).addAll(follow.get(rule.L));
                            }
                            flag |= follow.get(v).addAll(rst_first);
                        }
                    }
                }
            }
            if (!flag) break;
        }
    }

    public Set<String> first(String X) {
        return Collections.unmodifiableSet(first.get(X));
    }

    public Set<String> first(String[] X) {
        Set<String> ret = new HashSet<>();
        boolean epsilonExist = true;
        int idx = 0;
        int len = X.length;
//        System.out.println(Arrays.toString(X));
        while (idx < len && epsilonExist) {
//            System.out.println(X[idx]);
            Set<String> Xi = first(X[idx]);
            Set<String> tmp = new HashSet<>(Xi);    //copy
            epsilonExist = tmp.remove(Grammar.EPSILON);
            ret.addAll(tmp);
            idx++;
        }
        if (idx == len && epsilonExist) ret.add(Grammar.EPSILON);

        return ret;
    }


    public Set<String> follow(String X) {
        return Collections.unmodifiableSet(follow.get(X));
    }


    public List<Rule> getRulesOf(String X) {
        List<Rule> ret = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.L.equals(X)) ret.add(rule);
        }

        return ret;
    }

    public void delInvalidRule() {
        Set<String> reachable = new HashSet<>();
        reachable.add(S);
        int i = 0;
        while (i != getRuleNum()) { // 当集合不再变更
            Rule rule = getRule(i);
            // 可达集合包含 且 集合新增
            if (reachable.contains(rule.L) && reachable.addAll(Arrays.asList(rule.R))) {
                i = 0;
            } else {
                i++;
            }
        }
        V.retainAll(reachable); // 去除不相关的非终结符号
        this.rules.removeIf(rule -> !V.contains(rule.L)); // 去除对应规则
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getName()).append("\n");
        sb.append("S: ").append(getS()).append('\n');
        sb.append("V: ").append(getV()).append('\n');
        sb.append("T: ").append(getT()).append('\n');
        sb.append("P: ").append('\n');
        getRules().forEach(rule -> sb.append('\t').append(rule).append('\n'));

        return sb.toString();
    }

}
