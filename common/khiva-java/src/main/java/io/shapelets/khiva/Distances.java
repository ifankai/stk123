/*
 * Copyright (c) 2019 Shapelets.io
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.shapelets.khiva;

/**
 * Khiva Distances class containing distances methods.
 */
public class Distances extends Library {

    private native static long euclidean(long ref);

    private native static long dtw(long ref);

    private native static long hamming(long ref);

    private native static long manhattan(long ref);

    private native static long sbd(long ref);

    private native static long squaredEuclidean(long ref);


    /**
     * Calculates euclidean distances between time series.
     *
     * @param tss Array containing the input time series.
     * @return Array with an upper triangular matrix where each position corresponds to the distance between two
     * time series. Diagonal elements will be zero. For example: Position row 0 column 1 records the distance
     * between time series 0 and time series 1.
     * @throws KhivaException If the native function call fails.
     */
    public static Array euclidean(Array tss) {
        long ref = euclidean(tss.getReference());
        return Array.fromNative(ref);
    }

    /**
     * Calculates the Dynamic Time Warping Distance.
     *
     * @param tss Expects an input array whose dimension zero is the length of the time series (all the same) and
     *            dimension one indicates the number of time series.
     * @return Array with an upper triangular matrix where each position corresponds to the distance between
     * two time series. Diagonal elements will be zero. For example: Position row 0 column 1 records the
     * distance between time series 0 and time series 1.
     * @throws KhivaException If the native function call fails.
     */
    public static Array dtw(Array tss) {
        long ref = dtw(tss.getReference());
        return Array.fromNative(ref);
    }

    /**
     * Calculates Hamming distances between time series.
     *
     * @param tss Expects an input array whose dimension zero is the length of the time series (all the same) and
     *            dimension one indicates the number of time series.
     * @return Array with an upper triangular matrix where each position corresponds to the
     * distance between two time series. Diagonal elements will be zero. For example: Position row 0 column 1 records
     * the
     * distance between time series 0 and time series 1.
     * @throws KhivaException If the native function call fails.
     */
    public static Array hamming(Array tss) {
        long ref = hamming(tss.getReference());
        return Array.fromNative(ref);
    }

    /**
     * Calculates the Shape-Based distance (SBD). It computes the normalized cross-correlation and it returns 1.0
     * minus the value that maximizes the correlation value between each pair of time series.
     *
     * @param tss Expects an input array whose dimension zero is the length of the time series (all the same) and
     *            dimension one indicates the number of time series.
     * @return Array with an upper triangular matrix where each position corresponds to the distance between two
     * time series. Diagonal elements will be zero. For example: Position row 0 column 1 records the distance between
     * time
     * series 0 and time series 1.
     * @throws KhivaException If the native function call fails.
     */
    public static Array sbd(Array tss) {
        long ref = sbd(tss.getReference());
        return Array.fromNative(ref);
    }

    /**
     * Calculates Manhattan distances between time series.
     *
     * @param tss Expects an input array whose dimension zero is the length of the time series (all the same) and
     *            dimension one indicates the number of time series.
     * @return Array with an upper triangular matrix where each position corresponds to the distance between two
     * time series. Diagonal elements will be zero. For example: Position row 0 column 1 records the distance between
     * time
     * series 0 and time series 1.
     * @throws KhivaException If the native function call fails.
     */
    public static Array manhattan(Array tss) {
        long ref = manhattan(tss.getReference());
        return Array.fromNative(ref);
    }

    /**
     * Calculates the non squared version of the euclidean distance.
     *
     * @param tss Array containing the input time series.
     * @return Array with an upper triangular matrix where each position corresponds to the distance between two
     * time series. Diagonal elements will be zero. For example: Position row 0 column 1 records the distance
     * between time series 0 and time series 1.
     * @throws KhivaException If the native function call fails.
     */
    public static Array squaredEuclidean(Array tss) {
        long ref = squaredEuclidean(tss.getReference());
        return Array.fromNative(ref);
    }
}
