package com.lab2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    static int attachments = 0; // считает количество скобок при вложенности
    static boolean flagAttachment = false; // появляется флаг при вложенности

    public static void main(String[] args) throws FileNotFoundException {

        Scanner scan = new Scanner(new File("code.txt"));
        int CL = 0; // количество условных операторов
        int CLI = 0; // количество вложенности
        int operators = 0; // количество операторов (все, кроме ключевых слов)
        boolean isDefault = false; // флаг при нахождении в default

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
//			строка не должна содержать любой из операторов, скобки, импорты
            boolean isImportantSign = line.contains("case") || line.contains("default") || line.contains("if")
                    || line.contains("else") || line.contains("for") || line.contains("switch") || line.contains("while") || line.contains("do");
            if (!(line.contains("package") || line.contains("import") || line.contains("class") || line.contains("public")
                    || (line.contains("{") && !isImportantSign) || (line.contains("}") && !isImportantSign) || line.matches("[ ]*") || line.matches("[	]*"))) {
                operators++;
            }

            if (line.matches(".*case.*")) {
                if (flagAttachment) {
                    CL++;
                    CLI++;
                } else {
                    CL++;
                }
                flagAttachment = true;
                attachments++;
            }

//			если строка содержит default и флаг на вложенность, то +1 к количеству условных операторов и вложенности,
//			если нет флага на вложенность, то +1 к количеству условных операторов
//			активируется флаг на default и если он вложен, тогда +1 к вложенности, если нет, тогда флаг на вложенность default снимается
            if (line.matches(".*default.*")) {
                isDefault = true;
                if (flagAttachment) {
                    CL++;
                    attachments--;
                } else {
                    attachments = 0;
                    isDefault = false;
                    CL++;
                }
            }

//			при попадании в break вне default и если есть вложенность, тогда вложенность уменьшается на 1
            if (line.matches(".*break.*") && !isDefault) {
                if (flagAttachment) {
                    attachments--;
                }
            }

//			при попадании в break в default вложенность увеличивается на 1 и вложенность default снимается
            if (line.matches(".*break.*") && isDefault) {
                isDefault = false;
                attachments++;
                check(line, isDefault);
            }

//			если строка содержит for или while флаг на вложенность, то +1 к количеству условных операторов и вложенности,
//			если нет флага на вложенность, то +1 к количеству условных операторов
//			появляется вложенность, то есть меняется флаг и +1 к вложенности
            if (line.matches(".*for.*") || line.matches(".*while.*")) {
                if (flagAttachment) {
                    CL++;
                    CLI++;
                } else {
                    CL++;
                }
                flagAttachment = true;
                attachments++;
            }

//			при попадании в if или else флаг на вложенность, то +1 к количеству условных операторов и вложенности,
//			если нет флага на вложенность, то +1 к количеству условных операторов
//			активируется флаг на вложенность
            if (line.matches(".*if[ ]*[(].*") || line.matches(".*else.*")) {
                check(line, isDefault);
                if (flagAttachment) {
                    CL++;
                    CLI++;
                } else {
                    CL++;
                }
                flagAttachment = true;
            }

//			при наличии скобок в строке запускается проверка на вложенность
            if (line.matches(".*[{]+.*") || line.matches(".*[}]+.*")) {
                flagAttachment = true;
                check(line, isDefault);
            }
        }

//		вывод результатов
        System.out.println("CL = " + CL);
        System.out.println("cl = " + Math.round(((double) CL / operators) * 100) / 100.0);
        System.out.println("CLI = " + CLI);
    }

    //	проверяет вложенность
//	если строка содержит скобки, то либо +1, либо -1 к вложенности
//	также отменяет вложенность
    public static void check(String line, boolean isDefault) {
//		добавляет вложенность, если она активированна
        if (line.matches(".*[{].*")) {
            if (flagAttachment) {
                attachments++;
            }
        }

//		уменьшает вложенность, если она активированна
        if (line.matches(".*[}].*")) {
            if (flagAttachment) {
                attachments--;
            }
        }

//		если вложенность равна 0 и код не содрежит case и не находится в default, тогда вложенность отменяется
        if (attachments == 0 && !line.matches(".*case.*") && !isDefault) {
            flagAttachment = false;
        }

//		если вложенность равна 0 и код  содрежит default, тогда вложенность отменяется
        if (attachments == 0 && line.matches(".*default.*")) {
            flagAttachment = false;
        }
    }

}