import top.yzzblog.compiler.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner("./test.sy");
        char ch = scanner.scan();
        while (ch != '$') {
            System.out.print(ch);
            ch = scanner.scan();
        }
    }
}
