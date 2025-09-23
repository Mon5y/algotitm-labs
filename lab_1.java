import java.util.*;

public class Main {
    public static boolean checkBrackets(String input) {
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            switch (c) {
                case '{':
                case '[':
                case '(':
                    stack.push(c);
                    break;
                case ')':
                    if (stack.isEmpty() || stack.peek() != '(') {
                        System.out.println("Результат: Ошибка");
                        return false;
                    } else {
                        stack.pop();
                    }
                    break;
                case ']':
                    if (stack.isEmpty() || stack.peek() != '[') {
                        System.out.println("Результат: Ошибка");
                        return false;
                    } else {
                        stack.pop();
                    }
                    break;
                case '}':
                    if (stack.isEmpty() || stack.peek() != '{') {
                        System.out.println("Результат: Ошибка");
                        System.out.println("Причина:" + stack.peek() + " Скобка закрыта");
                        return false;
                    } else {
                        stack.pop();
                    }
                    break;
                default:
                    System.out.println("Результат: Ошибка");
                    System.out.println("Причина: Неизвестный символ");
                    return false;
            }
        }
        
        if (stack.isEmpty()) {
            System.out.println("Результат: Правильно");
            return true;
        } else {
            System.out.println("Результат: false");
            System.out.println("Причина: " + stack.peek() + " скобка не закрыта");
            return false;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Пожалуйста, введите скобки: ");
        String input = scanner.nextLine();
        checkBrackets(input);
        scanner.close();
    }
}