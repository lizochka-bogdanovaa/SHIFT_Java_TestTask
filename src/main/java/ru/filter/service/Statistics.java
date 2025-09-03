package ru.filter.service;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Statistics {
    private long count = 0;
    private Number minValue = null;
    private Number maxValue = null;
    private BigInteger sumInt = BigInteger.ZERO;
    private double sumFloat = 0.0;
    private int minStringLength = Integer.MAX_VALUE;
    private int maxStringLength = 0;

    public void addInteger(String value) {
        BigInteger bigInt = new BigInteger(value);
        count++;
        sumInt = sumInt.add(bigInt);

        if (minValue == null || bigInt.compareTo(new BigInteger(minValue.toString())) < 0) {
            minValue = bigInt;
        }
        if (maxValue == null || bigInt.compareTo(new BigInteger(maxValue.toString())) > 0) {
            maxValue = bigInt;
        }
    }

    public void addFloat(String value) {
        double num = Double.parseDouble(value);
        count++;
        sumFloat += num;

        if (minValue == null || num < minValue.doubleValue()) {
            minValue = num;
        }
        if (maxValue == null || num > maxValue.doubleValue()) {
            maxValue = num;
        }
    }

    public void addString(String value) {
        count++;
        int length = value.length();
        minStringLength = Math.min(minStringLength, length);
        maxStringLength = Math.max(maxStringLength, length);
    }

    public long getCount() { return count; }
    public Number getMin() { return minValue; }
    public Number getMaxValue() { return maxValue; }
    public BigInteger getSumInt() { return sumInt; }
    public double getSumFloat() { return sumFloat; }
    public double getAverage() {
        if (count > 0) {
            double average;
            if (sumInt.equals(BigInteger.ZERO)) {
                average = sumFloat / count;
            } else {
                average = sumInt.doubleValue() / count;
            }
            return roundAverage(average);
        } else {
            return 0;
        }
    }

    private double roundAverage(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public int getMinStringLength() { return minStringLength == Integer.MAX_VALUE ? 0 : minStringLength; }
    public int getMaxStringLength() { return maxStringLength; }
}