import java.util.*;

public class ExpressionCalculator {
    private static final String OPERATORS = "+-*/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine()) {
            System.err.println("Ошибка: пустое выражение");
            return;
        }
        String expression = scanner.nextLine().trim();
        scanner.close();

        if (!expression.endsWith("=")) {
            System.err.println("Ошибка: выражение должно заканчиваться символом '='");
            return;
        }
        // Убираем '=' в конце
        expression = expression.substring(0, expression.length() - 1);

        try {
            checkBrackets(expression);
            List<String> postfix = infixToPostfix(expression);
            double result = evaluatePostfix(postfix);
            
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                throw new ArithmeticException("Деление на ноль или некорректная операция");
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
        List<String> tokens = tokenize(expression);
        List<String> postfix = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        
        boolean expectOperand = true; // Ожидаем ли мы число (нужно для унарных знаков)

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            if (isNumber(token)) {
                postfix.add(token);
                expectOperand = false;
            } else if (token.equals("(")) {
                stack.push(token);
                expectOperand = true;
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new RuntimeException("Несбалансированные скобки");
                }
                stack.pop();
                expectOperand = false;
            } else if (OPERATORS.contains(token)) {
                // Обработка унарного плюса или минуса
                if (expectOperand) {
                    if (token.equals("-")) {
                        // Превращаем унарный минус в число через ноль или специальный унарный оператор, 
                        // но проще добавить "0" перед ним для корректного вычисления (например, 0 - 5)
                        postfix.add("0");
                        stack.push("-");
                    } else if (token.equals("+")) {
                        // Унарный плюс просто пропускаем
                    } else {
                        throw new RuntimeException("Неверное положение оператора: " + token);
                    }
                } else {
                    while (!stack.isEmpty() && !stack.peek().equals("(") && priority(stack.peek().charAt(0)) >= priority(token.charAt(0))) {
                        postfix.add(stack.pop());
                    }
                    stack.push(token);
                    expectOperand = true;
                }
            }
        }

        while (!stack.isEmpty()) {
            String op = stack.pop();
            if (op.equals("(")) {
                throw new RuntimeException("Несбалансированные скобки");
            }
            postfix.add(op);
        }
        return postfix;
    }

    // Безопасный токенизатор, который корректно разбивает строку на числа и знаки
    private static List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (OPERATORS.indexOf(c) != -1 || c == '(' || c == ')') {
                if (sb.length() > 0) {
                    tokens.add(sb.toString());
                    sb.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else if (Character.isDigit(c) || c == '.') {
                sb.append(c);
            } else {
                throw new RuntimeException("Недопустимый символ в выражении: " + c);
            }
        }
        if (sb.length() > 0) {
            tokens.add(sb.toString());
        }
        return tokens;
    }

    private static double evaluatePostfix(List<String> postfix) {
        Deque<Double> stack = new ArrayDeque<>();
        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                if (stack.size() < 2) {
                    throw new RuntimeException("Неверное выражение");
                }
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
        switch (op) {
            case '*':
            case '/':
                return 2;
            case '+':
            case '-':
                return 1;
            default:
                return 0;
        }
    }

    private static String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            return String.format("%s", result);
        }
    }
}
