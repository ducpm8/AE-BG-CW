package com.web.data;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

import com.web.entity.*;
import com.web.util.ConstVal;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataGrabP3 {
	
	private AmazonOrderReport orderReport;
	
	public static String homepage = "https://www.aliexpress.com";
	public static String myorders = "https://trade.aliexpress.com/orderList.htm";
	
	public Map<String, List> doCollectTrackingNo(String fileName, P2SubmitData sbData)
    {
		String sheetName = sbData.getSheetName();
		String account =  sbData.getAccount();
		String password = sbData.getPassword();
		
		Map<String, List> result = new HashMap<String, List>();
		
		List<AmazonOrderReport> orderReportList = new ArrayList<AmazonOrderReport>();
		
		List<AmazonOrderReport> orderReportFailList = new ArrayList<AmazonOrderReport>();
		
		
		WebDriver driver = new ChromeDriver();
		
		driver.manage().window().maximize();
		
		//Check user password

        try {
        	
        	String OrderNumber = StringUtils.EMPTY;
        	int quantity = 0;
            String price;
        	char extention;
        	FileInputStream file;
        	
        	file = new FileInputStream(new File(fileName));
        	extention =  fileName.charAt(fileName.length() - 1);
        	if (extention == 'x') {
        		//Get the workbook instance for XLS file 
            	XSSFWorkbook wbs = new XSSFWorkbook (file);
                
                for (int i=0; i < wbs.getNumberOfSheets(); i++) {
            	//for (int i=0; i < 1; i++) {
                	Sheet mySheet = wbs.getSheetAt(i);
                	
                	if (sheetName != null && StringUtils.isNotEmpty(sheetName)) {
                		sheetName = wbs.getSheetAt(i).getSheetName();
                	}
                	
                	if (mySheet.getSheetName().equals(sheetName)) {
                    
	                    Row row;
	                    
	                    for (int j=0; j <= mySheet.getLastRowNum(); j++) {
	                    	orderReport = new AmazonOrderReport();
	                    	
	                    	row = mySheet.getRow(j);
	                    	
	                    	if (row != null) {
		                    	//URL data
		                    	Cell cell = row.getCell(0);
		                    	
		                    	if (cell != null) {
		                    	
			                    	switch (cell.getCellType()) { 
			    	                	
			    	                	case Cell.CELL_TYPE_STRING: 
			    	                		//productID_Trim2_SKU = cell.getStringCellValue();
			    	                		
			    	                		driver.navigate().to("https://www.aliexpress.com");
			    	                		
			    	                		//Input productID
			    	                		//driver.findElement(By.xpath("//input[contains(@id,'search-key')]")).sendKeys(productID_Trim2_SKU);
			    	                		
			    	                		//Click Search
			    	                		driver.findElement(By.xpath("//input[contains(@class,'search-button')]")).click();
			    	                		
			    	                		
			    	                        price = driver.findElement(By.xpath("//span[contains(@id, 'j-sku-price')]")).getText();
			    	                		
			    	                        cell.setCellValue(price);
			    	                        
			    	                		break; 
			            				default :
			                    	}
		                    	}
	                    	}
	                    }
                	}
                	
                	break;
                }
                file.close();
        	} else {
        		file = new FileInputStream(new File(fileName));
    			
            	//Get the workbook instance for XLS file 
            	HSSFWorkbook workbook = new HSSFWorkbook(file);
            	
            	ArrayList<String> tabs2 = new ArrayList<String>();
            	
            	HSSFRow rowHS;
            	HSSFCell cellHS;
            	HSSFCell cellHS2;
            	
            	String productName = "";
            	String cellVal = "";
            	
            	driver.navigate().to(homepage);
            	
            	WebElement element;
            	List<WebElement> elements;
            	
            	List<WebElement> popup = driver.findElements(By.xpath("//a[contains(@data-role,'layer-close')]"));
            	if (popup.size() > 0) {
            		popup.get(0).click();
            		Thread.sleep(5000);
            	}	
            	
            	elements = driver.findElements(By.xpath("//a[contains(@data-role,'sign-link')]"));
            	
            	if (elements.size() > 0) {
            		//Login
            		if (elements.get(0).isDisplayed()) {
            			elements.get(0).click();
                		
                		driver.switchTo().defaultContent(); // you are now outside both frames
                		driver.switchTo().frame("alibaba-login-box");
                		
                		driver.findElement(By.id("fm-login-id")).clear();
                		driver.findElement(By.id("fm-login-id")).sendKeys(account);
                		
                		driver.findElement(By.id("fm-login-password")).clear();
                		driver.findElement(By.id("fm-login-password")).sendKeys(password);
                		driver.findElement(By.id("fm-login-submit")).click();
                		
                		//Wait until login complete
                		boolean doTheLoop = true;
                	    int i = 0;
                	    while (doTheLoop){ 
                	        i = i+200;
                	        Thread.sleep(200);
                	        if (i>30000){
                	            doTheLoop = false;
                	            throw new IllegalArgumentException("Can not go to My Orders");
                	        }
                	        if (driver.getCurrentUrl().indexOf("login") < 0 ){
                	            doTheLoop = false;
                	        }      
    	            	}
            		}
            		
            		//Go to My Orders
            		
            		driver.navigate().to(myorders);
            		
            		//driver.findElement(By.xpath("//ul[@class='flyout-quick-entry']/li[2]")).click();
            		
            		if (!driver.getCurrentUrl().contains("trade.aliexpress.com")) {
            			boolean doTheLoop = true;
                	    int i = 0;
                	    while (doTheLoop){ 
                	        i = i+200;
                	        Thread.sleep(2000);
                	        if (i>10000){
                	            doTheLoop = false;
                	            throw new IllegalArgumentException("Can not go to My Orders");
                	        }
                	        if (driver.getCurrentUrl().contains("trade.aliexpress.com")){
                	            doTheLoop = false;
                	        }      
    	            	}
            		}
            		
            		//Awaiting delivery
        			//
        			String numberOfAwaitingDeliveryOrder = driver.findElement(By.id("remiandTips_waitBuyerAcceptGoods")).getAttribute("value");
        			
        			if (Integer.valueOf(numberOfAwaitingDeliveryOrder) > 0) {
        				driver.findElement(By.id("remiandTips_waitBuyerAcceptGoods")).click();
        			} else {
        				throw new IllegalArgumentException("There isn't any awaiting delivery order");
        			}
            		
            	}
            	for (int i=0; i < workbook.getNumberOfSheets(); i++) {
                	HSSFSheet sheet = workbook.getSheetAt(i);
                	
                	if (sheetName == null || StringUtils.isEmpty(sheetName)) {
                		sheetName = workbook.getSheetAt(i).getSheetName();
                	}
                	
                	if (sheet.getSheetName().equals(sheetName)) {
                		
	                	for (int j=1; j <= sheet.getLastRowNum(); j++) {
	                		productName = "";
	                		orderReport = new AmazonOrderReport();
	                		rowHS = sheet.getRow(j);
	                		if (rowHS != null) {
	                			
	                			//Order Number
		                		cellHS = rowHS.getCell(7);
		                		if (cellHS != null) {
		                			
		                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
		                			OrderNumber = cellHS.getStringCellValue();
		                			
			                		if (StringUtils.isEmpty(OrderNumber)) {
			                			//Can not obtains Order Number in xls input
			                			
			                			orderReport.setSku(StringUtils.EMPTY);
				                        
			                			orderReport.setURL(StringUtils.EMPTY);
			                			
			                			orderReport.setVendorOrdernumber(StringUtils.EMPTY);
			                			
			                			orderReport.setReason("Can not obtains OrderNumber");
			                			orderReport.setLine(String.valueOf(j+1));
			                			orderReportFailList.add(orderReport);
			                			
			                			continue;
			                		}
			                		
			                		try {
			                			
			                			OrderNumber = OrderNumber.replaceAll("[^\\d.]", "");
			                			
				                		//Input Order Number
			                			element = (new WebDriverWait(driver, 10))
				                        		   .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@id,'order-no')]")));
			                			
			                			element.clear();
			                			element.sendKeys(OrderNumber);
		    	                		
		    	                		//Click Search
			                			element = (new WebDriverWait(driver, 10))
				                        		   .until(ExpectedConditions.elementToBeClickable(By.id("search-btn")));
			                			element.click();
			                			
			                			//Check product exist
			                			List<WebElement> countProduct = driver.findElements(By.xpath("//label[@class='ui-label']"));
			                			
			                			String rootTabName =  driver.getWindowHandle();
			                			
			                			if (countProduct.size() < 1) {
			                				if (driver.findElements(By.xpath("//td[@class='zero-result']")).size() > 0){
			                					throw new IllegalArgumentException("Order Number not found.");
			                				} else {
			                				
				                				boolean doTheLoop = true;
				                        	    int k = 0;
				                        	    while (doTheLoop){ 
				                        	        k = k+200;
				                        	        Thread.sleep(2000);
				                        	        if (k>10000){
				                        	            doTheLoop = false;
				                        	            throw new IllegalArgumentException("Order Number search timeout.");
				                        	        }
				                        	        if (driver.findElements(By.xpath("//label[@class='ui-label']")).size() > 0){
				                        	            doTheLoop = false;
				                        	        }      
				            	            	}
			                				}
			                			}
			                			
			                			String totalOrder = "";
			                			
			                			totalOrder = driver.findElements(By.xpath("//label[@class='ui-label']")).get(0).getText();
			                			
			                			totalOrder = totalOrder.substring(0, totalOrder.indexOf("/"));
			                			
			                			if (!totalOrder.equals("1")) {
			                				throw new IllegalArgumentException("More than 1 order number returned. Result " + totalOrder);
			                			}
			                			
			                			//View Detail link click.
			                			
			                			// a view-detail-link
			                			if (driver.findElements(By.xpath("//a[@class='view-detail-link']")).size() < 1 ) {
			                				throw new IllegalArgumentException("Detail link not found.");
			                			} else {
			                				driver.findElements(By.xpath("//a[@class='view-detail-link']")).get(0).click();
			                			}
			                			
			                			tabs2 = new ArrayList<String> (driver.getWindowHandles());
			                		    driver.switchTo().window(tabs2.get(1));
			                		    
			                		    boolean doTheLoop = true;
		                        	    int k = 0;
		                        	    while (doTheLoop){ 
		                        	        k = k+200;
		                        	        Thread.sleep(1000);
		                        	        if (k>1000){
		                        	            doTheLoop = false;
		                        	            throw new IllegalArgumentException("Tracking Number not found");
		                        	        }
		                        	        if (driver.findElements(By.xpath("//tr[@id='logistic-item1']/td[@class='no']")).size() > 0){
		                        	            doTheLoop = false;
		                        	        }      
		            	            	}
			                			
			                			String trackingNo = "";
			                			
			                			if (driver.findElements(By.xpath("//tr[@id='logistic-item1']/td[@class='no']")).size() > 0) {
			                				trackingNo = driver.findElement(By.xpath("//tr[@id='logistic-item1']/td[@class='no']")).getText();
			                			} else {
			                				throw new IllegalArgumentException("Tracking Number not found");
			                			}
			                    	    
			                    	    for (int m = 0; m < 31; m++) {
			                    	    	
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
					                    	    		orderReport.setProductName(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 3:
					                    	    		orderReport.setTotalPruchasePrice(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 4:
					                    	    		orderReport.setTotalCustomerSpendonOrder(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 5:
					                    	    		orderReport.setMarginDollar(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 6:
					                    	    		orderReport.setMarginPercent(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 9:
					                    	    		orderReport.setPaymentsDate(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 10:
					                    	    		orderReport.setPurchaseDate(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 11:	
					                    	    		orderReport.setReportingDate(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 12:	
					                    	    		orderReport.setPromiseDate(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 13:	
					                    	    		orderReport.setDaysPastPromise(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 14:	
					                    	    		orderReport.setBuyerEmail(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 15:	
					                    	    		orderReport.setBuyerName(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 16:	
					                    	    		orderReport.setBuyerPhoneNumber(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 17:	
					                    	    		orderReport.setSku(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 18:	
					                    	    		orderReport.setProductName2(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 19:	
					                    	    		orderReport.setQuantityPurchased(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 20:	
					                    	    		orderReport.setQuantityShipped(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 21:	
					                    	    		orderReport.setQuantityToShip(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 22:	
					                    	    		orderReport.setShipServiceLevel(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 23:	
					                    	    		orderReport.setRecipientName(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 24:	
					                    	    		orderReport.setShipAddress1(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 25:	
					                    	    		orderReport.setShipAddress2(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 26:	
					                    	    		orderReport.setShipAddress3(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 27:	
					                    	    		orderReport.setShipCity(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 28:	
					                    	    		orderReport.setShipState(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 29:	
					                    	    		orderReport.setShipPostalCode(cellHS2.getStringCellValue());
					                    	    		break;
					                    	    	case 30:	
					                    	    		orderReport.setShipCountry(cellHS2.getStringCellValue());
					                    	    		break;

					                    	    	default :
				                    	    	}
			                    	    	}
			                    	    }
			                    	    
			                    	    orderReport.setVendorOrdernumber(OrderNumber);
			                    	    orderReport.setTrackingNumber(trackingNo);
			                    	    
				                        orderReportList.add(orderReport);
				                        
				                        switchTab(rootTabName, driver);
				                        
			                		    driver.switchTo().window(rootTabName);
				                		
			                		} catch (IllegalArgumentException ex) {
			                			if (ex.getMessage().contains("Tracking Number not found")) {
			                				orderReport.setVendorOrdernumber(OrderNumber);
				                			orderReport.setURL(driver.getCurrentUrl());
				                			orderReport.setReason(ex.getMessage());
				                			orderReport.setLine(String.valueOf(j+1));
				                			
				                			orderReportFailList.add(orderReport);
				                			
				                			driver.close();
				                		    driver.switchTo().window(tabs2.get(0));
				                			
				                			continue;
			                			}
			                			
			                			orderReport.setVendorOrdernumber(OrderNumber);
			                			orderReport.setURL(driver.getCurrentUrl());
			                			orderReport.setReason(ex.getMessage());
			                			orderReport.setLine(String.valueOf(j+1));
			                			
			                			orderReportFailList.add(orderReport);
			                			
			                			driver.navigate().to(myorders);
			                			
			                			continue;
			                			
			                		} catch (Exception ex) {
			                			//Error occurred in Selenium
			                			
			                			orderReport.setVendorOrdernumber(OrderNumber);
			                			orderReport.setURL(driver.getCurrentUrl());
			                			orderReport.setReason(ex.getMessage());
			                			orderReport.setLine(String.valueOf(j+1));
			                			
			                			orderReportFailList.add(orderReport);
			                			
			                			driver.navigate().to(myorders);
			                			
			                			continue;
			                		}
			                		
		                		}
	                		}
	                	}
                	}
                	
                	
            	}
            	
//            	FileOutputStream fileOut = new FileOutputStream(fileName);
//                workbook.write(fileOut);
//                fileOut.close();
        	}
        	
        	file.close();
            driver.close();
            driver.quit();
            
        } catch(Exception ioe) {
            ioe.printStackTrace();
        } finally {
            result.put("SUCCESS", orderReportList);
            result.put("FAIL", orderReportFailList);
            result.put("SHEET", new ArrayList<String>(Arrays.asList(sheetName)));
        }
        
        return result;
        
    }
	
	public void switchTab(String rootTab, WebDriver drv) {
		 for(String handle : drv.getWindowHandles()) {
             if (!handle.equals(rootTab)) {
            	 drv.switchTo().window(handle);
            	 drv.close();
             }
         }
	}
}

