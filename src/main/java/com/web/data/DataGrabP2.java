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
import com.web.util.AmazonFeeCal;
import com.web.util.ConstVal;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataGrabP2 {
	
	private AmazonOrderReport orderReport;
	private AmazonFeeCal amazonCalculate;
	
	public static String homepage = "https://www.aliexpress.com";
	
	public Map<String, List> doPlaceOrder(String fileName, P2SubmitData sbData)
    {
		String sheetName = sbData.getSheetName();
		String account =  sbData.getAccount();
		String password = sbData.getPassword();
		
		Double profit = 0.0;
		Map<String, List> result = new HashMap<String, List>();
		
		List<AmazonOrderReport> orderReportList = new ArrayList<AmazonOrderReport>();
		
		List<AmazonOrderReport> orderReportFailList = new ArrayList<AmazonOrderReport>();
		
		AmazonInventoryReport inventoryReport = new AmazonInventoryReport();
		amazonCalculate = new AmazonFeeCal();
		
		WebDriver driver = new ChromeDriver();
		
		driver.manage().window().maximize();
		
		//Check user password

        try {
        	
        	String productID_Trim2_SKU = StringUtils.EMPTY;
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
			    	                		productID_Trim2_SKU = cell.getStringCellValue();
			    	                		
			    	                		driver.navigate().to("https://www.aliexpress.com");
			    	                		
			    	                		//Input productID
			    	                		driver.findElement(By.xpath("//input[contains(@id,'search-key')]")).sendKeys(productID_Trim2_SKU);
			    	                		
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
//                FileOutputStream fileOut = new FileOutputStream(fileName);
//                wbs.write(fileOut);
//                fileOut.close();
        	} else {
        		file = new FileInputStream(new File(fileName));
    			
            	//Get the workbook instance for XLS file 
            	HSSFWorkbook workbook = new HSSFWorkbook(file);
            	
            	HSSFRow rowHS;
            	HSSFCell cellHS;
            	HSSFCell cellHS2;
            	
            	String productName = "";
            	String cellVal = "";
            	
            	driver.navigate().to(homepage);
            	
            	WebElement element;
            	
            	List<WebElement> popup = driver.findElements(By.xpath("//a[contains(@data-role,'layer-close')]"));
            	if (popup.size() > 0) {
            		popup.get(0).click();
            		Thread.sleep(5000);
            	}
            	
            	List<WebElement> elements;
            	
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
                	        i = i+2000;
                	        Thread.sleep(2000);
                	        if (i>30000){
                	            doTheLoop = false;
                	        }
                	        if (driver.getCurrentUrl().indexOf("login") < 0 ){
                	            doTheLoop = false;
                	        }      
    	            	}
            		}
            	}
            	outerloop:
            	for (int i=0; i < workbook.getNumberOfSheets(); i++) {
            	//for (int i=0; i < 1; i++) {
            		//Get first sheet from the workbook
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
				                		//Input productID
			                			element = (new WebDriverWait(driver, 10))
				                        		   .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@id,'search-key')]")));
			                			
			                			element.clear();
			                			element.sendKeys(productID_Trim2_SKU);
		    	                		
		    	                		//Click Search
			                			element = (new WebDriverWait(driver, 10))
				                        		   .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@class,'search-button')]")));
			                			element.click();
			                			
			                			//Check product exist
			                			List<WebElement> countProduct = driver.findElements(By.xpath("//strong[@class='search-count']"));
			                			
			                			if (countProduct.size() > 0) {
			                				throw new IllegalArgumentException("Product not found");
			                			}
			                			
			                			productName = driver.findElement(By.xpath("//h1[@class='product-name']")).getText();
			                			
			                			String totalStockQuantity = "";
			                			if (driver.findElements(By.id("j-sell-stock-num")).size() > 0) {
			                				totalStockQuantity = driver.findElement(By.id("j-sell-stock-num")).getText();
			                				
			                				totalStockQuantity = totalStockQuantity.replaceAll("[^\\d.]", "");
			                				
			                				if (quantity > Integer.valueOf(totalStockQuantity)) {
			                					//Quantity too much
					                			
					                			orderReport.setSku(productID_Trim2_SKU);
						                        
					                			orderReport.setURL(driver.getCurrentUrl());
					                			
					                			orderReport.setReason("Purchase quantity larger than Ali stock quantity");
					                			orderReport.setLine(String.valueOf(j+1));
					                			orderReportFailList.add(orderReport);
					                			
					                			continue;
			                				}
			                			}
			                			
			                			//Set quantity
			                			String js = "";
			                			js = "document.getElementById('j-p-quantity-input').value=" + quantity;
			                			if (driver instanceof JavascriptExecutor) {
			                			    ((JavascriptExecutor)driver).executeScript(js);
			                			} else {
			                			    throw new IllegalArgumentException("This driver does not support JavaScript!");
			                			}
			                			
			                			while (true) {
			                				try {
				                				//Buy Now
					                			element = (new WebDriverWait(driver, 10))
						                        		   .until(ExpectedConditions.elementToBeClickable(By.id("j-buy-now-btn")));
					                			
					                			element.click();
					                			
					                			break;
			                				} catch (Exception e) {
			                					JavascriptExecutor jsx = (JavascriptExecutor)driver;
			                		    	    jsx.executeScript("window.scrollBy(0,30)", "");
			                		    	    
			                					Thread.sleep(500);
			                				}
			                			}
			                			
