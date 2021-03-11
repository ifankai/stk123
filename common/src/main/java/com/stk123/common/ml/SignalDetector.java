package com.stk123.common.ml;

import java.text.DecimalFormat;
import java.util.*;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * 算法来源：
 * https://stackoverflow.com/questions/22583391/peak-signal-detection-in-realtime-timeseries-data/22640362#22640362
 */
public class SignalDetector {

    public HashMap<String, List> analyzeDataForSignals(List<Double> data, int lag, Double threshold, Double influence) {

        // init stats instance
        SummaryStatistics stats = new SummaryStatistics();

        // the results (peaks, 1 or -1) of our algorithm
        List<Integer> signals = new ArrayList<Integer>(Collections.nCopies(data.size(), 0));

        // filter out the signals (peaks) from our original list (using influence arg)
        List<Double> filteredData = new ArrayList<Double>(data);

        // the current average of the rolling window
        List<Double> avgFilter = new ArrayList<Double>(Collections.nCopies(data.size(), 0.0d));

        // the current standard deviation of the rolling window
        List<Double> stdFilter = new ArrayList<Double>(Collections.nCopies(data.size(), 0.0d));

        // init avgFilter and stdFilter
        for (int i = 0; i < lag; i++) {
            stats.addValue(data.get(i));
        }
        avgFilter.set(lag - 1, stats.getMean());
        stdFilter.set(lag - 1, Math.sqrt(stats.getPopulationVariance())); // getStandardDeviation() uses sample variance
        stats.clear();

        // loop input starting at end of rolling window
        for (int i = lag; i < data.size(); i++) {

            // if the distance between the current value and average is enough standard deviations (threshold) away
            if (Math.abs((data.get(i) - avgFilter.get(i - 1))) > threshold * stdFilter.get(i - 1)) {

                // this is a signal (i.e. peak), determine if it is a positive or negative signal
                if (data.get(i) > avgFilter.get(i - 1)) {
                    signals.set(i, 1);
                } else {
                    signals.set(i, -1);
                }

                // filter this signal out using influence
                filteredData.set(i, (influence * data.get(i)) + ((1 - influence) * filteredData.get(i - 1)));
            } else {
                // ensure this signal remains a zero
                signals.set(i, 0);
                // ensure this value is not filtered
                filteredData.set(i, data.get(i));
            }

            // update rolling average and deviation
            for (int j = i - lag; j < i; j++) {
                stats.addValue(filteredData.get(j));
            }
            avgFilter.set(i, stats.getMean());
            stdFilter.set(i, Math.sqrt(stats.getPopulationVariance()));
            stats.clear();
        }

        HashMap<String, List> returnMap = new HashMap<String, List>();
        returnMap.put("signals", signals);
        returnMap.put("filteredData", filteredData);
        returnMap.put("avgFilter", avgFilter);
        returnMap.put("stdFilter", stdFilter);

        return returnMap;

    }

    public static void main(String[] args) throws Exception {
        DecimalFormat df = new DecimalFormat("#0.000");

        ArrayList<Double> data = new ArrayList<Double>(Arrays.asList(1d, 1d, 1.1d, 1d, 0.9d, 1d, 1d, 1.1d, 1d, 0.9d, 1d,
                1.1d, 10d, 1d, 0.5d, 1d, 1d, 1.1d, 1d, 1d, 1d, 1d, 1.1d, 0.9d, 1d, 1.1d, 1d, 1d, 0.9d, 1d, 1.1d, 1d, 1d,
                1.1d, 1d, 0.8d, 0.9d, 1d, 1.2d, 0.9d, 1d, 1d, 1.1d, 1.2d, 1d, 1.5d, 1d, 3d, 2d, 5d, 3d, 2d, 1d, 1d, 1d,
                0.9d, 1d, 1d, 3d, 2.6d, 4d, 3d, 3.2d, 2d, 1d, 1d, 0.8d, 4d, 4d, 2d, 2.5d, 1d, 1d, 1d));

        SignalDetector signalDetector = new SignalDetector();
        int lag = 10;
        double threshold = 5;
        double influence = 0;

        HashMap<String, List> resultsMap = signalDetector.analyzeDataForSignals(data, lag, threshold, influence);
        // print algorithm params
        System.out.println("lag: " + lag + "\t\tthreshold: " + threshold + "\t\tinfluence: " + influence);

        System.out.println("Data size: " + data.size());
        System.out.println("Signals size: " + resultsMap.get("signals").size());

        // print data
        System.out.print("Data:\t\t");
        for (double d : data) {
            System.out.print(df.format(d) + "\t");
        }
        System.out.println();

        // print signals
        System.out.print("Signals:\t");
        List<Integer> signalsList = resultsMap.get("signals");
        for (int i : signalsList) {
            System.out.print(df.format(i) + "\t");
        }
        System.out.println();

        // print filtered data
        System.out.print("Filtered Data:\t");
        List<Double> filteredDataList = resultsMap.get("filteredData");
        for (double d : filteredDataList) {
            System.out.print(df.format(d) + "\t");
        }
        System.out.println();

        // print running average
        System.out.print("Avg Filter:\t");
        List<Double> avgFilterList = resultsMap.get("avgFilter");
        for (double d : avgFilterList) {
            System.out.print(df.format(d) + "\t");
        }
        System.out.println();

        // print running std
        System.out.print("Std filter:\t");
        List<Double> stdFilterList = resultsMap.get("stdFilter");
        for (double d : stdFilterList) {
            System.out.print(df.format(d) + "\t");
        }
        System.out.println();

        System.out.println();
        for (int i = 0; i < signalsList.size(); i++) {
            if (signalsList.get(i) != 0) {
                System.out.println("Point " + i + " gave signal " + signalsList.get(i));
            }
        }
    }

