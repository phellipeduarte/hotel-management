package utils;

import java.util.Scanner;

public class InputOutputHandler {

    static Scanner scanner = new Scanner(System.in);

    private InputOutputHandler(){}

    public static String waitStringAnswer(String question) {
        System.out.print("\n" + question + "\n");
        return scanner.next();
    }

    public static Integer waitIntegerAnswer(String question) {
        System.out.print("\n" + question + "\n");
        return scanner.nextInt();
    }
}
