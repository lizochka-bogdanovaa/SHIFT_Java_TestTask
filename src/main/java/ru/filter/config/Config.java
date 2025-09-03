package ru.filter.config;

public class Config {
    private String outputPath = ".";
    private String outputFilesNamePrefix = "";
    private boolean appendToOutputFilesMode = false;
    private boolean shortStatisticsMode = false;
    private boolean fullStatisticsMode = false;
    private String[] inputFiles;

    public String getOutputPath() { return outputPath; }
    public void setOutputPath(String outputPath) { this.outputPath = outputPath; }

    public String getOutputFilesNamePrefix() { return outputFilesNamePrefix; }
    public void setOutputFilesNamePrefix(String outputFilesNamePrefix) {
        this.outputFilesNamePrefix = outputFilesNamePrefix;
    }

    public boolean isAppendToOutputFilesMode() { return appendToOutputFilesMode; }
    public void setAppendToOutputFilesMode(boolean appendToOutputFilesMode) { this.appendToOutputFilesMode = appendToOutputFilesMode; }

    public boolean isShortStatisticsMode() { return shortStatisticsMode; }
    public void setShortStatisticsMode(boolean shortStatisticsMode) { this.shortStatisticsMode = shortStatisticsMode; }

    public boolean isFullStatisticsMode() { return fullStatisticsMode; }
    public void setFullStatisticsMode(boolean fullStatisticsMode) { this.fullStatisticsMode = fullStatisticsMode; }

    public String[] getInputFiles() { return inputFiles; }
    public void setInputFiles(String[] inputFiles) { this.inputFiles = inputFiles; }
}