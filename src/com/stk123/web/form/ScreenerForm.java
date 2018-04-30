package com.stk123.web.form;

import java.util.Arrays;

public class ScreenerForm {
	
	private String codes;
	
	private Double peFrom;
	private Double peTo;
	
	private Double pbFrom;
	private Double pbTo;
	
	private Double psFrom;
	private Double psTo;
	
	private Double revenueGrowthRateFrom;
	private Double revenueGrowthRateTo;
	
	private Double netProfitGrowthRateFrom;
	private Double netProfitGrowthRateTo;
	
	private Double grossProfitMarginFrom;
	private Double grossProfitMarginTo;
	
	private Double listingDaysFrom;
	private Double listingDaysTo;
	
	private Double erLowFrom;
	private Double erLowTo;
	
	private Double erHighFrom;
	private Double erHighTo;
	
	private Double erPeFrom;
	private Double erPeTo;
	
	private Double marketCapFrom;
	private Double marketCapTo;
	
	private Double debtRateFrom;
	private Double debtRateTo;
	
	private String comment;
	private String[] industry;
	private String[] strategy;
	private String[] astrategy;
	
	private boolean excludeLastCode;
	
	
	public String[] getAstrategy() {
		return astrategy;
	}

	public void setAstrategy(String[] astrategy) {
		this.astrategy = astrategy;
	}

	public String[] getStrategy() {
		return strategy;
	}

	public void setStrategy(String[] strategy) {
		this.strategy = strategy;
	}

	public boolean isExcludeLastCode() {
		return excludeLastCode;
	}

	public void setExcludeLastCode(boolean excludeLastCode) {
		this.excludeLastCode = excludeLastCode;
	}

	public String[] getIndustry() {
		return industry;
	}

