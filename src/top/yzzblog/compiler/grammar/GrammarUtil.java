package top.yzzblog.compiler.grammar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GrammarUtil {
    private static final Logger logger = LoggerFactory.getLogger(GrammarUtil.class);

    /**
     * 左递归消除
     *
     * @param grammar
     * @return
     */
    public static Grammar rmLRecursion(Grammar grammar) {
        Grammar ret = Grammar.extend(grammar);

        for (int i = 0; i < grammar.getV().size(); i++) {
            String Ai = grammar.getV().get(i);
            List<Rule> ris = grammar.getRulesOf(Ai);; //找到所有以Ai为左部的规则

            for (int j = 0; j < i; j++) {
                String Aj = grammar.getV().get(j);
                // 1.找到所有以Aj为左部的规则
                List<Rule> rjs = grammar.getRulesOf(Aj);

                // 2.找到所有形如Ai -> Ajγ的规则
                List<Rule> tmp = new ArrayList<>(ris);
                for (Rule rule : tmp) {
                    if (rule.R[0].equals(Aj)) { // 右部首位为Aj
                        // 3. rj in rjs代入替换
                        ris.remove(rule);
                        for (Rule rj : rjs) {   // rj 右部拼接上 ri去头后的右部作为新右部
                            Rule r = new Rule(Ai + "::" + rj.R_str + rule.shiftRHead());
                            ris.add(r);
                        }
                    }
                }
            }
//            System.out.println("消除左递归前:\n" + ris);

            // 4.消除存在于Ai的直接左递归
            List<Integer> indices = new ArrayList<>();
            String L = ris.get(0).L;
            for (int idx = 0; idx < ris.size(); idx++) {
                Rule rule = ris.get(idx);
                if (L.equals(rule.R[0])) indices.add(idx);
            }
            if (indices.isEmpty()) {  // 当不存在直接左递归
                ret.addRules(ris);
            } else {
                List<Rule> rules = new ArrayList<>();   // 新建规则表
                String L_ = L + "$";
                ret.getV().add(L_);  // 将新添的符号加入到 V
                for (int idx = 0; idx < ris.size(); idx++) {
                    Rule rule = ris.get(idx);
                    if (indices.contains(idx)) {    // 如果当前索引规则存在左递归
                        rules.add(new Rule(L_ + "::" + rule.shiftRHead() + L_));
                    } else {
                        rules.add(new Rule(L + "::" + rule.R_str + L_));
                    }
                }
                rules.add(new Rule(L_)); // 为新增非终结符添加空规则 ε
                ret.addRules(rules);
            }

//            System.out.println("消除左递归后:\n" + ret);
        }

        // 5.消除无关项
        ret.delInvalidRule();

        return ret;
    }


    /**
     * 左公共因子提取（目前只支持单因子的提取，长前缀|隐含公共因子等不支持）
     * @param grammar
     * @return
     */
    public static Grammar extractCommonFactor(Grammar grammar) {
        List<String> V = grammar.getV();
        Grammar ret = Grammar.extend(grammar);

        for (String v : V) {
            // 1. 提取以v为左部的规则
            List<Rule> rules = grammar.getRulesOf(v);

            // 2.搜索规则是否存在左公共因子
            HashMap<String, List<Rule>> map = new HashMap<>();
            for (Rule rule : rules) {   //将相同因子开头的规则按照开头划分
                String head = rule.R[0];
                List<Rule> l = map.getOrDefault(head, null);
                if (null == map.getOrDefault(head, null)) {
                    l = new ArrayList<>();
                    map.put(head, l);
                }
                l.add(rule);
            }
            int max = 0;
            String commonHead = null;
            for (String key : map.keySet()) {   //寻找最多规则的公共头
                if (map.get(key).size() > max) {
                    max = map.get(key).size();
                    commonHead = key;
                }
            }

            if (max == 1) {
                ret.addRules(rules);  //最大规则集长度为1 直接加入
            } else {
                List<Rule> extRules = map.get(commonHead);
//                System.out.println("提取: " + extRules);
                rules.removeAll(extRules);
                String v_ = v + '$';
                ret.getV().add(v_);
                ret.addRules(rules);
                ret.addRule(new Rule(v + "::" + commonHead + v_));
                for (Rule rule : extRules) {
                    ret.addRule(new Rule(v_ + "::" + rule.shiftRHead()));
                }
            }


        }


        return ret;
    }

    public static boolean isLL1(Grammar grammar) {
        // 左递归、公共因子现象
        if (isCommonFactorExist(grammar)) {
            logger.debug("非LL1：存在左公共因子");
            return false;
        }

        if (isLRecursionExist(grammar)) {
            logger.debug("非LL1：存在左递归");
            return false;
        }

        grammar.compile();
        for (String v : grammar.getV()) {   // 遍历所有非终结符号下的规则
            List<Set<String>> sets = new ArrayList<>();
            for (Rule rule : grammar.getRulesOf(v)) {
                sets.add(grammar.select(rule));
            }

            for (int i = 0; i < sets.size(); i++) {
                Set<String> s1 = new HashSet<>(sets.get(i));
                for (int j = i + 1; j < sets.size(); j++) {
                    Set<String> s2 = new HashSet<>(sets.get(j));
                    s2.retainAll(s1);
                    if (!s2.isEmpty()) {
                        logger.info("非LL1：关于非终结符【" + v + "】的规则select集存在交集:" + s2);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * 判断是否存在左递归
     * @param grammar
     * @return
     */
    public static boolean isLRecursionExist(Grammar grammar) {
        for (Rule rule : grammar.getRules()) {
            String L = rule.L;
            if (t(grammar, L, rule)) return true;
        }
        return false;
    }

    public static boolean isCommonFactorExist(Grammar grammar) {
        for (String v : grammar.getV()) {
            HashMap<String, Integer> map = new HashMap<>();
            for (Rule rule : grammar.getRulesOf(v)) {
                String R = rule.R[0];
                Integer i;
                if ((i = map.putIfAbsent(R, 1)) != null) {
                    map.put(R, i + 1);
                }
            }
            return map.values().removeIf(integer -> integer > 1);
        }
        return false;
    }

    private static boolean t(Grammar grammar, String L, Rule rule) { // 当代入rule后是否存在左递归
        if (L.equals(rule.R[0])) return true;
        if (grammar.getT().contains(rule.R[0])) return false;

        for (Rule r : grammar.getRulesOf(rule.R[0])) {
            if (t(grammar, L, r)) return true;
        }
        return false;
    }

}
