package com.web.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import com.web.entity.*;

public class AmazonFeeCal {
	
	public AmazonInventoryReport priceCalculate(AmazonInventoryReport orgInventory) {
		
		if (doubleCheck(orgInventory.getCostFromAE()) && doubleCheck(orgInventory.getShippingCostEpacket())) {
	        double ProductPrice = Double.parseDouble(orgInventory.getCostFromAE());
	    	double ShippingCostEpacket = Double.parseDouble(orgInventory.getShippingCostEpacket());
	    	double AmazonFee;
	    	double AmazonFeeSub = 0;
	    	double COGS;
	    	double MarginTargetPercent = 0.5;
	    	double MarginTargetDolar;
	    	double AmzListingPriceMin;
	    	double MinAbsoluteMargin = 3;
	    	double SuspectListPrice;
	    	double AbsoluteListPrice;
	    	
	    	AmazonFee = ProductPrice;
	    	boolean flg = true;
	    	
	    	do {
	    	
		    	COGS = Double.valueOf(roundDouble(AmazonFee + ShippingCostEpacket + ProductPrice));
		    	
		    	MarginTargetDolar = Double.valueOf(roundDouble(COGS * MarginTargetPercent));
		    	AmzListingPriceMin = Double.valueOf(roundDouble(COGS + MarginTargetDolar));
		    	SuspectListPrice = Double.valueOf(roundDouble(MinAbsoluteMargin + COGS));
		    	
		    	if (SuspectListPrice > AmzListingPriceMin) {
		    		AbsoluteListPrice = Double.valueOf(roundDouble(SuspectListPrice));
		    	} else {
		    		AbsoluteListPrice = Double.valueOf(roundDouble(AmzListingPriceMin));
		    	}
		    	
		    	AmazonFee = Double.valueOf(roundDouble(Double.valueOf(roundDouble(AbsoluteListPrice* 0.15))));
		    	
		    	if (AmazonFeeSub == AmazonFee) {
		    		flg = false;
		    	}
		    	
		    	AmazonFeeSub = AmazonFee;
		    	
	    	} while (flg);
	    	
	    	orgInventory.setAmazonFee(String.valueOf(AmazonFee));
	    	orgInventory.setCOGS(String.valueOf(COGS));
	    	orgInventory.setCOGS(String.valueOf(COGS));
	    	orgInventory.setMarginTargetPercent(String.valueOf(MarginTargetPercent*100));
	    	orgInventory.setMarginTargetDolar(String.valueOf(MarginTargetDolar));
	    	orgInventory.setAmzListingPriceMin(String.valueOf(AmzListingPriceMin));
	    	orgInventory.setMinAbsoluteMargin(String.valueOf(MinAbsoluteMargin));
	    	orgInventory.setSuspectListPrice(String.valueOf(SuspectListPrice));
	    	orgInventory.setAbsoluteListPrice(String.valueOf(AbsoluteListPrice));
    	
		}
    	return orgInventory;
    }
	
	public String roundDouble(Number n) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
	    Double d = n.doubleValue();
	    
	    return df.format(d);
	}
	
	public boolean doubleCheck(String number) {
		try
		{
		  Double.parseDouble(number.trim());
		}
		catch(NumberFormatException e)
		{
		  return false;
		}
		return true;
	}
}
