package com.web.entity;

public class AmazonInventoryReport {
	private String URL = "";
	private String CostFromAE= "";
	private String ProductID = "";
	private String ProductName = "";
	private String ShippingCostEpacket = "";
	private String AmazonFee = "";
	private String COGS = "";
	private String MarginTargetDolar = "";
	private String MarginTargetPercent = "";
	private String AmzListingPriceMin = "";
	private String MinAbsoluteMargin = "";
	private String SuspectListPrice = "";
	private String AbsoluteListPrice = "";
	private String PullNumbers = "";
	private String AssignedAmazonSku = "";
	private String ASIN = "";
	private String fileName = "";
	private String processDateTime = "";
	private String rating = "";
	
	//Error SKU
	private String reason = "";
	private String line = "";
	private String bg_product_name = "";
	
	/**
	 * @return the bg_product_name
	 */
	public String getBg_product_name() {
		return bg_product_name;
	}
	/**
	 * @param bg_product_name the bg_product_name to set
	 */
	public void setBg_product_name(String bg_product_name) {
		this.bg_product_name = bg_product_name;
	}
	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}
	/**
	 * @return the processDateTime
	 */
	public String getProcessDateTime() {
		return processDateTime;
	}
	/**
	 * @param processDateTime the processDateTime to set
	 */
	public void setProcessDateTime(String processDateTime) {
		this.processDateTime = processDateTime;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the line
	 */
	public String getLine() {
		return line;
	}
	/**
	 * @param line the line to set
	 */
	public void setLine(String line) {
		this.line = line;
	}
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	/**
	 * @return the assignedAmazonSku
	 */
	public String getAssignedAmazonSku() {
		return AssignedAmazonSku;
	}
	/**
	 * @param assignedAmazonSku the assignedAmazonSku to set
	 */
	public void setAssignedAmazonSku(String assignedAmazonSku) {
		AssignedAmazonSku = assignedAmazonSku;
	}
	/**
	 * @return the aSIN
	 */
	public String getASIN() {
		return ASIN;
	}
	/**
	 * @param aSIN the aSIN to set
	 */
	public void setASIN(String aSIN) {
		ASIN = aSIN;
	}
	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}
	/**
	 * @param uRL the uRL to set
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}
	/**
	 * @return the costFromAE
	 */
	public String getCostFromAE() {
		return CostFromAE;
	}
	/**
	 * @param costFromAE the costFromAE to set
	 */
	public void setCostFromAE(String costFromAE) {
		CostFromAE = costFromAE;
	}
	/**
	 * @return the productID
	 */
	public String getProductID() {
		return ProductID;
	}
	/**
	 * @param productID the productID to set
	 */
	public void setProductID(String productID) {
		ProductID = productID;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return ProductName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		ProductName = productName;
	}
	/**
	 * @return the shippingCostEpacket
	 */
	public String getShippingCostEpacket() {
		return ShippingCostEpacket;
	}
	/**
	 * @param shippingCostEpacket the shippingCostEpacket to set
	 */
	public void setShippingCostEpacket(String shippingCostEpacket) {
		ShippingCostEpacket = shippingCostEpacket;
	}
	/**
	 * @return the amazonFee
	 */
	public String getAmazonFee() {
		return AmazonFee;
	}
	/**
	 * @param amazonFee the amazonFee to set
	 */
	public void setAmazonFee(String amazonFee) {
		AmazonFee = amazonFee;
	}
	/**
	 * @return the cOGS
	 */
	public String getCOGS() {
		return COGS;
	}
	/**
	 * @param cOGS the cOGS to set
	 */
	public void setCOGS(String cOGS) {
		COGS = cOGS;
	}
	/**
	 * @return the marlginTargetPercent
	 */
	public String getMarginTargetDolar() {
		return MarginTargetDolar;
	}
	/**
	 * @param marlginTargetPercent the marlginTargetPercent to set
	 */
	public void setMarginTargetDolar(String marlginTargetPercent) {
		MarginTargetDolar = marlginTargetPercent;
	}
	/**
	 * @return the marginTargetPercent
	 */
	public String getMarginTargetPercent() {
		return MarginTargetPercent;
	}
	/**
	 * @param marginTargetPercent the marginTargetPercent to set
	 */
	public void setMarginTargetPercent(String marginTargetPercent) {
		MarginTargetPercent = marginTargetPercent;
	}
	/**
	 * @return the amzListingPriceMin
	 */
	public String getAmzListingPriceMin() {
		return AmzListingPriceMin;
	}
	/**
	 * @param amzListingPriceMin the amzListingPriceMin to set
	 */
	public void setAmzListingPriceMin(String amzListingPriceMin) {
		AmzListingPriceMin = amzListingPriceMin;
	}
	/**
	 * @return the minAbsoluteMargin
	 */
	public String getMinAbsoluteMargin() {
		return MinAbsoluteMargin;
	}
	/**
	 * @param minAbsoluteMargin the minAbsoluteMargin to set
	 */
	public void setMinAbsoluteMargin(String minAbsoluteMargin) {
		MinAbsoluteMargin = minAbsoluteMargin;
	}
	/**
	 * @return the suspectListPrice
	 */
	public String getSuspectListPrice() {
		return SuspectListPrice;
	}
	/**
	 * @param suspectListPrice the suspectListPrice to set
	 */
	public void setSuspectListPrice(String suspectListPrice) {
		SuspectListPrice = suspectListPrice;
	}
	/**
	 * @return the absoluteListPrice
	 */
	public String getAbsoluteListPrice() {
		return AbsoluteListPrice;
	}
	/**
	 * @param absoluteListPrice the absoluteListPrice to set
	 */
	public void setAbsoluteListPrice(String absoluteListPrice) {
		AbsoluteListPrice = absoluteListPrice;
	}
	/**
	 * @return the pullNumbers
	 */
	public String getPullNumbers() {
		return PullNumbers;
	}
	/**
	 * @param pullNumbers the pullNumbers to set
	 */
	public void setPullNumbers(String pullNumbers) {
		PullNumbers = pullNumbers;
	}
	
}
