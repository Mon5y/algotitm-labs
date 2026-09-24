import java.util.*;

public class num_around {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите x: ");
        int x = scanner.nextInt();

        long power3 = 1; // 3^K
        while (power3 <= x) {
            long power5 = 1; // 5^L (сброс для каждого K)
            while (power3 * power5 <= x) {
                long power7 = 1; // 7^M (сброс для каждого L)
                while (power3 * power5 * power7 <= x) {
                    long currentNum = power3 * power5 * power7;
                    System.out.println(currentNum);
                    power7 *= 7;
                }
                power5 *= 5;
            }
            power3 *= 3;
        }
    }
}
