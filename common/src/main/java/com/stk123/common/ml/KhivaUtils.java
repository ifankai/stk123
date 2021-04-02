package com.stk123.common.ml;

import io.shapelets.khiva.Array;
import io.shapelets.khiva.Library;
import io.shapelets.khiva.Matrix;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * https://khiva.readthedocs.io/en/latest/
 */
public class KhivaUtils {

    public static void main(String[] args) throws Exception {
        //Library.setKhivaBackend(Library.Backend.KHIVA_BACKEND_CPU);

        //testMass();
        //testMassMultiple();
        testFindBestNOccurrences();
    }

    public static void testMassMultiple() {
        double[] tss = {10, 10, 10, 11, 12, 11, 10, 10, 11, 12, 11, 14, 10, 12, 10, 10};
        long[] dimsTss = {4, 4, 1, 1};

        double[] query = {100, 110, 100, 100};
        long[] dimsQuery = {4, 1, 1, 1};

        try (Array t = Array.fromPrimitiveArray(tss, dimsTss); Array q = Array.fromPrimitiveArray(query, dimsQuery)) {

            double[] expectedDistance = {3.265986, 2.570482, 2.828427, 0.0};
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

    public static double[] mass(List<double[]> tsss, double[] query){
        long[] dimsTss = {query.length, tsss.size(), 1, 1};
        long[] dimsQuery = {query.length, 1, 1, 1};

        double[] tss = new double[]{};
        tsss.forEach(e -> ArrayUtils.addAll(tss, e));

        try (
                Array t = Array.fromPrimitiveArray(tss, dimsTss);
                Array q = Array.fromPrimitiveArray(query, dimsQuery)
        ) {
            Array[] result = Matrix.findBestNOccurrences(q, t, 1);
            double[] distances = result[0].getData();

//            System.out.println(Arrays.toString(distances));
//            System.out.println(Arrays.toString(getIndexesOfMin(distances, 3)));

            result[0].close();
            result[1].close();
            return distances;
        }
    }

    public static void testFindBestNOccurrences() throws Exception {
        double[] tss = {10, 10, 11, 11, 12, 11, 10, 10, 11, 12, 11, 10, 10, 11, 10, 10, 11, 11, 12, 11, 10, 10, 11, 12,
                11, 10, 10};
        long[] dimsTss = {3, 9, 1, 1};

        double[] query = {10, 11, 12};
        long[] dimsQuery = {3, 1, 1, 1};

        try (Array t = Array.fromPrimitiveArray(tss, dimsTss); Array q = Array.fromPrimitiveArray(query, dimsQuery)) {
            Array[] result = Matrix.findBestNOccurrences(q, t, 1);
            double[] distances = result[0].getData();
            int[] indexes = result[1].getData();

            System.out.println(Arrays.toString(distances));
            System.out.println(Arrays.toString(getIndexesOfMin(distances, 3)));

            result[0].close();
            result[1].close();
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
        int length = num>=array.length?array.length:num;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
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
