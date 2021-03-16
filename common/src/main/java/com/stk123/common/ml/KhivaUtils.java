package com.stk123.common.ml;

import io.shapelets.khiva.Array;
import io.shapelets.khiva.Library;
import io.shapelets.khiva.Matrix;

import java.util.Arrays;
import java.util.Comparator;

public class KhivaUtils {

    public static void main(String[] args) {
        //Library.setKhivaBackend(Library.Backend.KHIVA_BACKEND_CPU);

        //testMass();
        testMassMultiple();
    }

    public static void testMassMultiple() {
        double[] tss = {10, 10, 10, 11, 12, 11, 10, 10, 11, 12, 11, 14, 10, 12, 10, 10};
        long[] dimsTss = {4, 4, 1, 1};

        double[] query = {100, 110, 100, 100};
        long[] dimsQuery = {4, 1, 1, 1};

        try (Array t = Array.fromPrimitiveArray(tss, dimsTss); Array q = Array.fromPrimitiveArray(query, dimsQuery)) {

            double[] expectedDistance = {1.8388, 0.8739, 1.5307, 3.6955, 3.2660, 3.4897, 2.8284, 1.2116, 1.5307, 2.1758,
                    2.5783, 3.7550, 2.8284, 2.8284, 3.2159, 0.5020};
            Array result = Matrix.mass(q, t);
            double[] distances = result.getData();

            System.out.println(Arrays.toString(distances));
            System.out.println(getIndexOfMin(distances));

            result.close();
        }

    }

    public static void testMass(){
        double[] tss = {10, 10, 10, 11, 12, 11, 10, 10, 11, 12, 11, 14, 10, 10};
        long[] dimsTss = {14, 1, 1, 1};

        double[] query = {4, 3, 8};
        long[] dimsQuery = {3, 1, 1, 1};

        try (Array t = Array.fromPrimitiveArray(tss, dimsTss); Array q = Array.fromPrimitiveArray(query, dimsQuery)) {

            double[] expectedDistance = {1.732051, 0.328954, 1.210135, 3.150851, 3.245858, 2.822044, 0.328954, 1.210135,
                    3.150851, 0.248097, 3.30187, 2.82205};
            Array result = Matrix.mass(q, t);
            double[] distances = result.getData();

            System.out.println(Arrays.toString(distances));
            System.out.println(getIndexOfMin(distances));

            result.close();
        }
    }

    public static double[] mass(double[] tss, double[] query){
        long[] dimsTss = {tss.length, 1, 1, 1};
        long[] dimsQuery = {query.length, 1, 1, 1};

        try (
                Array t = Array.fromPrimitiveArray(tss, dimsTss);
                Array q = Array.fromPrimitiveArray(query, dimsQuery)
        ) {

            Array result = Matrix.mass(q, t);
            double[] distances = result.getData();

            result.close();
            return distances;
        }
    }

    public static int getIndexOfMin(double[] array) {
        int minAt = 0;
        for (int i = 0; i < array.length; i++) {
            minAt = array[i] < array[minAt] ? i : minAt;
        }
        return minAt;
    }

    public static int[] getIndexesOfMin(double[] array, int num) {
        //create sort able array with index and value pair
        IndexValuePair[] pairs = new IndexValuePair[array.length];
        for (int i = 0; i < array.length; i++) {
            pairs[i] = new KhivaUtils.IndexValuePair(i, array[i]);
        }

        //sort
        Arrays.sort(pairs, Comparator.comparingDouble(o -> o.value));

        //extract the indices
        int[] result = new int[num];
        for (int i = 0; i < num; i++) {
            result[i] = pairs[i].index;
        }
        return result;
    }

    static class IndexValuePair {
        private int index;
        private double value;

        public IndexValuePair(int index, double value) {
            this.index = index;
            this.value = value;
        }
    }
}
