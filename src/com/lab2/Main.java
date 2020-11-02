package com.lab2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    static int CL = 0; // количество условных операторов
    static int CLI = 0; // количество условных операторов
    static int deep = 0; // количество вложенности
    static int operators = 0; // количество операторов (все, кроме ключевых слов)
    static int brackets = 0;
    static boolean isNesting = false;
    static LinkedList<Node> switchPosition = new LinkedList<>();

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(new File("code.txt"));
        countValues(scan);

        System.out.println("CL = " + CL);
        System.out.println("cl = " + Math.round(((double) CL / operators) * 100) / 100.0);
        System.out.println("CLI = " + CLI);
    }

    private static void countValues(Scanner scan){
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            boolean isImportantSign = line.contains("case") || line.contains("default") || line.contains("if")
                    || line.contains("else") || line.contains("for") || line.contains("switch") || line.contains("while") || line.contains("do");
            if (!(line.contains("package") || line.contains("import") || line.contains("class") || line.contains("public")
                    || (line.contains("{") && !isImportantSign) || (line.contains("}") && !isImportantSign) || line.matches("[ ]*") || line.matches("[	]*"))) {
                operators++;
            }
            checkLine(line, scan);
        }
    }

    public static void checkLine(String line, Scanner scanner) {
        if (line.contains("switch")){
            for (int i = 0; i < switchPosition.size(); i++) {
                if (switchPosition.get(i).switchPosition == brackets){
                    deep = switchPosition.get(i).deep;
                    if (isNesting)
                        deep++;
                    switchPosition.remove(i);
                    break;
                }
            }
            switchPosition.add(new Node(brackets, deep));
        }

        if (line.contains("case")) {
            countParameters();
            for (int i = 0; i < switchPosition.size(); i++) {
                if (switchPosition.get(i).switchPosition == brackets){
                    deep = switchPosition.get(i).deep;
                    if (isNesting)
                        deep++;
                    switchPosition.remove(i);
                    break;
                }
            }
            switchPosition.add(new Node(brackets, deep));
        }

        if (line.contains("default")) {
            countParameters();
            for (int i = 0; i < switchPosition.size(); i++) {
                if (switchPosition.get(i).switchPosition == brackets){
                    deep = switchPosition.get(i).deep;
                    switchPosition.remove(i);
                    break;
                }
            }
        }

        if (line.contains("if") && line.contains("{")) {
            countParameters();
        }

        if (line.contains("else") && line.contains("{")) {
            isNesting = true;
        }

        if (line.contains("if") && !line.contains("{")) {
            countParameters();
            checkNext(scanner);
        }

        if (line.contains("for") && line.contains("{")) {
            countParameters();
        }

        if (line.contains("for") && !line.contains("{")) {
            countParameters();
            checkNext(scanner);
        }

        if (line.contains("while") && line.contains("{")) {
            countParameters();
        }

        if (line.contains("while") && !line.contains("{") && line.trim().charAt(line.length() - 1) != ';') {
            countParameters();
            checkNext(scanner);
        }

        if (line.contains("while") && !line.contains("{") && line.trim().charAt(line.length() - 1) == ';') {
            CL++;
            if (isNesting) {
                deep++;
            }
        }

        if (line.contains("else") && !line.contains("{")) {
            isNesting = true;
            checkNext(scanner);
        }

        if (line.contains("else if"))
            deep++;


        if (line.contains("{")) {
            brackets++;
        }

        if (line.contains("}")) {
            brackets--;
            CLI = Math.max(CLI, deep);
            deep--;
            for (int i = 0; i < switchPosition.size(); i++) {
                if (switchPosition.get(i).switchPosition == brackets){
                    deep = switchPosition.get(i).deep;
                    switchPosition.remove(i);
                    break;
                }
            }
        }

        if (brackets == 0) {
            isNesting = false;
        }
    }

    private static void countParameters(){
        CL++;
        if (isNesting) {
            deep++;
        }
        isNesting = true;
    }

    private static void checkNext(Scanner scanner) {
        String nextLine;
        if (scanner.hasNextLine()) {
            nextLine = scanner.nextLine();
            checkLine(nextLine, scanner);
            if (brackets == 0) {
                isNesting = false;
            }
        }
        CLI = Math.max(CLI, deep);
        deep--;
    }

    static class Node {
        int switchPosition;
        int deep;

        Node(int switchPosition, int deep){
            this.switchPosition = switchPosition;
            this.deep = deep;
        }
    }
}

