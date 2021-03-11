/*
 * Copyright (c) 2019 Shapelets.io
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.shapelets.khiva;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NormalizationTest {
    private static final double DELTA = 1e-6;

    @BeforeClass
    public static void setUp() throws Exception {
        Library.setKhivaBackend(Library.Backend.KHIVA_BACKEND_CPU);
    }

    @Test
    public void testZnorm() throws Exception {
        double[] tss = {0, 1, 2, 3, 4, 5, 6, 7};
        long[] dims = {4, 2, 1, 1};
        try (Array a = Array.fromPrimitiveArray(tss, dims); Array b = Normalization.znorm(a)) {
            double[] result = b.getData();
            double[] expected = {-1.341640786499870, -0.447213595499958, 0.447213595499958, 1.341640786499870};
            for (int i = 0; i < expected.length; i++) {
                assertEquals(expected[i], result[i], DELTA);
                assertEquals(expected[i], result[i + 4], DELTA);
            }
        }
    }

    @Test
    public void testZnormInPlace() throws Exception {
        double[] tss = {0, 1, 2, 3, 4, 5, 6, 7};
        long[] dims = {4, 2, 1, 1};
        try (Array a = Array.fromPrimitiveArray(tss, dims)) {
            Normalization.znormInPlace(a);
            double[] result = a.getData();
            assertEquals(-1.341640786499870, result[0], DELTA);
            assertEquals(-0.447213595499958, result[1], DELTA);
            assertEquals(0.447213595499958, result[2], DELTA);
            assertEquals(1.341640786499870, result[3], DELTA);

            assertEquals(-1.341640786499870, result[4], DELTA);
            assertEquals(-0.447213595499958, result[5], DELTA);
            assertEquals(0.447213595499958, result[6], DELTA);
            assertEquals(1.341640786499870, result[7], DELTA);
        }
    }

    @Test
    public void testMaxMinNorm() throws Exception {
        double[] tss = {0, 1, 2, 3, 4, 5, 6, 7};
        long[] dims = {4, 2, 1, 1};
        try (Array a = Array.fromPrimitiveArray(tss, dims); Array b = Normalization.maxMinNorm(a, 2.0, 1.0)) {
            double[] result = b.getData();
            double[] expected = {1.0, 1.3333333333333, 1.66666667, 2.0, 1.0, 1.3333333333333, 1.66666667, 2.0};
            assertArrayEquals(expected, result, DELTA);
        }
    }

    @Test
    public void testMaxMinNormInPlace() throws Exception {
        double[] tss = {0, 1, 2, 3, 4, 5, 6, 7};
        long[] dims = {4, 2, 1, 1};
        try (Array a = Array.fromPrimitiveArray(tss, dims)) {
            Normalization.maxMinNormInPlace(a, 2.0, 1.0);
            double[] result = a.getData();
            double[] expected = {1.0, 1.3333333333333, 1.66666667, 2.0, 1.0, 1.3333333333333, 1.66666667, 2.0};
            assertArrayEquals(expected, result, DELTA);
        }
    }

    @Test
    public void decimalScalingNorm() throws Exception {
        double[] tss = {0, 1, -2, 3, 40, 50, 60, -70};
        long[] dims = {4, 2, 1, 1};
        try (Array a = Array.fromPrimitiveArray(tss, dims); Array b = Normalization.decimalScalingNorm(a)) {
            double[] result = b.getData();
            double[] expected = {0.0, 0.1, -0.2, 0.3, 0.4, 0.5, 0.6, -0.7};
            assertArrayEquals(expected, result, DELTA);
        }
    }

    @Test
    public void decimalScalingNormInPlace() throws Exception {
        double[] tss = {0, 1, -2, 3, 40, 50, 60, -70};
        long[] dims = {4, 2, 1, 1};
        try (Array a = Array.fromPrimitiveArray(tss, dims)) {
            Normalization.decimalScalingNormInPlace(a);
            double[] result = a.getData();
            double[] expected = {0.0, 0.1, -0.2, 0.3, 0.4, 0.5, 0.6, -0.7};
            assertArrayEquals(expected, result, DELTA);
        }
    }

    @Test
    public void testMeanNorm() throws Exception {
        float[] tss = {0, 1, 2, 3, 4, 5, 6, 7};
        long[] dims = {4, 2, 1, 1};
        try (Array a = Array.fromPrimitiveArray(tss, dims); Array b = Normalization.meanNorm(a)) {
            float[] result = b.getData();
            float[] expectedResult = {-0.5f, -0.166666667f, 0.166666667f, 0.5f, -0.5f, -0.166666667f, 0.166666667f,
                                      0.5f};
            assertEquals(expectedResult.length, result.length, DELTA);
            for (int i = 0; i < result.length; i++) {
                assertEquals(expectedResult[i], result[i], DELTA);
            }
        }
    }

    @Test
    public void testMeanNormInPlace() throws Exception {
        float[] tss = {0, 1, 2, 3, 4, 5, 6, 7};
        long[] dims = {4, 2, 1, 1};
        try (Array a = Array.fromPrimitiveArray(tss, dims)) {
            Normalization.meanNormInPlace(a);
            float[] result = a.getData();
            float[] expectedResult = {-0.5f, -0.166666667f, 0.166666667f, 0.5f, -0.5f, -0.166666667f, 0.166666667f,
                                      0.5f};
            assertEquals(expectedResult.length, result.length, DELTA);
            for (int i = 0; i < result.length; i++) {
                assertEquals(expectedResult[i], result[i], DELTA);
            }
        }
    }
}
