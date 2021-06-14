package top.yzzblog.compiler.grammar;

import java.util.*;

public class GrammarUtil {
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
        delInvalidRule(ret);

        return ret;
    }

    /**
     * 无关项消除
     *
     * @param grammar
     */
    public static void delInvalidRule(Grammar grammar) {
        Set<String> reachable = new HashSet<>();
        reachable.add(grammar.getS());
        int i = 0;
        while (i != grammar.getRuleNum()) { // 当集合不再变更
            Rule rule = grammar.getRule(i);
            // 可达集合包含 且 集合新增
            if (reachable.contains(rule.L) && reachable.addAll(Arrays.asList(rule.R))) {
                i = 0;
            } else {
                i++;
            }
        }
        grammar.getV().retainAll(reachable); // 去除不相关的非终结符号
        grammar.removeRuleIf(rule -> !grammar.getV().contains(rule.L)); // 去除对应规则
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


}