//			                			if (driver.findElements(By.id("qp-shippingInfo-edit")).size() > 0) {
//			                    	    	driver.findElement(By.id("qp-shippingInfo-edit")).click();
//			                    	    }
			                			
			                			boolean doTheLoop = true;
			                    	    int h = 0;
			                    	    while (doTheLoop){ 
			                    	        h = h+200;
			                    	        Thread.sleep(1000);
			                    	        if (h>2000){
			                    	        	throw new IllegalArgumentException("Size or Type or Color required");
			                    	        }
			                    	        if (driver.findElements(By.xpath("//a[contains(@class,'sa-edit')]")).size() > 0 ){
			                    	            doTheLoop = false;
			                    	        }      
			        	            	}
			                			
			                    	    while (true) {
			                    	    	try {
			                    	    		//Add new address
					                			List<WebElement> ShippAddress = driver.findElements(By.xpath("//a[contains(@class,'sa-edit')]"));
					                			if (ShippAddress.size() >0) {
					                				ShippAddress.get(0).click();
					                				break;
					                			}
			                    	    	} catch (Exception e) {
			                    	    		JavascriptExecutor jsx = (JavascriptExecutor)driver;
			                    	    	    jsx.executeScript("window.scrollBy(0,30)", "");
			                    	    	    
			                    	    		Thread.sleep(200);
			                    	    	}
			                    	    }
			                    	    
			                			driver.findElement(By.name("contactPerson")).clear();
			                			
			                			//ContactName
			                			cellHS = rowHS.getCell(16);
				                		
				                		if (cellHS == null) {
				                			throw new IllegalArgumentException("ContactName blank");
				                		}
				                		
				                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
				                		cellVal = "";
				                		cellVal = cellHS.getStringCellValue();
				                		
				                		if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
				                			driver.findElement(By.name("contactPerson")).sendKeys(cellVal);
				                			
			                				driver.findElement(By.name("contactPerson")).sendKeys(cellVal);
				                			
				                			for (int idx=0;idx<20;idx++) {
				                				if (driver.findElement(By.name("contactPerson")).getAttribute("value").equals(cellVal)) {
				                					break;
				                				} else {
				                					driver.findElement(By.name("contactPerson")).clear();
				                					driver.findElement(By.name("contactPerson")).sendKeys(cellVal);
				                					Thread.sleep(2000);
				                				}
				                			}
				                			
