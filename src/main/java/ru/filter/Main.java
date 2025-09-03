package ru.filter;


import ru.filter.config.Config;
import ru.filter.service.FilterUtility;

public class Main {
    public static void main(String[] args) {
        try {
            Config config = parseMainArguments(args);
            if (config != null) {
                FilterUtility utility = new FilterUtility(config);
                utility.process();
            }
        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            printMenu();
        }
    }

    private static Config parseMainArguments(String[] args) {
        if (args.length == 0) {
            printMenu();
            return null;
        }

        Config config = new Config();
        int i = 0;

        while (i < args.length && args[i].startsWith("-")) {
            switch (args[i]) {
                case "-o":
                    if (i + 1 < args.length) {
                        config.setOutputPath(args[++i]);
                    } else {
                        System.err.println("Ошибка: опция -o требует задание пути");
                        return null;
                    }
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        config.setOutputFilesNamePrefix(args[++i]);
                    } else {
                        System.err.println("Ошибка: опция -p требует задания префикса");
                        return null;
                    }
                    break;
                case "-a":
                    config.setAppendToOutputFilesMode(true);
                    break;
                case "-s":
                    config.setShortStatisticsMode(true);
                    break;
                case "-f":
                    config.setFullStatisticsMode(true);
                    break;
                default:
                    System.err.println("Неизвестная опция: " + args[i]);
                    return null;
            }
            i++;
        }

        // Оставшиеся аргументы - входные файлы
        int fileCount = args.length - i;
        if (fileCount == 0) {
            System.err.println("Ошибка: не указаны входные файлы");
            return null;
        }

        String[] inputFiles = new String[fileCount];
        System.arraycopy(args, i, inputFiles, 0, fileCount);
        config.setInputFiles(inputFiles);

        return config;
    }

    private static void printMenu() {
        System.out.println("Пример запуска утилиты для фильтрации: java -jar util.jar [опции] file1.txt file2.txt ...");
        System.out.println("Опции:");
        System.out.println("  -o <path>    Путь для выходных файлов (по умолчанию: текущая папка проекта)");
        System.out.println("  -p <prefix>  Префикс имен выходных файлов");
        System.out.println("  -a           Режим добавления (дозаписи) в существующие файлы");
        System.out.println("  -s           Краткая статистика (количество элементов в файлах)");
        System.out.println("  -f           Полная статистика (для чисел - мин и макс значения, сумма, среднее, для строк - размер самой короткой и длинной строки)");
    }
}