package com.srgood.dbot.math.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;

import com.srgood.dbot.math.api.IMathGroup;
import com.srgood.dbot.math.api.IMathHandler;

public enum MathHandler implements IMathHandler {

    INSTANCE;

    public IMathGroup parse(String exp) {
//        System.out.println("parse called for: " + exp);

        exp = exp.trim().replaceAll("\\s+", "");

        if (!(exp.contains("+") || exp.contains("-") || exp.contains("*") || exp.contains("/") || exp.contains("(") || exp.contains(")"))) {
            return new MathGroupBasic(new BigDecimal(exp));
        }

        if (!exp.contains("(") && !exp.contains(")")) {
            return parseNoParens(exp);
        } else {
            if (countCharInString(exp, '(') != countCharInString(exp, ')')) {
                return null;
            }
            return parseWithParens(exp);
        }
    }
    
    private int countCharInString(String s, char target) {
        int ret = 0;
        
        for (char c : s.toCharArray()) {
            if (c == target) {
                ret++;
            }
        }
        
        return ret;
    }

    private IMathGroup parseWithParens(String exp) {
        System.out.println("paren parse of: " + exp);
        
        int firstGroupOpenIndex = -1;
        int firstGroupCloseIndex = -1;
        
        int openGroups = 0;
        boolean openFound = false;
        
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            
            if (c == '(') {
                if (openGroups == 0) {
                    firstGroupOpenIndex = i;
                }
                
                openGroups++;
                openFound = true;
            } else if (c == ')') {
                openGroups--;
                if (openGroups == 0 && openFound) {
                    firstGroupCloseIndex = i;
                }
            }
            
            if (openGroups < 0) {
                return null;
            }
            
            if (openFound && openGroups == 0) {
                String subBase = exp.substring(firstGroupOpenIndex, firstGroupCloseIndex + 1);
                String subNoParens = subBase.substring(1, subBase.length() - 1);
                BigDecimal parseResult = parse(subNoParens).eval();
                String quoteToReplace = cleanReplacementString(Matcher.quoteReplacement(subBase));
                System.out.println("old exp = " + exp);
                System.out.println("subBase = " + subBase);
                System.out.println("quoteReplacement(subBase) = " + quoteToReplace);
                System.out.println("subNoParens = " + subNoParens);
                System.out.println("replacing sub with: " + parseResult);
                exp = exp.replaceFirst(quoteToReplace, parseResult.toString());
                System.out.println("new exp: " + exp);
                return parse(exp);
            }
        }
        return null;
    }
    
    private String cleanReplacementString(String s) {
        return s.replace("*", "\\*").replace("+", "\\+").replace("(", "\\(").replace(")", "\\)");
    }

    private IMathGroup parseNoParens(String exp) {
        // a*b*c/d+c
        // AddGroup(MathGroupMultiply(new MathGroupBasic(a), "b", "c/d"), "")

        try {

            String[] topLevelComponents;

            int mode = 0;

            if (exp.contains("+")) {
//                System.out.println("Splitting on +");
                topLevelComponents = exp.split("\\+");
                mode = 0;
            } else if (exp.contains("-")) {
//                System.out.println("Splitting on -");
                topLevelComponents = exp.split("-");
                mode = 1;
            } else if (exp.contains("*")) {
//                System.out.println("Splitting on *");
                topLevelComponents = exp.split("\\*");
                mode = 2;
            } else if (exp.contains("/")) {
//                System.out.println("Splitting on /");
                topLevelComponents = exp.split("/");
                mode = 3;
            } else {
                topLevelComponents = new String[] { exp };
                mode = 4;
            }

            IMathGroup[] retParams = new IMathGroup[topLevelComponents.length];

            for (int i = 0; i < topLevelComponents.length; i++) {
//                System.out.println("Parsing sub-component");
                retParams[i] = parse(topLevelComponents[i]);
            }

//            System.out.println("retParams: " + Arrays.deepToString(retParams));

            IMathGroup ret;

            if (mode == 0) {
//                System.out.println("Returning Addition");
                ret = new MathGroupAddition(retParams);
            } else if (mode == 1) {
//                System.out.println("Returning Subtraction");
                ret = new MathGroupSubtraction(retParams);
            } else if (mode == 2) {
//                System.out.println("Returning Multiplication");
                ret = new MathGroupMultiplication(retParams);
            } else if (mode == 3) {
//                System.out.println("Returning Division");
                ret = new MathGroupDivision(retParams);
            } else {
//                System.out.println("Returning Basic");
                ret = new MathGroupBasic(retParams[0]);
            }

//            System.out.println("Returning: " + ret);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
