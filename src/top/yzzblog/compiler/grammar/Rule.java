package top.yzzblog.compiler.grammar;

import top.yzzblog.compiler.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rule {
    public final String L;
    public final String[] R;
    public final String R_str;

    public Rule(String rule) {
        String[] lr = rule.split("::");
        L = lr[0];
        if (lr.length > 1) {
            this.R_str = lr[1];
            String pattern = ".\\$*";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(lr[1]);
            List<String> R = new ArrayList<>();
            while (m.find()) R.add(m.group());
            this.R = R.toArray(new String[0]);
        } else {
            this.R_str = Grammar.EPSILON;
            this.R = new String[]{this.R_str};
        }
//        int len = lr[1].length();
//        R = new String[len];
//        for (int i = 0; i < len; i++) {
//            // 这边先假设符号都为单字节，后续再做修改
//            R[i] = lr[1].substring(i, i + 1);
//        }

    }

    public String shiftRHead() {
        return R_str.substring(R[0].length());
    }

    public Rule(String L, String[] R) {
        this.L = L;
        this.R = R;
        this.R_str = Util.arrayToString(R);
    }

    public boolean isEpsilon() {
        return R.length == 1 && R[0].equals(Grammar.EPSILON);
    }


    @Override
    public String toString() {
        return L + " -> " + R_str;
    }
}
