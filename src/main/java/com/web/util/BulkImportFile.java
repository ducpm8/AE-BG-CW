package com.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.web.data.P2SubmitData;
import com.web.entity.AmazonOrderReport;

public class BulkImportFile {
	public Map<String, List> createInput(String fileName) throws IOException
    {
		AmazonOrderReport orderReport = new AmazonOrderReport();
		AmazonOrderReport orderReportFail = new AmazonOrderReport();
		
		Map<String, List> result = new HashMap<String, List>();
		
		List<AmazonOrderReport> orderReportList = new ArrayList<AmazonOrderReport>();
		List<AmazonOrderReport> orderReportFailList = new ArrayList<AmazonOrderReport>();
		
		Map<String, Integer> mapOrder = new HashMap<String, Integer>();
		
		List<Map<String, Integer>> mapOrderList = new ArrayList<Map<String,Integer>>();
		
		//try {
        	
        	String productID_Trim2_SKU = StringUtils.EMPTY;
        	int quantity = 0;
        	int k=0;
            String price;
        	char extention;
        	String sheetName;
        	FileInputStream file;
        	
        	file = new FileInputStream(new File(fileName));
        	extention =  fileName.charAt(fileName.length() - 1);
    		file = new FileInputStream(new File(fileName));
			
        	//Get the workbook instance for XLS file 
        	HSSFWorkbook workbook = new HSSFWorkbook(file);
        	
        	HSSFRow rowHS;
        	HSSFCell cellHS;
        	HSSFCell cellHS2;
        	
        	String productName = "";
        	String cellVal = "";
        	
        	//for (int i=0; i < workbook.getNumberOfSheets(); i++) {
            	//for (int i=0; i < 1; i++) {
            		//Get first sheet from the workbook
        	HSSFSheet sheet = workbook.getSheetAt(0);
        		
        	for (int j=1; j <= sheet.getLastRowNum(); j++) {
        		productName = "";
        		orderReport = new AmazonOrderReport();
        		rowHS = sheet.getRow(j);
        		if (rowHS != null) {
        			
        			//SKU
            		cellHS = rowHS.getCell(10);
            		if (cellHS != null) {
            			
            			cellHS.setCellType(Cell.CELL_TYPE_STRING);
            			productID_Trim2_SKU = cellHS.getStringCellValue();
            			
                		if (StringUtils.isEmpty(productID_Trim2_SKU)) {
                			//Can not obtains SKU in csv input
                			
                			orderReport.setSku(StringUtils.EMPTY);
	                        
                			orderReport.setURL(StringUtils.EMPTY);
                			
                			orderReport.setReason("Can not obtains SKU");
                			orderReport.setLine(String.valueOf(j+1));
                			orderReportFailList.add(orderReport);
                			
                			continue;
                		}
                		
                		//Quantity
                		cellHS = rowHS.getCell(12);
                		cellHS.setCellType(Cell.CELL_TYPE_NUMERIC);
                		quantity =(int) cellHS.getNumericCellValue();
                		
                		if (quantity == 0) {
                			//Can not obtains SKU in csv input
                			
                			orderReport.setSku(productID_Trim2_SKU);
	                        
                			orderReport.setURL(StringUtils.EMPTY);
                			
                			orderReport.setReason("Can not obtains Quantity");
                			orderReport.setLine(String.valueOf(j+1));
                			orderReportFailList.add(orderReport);
                			
                			continue;
                	
                		}
                		
                		try {
                		
//                		//ContactName
//            			cellHS = rowHS.getCell(16);
//            			
//            			if (cellHS == null) {
//                			throw new IllegalArgumentException("ContactName blank");
//                		}
//                		
//                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
//                		cellVal = "";
//                		cellVal = cellHS.getStringCellValue();
//                		
//                		orderReport.setRecipientName(cellVal);
//                		
//                		//Street Address
//                		cellHS = rowHS.getCell(17);
//                		if (cellHS == null) {
//                			throw new IllegalArgumentException("ship-address-1 blank");
//                		}
//                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
//                		if (cellHS.getStringCellValue().isEmpty()) {
//                			throw new IllegalArgumentException("ship-address-1 blank");
//                		}
//                		
//                		cellVal = "";
//                		cellVal = cellHS.getStringCellValue();
//                		
//                		if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
//                			orderReport.setShipAddress1(cellVal);
//                		} else {
//                			throw new IllegalArgumentException("Address include non English characters: " +cellVal );
//                		}
//                		
//                		cellHS = rowHS.getCell(18);
//                		if (cellHS != null) {
//                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
//                			if (!cellHS.getStringCellValue().isEmpty()) {
//                				cellVal = "";
//		                		cellVal = cellHS.getStringCellValue();
//                				if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
//                					orderReport.setShipAddress2(cellVal);
//		                		} else {
//		                			throw new IllegalArgumentException("Address2 include non English characters: " +cellVal );
//		                		}
//	                			
//	                		}
//                		}
//
//                		
//                		cellHS = rowHS.getCell(19);
//                		
//                		if (cellHS != null) {
//                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
//                			cellVal = "";
//	                		cellVal = cellHS.getStringCellValue();
//                			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
//                				
//                				orderReport.setShipAddress2(orderReport.getShipAddress2() + " " + cellVal);
//                				
//	                		} else {
//	                			throw new IllegalArgumentException("Address2 include non English characters: " +cellVal );
//	                		}
//                		}
//            			
//                		//City
//                		cellHS = rowHS.getCell(20);
//                		
//                		if (cellHS == null) {
//                			throw new IllegalArgumentException("ship-city blank");
//                		}
//                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
//                		
//                		cellVal = "";
//                		cellVal = cellHS.getStringCellValue();
//            			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
//            				orderReport.setShipCity(cellVal);
//                		} else {
//                			throw new IllegalArgumentException("City include non English characters: " +cellVal );
//                		}
//            			
                    	//State Province
                		cellHS = rowHS.getCell(21);
                		
                		if (cellHS == null) {
                			throw new IllegalArgumentException("ship-state blank");
                		}
                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
            			
                		String stateFullName = "";
            			if (!ConstVal.stateMap.containsKey(cellHS.getStringCellValue())) {
            				stateFullName = cellHS.getStringCellValue();
            			} else {
            				stateFullName = ConstVal.stateMap.get(cellHS.getStringCellValue());
            			}
                    	
            			orderReport.setShipStateFull(stateFullName);
//                		
//            			//Zip
//                		HSSFCell cellHSZip;
//                		
//                		cellHSZip = rowHS.getCell(22);
//                		
//                		String zipCode = "";
//                		
//                		if (cellHSZip == null) {
//                			//Zip not exist
//                			throw new IllegalArgumentException("zip code blank"); 
//                			//throw new IllegalArgumentException("ship-postal-code blank");
//                		} else {
//                			cellHSZip.setCellType(Cell.CELL_TYPE_STRING);
//                			if (cellHSZip.getStringCellValue().isEmpty()) {
//	                			//Zip not exist
//                				throw new IllegalArgumentException("zip code blank"); 
//	                		} else {
//	                			
//	                			cellHSZip.setCellType(Cell.CELL_TYPE_STRING);
//                				zipCode = cellHSZip.getStringCellValue();
//                				
//                				cellVal = "";
//		                		cellVal = cellHS.getStringCellValue();
//	                			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
//	                				
//	                				orderReport.setShipPostalCode(cellVal);
//	                				
//		                		} else {
//		                			throw new IllegalArgumentException("Zip Code include non English characters: " +cellVal );
//		                		}
//	                		}
//                		} 
//                		
//            			//Tel
//                		HSSFCell cellHSTel;
//                		cellHSTel = rowHS.getCell(9);
//                		if (cellHSTel == null) {
//                			throw new IllegalArgumentException("buyer-phone-number blank");
//                		}
//                		
//                		String tel = "";
//                		
//                		cellHSTel.setCellType(Cell.CELL_TYPE_STRING);
//            			tel = cellHSTel.getStringCellValue();
//            			if (!tel.isEmpty()) {
//            				tel= tel.replace("-", "");
//            				tel= tel.replace("(", "");
//            				tel= tel.replace(")", "");
//            				tel= tel.replace(" ", "");
//            				tel= tel.replace("+1", "");
//            			}
//            			
//            			orderReport.setBuyerPhoneNumber(tel);
//            			
//            			cellHSTel = rowHS.getCell(23);
//            			if (cellHSTel == null) {
//                			throw new IllegalArgumentException("Country blank");
//                		}
//            			
//            			cellHSTel.setCellType(Cell.CELL_TYPE_STRING);
//            			cellVal = cellHSTel.getStringCellValue();
//            			
//            			if (cellVal.isEmpty() || !cellVal.equals("US")) {
//            				throw new IllegalArgumentException("Country is not US");
//            			}
            			
            			cellHS = rowHS.getCell(16);
            			if (cellHS == null) {
                			throw new IllegalArgumentException("ContactName blank");
                		}
                		
                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
                		cellVal = "";
                		cellVal = cellHS.getStringCellValue();
                		
            			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
                			orderReport.setRecipientName(cellVal);
                		} else {
                			throw new IllegalArgumentException("Address include non English characters: " +cellVal );
                		}
            			
            			orderReport.setShipCountry("UNITED STATES");
            			orderReport.setWarehouse("CN");
            			
            			for (int m = 0; m < 24; m++) {
                	    	
                	    	cellHS2 = rowHS.getCell(m);
                	    	if (cellHS2 != null) {
	                			cellHS2.setCellType(Cell.CELL_TYPE_STRING);
	                			
                    	    	switch (m) {
	                    	    	case 0:
	                    	    		orderReport.setOrderId(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 1:
	                    	    		orderReport.setOrderItemId(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 2:
	                    	    		orderReport.setPurchaseDate(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 3:	
	                    	    		orderReport.setPaymentsDate(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 4:
	                    	    		orderReport.setReportingDate(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 5:
	                    	    		orderReport.setPromiseDate(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 6:
	                    	    		orderReport.setDaysPastPromise(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 7:
	                    	    		orderReport.setBuyerEmail(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 8:
	                    	    		orderReport.setBuyerName(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 9:
	                    	    		orderReport.setBuyerPhoneNumber(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 10:
	                    	    		orderReport.setSku(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 11:
	                    	    		orderReport.setProductName2(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 12:
	                    	    		orderReport.setQuantityPurchased(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 13:
	                    	    		orderReport.setQuantityShipped(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 14:
	                    	    		orderReport.setQuantityToShip(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 15:
	                    	    		orderReport.setShipServiceLevel(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	//case 16:
	                    	    		//orderReport.setRecipientName(cellHS2.getStringCellValue());
	                    	    		//break;
	                    	    	case 17:
	                    	    		orderReport.setShipAddress1(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 18:
	                    	    		orderReport.setShipAddress2(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 19:
	                    	    		orderReport.setShipAddress3(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 20:
	                    	    		orderReport.setShipCity(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 21:
	                    	    		orderReport.setShipState(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 22:
	                    	    		orderReport.setShipPostalCode(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	case 23:
	                    	    		orderReport.setShipCountry(cellHS2.getStringCellValue());
	                    	    		break;
	                    	    	default :
                    	    	}
                	    	}
                	    }
            			
            			orderReportList.add(k, orderReport);
            			mapOrder.put(orderReport.getOrderId().replaceAll("-", ""), k);
            			k = k + 1;
            			
	            		} catch (IllegalArgumentException iae) {
	            			orderReportFail.setSku(productID_Trim2_SKU);
	            			orderReportFail.setReason(iae.getMessage());
	            			orderReportFail.setLine(String.valueOf(j+1));
	            			
	            			orderReportFailList.add(orderReportFail);
	            			
	            			continue;
	            			
	            		} catch (Exception e) {
	            			
	            			orderReportFail.setSku(productID_Trim2_SKU);
	            			orderReportFail.setReason(e.getMessage());
	            			orderReportFail.setLine(String.valueOf(j+1));
	            			
	            			orderReportFailList.add(orderReportFail);
	            			
	            			continue;
	            		}
                		
            		}
        		}
        	}
		
		String rootPath = System.getProperty("catalina.home");
		
		File dir = new File(rootPath + File.separator + "tmpFiles");
		if (!dir.exists())
			dir.mkdirs();
		
		//method 1
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		//Write to CSV file
		final String csvFile = dir.getAbsolutePath() + File.separator +  timestamp.getTime()  + ".csv";
        FileWriter writer = new FileWriter(csvFile);
        String mailBody = "";
        
        CSVUtils.writeLine(writer, Arrays.asList("Sale Record Id",
        		"Buyer Country",
        		"Buyer Fullname",
        		"Buyer Address 1",
        		"Buyer Address 2",
        		"Buyer State",
        		"Buyer City",
        		"Buyer Zip",
        		"Buyer Phone Number",
        		"Remark",
        		"Warehouse",
        		"Product ID",
        		"Quantity",
        		"Product ID",
        		"Quantity",
        		"Product ID",
        		"Quantity",
        		"Product ID",
        		"Quantity",
        		"Product ID",
        		"Quantity"));
        
        if (orderReportList != null) {
        	mailBody = "Success : " + orderReportList.size() + "\r\n";
	        for (int i=0; i < orderReportList.size(); i++) {
	        	String shipAdd = "";
	        	if (!orderReportList.get(i).getShipAddress2().isEmpty()) {
	        		shipAdd = orderReportList.get(i).getShipAddress2();
	        	}
	        	if (!orderReportList.get(i).getShipAddress3().isEmpty()) {
	        		shipAdd = shipAdd + " " + orderReportList.get(i).getShipAddress3();
	        	}
	        	
	        	CSVUtils.writeLine(writer, Arrays.asList(orderReportList.get(i).getOrderId().replaceAll("-", ""), 
	        			orderReportList.get(i).getShipCountry(), 
	        			orderReportList.get(i).getRecipientName(),
	        			orderReportList.get(i).getShipAddress1(),
	        			shipAdd,
	        			orderReportList.get(i).getShipStateFull(),
	        			orderReportList.get(i).getShipCity(),
	        			orderReportList.get(i).getShipPostalCode(),
	        			orderReportList.get(i).getBuyerPhoneNumber(),
	        			"",
	        			orderReportList.get(i).getWarehouse(),
	        			orderReportList.get(i).getSku(),
	        			orderReportList.get(i).getQuantityPurchased(),
	        			"",
	        			"",
	        			"",
	        			"",
	        			"",
	        			"",
	        			"",
	        			""), ',', '"');
	        }
        }

        writer.flush();
        writer.close();
        
        mapOrderList.add(mapOrder);
        
        result.put("FAIL", orderReportFailList);
		result.put("SUCCESS", orderReportList);
		result.put("ORDERLIST", mapOrderList);
		result.put("CSV", new ArrayList<String>(Arrays.asList(csvFile)));
		
		return result;
    }
	
}
