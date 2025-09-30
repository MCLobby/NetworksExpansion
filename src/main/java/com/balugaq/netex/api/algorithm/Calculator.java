package com.balugaq.netex.api.algorithm;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author balugaq
 */
@NullMarked
public class Calculator {
    // 运算符优先级映射
    private static final Map<String, Integer> PRIORITY = new HashMap<>();

    static {
        // 单目运算符优先级最高
        PRIORITY.put("~", 4);
        PRIORITY.put("!", 4);

        // 位运算符
        PRIORITY.put("&", 3);
        PRIORITY.put("^", 3);
        PRIORITY.put("|", 3);
        PRIORITY.put("<<", 3);
        PRIORITY.put(">>", 3);

        // 算术运算符
        PRIORITY.put("*", 2);
        PRIORITY.put("/", 2);
        PRIORITY.put("+", 1);
        PRIORITY.put("-", 1);
        PRIORITY.put("(", 0);
    }

    @SuppressWarnings("DuplicatedCode")
    public static BigDecimal calculate(String expression) throws CalculateException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new CalculateException("表达式为空");
        }

        // 预处理：替换各种括号为标准小括号
        String expr = replaceBrackets(expression);

        // 预处理：移除所有空格
        expr = expr.replaceAll("\\s+", "");

        // 自动补全括号
        expr = completeParentheses(expr);

        // 校验括号是否匹配
        if (!isValidParentheses(expr)) {
            throw new CalculateException("括号无法自动补全为有效表达式");
        }

        // 双栈：操作数栈(使用BigDecimal支持浮点数)和运算符栈
        Deque<BigDecimal> numStack = new ArrayDeque<>();
        Deque<String> opStack = new ArrayDeque<>();

        int i = 0;
        int n = expr.length();

        while (i < n) {
            char c = expr.charAt(i);

            // 处理数字、小数点和百分比
            if (Character.isDigit(c) || c == '.') {
                int j = i;
                // 收集完整的数字（包括小数点）
                while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                    j++;
                }

                // 检查是否是百分比
                boolean isPercentage = false;
                if (j < n && expr.charAt(j) == '%') {
                    isPercentage = true;
                    j++;
                }

                String numStr = expr.substring(i, j - (isPercentage ? 1 : 0));
                // 解析数字，支持.123和123.格式
                BigDecimal num = parseNumber(numStr);

                // 处理百分比
                if (isPercentage) {
                    num = num.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                }

                numStack.push(num);
                i = j;
            }
            // 处理左括号
            else if (c == '(') {
                opStack.push(String.valueOf(c));
                i++;
            }
            // 处理右括号
            else if (c == ')') {
                // 计算到最近的左括号为止
                String pk = opStack.peek();
                if (pk == null) {
                    throw new CalculateException("括号不匹配: " + expression);
                }
                
                while (!pk.equals("(")) {
                    calculateTop(numStack, opStack);
                }
                opStack.pop(); // 弹出左括号
                i++;
            }
            // 处理双字符运算符(<<, >>)
            else if ((c == '<' || c == '>') && i + 1 < n && expr.charAt(i + 1) == c) {
                String op = expr.substring(i, i + 2);
                // 处理运算符优先级
                while (!opStack.isEmpty() && getPriority(opStack.peek()) >= getPriority(op)) {
                    calculateTop(numStack, opStack);
                }
                opStack.push(op);
                i += 2;
            }
            // 处理单目运算符(~, !)
            else if (c == '~' || c == '!') {
                String op = String.valueOf(c);
                // 单目运算符优先级最高，直接入栈
                opStack.push(op);
                i++;
            }
            // 处理其他运算符
            else if (isOperator(String.valueOf(c))) {
                // 处理正号作为一元运算符的情况
                if (c == '+' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(String.valueOf(expr.charAt(i - 1))))) {
                    // 检查下一个字符是否是数字或小数点
                    if (i + 1 >= n || (!Character.isDigit(expr.charAt(i + 1)) && expr.charAt(i + 1) != '.')) {
                        throw new CalculateException("无效的正号位置: " + expression);
                    }

                    int j = i + 1;
                    // 收集完整的数字（包括小数点）
                    while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                        j++;
                    }

                    // 检查是否是百分比
                    boolean isPercentage = false;
                    if (j < n && expr.charAt(j) == '%') {
                        isPercentage = true;
                        j++;
                    }

                    String numStr = expr.substring(i + 1, j - (isPercentage ? 1 : 0));
                    BigDecimal num = parseNumber(numStr);

                    // 处理百分比
                    if (isPercentage) {
                        num = num.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                    }

                    numStack.push(num);
                    i = j;
                }
                // 处理负号作为一元运算符的情况
                else if (c == '-' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(String.valueOf(expr.charAt(i - 1))))) {
                    // 检查下一个字符是否是数字或小数点
                    if (i + 1 >= n || (!Character.isDigit(expr.charAt(i + 1)) && expr.charAt(i + 1) != '.')) {
                        throw new CalculateException("无效的负号位置: " + expression);
                    }

                    int j = i + 1;
                    // 收集完整的数字（包括小数点）
                    while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                        j++;
                    }

                    // 检查是否是百分比
                    boolean isPercentage = false;
                    if (j < n && expr.charAt(j) == '%') {
                        isPercentage = true;
                        j++;
                    }

                    String numStr = "-" + expr.substring(i + 1, j - (isPercentage ? 1 : 0));
                    BigDecimal num = parseNumber(numStr);

                    // 处理百分比
                    if (isPercentage) {
                        num = num.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                    }

                    numStack.push(num);
                    i = j;
                } else {
                    // 处理二元运算符
                    String op = String.valueOf(c);
                    // 当前运算符优先级小于等于栈顶运算符时，先计算栈顶的
                    while (!opStack.isEmpty() && getPriority(opStack.peek()) >= getPriority(op)) {
                        calculateTop(numStack, opStack);
                    }
                    opStack.push(op);
                    i++;
                }
            }
            // 非法字符
            else {
                throw new CalculateException("无效字符: " + c);
            }
        }

        // 处理剩余的运算符
        while (!opStack.isEmpty()) {
            calculateTop(numStack, opStack);
        }

        if (numStack.size() != 1) {
            throw new CalculateException("无效的表达式: " + expression);
        }

        return numStack.pop();
    }

    // 替换各种括号为标准小括号
    @SuppressWarnings("RegExpRedundantEscape")
    private static String replaceBrackets(String expr) {
        // 替换中文括号和其他类型的括号为标准小括号
        return expr
            .replaceAll("[\\[\\{（【《]", "(")
            .replaceAll("[\\]\\}）】》]", ")");
    }

    // 自动补全括号
    private static String completeParentheses(String expr) {
        int openCount = 0;
        int closeCount = 0;

        // 统计现有括号数量
        for (char c : expr.toCharArray()) {
            if (c == '(') {
                openCount++;
            } else if (c == ')') {
                closeCount++;
            }
        }

        // 计算需要补全的括号数量
        int diff = openCount - closeCount;

        // 如果左括号多，在末尾添加相应数量的右括号
        if (diff > 0) {
            expr = expr + ")".repeat(diff);
        }
        // 如果右括号多，在开头添加相应数量的左括号
        else if (diff < 0) {
            expr = "(".repeat(Math.max(0, -diff)) + expr;
        }

        return expr;
    }

    // 解析数字，支持.123和123.格式
    private static BigDecimal parseNumber(String numStr) throws CalculateException {
        try {
            // 处理123.这种格式
            if (numStr.endsWith(".")) {
                numStr += "0";
            }
            // 处理.123这种格式
            if (numStr.startsWith(".") && numStr.length() > 1) {
                numStr = "0" + numStr;
            }
            return new BigDecimal(numStr);
        } catch (NumberFormatException e) {
            throw new CalculateException("无效的数字格式: " + numStr);
        }
    }

    // 计算栈顶的运算符
    private static void calculateTop(Deque<BigDecimal> numStack, Deque<String> opStack) throws CalculateException {
        String op = opStack.pop();

        // 处理单目运算符
        if (op.equals("~") || op.equals("!")) {
            if (numStack.isEmpty()) {
                throw new CalculateException("无效的表达式，单目运算符缺少操作数");
            }

            BigDecimal a = numStack.pop();
            // 位运算需要整数，进行类型转换
            long aLong = a.longValue();
            long resultLong = switch (op) {
                case "~" ->  // 按位非
                    ~aLong;
                case "!" ->  // 逻辑非
                    (aLong == 0) ? 1 : 0;
                default -> throw new CalculateException("无效的单目运算符: " + op);
            };

            numStack.push(new BigDecimal(resultLong));
            return;
        }

        // 处理双目运算符
        if (numStack.size() < 2) {
            throw new CalculateException("无效的表达式，双目运算符缺少操作数");
        }

        BigDecimal b = numStack.pop();
        BigDecimal a = numStack.pop();
        BigDecimal result;

        // 区分算术运算符和位运算符处理
        switch (op) {
            case "+":
                result = a.add(b);
                break;
            case "-":
                result = a.subtract(b);
                break;
            case "*":
                result = a.multiply(b);
                break;
            case "/":
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("除数不能为0");
                }
                // 除法保留10位小数，四舍五入
                result = a.divide(b, 10, RoundingMode.HALF_UP);
                break;
            // 位运算符需要转换为long处理
            case "&":
                long aLongAnd = a.longValue();
                long bLongAnd = b.longValue();
                result = new BigDecimal(aLongAnd & bLongAnd);
                break;
            case "|":
                long aLongOr = a.longValue();
                long bLongOr = b.longValue();
                result = new BigDecimal(aLongOr | bLongOr);
                break;
            case "^":
                long aLongXor = a.longValue();
                long bLongXor = b.longValue();
                result = new BigDecimal(aLongXor ^ bLongXor);
                break;
            case "<<":
                long aLongLsh = a.longValue();
                long bLongLsh = b.longValue();
                result = new BigDecimal(aLongLsh << bLongLsh);
                break;
            case ">>":
                long aLongRsh = a.longValue();
                long bLongRsh = b.longValue();
                result = new BigDecimal(aLongRsh >> bLongRsh);
                break;
            default:
                throw new CalculateException("无效的运算符: " + op);
        }

        numStack.push(result);
    }

    // 获取运算符优先级
    private static int getPriority(String op) {
        return PRIORITY.getOrDefault(op, 0);
    }

    // 检查括号是否匹配
    private static boolean isValidParentheses(String expr) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : expr.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    // 判断是否为运算符
    private static boolean isOperator(String op) {
        return PRIORITY.containsKey(op);
    }
}
