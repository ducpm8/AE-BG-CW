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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;

import com.web.entity.*;
import com.web.util.AmazonFeeCal;
import com.web.util.PageFunction;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataGrabBG {
	
	private AmazonInventoryReport inventoryReport;
	private AmazonFeeCal amazonCalculate;
	
	public static String homepage = "http://www.banggood.com";
	
	public Map<String, List> doPullPrice(File fileIn, String sheetName)
    {
		
		Map<String, List> result = new HashMap<String, List>();
		
		List<AmazonInventoryReport> inventoryReportList = new ArrayList<AmazonInventoryReport>();
		
		List<AmazonInventoryReport> inventoryReportFailList = new ArrayList<AmazonInventoryReport>();
		
		amazonCalculate = new AmazonFeeCal();
		
		ChromeOptions op = new ChromeOptions();
    	op.addExtensions(new File("C:\\wd\\Block-image_v1.1.crx"));
    	WebDriver driver = new ChromeDriver(op);
		
		//WebDriver driver = new ChromeDriver();
		
		driver.manage().window().maximize();

        try {
        	
        	String productID_Trim2_SKU = StringUtils.EMPTY;
            String price;
        	char extention;
        	FileInputStream file;
        	
        	file = new FileInputStream(fileIn);
        	extention =  fileIn.getName().charAt(fileIn.getName().length() - 1);
        	if (extention == 'x') {
        		
        	} else {
        		//file = new FileInputStream(new File(fileName));
    			
            	//Get the workbook instance for XLS file 
            	HSSFWorkbook workbook = new HSSFWorkbook(file);
            	
            	HSSFRow rowHS;
            	HSSFCell cellHS;
            	HSSFCell cellHS2;
            	
            	//driver.navigate().to(homepage);
            	
            	driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
            	
            	try {
            		driver.navigate().to(homepage);
            	    driver.findElement(By.xpath("//div[@class='country']//div[@class='active']"));
            	} catch (TimeoutException e) {
            		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
            		  	
            	} finally {
            		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            	}
            	
            	String js = "";
    			js = "ship();";
    			if (driver instanceof JavascriptExecutor) {
    			    ((JavascriptExecutor)driver).executeScript(js);
    			} else {
    			    throw new IllegalArgumentException("This driver does not support JavaScript!");
    			}
            	
    			Thread.sleep(2000);
    			
            	//$(".country .active").trigger("click");
            	//Select US
            	driver.findElement(By.xpath("//div[@class='country']//div[@class='active']")).click();
            	
            	Thread.sleep(1000);
            	List<WebElement> country;
            	country = driver.findElements(By.xpath("//div[@class='country']//div[@class='country_box']//li"));
            	
            	for (int o=0; o<country.size(); o++) {
            		if (country.get(o).getText().equals("United States")) {
            			country.get(o).click();
            			break;
            		}
            	}
            	Thread.sleep(1000);
            	//Select USD
            	driver.findElement(By.xpath("//div[@class='currency']//div[@class='active']")).click();
            	
            	country = driver.findElements(By.xpath("//div[@class='currency']//div[@class='currency_box']//li"));
            	
            	for (int o=0; o<country.size(); o++) {
            		if (country.get(o).getText().contains("USD")) {
            			country.get(o).click();
            			break;
            		}
            	}
            	Thread.sleep(1000);
            	js = "saveShip(0);";
    			if (driver instanceof JavascriptExecutor) {
    			    ((JavascriptExecutor)driver).executeScript(js);
    			} else {
    			    throw new IllegalArgumentException("This driver does not support JavaScript!");
    			}
    			
    			//Thread.sleep(1000);
            	//Select currency USD
            	
            	WebElement element;
            	
            	for (int i=0; i < workbook.getNumberOfSheets(); i++) {
                	HSSFSheet sheet = workbook.getSheetAt(i);
                	
                	if (sheetName == null || StringUtils.isEmpty(sheetName)) {
                		sheetName = workbook.getSheetAt(i).getSheetName();
                	}
                	if (sheet.getSheetName().equals(sheetName)) {
                	
	                	for (int j=1; j <= sheet.getLastRowNum(); j++) {
	                		inventoryReport = new AmazonInventoryReport();
	                		rowHS = sheet.getRow(j);
	                		if (rowHS != null) {
		                		cellHS = rowHS.getCell(0);
		                		if (cellHS != null && cellHS.getCellType() != Cell.CELL_TYPE_BLANK) {
		                			
		                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
		                			productID_Trim2_SKU = cellHS.getStringCellValue();
		                			
		                			cellHS2 = rowHS.getCell(1);
		                			
		                			cellHS2.setCellType(Cell.CELL_TYPE_STRING);
		                			
			                		if (StringUtils.isEmpty(productID_Trim2_SKU) && StringUtils.isNotEmpty(cellHS2.getStringCellValue())) {
			                			//Can not obtains SKU in csv input
			                			
			                			inventoryReport.setProductID(StringUtils.EMPTY);
				                        
				                        if(cellHS2 != null) {
				                        	inventoryReport.setASIN(cellHS2.getStringCellValue());
				                        }
			                			
			                			inventoryReport.setURL(StringUtils.EMPTY);
			                			
			                			inventoryReport.setReason("Can not obtains SKU");
			                			inventoryReport.setLine(String.valueOf(j+1));
			                			inventoryReportFailList.add(inventoryReport);
			                			driver.navigate().to(homepage);
			                			
			                			continue;
			                		} else if (StringUtils.isEmpty(productID_Trim2_SKU) && StringUtils.isEmpty(cellHS2.getStringCellValue())) {
			                			driver.navigate().to(homepage);
			                			continue;
			                		}
			                		
			                		try {
			                			
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
				                			
				                			inventoryReport.setProductID(productID_Trim2_SKU);
				                			
				                			cellHS2.setCellType(Cell.CELL_TYPE_STRING);
					                        if(cellHS2 != null) {
					                        	inventoryReport.setASIN(cellHS2.getStringCellValue());
					                        }
				                			
				                			inventoryReport.setURL(driver.getCurrentUrl());
				                			
				                			inventoryReport.setReason("Product not found");
				                			inventoryReport.setLine(String.valueOf(j+1));
				                			inventoryReportFailList.add(inventoryReport);
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
				                    	    	inventoryReport.setProductID(productID_Trim2_SKU);
					                			
					                			cellHS2.setCellType(Cell.CELL_TYPE_STRING);
						                        if(cellHS2 != null) {
						                        	inventoryReport.setASIN(cellHS2.getStringCellValue());
						                        }
					                			
					                			inventoryReport.setURL(driver.getCurrentUrl());
					                			
					                			inventoryReport.setReason("Standard Shipping unavailable");
					                			inventoryReport.setLine(String.valueOf(j+1));
					                			inventoryReportFailList.add(inventoryReport);
					                			
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
			                    	    	inventoryReport.setProductID(productID_Trim2_SKU);
				                			cellHS2.setCellType(Cell.CELL_TYPE_STRING);
					                        if(cellHS2 != null) {
					                        	inventoryReport.setASIN(cellHS2.getStringCellValue());
					                        }
				                			
				                			inventoryReport.setURL(driver.getCurrentUrl());
				                			
				                			inventoryReport.setReason("Tracking id unavailable");
				                			inventoryReport.setLine(String.valueOf(j+1));
				                			inventoryReportFailList.add(inventoryReport);
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
			                    	    
			                    	    String radioName = "";
			                    	    String shipment = "0";
			                    	    radioName = driver.findElement(By.xpath("//span[@class='tracking_info']/ol/li[2]")).getText();
			                    	    if (radioName.indexOf("Tracking number") >= 0) {
			                    	    	shipment = radioName.replaceAll("[^\\d.]", "");
			                    	    }
			                    	    
			                    	    inventoryReport.setURL(driver.getCurrentUrl());
			                    	    
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
			                    	    
			                    	    //PageFunction.waitForLoad(driver);
			                    	    
			                    	    inventoryReport.setShippingCostEpacket(shipment);
				                        inventoryReport.setMinAbsoluteMargin("3");
				                        inventoryReport.setMarginTargetPercent("50");
				                		
				                        if(cellHS2 != null) {
				                        	inventoryReport.setASIN(cellHS2.getStringCellValue());
				                        }
				                        
				                        
				                        inventoryReport.setCostFromAE(price);
				                        inventoryReport.setProductID(productID_Trim2_SKU);
				                        
				                        inventoryReport.setAssignedAmazonSku(productID_Trim2_SKU + "-BG");
				                        inventoryReport.setLine(String.valueOf(j+1));
				                        
				                        //Price calculation
				                        inventoryReport = amazonCalculate.priceCalculate(inventoryReport);
				                        inventoryReportList.add(inventoryReport);
			                    	    
				                        driver.navigate().to(homepage);
				                    
			                		} catch (IllegalArgumentException iae) {
			                			
			                			inventoryReport.setProductID(productID_Trim2_SKU);
			                			inventoryReport.setURL(driver.getCurrentUrl());
			                			
			                			inventoryReport.setReason(iae.getMessage());
			                			inventoryReport.setLine(String.valueOf(j+1));
			                			
			                			inventoryReportFailList.add(inventoryReport);
			                			
			                			if (driver.getCurrentUrl().indexOf("shopping_cart") > 0 ){
			                				driver.findElement(By.xpath("//span[@class='opt_remove scartremove']")).click();
				                			Thread.sleep(500);
				                    	    driver.findElement(By.xpath("//span[@class='button_yes']")).click();
				                    	    
				                    	    Thread.sleep(2000);
		                    	        } 
			                			
			                			driver.navigate().to(homepage);
			                			
			                			continue;
			                			
			                		} catch (Exception ex) {
			                			//Error occurred in Selenium
			                			
			                			inventoryReport.setProductID(productID_Trim2_SKU);
			                			
				                        if(cellHS2 != null) {
				                        	inventoryReport.setASIN(cellHS2.getStringCellValue());
				                        }
				                        
			                			inventoryReport.setURL(driver.getCurrentUrl());
			                			
			                			inventoryReport.setReason("Selenium error occurred");
			                			inventoryReport.setLine(String.valueOf(j+1));
			                			
			                			inventoryReportFailList.add(inventoryReport);
			                			
			                			if (driver.getCurrentUrl().indexOf("shopping_cart") > 0 ){
			                				driver.findElement(By.xpath("//span[@class='opt_remove scartremove']")).click();
				                			Thread.sleep(500);
				                    	    driver.findElement(By.xpath("//span[@class='button_yes']")).click();
				                    	    
				                    	    Thread.sleep(2000);
		                    	        }
			                			
			                			driver.navigate().to(homepage);
			                			continue;
			                		}
		                		}
	                		}
	                	}
                	}
            	}
        	}
        	
        	file.close();
            
        } catch(Exception ioe) {
            ioe.printStackTrace();
        } finally {
        	
        	driver.close();
            driver.quit();
            
            result.put("SUCCESS", inventoryReportList);
            result.put("FAIL", inventoryReportFailList);
            result.put("SHEET", new ArrayList<String>(Arrays.asList(sheetName)));
        }
        
        return result;
        
    }
}