//				                			h = 0;
//				                			while (driver.findElement(By.name("contactPerson")).getAttribute("value").isEmpty()) {
//				                    	        driver.findElement(By.name("contactPerson")).sendKeys(cellVal);
//				                    	        h = h+2000;
//				                    	        if (h>10000){
//				                    	        	throw new IllegalArgumentException("ContactName not blank but can not set");
//				                    	        }
//				                    	        Thread.sleep(2000);
//				                			}
				                		} else {
				                			throw new IllegalArgumentException("ContactName include non English characters : " +cellVal );
				                		}

			                			//Country/Region
				                		
			                			cellHS = rowHS.getCell(23);
				                		
				                		if (cellHS == null) {
				                			throw new IllegalArgumentException("ship-country blank");
				                		}
				                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
				                		
				                		if (cellHS.getStringCellValue().isEmpty()) {
				                			throw new IllegalArgumentException("ship-country blank");
				                		} else if (!cellHS.getStringCellValue().equals("US")) {
				                			throw new IllegalArgumentException("ship-country isn't US");
				                		}
				                		
				                		Select select = new Select(driver.findElement(By.xpath("//select[contains(@name,'country')]")));
				                		select.selectByValue("US");
			                			
			                			//Street Address
				                		cellHS = rowHS.getCell(17);
				                		driver.findElement(By.name("address")).clear();
				                		driver.findElement(By.name("address2")).clear();
				                		if (cellHS == null) {
				                			throw new IllegalArgumentException("ship-address-1 blank");
				                		}
				                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
				                		if (cellHS.getStringCellValue().isEmpty()) {
				                			throw new IllegalArgumentException("ship-address-1 blank");
				                		}
				                		
				                		cellVal = "";
				                		cellVal = cellHS.getStringCellValue();
				                		
				                		if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
				                			driver.findElement(By.name("address")).sendKeys(cellVal);
				                			h = 0;
				                			while (driver.findElement(By.name("address")).getAttribute("value").isEmpty()) {
				                    	        driver.findElement(By.name("address")).sendKeys(cellVal);
				                    	        h = h+2000;
				                    	        if (h>10000){
				                    	        	throw new IllegalArgumentException("ship-address-1 not blank but can not set");
				                    	        }
				                    	        Thread.sleep(2000);
				                			}
				                		} else {
				                			throw new IllegalArgumentException("Address include non English characters: " +cellVal );
				                		}
				                		
				                		cellHS = rowHS.getCell(18);
				                		
				                		if (cellHS != null) {
				                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
				                			if (!cellHS.getStringCellValue().isEmpty()) {
				                				cellVal = "";
						                		cellVal = cellHS.getStringCellValue();
				                				if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
				                					driver.findElement(By.name("address2")).sendKeys(cellVal);
						                		} else {
						                			throw new IllegalArgumentException("Address2 include non English characters: " +cellVal );
						                		}
					                			
					                		}
				                		}
				                		cellHS = rowHS.getCell(19);
				                		
				                		if (cellHS != null) {
				                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
				                			driver.findElement(By.name("address2")).sendKeys(" ");
				                			cellVal = "";
					                		cellVal = cellHS.getStringCellValue();
				                			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
				                				driver.findElement(By.name("address2")).sendKeys(cellVal);
					                		} else {
					                			throw new IllegalArgumentException("Address2 include non English characters: " +cellVal );
					                		}
				                		}
				                		
			                			//State Province
				                		cellHS = rowHS.getCell(21);
				                		
				                		if (cellHS == null) {
				                			throw new IllegalArgumentException("ship-state blank");
				                		}
				                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
			                			
				                		String stateFullName = "";
				                		String tmpState = "";
				                		String firstChar, secondChar = "";
				                		
			                			if (!ConstVal.stateMap.containsKey(cellHS.getStringCellValue())) {
			                				
			                				tmpState = cellHS.getStringCellValue();
			                				
			                				if (tmpState.contains(" ")) {
			                					
			                					if (tmpState.split(" ").length == 2) {
			                					
				                					tmpState = tmpState.toLowerCase();
				                					
				                					firstChar = tmpState.split(" ")[0];
				                					
				                					secondChar = tmpState.split(" ")[1];
				                					
				                					tmpState = firstChar.substring(0, 1).toUpperCase() + firstChar.substring(1) + " " + secondChar.substring(0, 1).toUpperCase() + secondChar.substring(1);
			                					}
			                				} else {
			                					tmpState = tmpState.substring(0, 1).toUpperCase() + tmpState.substring(1).toLowerCase();
			                				}
			                				
			                				stateFullName = tmpState;
			                				
			                			} else {
			                				stateFullName = ConstVal.stateMap.get(cellHS.getStringCellValue());
			                			}
			                			
			                			try {
			                				driver.findElement(By.xpath("//div[contains(@class,'sa-province-group')]/div[1]/select/option[@value='" +stateFullName+ "']")).click();
			                				select = new Select(driver.findElement(By.xpath("//div[contains(@class,'sa-province-group')]/div[1]/select")));
			                			} catch (Exception e) {
			                				try {
						                		select.selectByVisibleText(stateFullName);
				                			} catch (Exception ex) {
				                				throw new IllegalArgumentException("ship-state Invalid");
				                			}
			                			}
			                			
			                			if (!select.getFirstSelectedOption().getText().equals(stateFullName)) {
			                				throw new IllegalArgumentException("ship-state select fail");
			                			}
				                		
			                			//City
				                		cellHS = rowHS.getCell(20);
				                		driver.findElement(By.name("city")).clear();
				                		
				                		if (cellHS == null) {
				                			throw new IllegalArgumentException("ship-city blank");
				                		}
				                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
				                		
				                		cellVal = "";
				                		cellVal = cellHS.getStringCellValue();
			                			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
			                				driver.findElement(By.name("city")).sendKeys(cellVal);
			                				h = 0;
				                			while (driver.findElement(By.name("city")).getAttribute("value").isEmpty()) {
				                    	        driver.findElement(By.name("city")).sendKeys(cellVal);
				                    	        h = h+2000;
				                    	        if (h>10000){
				                    	        	throw new IllegalArgumentException("city not blank but can not set");
				                    	        }
				                    	        Thread.sleep(2000);
				                			}
				                		} else {
				                			throw new IllegalArgumentException("City include non English characters: " +cellVal );
				                		}
				                		
			                			//Zip
				                		HSSFCell cellHSZip;
				                		
				                		cellHSZip = rowHS.getCell(22);
				                		
				                		String zipCode = "";
				                		driver.findElement(By.name("zip")).clear();
				                		
				                		if (cellHSZip == null) {
				                			//Zip not exist
				                			driver.findElement(By.xpath("//label[contains(@class,'sa-no-zip-code')]")).click();	
				                			//throw new IllegalArgumentException("ship-postal-code blank");
				                		} else {
				                			cellHSZip.setCellType(Cell.CELL_TYPE_STRING);
				                			if (cellHSZip.getStringCellValue().isEmpty()) {
					                			//Zip not exist
					                			driver.findElement(By.xpath("//label[contains(@class,'sa-no-zip-code')]")).click();	
					                		} else {
					                			
					                			cellHSZip.setCellType(Cell.CELL_TYPE_STRING);
				                				zipCode = cellHSZip.getStringCellValue();
				                				
				                				cellVal = "";
						                		cellVal = cellHS.getStringCellValue();
					                			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
					                				driver.findElement(By.name("zip")).sendKeys(zipCode);
					                				h = 0;
						                			while (driver.findElement(By.name("zip")).getAttribute("value").isEmpty()) {
						                    	        driver.findElement(By.name("zip")).sendKeys(cellVal);
						                    	        h = h+2000;
						                    	        if (h>10000){
						                    	        	throw new IllegalArgumentException("zip not blank but can not set");
						                    	        }
						                    	        Thread.sleep(2000);
						                			}
						                		} else {
						                			throw new IllegalArgumentException("Zip Code include non English characters: " +cellVal );
						                		}
					                		}
				                		} 
				                		
			                			//Tel
				                		HSSFCell cellHSTel;
				                		cellHSTel = rowHS.getCell(9);
				                		if (cellHSTel == null) {
				                			throw new IllegalArgumentException("buyer-phone-number blank");
				                		}
				                		
				                		String tel = "";
				                		driver.findElement(By.name("phoneCountry")).clear();
				                		driver.findElement(By.name("phoneCountry")).sendKeys("1");
				                		//driver.findElement(By.name("phoneArea")).clear();
				                		//driver.findElement(By.name("phoneNumber")).clear();
				                		driver.findElement(By.name("mobileNo")).clear();
				                		
				                		cellHSTel.setCellType(Cell.CELL_TYPE_STRING);
			                			tel = cellHSTel.getStringCellValue();
			                			if (!tel.isEmpty()) {
			                				tel= tel.replace("-", "");
			                				tel= tel.replace("(", "");
			                				tel= tel.replace(")", "");
			                				tel= tel.replace(" ", "");
			                				tel= tel.replace("+1", "");
			                			}
			                			
			                			
			                			driver.findElement(By.name("mobileNo")).sendKeys(tel);
			                			h = 0;
			                			while (driver.findElement(By.name("mobileNo")).getAttribute("value").isEmpty()) {
			                    	        driver.findElement(By.name("mobileNo")).sendKeys(cellVal);
			                    	        h = h+2000;
			                    	        if (h>10000){
			                    	        	throw new IllegalArgumentException("mobileNo not blank but can not set");
			                    	        }
			                    	        Thread.sleep(1000);
			                			}
				                		
