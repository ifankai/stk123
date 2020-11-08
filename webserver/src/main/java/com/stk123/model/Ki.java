package com.stk123.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ki extends K{

	public List<Cent> values = new ArrayList<Cent>();

	/**
	 * calculate MACD values
	 * @param list:Price list to calculate, the first at head, the last at tail.
	 * @param shortPeriod:the short period value.
	 * @param longPeriod:the long period value.
	 * @param midPeriod:the mid period value.
	 * @return
	 */
	public static final HashMap<String, Double> getMACD(final List<Double> list, final int shortPeriod,
			final int longPeriod, int midPeriod) {
		HashMap<String, Double> macdData = new HashMap<String, Double>();
		List<Double> diffList = new ArrayList<Double>();
		Double shortEMA = 0.0;
		Double longEMA = 0.0;
		Double dif = 0.0;
		Double dea = 0.0;

		for (int i = list.size() - 1; i >= 0; i--) {
			List<Double> sublist = list.subList(0, list.size() - i);
			shortEMA = Ki.getEMA(sublist, shortPeriod);
			longEMA = Ki.getEMA(sublist, longPeriod);
			dif = shortEMA - longEMA;
			diffList.add(dif);
		}
		dea = Ki.getEMA(diffList, midPeriod);
		macdData.put("DIF", dif);
		macdData.put("DEA", dea);
		macdData.put("MACD", (dif - dea) * 2);
		return macdData;
	}



}
class Cent{
	public double value;
	public double amount;
	public int weight;
}
