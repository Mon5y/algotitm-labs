import java.util.*;

public class ExpressionCalculator {
    private static final String OPERATORS = "+-*/";
    private static final String DELIMITERS = "() " + OPERATORS;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String expression = scanner.nextLine().trim();
        scanner.close();

        if (!expression.endsWith("=")) {
            System.err.println("Ошибка: выражение должно заканчиваться символом '='");
            return;
        }
        expression = expression.substring(0, expression.length() - 1).replace(" ", "");

        try {
            checkBrackets(expression);
            List<String> postfix = infixToPostfix(expression);
            double result = evaluatePostfix(postfix);
            if (result == Double.POSITIVE_INFINITY || result == Double.NEGATIVE_INFINITY) {
                throw new ArithmeticException("Деление на ноль");
            }
            System.out.println(formatResult(result));
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static void checkBrackets(String expression) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    throw new RuntimeException("Несбалансированные скобки");
                }
            }
        }
        if (!stack.isEmpty()) {
            throw new RuntimeException("Несбалансированные скобки");
        }
    }

    private static List<String> infixToPostfix(String expression) {
        List<String> postfix = new ArrayList<>();
        Deque<Character> stack = new ArrayDeque<>();
        StringTokenizer tokenizer = new StringTokenizer(expression, DELIMITERS, true);
        String prevToken = "";

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.isBlank()) continue;

            if (isNumber(token)) {
                postfix.add(token);
            } else if (token.equals("(")) {
                stack.push('(');
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.add(String.valueOf(stack.pop()));
                }
                if (stack.isEmpty()) {
                    throw new RuntimeException("Несбалансированные скобки");
                }
                stack.pop();
            } else if (OPERATORS.contains(token)) {
                if (prevToken.isEmpty() || OPERATORS.contains(prevToken) || prevToken.equals("(")) {
                    throw new RuntimeException("Неверное положение оператора");
                }
                while (!stack.isEmpty() && priority(stack.peek()) >= priority(token.charAt(0))) {
                    postfix.add(String.valueOf(stack.pop()));
                }
                stack.push(token.charAt(0));
            }
            prevToken = token;
        }

        while (!stack.isEmpty()) {
            char op = stack.pop();
            if (op == '(') {
                throw new RuntimeException("Несбалансированные скобки");
            }
            postfix.add(String.valueOf(op));
        }
        return postfix;
    }

    private static double evaluatePostfix(List<String> postfix) {
        Deque<Double> stack = new ArrayDeque<>();
        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": 
                        if (b == 0) throw new ArithmeticException("Деление на ноль");
                        stack.push(a / b); 
                        break;
                }
            }
        }
        if (stack.size() != 1) {
            throw new RuntimeException("Неверное выражение");
        }
        return stack.pop();
    }

    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int priority(char op) {
        return switch (op) {
            case '*', '/' -> 2;
            case '+', '-' -> 1;
            default -> 0;
        };
    }

    private static String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            return String.format("%s", result);
        }
    }
}