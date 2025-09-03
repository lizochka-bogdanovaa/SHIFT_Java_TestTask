package ru.filter.service;

import ru.filter.config.Config;
import ru.filter.config.DataType;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FilterUtility {
    private boolean hasData = false;
    private static final Pattern INTEGER_REG_EXP = Pattern.compile("^-?\\d+$");
    private static final Pattern FLOAT_REG_EXP = Pattern.compile("^-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?$");

    private final Config config;
    private final Map<DataType, Statistics> statistics = new HashMap<>();
    private final Map<DataType, PrintWriter> writers = new HashMap<>();
    private final Map<DataType, Boolean> fileCreated = new HashMap<>();

    public FilterUtility(Config config) {
        this.config = config;
        for (DataType type : DataType.values()) {
            statistics.put(type, new Statistics());
            fileCreated.put(type, false);
        }
    }

    public void process() {

        try {
            processInputFiles();
            if (!hasData) {
                System.out.println("Фильтрация не произведена: входные файлы не содержат данных");
                return;
            }
            printStatistics();
        } catch (Exception e) {
            System.err.println("Ошибка при обработке: " + e.getMessage());
        } finally {
            closeWriters();
        }
    }

    private void processInputFiles() {
        boolean allFilesEmpty = true;

        for (String inputFile : config.getInputFiles()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                String line;
                boolean fileHasContent = false;

                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        processFileLine(line.trim());
                        fileHasContent = true;
                        hasData = true;
                    }
                }

                if (fileHasContent) {
                    allFilesEmpty = false;
                }

            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла " + inputFile + ": " + e.getMessage());
            }
        }

        if (allFilesEmpty) {
            hasData = false;
        }
    }

    private void processFileLine(String line) {
        if (line.isEmpty()) return;

        try {
            if (INTEGER_REG_EXP.matcher(line).matches()) {
                BigInteger bigInt = new BigInteger(line);
                statistics.get(DataType.INTEGER).addInteger(line);
                writeToFile(DataType.INTEGER, line);
            } else if (FLOAT_REG_EXP.matcher(line).matches()) {
                Double.parseDouble(line);
                statistics.get(DataType.FLOAT).addFloat(line);
                writeToFile(DataType.FLOAT, line);
            } else {
                statistics.get(DataType.STRING).addString(line);
                writeToFile(DataType.STRING, line);
            }
        } catch (NumberFormatException e) {
            statistics.get(DataType.STRING).addString(line);
            writeToFile(DataType.STRING, line);
        }
    }

    private void writeToFile(DataType type, String content) {
        try {
            // Создаем writer при первой же записи этого типа
            if (!writers.containsKey(type) && !fileCreated.get(type)) {
                createWriterForType(type);
                fileCreated.put(type, true);
            }

            PrintWriter writer = writers.get(type);
            if (writer != null) {
                writer.println(content);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл для типа " + type + ": " + e.getMessage());
        }
    }

    private void createWriterForType(DataType type) throws IOException {
        String filename = config.getOutputFilesNamePrefix() +
                (type == DataType.INTEGER ? "integers.txt" :
                        type == DataType.FLOAT ? "floats.txt" : "strings.txt");

        File outputFile = new File(config.getOutputPath(), filename);
        outputFile.getParentFile().mkdirs();

        boolean fileExists = outputFile.exists();

        if (statistics.get(type).getCount() > 0) {
            if (config.isAppendToOutputFilesMode()) {
                if (fileExists) {
                    System.out.println("Режим дозаписи: данные будут добавлены в существующий файл " + filename);
                } else {
                    System.out.println("Предупреждение: файл " + filename + " не существует, создается новый (несмотря на режим дозаписи)");
                }
            }

            writers.put(type, new PrintWriter(
                    new FileWriter(outputFile, config.isAppendToOutputFilesMode()), true));

        } else if (config.isAppendToOutputFilesMode() && fileExists) {
            System.out.println("Режим дозаписи: файл " + filename + " существует, но данных для записи нет");
            writers.put(type, null);
        } else {
            writers.put(type, null);
        }
    }

    private PrintWriter getWriter(DataType type) {
        return writers.get(type);
    }

    private void printStatistics() {
        if (!hasData || (!config.isShortStatisticsMode() && !config.isFullStatisticsMode())) {
            return;
        }

        System.out.println("--- СТАТИСТИКА по файлам ---");

        for (DataType type : DataType.values()) {
            Statistics stats = statistics.get(type);
            if (stats.getCount() > 0) {
                System.out.println("\n" + getTypeName(type) + ":");
                System.out.println("  Количество: " + stats.getCount());

                if (config.isFullStatisticsMode()) {
                    if (type == DataType.INTEGER) {
                        System.out.println("  Минимальное значение: " + stats.getMin());
                        System.out.println("  Максимальное значение: " + stats.getMaxValue());
                        System.out.println("  Сумма элементов: " + stats.getSumInt());
                        System.out.println("  Среднее значение: " + stats.getAverage());
                    } else if (type == DataType.FLOAT) {
                        System.out.println("  Минимальное значение: " + stats.getMin());
                        System.out.println("  Максимальное значение: " + stats.getMaxValue());
                        System.out.println("  Сумма элементов: " + stats.getSumFloat());
                        System.out.println("  Среднее значение: " + stats.getAverage());
                    } else if (type == DataType.STRING) {
                        System.out.println("  Минимальная длина строки: " + stats.getMinStringLength());
                        System.out.println("  Максимальная длина строки: " + stats.getMaxStringLength());
                    }
                }
            }
        }
    }

    private String getTypeName(DataType type) {
        return switch (type) {
            case INTEGER -> "Целые числа";
            case FLOAT -> "Вещественные числа";
            case STRING -> "Строки";
        };
    }

    private void closeWriters() {
        for (PrintWriter writer : writers.values()) {
            if (writer != null) {
                writer.close();
            }
        }
    }
}