    /*
    Output:

    lag: 30     threshold: 5.0      influence: 0.0
    Data size: 74
    Signals size: 74
    Data:           1.000   1.000   1.100   1.000   0.900   1.000   1.000   1.100   1.000   0.900   1.000   1.100   1.000   1.000   0.900   1.000   1.000   1.100   1.000   1.000   1.000   1.000   1.100   0.900   1.000   1.100   1.000   1.000   0.900   1.000   1.100   1.000   1.000   1.100   1.000   0.800   0.900   1.000   1.200   0.900   1.000   1.000   1.100   1.200   1.000   1.500   1.000   3.000   2.000   5.000   3.000   2.000   1.000   1.000   1.000   0.900   1.000   1.000   3.000   2.600   4.000   3.000   3.200   2.000   1.000   1.000   0.800   4.000   4.000   2.000   2.500   1.000   1.000   1.000
    Signals:        0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   1.000   0.000   1.000   1.000   1.000   1.000   1.000   0.000   0.000   0.000   0.000   0.000   0.000   1.000   1.000   1.000   1.000   1.000   1.000   0.000   0.000   0.000   1.000   1.000   1.000   1.000   0.000   0.000   0.000
    Filtered Data:  1.000   1.000   1.100   1.000   0.900   1.000   1.000   1.100   1.000   0.900   1.000   1.100   1.000   1.000   0.900   1.000   1.000   1.100   1.000   1.000   1.000   1.000   1.100   0.900   1.000   1.100   1.000   1.000   0.900   1.000   1.100   1.000   1.000   1.100   1.000   0.800   0.900   1.000   1.200   0.900   1.000   1.000   1.100   1.200   1.000   1.000   1.000   1.000   1.000   1.000   1.000   1.000   1.000   1.000   1.000   0.900   1.000   1.000   1.000   1.000   1.000   1.000   1.000   1.000   1.000   1.000   0.800   0.800   0.800   0.800   0.800   1.000   1.000   1.000
    Avg Filter:     0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   1.003   1.003   1.007   1.007   1.003   1.007   1.010   1.003   1.000   0.997   1.003   1.003   1.003   1.000   1.003   1.010   1.013   1.013   1.013   1.010   1.010   1.010   1.010   1.010   1.007   1.010   1.010   1.003   1.003   1.003   1.007   1.007   1.003   1.003   1.003   1.000   1.000   1.007   1.003   0.997   0.983   0.980   0.973   0.973   0.970
    Std filter:     0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.000   0.060   0.060   0.063   0.063   0.060   0.063   0.060   0.071   0.073   0.071   0.080   0.080   0.080   0.077   0.080   0.087   0.085   0.085   0.085   0.083   0.083   0.083   0.083   0.083   0.081   0.079   0.079   0.080   0.080   0.080   0.077   0.077   0.075   0.075   0.075   0.073   0.073   0.063   0.071   0.080   0.078   0.083   0.089   0.089   0.086

    Point 45 gave signal 1
    Point 47 gave signal 1
    Point 48 gave signal 1
    Point 49 gave signal 1
    Point 50 gave signal 1
    Point 51 gave signal 1
    Point 58 gave signal 1
    Point 59 gave signal 1
    Point 60 gave signal 1
    Point 61 gave signal 1
    Point 62 gave signal 1
    Point 63 gave signal 1
    Point 67 gave signal 1
    Point 68 gave signal 1
    Point 69 gave signal 1
    Point 70 gave signal 1

    */

}
