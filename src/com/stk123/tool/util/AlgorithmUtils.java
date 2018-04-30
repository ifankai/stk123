package com.stk123.tool.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AlgorithmUtils {

	public static void main(String[] args) throws Exception {
		int[] a = { 0, 1, 2, 3, 8 }; // 整数数组
		int m = 2; // 待取出组合的个数
		//Test zuhe = new Test();
		List list = AlgorithmUtils.zuhe(a, m);
		print(list);
		
		/*List results = new ArrayList();
		combination(a,2,0,5, results);
		print(results);*/
	}
	
	public static void combination(int[] list, int r, int low, int n, List results) {
		if (low < r) {
			for (int j = low; j < n; j++) {
				if ((low > 0 && list[j] < list[low - 1]) || low == 0) {
					int temp = list[low];
					list[low] = list[j];
					list[j] = temp;
					combination(list, r, low + 1, n, results);
					temp = list[low];
					list[low] = list[j];
					list[j] = temp;
				}
			}
		}
		if (low == r) {
			int[] result = new int[r];
			for (int i = 0; i < r; i++) {
				//System.out.print(list[i] + " ");
				result[r-i-1] = list[i];
			}
			results.add(result);
		}
	}
	
	public static void permutation(List<int[]> result, int[] arr, int index, int num){  
        if (index == num){  
            int[] newArr = new int[num];  
            System.arraycopy(arr, 0, newArr, 0, num);  
            result.add(newArr);  
            return;  
        }  
        for (int i = index; i < arr.length; i++) {  
            swap(arr, index, i);  
            permutation(result, arr, index + 1, num);  
            swap(arr, index, i);  
        }  
    }  
  
    private static void swap(int arr[], int i, int j){  
        if(i != j){  
            arr[i] ^= arr[j];  
            arr[j] ^= arr[i];  
            arr[i] ^= arr[j];  
        }  
    }  
  
    public static void testPermutation() throws Exception {  
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};  
        List<int[]> result = new ArrayList<>();  
        permutation(result, arr, 0, 2);//从arr中取两个元素排列(即N=8,M=2)  
        System.out.println(result.size());  
        for (int[] is : result) {  
            System.out.println(Arrays.toString(is));  
        }  
    }
	
	public static void print(List list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println();
			int[] temp = (int[]) list.get(i);
			for (int j = 0; j < temp.length; j++) {
				System.out.print(temp[j] + " ");
			}
		}
	}
	
	/**
	 * @param a:组合数组
	 * @param k:生成组合个数
	 * @return :所有可能的组合数组列表
	 */
	public static List zuhe(int[] a, int m) {
		List list = new ArrayList();
		combination(a,m,0,a.length, list);
		return list;
	}

	// 根据辅助数组和原始数组生成 结果数组
	public static int[] createResult(int[] a, int[] temp, int m) {
		int[] result = new int[m];
		int j = 0;
		for (int i = 0; i < a.length; i++) {
			if (temp[i] == 1) {
				result[j] = a[i];
				j++;
			}
		}
		return result;
	}

}
