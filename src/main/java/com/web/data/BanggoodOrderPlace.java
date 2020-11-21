package com.web.data;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import org.openqa.selenium.TimeoutException;

import com.web.entity.*;
import com.web.util.ConstVal;
import com.web.util.PageFunction;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BanggoodOrderPlace {
	
	private AmazonOrderReport orderReport;
	
	public static String homepage = "http://www.banggood.com";
	public static String loginPage = "https://www.banggood.com/login.html";
	
	public Map<String, List> doPlaceOrder(String fileName, P2SubmitData sbData)
    {
		String sheetName = sbData.getSheetName();
		String account =  sbData.getAccount();
		String password = sbData.getPassword();
		
		Map<String, List> result = new HashMap<String, List>();
		
		List<AmazonOrderReport> orderReportList = new ArrayList<AmazonOrderReport>();
		
		List<AmazonOrderReport> orderReportFailList = new ArrayList<AmazonOrderReport>();
		
		WebDriver driver = new ChromeDriver();
		
		driver.manage().window().maximize();
		
        try {
        	
        	String productID_Trim2_SKU = StringUtils.EMPTY;
        	int quantity = 0;
            String price;
        	char extention;
        	FileInputStream file;
        	
        	file = new FileInputStream(new File(fileName));
        	extention =  fileName.charAt(fileName.length() - 1);
        	if (extention == 'x') {
        		
        	} else {
        		file = new FileInputStream(new File(fileName));
    			
            	//Get the workbook instance for XLS file 
            	HSSFWorkbook workbook = new HSSFWorkbook(file);
            	
            	HSSFRow rowHS;
            	HSSFCell cellHS;
            	HSSFCell cellHS2;
            	
            	String productName = "";
            	String cellVal = "";
            	
            	driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
            	
            	try {
            		driver.navigate().to(loginPage);
            	    driver.findElement(By.xpath("//form[@id='minilogin']//input[@name='email']"));
            	} catch (TimeoutException e) {
            		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
            		  	driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            	}
            	
            	driver.findElement(By.xpath("//form[@id='minilogin']//input[@name='email']")).clear();
            	driver.findElement(By.xpath("//form[@id='minilogin']//input[@name='email']")).sendKeys(account);
            	
            	driver.findElement(By.xpath("//form[@id='minilogin']//input[@name='pwd']")).clear();
            	driver.findElement(By.xpath("//form[@id='minilogin']//input[@name='pwd']")).sendKeys(password);
            	
            	if (driver.findElements(By.id("login_image_code")).size() > 0) {
        			
        			driver.findElement(By.id("login_image_code")).sendKeys("");
        			boolean doTheLoop = true;
            	    int h = 0;
            	    while (doTheLoop) { 
            	        h = h+2000;
            	        Thread.sleep(5000);
            	        if (h>30000){
            	        	throw new IllegalArgumentException("Captcha require");
            	        }
            	        if (!driver.findElement(By.id("login_image_code")).getAttribute("value").isEmpty()){
            	            doTheLoop = false;
            	        }
	            	}
            	    
            	    //Capcha wrong
            	    String style;
            	    doTheLoop = true;
            	    h = 0;
            	    while (doTheLoop){ 
            	        h = h+2000;
            	        Thread.sleep(5000);
            	        if (h>30000){
            	        	throw new IllegalArgumentException("Captcha require");
            	        }
            	        
            	        //Button Login
            	        
            	        if (driver.findElements(By.className("middle_signIn_button_20161124")).size() > 0) {
            	        	driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
                        	
                        	try {
                        		driver.findElement(By.className("middle_signIn_button_20161124")).click();
                        	} catch (TimeoutException e) {
                        		driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
                        	} finally {
                        		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
                        	}
            	        	
            	        	//driver.findElement(By.className("middle_signIn_button_20161124")).click();
            	        }
            	        
            	        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
                    	
                    	try {
                    		//captcha-input-message
                    		if (!driver.getCurrentUrl().contains("login")) {
                    			 doTheLoop = false;
                    		}
                    	} catch (TimeoutException e) {
                    		driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
                    		doTheLoop = false;
                    	} finally {
                    		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
                    	}
	            	}
        		} else {
        			
        			driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
                	
                	try {
                		driver.findElement(By.className("middle_signIn_button_20161124")).click();
                	} catch (TimeoutException e) {
                		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
                	} finally {
                		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
                	}
        			
        			//Button Login
        			//driver.findElement(By.className("middle_signIn_button_20161124")).click();
        		}
            	
            	
            	WebElement element;
            	List<WebElement> elements;
            	String js = "";
            	
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
			                			WebDriverWait wait = new WebDriverWait(driver, 15);
			                			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='keywords']")));
			                			
			                			//Input productID
			                			driver.findElement(By.xpath("//input[@name='keywords']")).clear();
			                			driver.findElement(By.xpath("//input[@name='keywords']")).sendKeys(productID_Trim2_SKU);
		    	                		
			                			driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
			                        	
			                        	try {
			                        		//Click Search
				                			driver.findElement(By.xpath("//input[@type='submit']")).click();
			                        	} catch (TimeoutException e) {
			                        		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
			                        	} finally {
			                        		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			                        	}
			                			
			                			//Click to product
			                			
			                			List<WebElement> listProduct = driver.findElements(By.xpath("//ul[@class='goodlist_1 ']/li/span/a/img"));
			                			
			                			if (listProduct.size() < 1) {
			                				//Product not found
				                			
				                			orderReport.setSku(productID_Trim2_SKU);
				                			
				                			orderReport.setURL(driver.getCurrentUrl());
				                			
				                			orderReport.setReason("Product not found");
				                			orderReport.setLine(String.valueOf(j+1));
				                			orderReportFailList.add(orderReport);
				                			driver.navigate().to(homepage);
				                			continue;
			                			}
			                			
			                			//Click to first image
			                			//listProduct.get(0).click();
			                			driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
			                        	
			                        	try {
			                        		//Click to first image
				                			listProduct.get(0).click();
			                        	} catch (TimeoutException e) {
			                        		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
			                        	} finally {
			                        		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			                        	}
			                			
			                			price = driver.findElement(By.className("now")).getText();
			                			
			                			//Button order click
			                			driver.findElement(By.className("buynow")).click();
			                			
			                			//PageFunction.waitForLoad(driver);
			                			boolean doTheLoop = true;
			                    	    int h = 0;
			                    	    while (doTheLoop) {
			                    	        h = h+1;
			                    	        Thread.sleep(1000);
			                    	        if (h>3){
			                    	        	 throw new IllegalArgumentException("This product require size or color select"); 
			                    	        }
			                    	        if (driver.getCurrentUrl().indexOf("shopping_cart") > 0 ){
			                    	            doTheLoop = false;
			                    	        }      
			        	            	}
			                    	    
			                    	    //Click to selectbox Shipment
			                    	    
			                    	    //$(".td_shipping span strong").trigger("click")
			                    	    if (!driver.findElement(By.xpath("//div[@class='td_shipping']/span/strong")).getText().contains("Standard Shipping")) {
				                    	    driver.findElement(By.xpath("//div[@class='td_shipping']/span/strong")).click();
				                    	    
				                    	    List<WebElement> elementsShip = driver.findElements(By.xpath("//span[@class='td_shipping_list']/p"));
				                    	    String shipmentName = "";
				                    	    int tmpI = 0;
				                    	    js = "";
				                    	    for (int m=0; m < elementsShip.size(); m++) {
				                    	    	shipmentName = "";
					                    	    //Check standard shipping option exist
					                    	    shipmentName = elementsShip.get(m).getAttribute("innerHTML");
					                    	    
					                    	    if (shipmentName.indexOf("Standard Shipping") >= 0) {
					                    	    	tmpI = m + 1;
					                    	    	js = "$('.td_shipping_list p:nth-child(" + tmpI + ")').trigger('click')";
					                    	    	break;
					                    	    }
				                    	    }
				                    	    if (shipmentName.indexOf("Standard Shipping") < 0) {
				                    	    	orderReport.setSku(productID_Trim2_SKU);
					                			
					                			orderReport.setURL(driver.getCurrentUrl());
					                			
					                			orderReport.setReason("Standard Shipping unavailable");
					                			orderReport.setLine(String.valueOf(j+1));
					                			orderReportFailList.add(orderReport);
					                			
					                			driver.findElement(By.xpath("//span[@class='opt_remove scartremove']")).click();
					                			Thread.sleep(500);
					                			driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
					                        	
					                        	try {
					                        		driver.findElement(By.xpath("//span[@class='button_yes']")).click();
					                        	} catch (TimeoutException e) {
					                        		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
					                        	} finally {
					                        		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
					                        	}
						                    	    
					                			driver.navigate().to(homepage);
					                			continue;
				                    	    }
				                    	    
				                    	    Thread.sleep(1000);
				                    	    
				                    	    String pDecide = "";
				                    	    pDecide = "//span[@class='td_shipping_list']/p[" + tmpI + "]";
				                    	    driver.findElement(By.xpath(pDecide)).click();
			                    	    }
			                    	    
			                    	    //Display list shipment
			                    	    Thread.sleep(500);
			                    	    if (driver.findElements(By.xpath("//span[@class='tracking_info']/ol/li[2]")).size() < 1) {
			                    	    	orderReport.setSku(productID_Trim2_SKU);
				                			
				                			orderReport.setURL(driver.getCurrentUrl());
				                			
				                			orderReport.setReason("Tracking id unavailable");
				                			orderReport.setLine(String.valueOf(j+1));
				                			orderReportFailList.add(orderReport);
				                			Thread.sleep(1000);
				                			driver.findElement(By.xpath("//span[@class='opt_remove scartremove']")).click();
				                			Thread.sleep(500);
				                			driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
				                        	try {
				                        		driver.findElement(By.xpath("//span[@class='button_yes']")).click();
				                        	} catch (TimeoutException e) {
				                        		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
				                        	} finally {
				                        		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
				                        	}
				                			driver.navigate().to(homepage);
				                			continue;
			                    	    }
			                    	    
			                    	    
			                    	    //Edit address link click
			                    	    
			                    	    driver.findElement(By.className("scartaddress_edit")).click();
			                    	    
			                    	    
			                    	    //First Name
			                			driver.findElement(By.id("c_entry_firstname")).clear();
			                			driver.findElement(By.id("c_entry_lastname")).clear();
			                			
			                			//ContactName
			                			cellHS = rowHS.getCell(16);
				                		
				                		if (cellHS == null) {
				                			throw new IllegalArgumentException("ContactName blank");
				                		}
				                		
				                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
				                		cellVal = "";
				                		cellVal = cellHS.getStringCellValue();
				                		
				                		String firstName = "";
				                		String lastName = "";
				                		
		                				if (cellVal.contains(" ")) {
		                					firstName = cellVal.split(" ")[0];
		                					lastName = cellVal.replace(firstName, "");
		                					if (lastName.trim().isEmpty()) {
		                						lastName = firstName;
		                					}
		                				} else {
		                					lastName = firstName;
		                				}
				                		
				                		if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
				                			driver.findElement(By.id("c_entry_firstname")).sendKeys(firstName);
				                			h = 0;
				                			while (driver.findElement(By.id("c_entry_firstname")).getAttribute("value").isEmpty()) {
				                				driver.findElement(By.id("c_entry_firstname")).sendKeys(firstName);
				                    	        h = h+2000;
				                    	        if (h>10000){
				                    	        	throw new IllegalArgumentException("Contact First Name not blank but can not set");
				                    	        }
				                    	        Thread.sleep(2000);
				                			}
				                		} else {
				                			throw new IllegalArgumentException("ContactName include non English characters : " +cellVal );
				                		}
				                		
				                		
				                		//Last name
			                			driver.findElement(By.id("c_entry_lastname")).sendKeys(lastName);
			                			h = 0;
			                			while (driver.findElement(By.id("c_entry_lastname")).getAttribute("value").isEmpty()) {
			                				driver.findElement(By.id("c_entry_lastname")).sendKeys(lastName);
			                    	        h = h+2000;
			                    	        if (h>10000){
			                    	        	throw new IllegalArgumentException("Contact Last Name not blank but can not set");
			                    	        }
			                    	        Thread.sleep(2000);
			                			}
				                		
			                			//Street Address
				                		cellHS = rowHS.getCell(17);
				                		driver.findElement(By.id("c_entry_street_address")).clear();
				                		driver.findElement(By.id("c_entry_street_address2")).clear();
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
				                			driver.findElement(By.id("c_entry_street_address")).sendKeys(cellVal);
				                			h = 0;
				                			while (driver.findElement(By.id("c_entry_street_address")).getAttribute("value").isEmpty()) {
				                    	        driver.findElement(By.id("c_entry_street_address")).sendKeys(cellVal);
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
				                					driver.findElement(By.id("c_entry_street_address2")).sendKeys(cellVal);
						                		} else {
						                			throw new IllegalArgumentException("Address2 include non English characters: " +cellVal );
						                		}
					                			
					                		}
				                		}
				                		cellHS = rowHS.getCell(19);
				                		
				                		if (cellHS != null) {
				                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
				                			driver.findElement(By.id("c_entry_street_address2")).sendKeys(" ");
				                			cellVal = "";
					                		cellVal = cellHS.getStringCellValue();
				                			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
				                				driver.findElement(By.id("c_entry_street_address2")).sendKeys(cellVal);
					                		} else {
					                			throw new IllegalArgumentException("Address2 include non English characters: " +cellVal );
					                		}
				                		}
			                			
				                		//City
				                		cellHS = rowHS.getCell(20);
				                		driver.findElement(By.id("add_ship_city")).clear();
				                		
				                		if (cellHS == null) {
				                			throw new IllegalArgumentException("ship-city blank");
				                		}
				                		cellHS.setCellType(Cell.CELL_TYPE_STRING);
				                		
				                		cellVal = "";
				                		cellVal = cellHS.getStringCellValue();
			                			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
			                				driver.findElement(By.id("add_ship_city")).sendKeys(cellVal);
			                				h = 0;
				                			while (driver.findElement(By.id("add_ship_city")).getAttribute("value").isEmpty()) {
				                    	        driver.findElement(By.id("add_ship_city")).sendKeys(cellVal);
				                    	        h = h+2000;
				                    	        if (h>10000){
				                    	        	throw new IllegalArgumentException("city not blank but can not set");
				                    	        }
				                    	        Thread.sleep(2000);
				                			}
				                		} else {
				                			throw new IllegalArgumentException("City include non English characters: " +cellVal );
				                		}
			                			
			                			driver.findElement(By.xpath("//div[@class='country_list bag_select bag_select_country']//div[@class='active']")).click();
			                			
			                			List<WebElement> country = driver.findElements(By.xpath("//div[@class='country_list bag_select bag_select_country']//div[@class='country_box']//li"));
			                        	
			                        	for (int o=0; o<country.size(); o++) {
			                        		if (country.get(o).getText().contains("United States")) {
			                        			country.get(o).click();
			                        			break;
			                        		}
			                        	}
			                        	
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
			                        	
			                			driver.findElement(By.xpath("//div[@class='country_list bag_select bag_select_province']//div[@class='active']")).click();
			                			
			                			List<WebElement> province = driver.findElements(By.xpath("//div[@class='country_list bag_select bag_select_province']//div[@class='country_box']//li"));
			                        	
			                        	for (int o=0; o<province.size(); o++) {
			                        		if (province.get(o).getText().contains(stateFullName)) {
			                        			province.get(o).click();
			                        			break;
			                        		}
			                        	}
				                		
			                			//Zip
				                		HSSFCell cellHSZip;
				                		
				                		cellHSZip = rowHS.getCell(22);
				                		
				                		String zipCode = "";
				                		driver.findElement(By.id("c_entry_postcod")).clear();
				                		
				                		if (cellHSZip == null) {
				                			//Zip not exist
				                			throw new IllegalArgumentException("zip code blank"); 
				                			//throw new IllegalArgumentException("ship-postal-code blank");
				                		} else {
				                			cellHSZip.setCellType(Cell.CELL_TYPE_STRING);
				                			if (cellHSZip.getStringCellValue().isEmpty()) {
					                			//Zip not exist
				                				throw new IllegalArgumentException("zip code blank"); 
					                		} else {
					                			
					                			cellHSZip.setCellType(Cell.CELL_TYPE_STRING);
				                				zipCode = cellHSZip.getStringCellValue();
				                				
				                				cellVal = "";
						                		cellVal = cellHS.getStringCellValue();
					                			if (cellVal.matches("\\A\\p{ASCII}*\\z")) {
					                				driver.findElement(By.id("c_entry_postcod")).sendKeys(zipCode);
					                				h = 0;
						                			while (driver.findElement(By.id("c_entry_postcod")).getAttribute("value").isEmpty()) {
						                    	        driver.findElement(By.id("c_entry_postcod")).sendKeys(cellVal);
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
				                		
				                		driver.findElement(By.id("address_telephone")).clear();
				                		
				                		cellHSTel.setCellType(Cell.CELL_TYPE_STRING);
			                			tel = cellHSTel.getStringCellValue();
			                			if (!tel.isEmpty()) {
			                				tel= tel.replace("-", "");
			                				tel= tel.replace("(", "");
			                				tel= tel.replace(")", "");
			                				tel= tel.replace(" ", "");
			                				tel= tel.replace("+1", "");
			                			}
			                			
			                			driver.findElement(By.id("address_telephone")).sendKeys(tel);
			                			h = 0;
			                			while (driver.findElement(By.id("address_telephone")).getAttribute("value").isEmpty()) {
			                    	        driver.findElement(By.id("address_telephone")).sendKeys(tel);
			                    	        h = h+2000;
			                    	        if (h>10000){
			                    	        	throw new IllegalArgumentException("mobileNo not blank but can not set");
			                    	        }
			                    	        Thread.sleep(2000);
			                			}
				                		
			                			//Save this add
			                			js = "submitAddAddress();";
			                			if (driver instanceof JavascriptExecutor) {
			                			    ((JavascriptExecutor)driver).executeScript(js);
			                			} else {
			                			    throw new IllegalArgumentException("This driver does not support JavaScript!");
			                			}
			                			
			                			Thread.sleep(1000);
			                			
			                			//Set quantity
			                			js = "$('.cart_ul_items .td_quantity input').val('" + quantity + "')";
			                			if (driver instanceof JavascriptExecutor) {
			                			    ((JavascriptExecutor)driver).executeScript(js);
			                			} else {
			                			    throw new IllegalArgumentException("This driver does not support JavaScript!");
			                			}
			                			
			                			Thread.sleep(500);
			                			
			                			//Select Standard Shipment
			                			List<WebElement> elementsShip = driver.findElements(By.xpath("//span[@class='td_shipping_list']/p"));
			                    	    String shipmentName = "";
			                    	    int tmpI = 0;
			                    	    js = "";
			                    	    for (int m=0; m < elementsShip.size(); m++) {
			                    	    	shipmentName = "";
				                    	    //Check standard shipping option exist
				                    	    shipmentName = elementsShip.get(m).getAttribute("innerHTML");
				                    	    
				                    	    if (shipmentName.indexOf("Standard Shipping") >= 0) {
				                    	    	tmpI = m + 1;
				                    	    	js = "$('.td_shipping_list p:nth-child(" + tmpI + ")').trigger('click')";
				                    	    	break;
				                    	    }
			                    	    }
			                    	    
			                    	    if (shipmentName.indexOf("Standard Shipping") < 0) {
			                    	    	throw new IllegalArgumentException("Standard Shipping unavailable");
			                    	    }
			                    	    
			                    	    if (driver instanceof JavascriptExecutor) {
			                			    ((JavascriptExecutor)driver).executeScript(js);
			                			} else {
			                			    throw new IllegalArgumentException("This driver does not support JavaScript!");
			                			}
			                			
			                    	    if (driver.findElements(By.xpath("//span[@class='tracking_info']/ol/li[2]")).size() < 1) {
			                    	    	throw new IllegalArgumentException("Tracking id unavailable");
			                    	    }
			                    	    
			                    	    //Click Tracking ID
			                    	    driver.findElement(By.xpath("//span[@class='tracking_info']/ol/li[2]/input")).click();
			                    	    
			                    	    //anchor Price
			                			String masPrice = driver.findElement(By.xpath("//div[@class='td_shipping_price']/span[5]/s")).getText();
			                    	    masPrice = masPrice.replaceAll("[^\\d.]", "");
			                					
			                			
			                			//Paypal checkout select
			                    	    driver.findElement(By.id("paypal")).click();
			                    	    
			                    	    driver.findElement(By.xpath("//div[@class='payment_btn']/a")).click();
			                    	    
			                    	    String checkoutPreCheck = "";
			                    	    
			                    	    checkoutPreCheck = driver.findElement(By.className("modal_msgbox_msg")).getText();
			                    	    
			                    	    if (checkoutPreCheck.contains("Clearance")) {
			                    	    	throw new IllegalArgumentException(checkoutPreCheck);
			                    	    } else if (!checkoutPreCheck.contains("Your order will be shipped to")) {
			                    	    	throw new IllegalArgumentException(checkoutPreCheck);
			                    	    } else {
			                    	    	driver.findElement(By.className("button_yes")).click();
			                    	    }
			                    	    
			                    	    doTheLoop = true;
			                    	    h = 0;
			                    	    while (doTheLoop){ 
			                    	        h = h+200;
			                    	        Thread.sleep(2000);
			                    	        if (h>1000){
			                    	        	throw new IllegalArgumentException("Paypal redirect fail");
			                    	        }
			                    	        //captcha-input-message
			                        		if (driver.getCurrentUrl().contains("www.paypal.com")) {
			                        			 doTheLoop = false;
			                        		}
			        	            	}
			                    	    
			                    	    driver.switchTo().defaultContent(); // you are now outside both frames
			                    		driver.switchTo().frame(driver.findElement(By.name("injectedUl")));
			                    	    
			                    		List<WebElement> loginRequireCheck = driver.findElements(By.id("email"));
			                    	    
			                    	    if (loginRequireCheck.size() > 0){
			                    	    	driver.findElement(By.id("email")).clear();
			                    	    	driver.findElement(By.id("email")).sendKeys(sbData.getAccountPP());
			                    	    	
			                    	    	driver.findElement(By.id("password")).clear();
			                    	    	driver.findElement(By.id("password")).sendKeys(sbData.getPasswordPP());
			                    	    	
			                    	    	driver.findElement(By.id("btnLogin")).click();
			                    	    }
			                    	    
			                    	    List<WebElement> loginSuccess;
			                    	    
			                    	    doTheLoop = true;
			                    	    h = 0;
			                    	    while (doTheLoop){ 
			                    	        h = h+200;
			                    	        Thread.sleep(2000);
			                    	        if (h>1000){
			                    	        	throw new IllegalArgumentException("Login paypal failed.");
			                    	        }
			                    	        loginSuccess = driver.findElements(By.xpath("//input[@type='submit']"));
			                    	        //captcha-input-message
			                        		if (loginSuccess.size() > 0) {
			                        			 doTheLoop = false;
			                        		}
			        	            	}
			                    	    
			                    	    driver.findElement(By.xpath("//div[@class='payWithCredit']/a[1]")).click();
			                    	    
			                    	    List<WebElement> cardList;
			                    	    cardList = driver.findElements(By.xpath("//li[@role='presentation']"));
			                    	    
			                    	    if (cardList.size() < 0) {
			                    	    	throw new IllegalArgumentException("Paypal card list empty.");
			                    	    }
			                    	    
			                    	    String liNo = "";
			                    	    int idx = 0;
			                    	    for (int k=0; k<cardList.size(); k++) {
			                    	    	if (cardList.get(k).getAttribute("innerHTML").contains(sbData.getFourLastDigit())) {
			                    	    		idx = k + 1;
			                    	    		liNo = "//li[@role='presentation'][" + idx + "]/a[1]";
			                    	    		driver.findElement(By.xpath(liNo)).click();
			                    	    	}
			                    	    }
			                    	    
			                    	    Thread.sleep(3000);
			                    	    
			                    	    driver.findElement(By.xpath("//input[@type='submit']")).click();
			                			
			                    	    //Optains Order Number
			                    	    driver.navigate().to("https://www.banggood.com/index.php?com=account&t=ordersList");
			                    	    
			                    	    List<WebElement> orderList =  driver.findElements(By.className("my_order_list"));
			                    	    
			                    	    if (orderList.size() < 1) {
			                    	    	throw new IllegalArgumentException("No order exist in [My Order]");
			                    	    }
			                    	    
			                    	    String sourceHTML = orderList.get(0).getAttribute("innerHTML");
			                    	    
			                    	    Document doc = Jsoup.parse(sourceHTML);
			                    	    
			                    	    Elements els = doc.getElementsByClass("num");
			                    	    String orderNumInt = "";
			                    	    for (Element orderNum : els) {
			                    	    	Elements aOrderNum = orderNum.select("a");
			                    	    	orderNumInt = aOrderNum.get(0).text(); 
			                    	    }
			                    	    
			                    	    els = doc.getElementsByClass("tracking_info");
			                    	    String trackingId = "";
			                    	    for (Element tracks : els) {
			                    	    	Elements aTrack = tracks.select("a");
			                    	    	trackingId = aTrack.get(0).text(); 
			                    	    }
			                    	    
			                    	    els = doc.getElementsByClass("price");
			                    	    String priceCheck = "";
			                    	    for (Element prices : els) {
			                    	    	priceCheck = prices.text(); 
			                    	    	priceCheck = priceCheck.replaceAll("[^\\d.]", "");
			                    	    }
			                    	    
			                    	    if (!masPrice.equals(priceCheck)) {
			                    	    	throw new IllegalArgumentException("Order sort incorrect");
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
			                    	    orderReport.setTotalPruchasePrice(masPrice);
			                    	    orderReport.setTotalCustomerSpendonOrder("");
			                    	    orderReport.setMarginDollar("");
			                    	    orderReport.setMarginPercent("");
			                    	    orderReport.setVendorOrdernumber(orderNumInt);
			                    	    orderReport.setTrackingNumber(trackingId);
			                			
				                        orderReportList.add(orderReport);
				                        
				                        driver.navigate().to(homepage);
				                		
			                		} catch (IllegalArgumentException ex) {
			                			
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
            result.put("SHEET", new ArrayList<String>(Arrays.asList(sheetName)));
        }
        
        return result;
        
    }
	
	public WebElement findDynamicElement(By by, int timeOut, WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		return element;
	}
}