//				                		if (tel.indexOf("-") > 0) {
//				                			if (tel.split("-").length < 3) {
//				                				throw new IllegalArgumentException("buyer-phone-number format incorrect");
//				                			} else {
//				                				driver.findElement(By.name("phoneCountry")).sendKeys(tel.split("-")[0]);
//				                				driver.findElement(By.name("phoneArea")).sendKeys(tel.split("-")[1]);
//				                				driver.findElement(By.name("phoneNumber")).sendKeys(tel.split("-")[2]);
//				                			}
//				                		} else {
//				                			driver.findElement(By.name("mobileNo")).sendKeys(tel);
//				                		}
				                		
				                		//Save this address
			                			while (true) {
			                				try {
			                					List<WebElement> SaveButton = driver.findElements(By.xpath("//div[contains(@class,'sa-btn-group')]/a[1]"));
						                		if (SaveButton.size() > 0) {
						                			for (int n=0;n<SaveButton.size(); n++) {
						                				if (SaveButton.get(n).getText().indexOf("Save") >= 0) {
						                					SaveButton.get(n).click();
						                					break;
						                				}
						                			}
						                		}
						                		break;
			                				} catch (Exception e) {
			                					JavascriptExecutor jsx = (JavascriptExecutor)driver;
			                		    	    jsx.executeScript("window.scrollBy(0,30)", "");
			                		    	    
			                		    	    Thread.sleep(200);
			                				}
			                			}
			                			
				                		//Select ePacket
				                		doTheLoop = true;
			                    	    h = 0;
			                    	    while (doTheLoop){ 
			                    	        h = h+2000;
			                    	        Thread.sleep(2000);
			                    	        if (h>30000){
			                    	        	doTheLoop = false;
			                    	        }
			                    	        if (driver.findElements(By.xpath("//div[contains(@class,'product-shipping-select')]")).size() > 0 ){
			                    	            doTheLoop = false;
			                    	        }      
			        	            	}
				                		
			                    	    while (true)
			                    	    {
			                    	    	try {
				                    	    	driver.findElement(By.xpath("//div[contains(@class,'product-shipping-select')]")).click();
				                    	    	break;
				                    	    } catch (Exception wde) {
				                    	    	
				                    	    	JavascriptExecutor jsx = (JavascriptExecutor)driver;
			                		    	    jsx.executeScript("window.scrollBy(0,30)", "");
			                		    	    
			                		    	    Thread.sleep(200);
				                    	    	
				                    	    	//throw new IllegalArgumentException("Can not complete shipping address form. Otherwise product out of stock.");
				                    	    }
			                    	    }
				                		
				                		//driver.findElement(By.xpath("//div[contains(@class,'product-shipping-select')]")).click();
				                		
			                    	    while (true) {
			                    	    	try {
			                    	    		List<WebElement> shipSelect = driver.findElements(By.xpath("//div[contains(@class,'shipping-radio')]/input"));
						                		
						                		if (shipSelect.size() > 0) {
						                			for (int k=0; k < shipSelect.size(); k++) {
						                				
						                				if (shipSelect.get(k).getAttribute("value").equals("EMS_ZX_ZX_US")) {
						                					shipSelect.get(k).click();
						                					break;
						                				}
						                			}
						                		}
						                		break;
			                    	    	} catch (Exception e) {
			                    	    		JavascriptExecutor jsx = (JavascriptExecutor)driver;
			                		    	    jsx.executeScript("window.scrollBy(0,10)", "");
			                		    	    
			                		    	    Thread.sleep(200);
			                    	    	}
			                    	    }
				                		
				                		//OK button
				                		try {
				                			driver.findElement(By.xpath("//div[contains(@class,'inner-new')]//input[contains(@class,'btn-ok')]")).click();
				                		} catch (Exception e) {
				                			js = "document.getElementsByClassName('btn-ok')[0].click();";
					                		
					                		if (driver instanceof JavascriptExecutor) {
				                			    ((JavascriptExecutor)driver).executeScript(js);
				                			} else {
				                			    throw new IllegalStateException("This driver does not support  JavaScript!");
				                			}
				                		}
				                		
				                		inventoryReport = new AmazonInventoryReport();
				                        //calculate profit for current product
				                        
				                		String originalPrice = driver.findElement(By.xpath("//div[@class='orderPrice-detail multiple-lan util-clearfix']/p[1]")).getText();
				                		String shipFee = driver.findElement(By.xpath("//div[@class='orderPrice-detail multiple-lan util-clearfix']/p[2]")).getText();
				                		
				                        inventoryReport.setCostFromAE(originalPrice.replaceAll("[^\\d.]", ""));
				                        inventoryReport.setShippingCostEpacket(shipFee.replaceAll("[^\\d.]", ""));
				                		
				                		//Captcha blank
				                		if (driver.findElements(By.id("captcha-input")).size() > 0) {
				                			
				                			while (true) {
				                				try {
				                					driver.findElement(By.id("flush-catpcha-image")).click();
				                					break;
				                				} catch (Exception e) {
				                					JavascriptExecutor jsx = (JavascriptExecutor)driver;
				                		    	    jsx.executeScript("window.scrollBy(0,30)", "");
				                					Thread.sleep(500);
				                				}
				                			}
				                			
				                			Thread.sleep(1000);
				                			
				                			driver.findElement(By.id("captcha-input")).sendKeys("");
				                			doTheLoop = true;
				                    	    h = 0;
				                    	    while (doTheLoop) { 
				                    	        h = h+2000;
				                    	        Thread.sleep(2000);
				                    	        if (h>30000){
				                    	        	throw new IllegalArgumentException("Captcha require");
				                    	        }
				                    	        if (!driver.findElement(By.id("captcha-input")).getAttribute("value").isEmpty()){
				                    	            doTheLoop = false;
				                    	        } 
				        	            	}
				                    	    
				                    	    //Capcha wrong
				                    	    String style;
				                    	    doTheLoop = true;
				                    	    h = 0;
				                    	    while (doTheLoop){ 
				                    	        h = h+2000;
				                    	        Thread.sleep(2000);
				                    	        if (h>30000){
				                    	        	throw new IllegalArgumentException("Captcha require");
				                    	        }
				                    	        
				                    	        while (true) {
					                				try {
					                					//Button Place Order
								                		driver.findElement(By.id("place-order-btn")).click();
					                					break;
					                				} catch (Exception e) {
					                					JavascriptExecutor jsx = (JavascriptExecutor)driver;
					                		    	    jsx.executeScript("window.scrollBy(0,30)", "");
					                		    	    
					                					Thread.sleep(500);
					                				}
					                			}
						                		
				                    	        //captcha-input-message
						                		if (driver.findElements(By.id("captcha-input-message")).size() < 1) {
						                			 doTheLoop = false;
						                		} else {
						                			style = driver.findElement(By.id("captcha-input-message")).getAttribute("style");
					                    	        
					                    	        if (style.indexOf("display: block") < 0) {
					                    	            doTheLoop = false;
					                    	        } else {
					                    	        	driver.findElement(By.id("captcha-input")).sendKeys("");
					                    	        }
						                		}
				        	            	}
				                		} else {
				                			//Button Place Order
				                			while (true) {
				                				try {
				                					//Button Place Order
							                		driver.findElement(By.id("place-order-btn")).click();
				                					break;
				                				} catch (Exception e) {
				                					JavascriptExecutor jsx = (JavascriptExecutor)driver;
				                		    	    jsx.executeScript("window.scrollBy(0,30)", "");
				                		    	    
				                					Thread.sleep(500);
				                				}
				                			}
				                		}
				                		
				                		if (driver.getCurrentUrl().indexOf("icashier.alipay.com") > 0) {
				                			if (driver.findElements(By.name("cardNo")).size() > 0) {
					                			driver.findElement(By.name("cardNo")).clear();
					                			driver.findElement(By.name("expiryMonth")).clear();
					                			driver.findElement(By.name("expiryYear")).clear();
					                			
					                			driver.findElement(By.name("firstName")).clear();
					                			driver.findElement(By.name("lastName")).clear();
					                			
					                			//CVS
					                			//name cvv2
					                			// Hidden
					                			js = "";
					                			js = "document.getElementsByName('cvv2')[0].style.visibility='hidden';document.getElementsByName('cvv2')[0].value = " + sbData.getCvs();
					                			if (driver instanceof JavascriptExecutor) {
					                			    ((JavascriptExecutor)driver).executeScript(js);
					                			} else {
					                			    throw new IllegalStateException("This driver does not support JavaScript!");
					                			}
					                			
					                			driver.findElement(By.name("firstName")).sendKeys(sbData.getCardHolderName1());
					                			driver.findElement(By.name("lastName")).sendKeys(sbData.getCardHolderName2());
					                			
					                			driver.findElement(By.xpath("//a[contains(@class,'create-new')]")).click();
					                			
					                			driver.findElement(By.id("address1")).clear();
					                			driver.findElement(By.id("address2")).clear();
					                			driver.findElement(By.id("city")).clear();
					                			
					                			driver.findElement(By.id("postCode")).clear();
					                			
					                			driver.findElement(By.name("cardNo")).sendKeys(sbData.getCardNo());
					                			driver.findElement(By.name("expiryMonth")).sendKeys(sbData.getExpMonth());
					                			driver.findElement(By.name("expiryYear")).sendKeys(sbData.getExpYear());
					                			
					                			//chon country US
					                			select = new Select(driver.findElement(By.id("country")));
						                		//select.deselectAll();
						                		//select.selectByVisibleText("Value1");
						                		select.selectByValue(sbData.getCountry());
						                		
					                			driver.findElement(By.id("address1")).sendKeys(sbData.getAdd1());
					                			driver.findElement(By.id("address2")).sendKeys(sbData.getAdd2());
					                			driver.findElement(By.id("city")).sendKeys(sbData.getCity());
					                			
					                			//chon country State
					                			
					                			
					                			if (!ConstVal.stateMap.containsKey(sbData.getState())) {
					                				stateFullName = sbData.getState();
					                			} else {
					                				stateFullName = ConstVal.stateMap.get(sbData.getState());
					                			}
					                			
					                			try {
					                				select = new Select(driver.findElement(By.id("state")));
					                				select.selectByValue(stateFullName);
					                			} catch (Exception ex) {
					                				throw new IllegalArgumentException("Billing address state invalid. Stop");
					                			}
					                			
						                		driver.findElement(By.id("postCode")).sendKeys(sbData.getPostcode());
						                		
						                		driver.findElement(By.id("j-paynow")).click();
				                			}
				                		}
				                		
				                		doTheLoop = true;
			                    	    h = 0;
			                    	    while (doTheLoop){ 
			                    	        h = h+200;
			                    	        Thread.sleep(200);
			                    	        if (h>2000){
			                    	        	throw new IllegalArgumentException("Problem with Credit Card");
			                    	        }
			                    	        if (driver.getCurrentUrl().indexOf("icashier.alipay.com") < 0 ){
			                    	            doTheLoop = false;
			                    	        }      
			        	            	}
			                    	    
			                    	    List<WebElement> headerList;
			                    	    headerList = driver.findElements(By.xpath("//h3[@class='ui-feedback-header']"));
			                    	    
			                    	    if (headerList.size() > 0) {
			                    	    	if ((headerList.get(0).getText().indexOf("Sorry") >= 0 && headerList.get(0).getText().indexOf("failed") >= 0)) {
			                    	    		throw new IllegalArgumentException("Order Failed. " + headerList.get(0).getText());
			                    	    	} else if (headerList.get(0).getText().indexOf("Payment failed") >= 0) {
			                    	    		throw new IllegalArgumentException("Order Failed. " + driver.findElement(By.xpath("//div[@id='pay-fail-body']")).getText());
			                    	    	}
			                    	    }
			                    	    
			                    	    doTheLoop = true;
			                    	    h = 0;
			                    	    while (doTheLoop){ 
			                    	        h = h+200;
			                    	        Thread.sleep(200);
			                    	        if (h>30000){
			                    	        	throw new IllegalArgumentException("Can not go back to obtain Order Number");
			                    	        }
			                    	        if (driver.findElements(By.xpath("//div[@class='ui-feedback-body']/p[2]/a[1]")).size() > 0 ){
			                    	            doTheLoop = false;
			                    	        }
			        	            	}
			                    	    
			                    	    //Goto Aliexpress Order List
			                    	    driver.findElement(By.xpath("//div[@class='ui-feedback-body']/p[2]/a[1]")).click();
			                    	    
			                    	    String orderNo = "";
			                    	    String totalPrice = "";
			                    	    //Get order number
			                    	    if (driver.findElements(ByClassName.className("order-info")).size() > 0) {
			                    	    	if (driver.findElements(By.xpath("//td[@class='order-info']//p[@class='first-row']//span[@class='info-body']")).size() > 0) {
			                    	    		orderNo = driver.findElements(By.xpath("//td[@class='order-info']//p[@class='first-row']//span[@class='info-body']")).get(0).getText();
			                    	    	} else {
			                    	    		throw new IllegalArgumentException("Can not obtain AliExpress Order Number");
			                    	    	}
			                    	    	if (driver.findElements(By.xpath("//p[@class='amount-num']")).size() > 0) {
			                    	    		totalPrice = driver.findElements(By.xpath("//p[@class='amount-num']")).get(0).getText();
			                    	    	} else {
			                    	    		throw new IllegalArgumentException("Can not obtain AliExpress Total Price");
			                    	    	}
			                    	    } else {
			                    	    	throw new IllegalArgumentException("Can not obtain AliExpress Order Number");
			                    	    }
			                    	    
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
					                    	    	case 16:
					                    	    		orderReport.setRecipientName(cellHS2.getStringCellValue());
					                    	    		break;
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
			                    	    
			                    	    orderReport.setProductName(productName);
			                    	    orderReport.setTotalPruchasePrice(totalPrice);
			                    	    orderReport.setTotalCustomerSpendonOrder("");
			                    	    orderReport.setMarginDollar("");
			                    	    orderReport.setMarginPercent("");
			                    	    orderReport.setVendorOrdernumber(orderNo);
			                    	    orderReport.setTrackingNumber("");
			                			
				                        orderReportList.add(orderReport);
				                        
				                        //Calculate profit
				                        inventoryReport = amazonCalculate.priceCalculate(inventoryReport);
				                        
				                        profit = profit + (Double.valueOf(inventoryReport.getAbsoluteListPrice()) - 
				                        		Double.valueOf(inventoryReport.getCostFromAE()) - 
				                        		Double.valueOf(inventoryReport.getShippingCostEpacket()) -
				                        		Double.valueOf(inventoryReport.getAmazonFee()));
				                		
			                		} catch (IllegalArgumentException ex) {
			                			
			                			if (ex.getMessage().indexOf("Stop") >= 0) {
			                				orderReport.setSku(productID_Trim2_SKU);
			                				orderReport.setProductName(productName);
				                			orderReport.setURL(driver.getCurrentUrl());
				                			
				                			orderReport.setReason(ex.getMessage());
				                			orderReport.setLine(String.valueOf(j+1));
				                			
				                			orderReportFailList.add(orderReport);
				                			
				                			break outerloop;
				                			
			                			}
			                			
			                			orderReport.setSku(productID_Trim2_SKU);
			                			orderReport.setProductName(productName);
			                			orderReport.setURL(driver.getCurrentUrl());
			                			
			                			orderReport.setReason(ex.getMessage());
			                			orderReport.setLine(String.valueOf(j+1));
			                			
			                			orderReportFailList.add(orderReport);
			                			
			                			driver.navigate().to(homepage);
			                			
			                			continue;
			                			
			                		} catch (Exception ex) {
			                			//Error occurred in Selenium
			                			
			                			orderReport.setSku(productID_Trim2_SKU);
			                			orderReport.setProductName(productName);
			                			orderReport.setURL(driver.getCurrentUrl());
			                			
			                			orderReport.setReason("Selenium error occurred" + ex.getMessage());
			                			orderReport.setLine(String.valueOf(j+1));
			                			
			                			orderReportFailList.add(orderReport);
			                			
			                			driver.navigate().to(homepage);
			                			
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
            result.put("PROFIT", new ArrayList<String>(Arrays.asList(profit.toString())));
            result.put("SHEET", new ArrayList<String>(Arrays.asList(sheetName)));
        }
        
        return result;
        
    }
}

