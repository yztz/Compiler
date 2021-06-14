package top.yzzblog.compiler.grammar;

import top.yzzblog.compiler.Util;

import java.util.*;
import java.util.function.Predicate;

public class GrammarUtil {
    public static Grammar rmLRecursion(Grammar grammar) {
        Grammar ret = Grammar.extend(grammar);

        for (int i = 0; i < grammar.V.size(); i++) {
            String Ai = grammar.V.get(i);
            List<Rule> ris = new ArrayList<>(); //用于存储Ai相关规则的临时表

            for (Rule rule : grammar.rules) {   // 找到以Ai为左部的规则
                if (rule.L.equals(Ai)) ris.add(rule);
            }

            for (int j = 0; j < i; j++) {
                String Aj = grammar.V.get(j);
                // 1.找到所有以Aj为左部的规则
                List<Rule> rjs = new ArrayList<>();
                for (Rule rule : ret.rules) {
                    if (rule.L.equals(Aj)) rjs.add(rule);
                }

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
            if (indices.isEmpty())  // 当不存在直接左递归
                ret.addRules(ris);
            else {
                List<Rule> rules = new ArrayList<>();   // 新建规则表
                String L_ = L + "$";
                ret.V.add(L_);  // 将新添的符号加入到 V
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


    public static void delInvalidRule(Grammar grammar) {
        Set<String> reachable = new HashSet<>();
        reachable.add(grammar.S);
        int i = 0;
        while (i != grammar.rules.size()) {
            Rule rule = grammar.rules.get(i);
            if (reachable.contains(rule.L) && reachable.addAll(Arrays.asList(rule.R))) {
                i = 0;
            } else {
                i++;
            }
        }
        grammar.V.retainAll(reachable);
        grammar.rules.removeIf(rule -> !grammar.V.contains(rule.L));
    }
}