	public void setIndustry(String industry[]) {
		this.industry = industry;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Double getMarketCapFrom() {
		return marketCapFrom;
	}

	public void setMarketCapFrom(Double marketCapFrom) {
		this.marketCapFrom = marketCapFrom;
	}

	public Double getMarketCapTo() {
		return marketCapTo;
	}

	public void setMarketCapTo(Double marketCapTo) {
		this.marketCapTo = marketCapTo;
	}

	public Double getDebtRateFrom() {
		return debtRateFrom;
	}

	public void setDebtRateFrom(Double debtRateFrom) {
		this.debtRateFrom = debtRateFrom;
	}

	public Double getDebtRateTo() {
		return debtRateTo;
	}

	public void setDebtRateTo(Double debtRateTo) {
		this.debtRateTo = debtRateTo;
	}

	public Double getErLowFrom() {
		return erLowFrom;
	}

	public void setErLowFrom(Double erLowFrom) {
		this.erLowFrom = erLowFrom;
	}

	public Double getErLowTo() {
		return erLowTo;
	}

	public void setErLowTo(Double erLowTo) {
		this.erLowTo = erLowTo;
	}

	public Double getErHighFrom() {
		return erHighFrom;
	}

	public void setErHighFrom(Double erHighFrom) {
		this.erHighFrom = erHighFrom;
	}

	public Double getErHighTo() {
		return erHighTo;
	}

	public void setErHighTo(Double erHighTo) {
		this.erHighTo = erHighTo;
	}

	public Double getErPeFrom() {
		return erPeFrom;
	}

	public void setErPeFrom(Double erPeFrom) {
		this.erPeFrom = erPeFrom;
	}

	public Double getErPeTo() {
		return erPeTo;
	}

	public void setErPeTo(Double erPeTo) {
		this.erPeTo = erPeTo;
	}

	public Double getRevenueGrowthRateFrom() {
		return revenueGrowthRateFrom;
	}

	public void setRevenueGrowthRateFrom(Double revenueGrowthRateFrom) {
		this.revenueGrowthRateFrom = revenueGrowthRateFrom;
	}

	public Double getRevenueGrowthRateTo() {
		return revenueGrowthRateTo;
	}

	public void setRevenueGrowthRateTo(Double revenueGrowthRateTo) {
		this.revenueGrowthRateTo = revenueGrowthRateTo;
	}

	public String getCodes() {
		return codes;
	}
	
	public Double getListingDaysFrom() {
		return listingDaysFrom;
	}

	public Double getListingDaysTo() {
		return listingDaysTo;
	}

	public void setListingDaysTo(Double listingDaysTo) {
		this.listingDaysTo = listingDaysTo;
	}

	public void setListingDaysFrom(Double listingDaysFrom) {
		this.listingDaysFrom = listingDaysFrom;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}
	public Double getPeFrom() {
		return peFrom;
	}
	public void setPeFrom(Double peFrom) {
		this.peFrom = peFrom;
	}
	public Double getPeTo() {
		return peTo;
	}
	public void setPeTo(Double peTo) {
		this.peTo = peTo;
	}
	public Double getPbFrom() {
		return pbFrom;
	}
	public void setPbFrom(Double pbFrom) {
		this.pbFrom = pbFrom;
	}
	public Double getPbTo() {
		return pbTo;
	}
	public void setPbTo(Double pbTo) {
		this.pbTo = pbTo;
	}
	public Double getPsFrom() {
		return psFrom;
	}
	public void setPsFrom(Double psFrom) {
		this.psFrom = psFrom;
	}
	public Double getPsTo() {
		return psTo;
	}
	public void setPsTo(Double psTo) {
		this.psTo = psTo;
	}
	public Double getNetProfitGrowthRateFrom() {
		return netProfitGrowthRateFrom;
	}
	public void setNetProfitGrowthRateFrom(Double netProfitGrowthRateFrom) {
		this.netProfitGrowthRateFrom = netProfitGrowthRateFrom;
	}
	public Double getNetProfitGrowthRateTo() {
		return netProfitGrowthRateTo;
	}
	public void setNetProfitGrowthRateTo(Double netProfitGrowthRateTo) {
		this.netProfitGrowthRateTo = netProfitGrowthRateTo;
	}
	public Double getGrossProfitMarginFrom() {
		return grossProfitMarginFrom;
	}
	public void setGrossProfitMarginFrom(Double grossProfitMarginFrom) {
		this.grossProfitMarginFrom = grossProfitMarginFrom;
	}
	public Double getGrossProfitMarginTo() {
		return grossProfitMarginTo;
	}
	public void setGrossProfitMarginTo(Double grossProfitMarginTo) {
		this.grossProfitMarginTo = grossProfitMarginTo;
	}

	@Override
	public String toString() {
		return "ScreenerForm [codes=" + codes + ", peFrom=" + peFrom + ", peTo=" + peTo + ", pbFrom=" + pbFrom
				+ ", pbTo=" + pbTo + ", psFrom=" + psFrom + ", psTo=" + psTo + ", revenueGrowthRateFrom="
				+ revenueGrowthRateFrom + ", revenueGrowthRateTo=" + revenueGrowthRateTo + ", netProfitGrowthRateFrom="
				+ netProfitGrowthRateFrom + ", netProfitGrowthRateTo=" + netProfitGrowthRateTo
				+ ", grossProfitMarginFrom=" + grossProfitMarginFrom + ", grossProfitMarginTo=" + grossProfitMarginTo
				+ ", listingDaysFrom=" + listingDaysFrom + ", listingDaysTo=" + listingDaysTo + ", erLowFrom="
				+ erLowFrom + ", erLowTo=" + erLowTo + ", erHighFrom=" + erHighFrom + ", erHighTo=" + erHighTo
				+ ", erPeFrom=" + erPeFrom + ", erPeTo=" + erPeTo + ", marketCapFrom=" + marketCapFrom
				+ ", marketCapTo=" + marketCapTo + ", debtRateFrom=" + debtRateFrom + ", debtRateTo=" + debtRateTo
				+ ", comment=" + comment + ", industry=" + Arrays.toString(industry) + ", strategy="
				+ Arrays.toString(strategy) + ", excludeLastCode=" + excludeLastCode + "]";
	}

	
	
}
