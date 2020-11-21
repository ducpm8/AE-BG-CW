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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Joiner;
import com.web.entity.*;
import com.web.util.AmazonFeeCal;
import com.web.util.PageFunction;
import com.web.util.config.CommonProperties;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataGrab {
	
	private AmazonInventoryReport inventoryReport;
	private AmazonFeeCal amazonCalculate;
	
	public static String homepage = "https://www.aliexpress.com";
	
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
		
		Connection connection = null;
		
        try {
        	
        	CommonProperties.loadProperties();

    		final String connectionString = CommonProperties.getConnectionString();
        	
        	Class.forName("org.sqlite.JDBC");
    	    
    	    connection = DriverManager.getConnection(connectionString);
    	    Statement statement = connection.createStatement();
    	    statement.setQueryTimeout(30);  // set timeout to 30 sec.
    	    
    	    
        	
        	String productID_Trim2_SKU = StringUtils.EMPTY;
            String price;
        	char extention;
        	FileInputStream file;
        	
        	String fileName = fileIn.getName();
        	String processDateTime;
        	
        	DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            processDateTime = sdf.format(date);
        	
        	file = new FileInputStream(fileIn);
        	extention =  fileIn.getName().charAt(fileIn.getName().length() - 1);
        	if (extention != 'x') {
        		//file = new FileInputStream(new File(fileName));
    			
            	//Get the workbook instance for XLS file 
            	HSSFWorkbook workbook = new HSSFWorkbook(file);
            	
            	HSSFRow rowHS;
            	HSSFCell cellHS;
            	HSSFCell cellHS2;
            	
            	driver.navigate().to(homepage);
            	
            	PageFunction.waitForLoad(driver);
            	
            	WebElement element;
            	
            	for (int i=0; i < workbook.getNumberOfSheets(); i++) {
            	//for (int i=0; i < 1; i++) {
            		//Get first sheet from the workbook
                	HSSFSheet sheet = workbook.getSheetAt(i);
                	
                	if (sheetName == null || StringUtils.isEmpty(sheetName)) {
                		sheetName = workbook.getSheetAt(i).getSheetName();
                	}
                	
                	if (sheet.getSheetName().equals(sheetName)) {
                	
	                	for (int j=1; j <= sheet.getLastRowNum(); j++) {
	                		inventoryReport = new AmazonInventoryReport();
	                	//for (int j=1; j < 3; j++) {
	                		rowHS = sheet.getRow(j);
	                		if (rowHS != null) {
		                		cellHS = rowHS.getCell(0);
		                		if (cellHS != null) {
		                			
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
			                			
			                			inventoryReport.setFileName(fileName);
				                        inventoryReport.setProcessDateTime(processDateTime);
			                			
			                			inventoryReportFailList.add(inventoryReport);
			                			
			                			//Insert to DB
				                        statement.executeUpdate("insert into pricing_fail values(" + FailObjecttoString(inventoryReport) + ")");
			                			
			                			continue;
			                		} else if (StringUtils.isEmpty(productID_Trim2_SKU) && StringUtils.isEmpty(cellHS2.getStringCellValue())) {
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
		    	                		List<WebElement> elems = driver.findElements(By.xpath("//span[contains(@id, 'j-sku-discount-price')]"));
		    	                		
		    	                		if ((elems.size() > 0)) {
		    	                			element = (new WebDriverWait(driver, 10))
					                        		   .until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(@id, 'j-sku-discount-price')]")));
		    	                			
		    	                			price = element.getText();
		    	                		} else {
		    	                			elems = driver.findElements(By.xpath("//span[contains(@id, 'j-sku-price')]"));
		    	                			
		    	                			if (elems.size() > 0) {
		    	                				
		    	                				element = (new WebDriverWait(driver, 10))
						                        		   .until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(@id, 'j-sku-price')]")));
		    	                				
					                			price = element.getText();
					                		} else {
					                			//Product not found
					                			
					                			inventoryReport.setProductID(productID_Trim2_SKU);
					                			
					                			cellHS2.setCellType(Cell.CELL_TYPE_STRING);
						                        if(cellHS2 != null) {
						                        	inventoryReport.setASIN(cellHS2.getStringCellValue());
						                        }
					                			
					                			inventoryReport.setURL(driver.getCurrentUrl());
					                			
					                			inventoryReport.setReason("Product not found");
					                			inventoryReport.setLine(String.valueOf(j+1));
					                			
					                			inventoryReport.setFileName(fileName);
						                        inventoryReport.setProcessDateTime(processDateTime);
					                			
					                			inventoryReportFailList.add(inventoryReport);
					                			
					                			//Insert to DB
						                        statement.executeUpdate("insert into pricing_fail values(" + FailObjecttoString(inventoryReport) + ")");
					                			
					                			continue;
					                		}
		    	                		}
				                        //Shipping Price
				                        driver.findElement(By.xpath("//div[@id='j-product-tabbed-pane']/ul/li[3]/a")).click();
				                        
				                        element = (new WebDriverWait(driver, 10))
				                        		   .until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='j-pnl-country-selector']/a")));
				                        
				                        element.click();
				                        element = (new WebDriverWait(driver, 10))
				                        		   .until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@data-code='us']/span")));
				                        
				                        element.click();
				                        
				                        element = (new WebDriverWait(driver, 10))
				                        		   .until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[@data-company-code='EMS_ZX_ZX_US']/td[2]")));
				                        
				                        String shipment = "";
				                        
				                        shipment = element.getAttribute("innerHTML");
				                        
				                        //Process Shipping Fee
				                        if (StringUtils.isNotEmpty(shipment) && !(shipment.indexOf("</del>") < 0)) {
				                        	shipment = shipment.substring(shipment.indexOf("</del>") + 6, shipment.length());
				                        }
				                		
				                		shipment = shipment.replace(StringUtils.SPACE, StringUtils.EMPTY);
				                		
				                		if (StringUtils.isNotEmpty(shipment) && !(shipment.indexOf("US$") < 0) && !(shipment.indexOf("</span>") < 0)) {
				                        	shipment = shipment.substring(shipment.indexOf("US$") + 3, shipment.indexOf("</span>"));
				                        }
				                        
				                        if (StringUtils.isNotEmpty(shipment) && shipment.indexOf("Free Shipping") < 0 && shipment.indexOf("FreeShipping") < 0) {
				                        	shipment = StringUtils.replace(shipment,"US $","");
				                        } else {
				                        	shipment = "0";
				                        }
				                        inventoryReport.setShippingCostEpacket(shipment);
				                        
				                        inventoryReport.setMinAbsoluteMargin("3");
				                        inventoryReport.setMarginTargetPercent("50");
				                		
				                        if(cellHS2 != null) {
				                        	inventoryReport.setASIN(cellHS2.getStringCellValue());
				                        }
				                        
				                        inventoryReport.setURL(driver.getCurrentUrl());
				                        inventoryReport.setCostFromAE(price);
				                        inventoryReport.setProductID(productID_Trim2_SKU);
				                        
				                        inventoryReport.setAssignedAmazonSku(productID_Trim2_SKU + "-HF-AE");
				                        inventoryReport.setLine(String.valueOf(j+1));
				                        
				                        //Price calculation
				                        inventoryReport = amazonCalculate.priceCalculate(inventoryReport);
				                        
				                        inventoryReport.setFileName(fileName);
				                        inventoryReport.setProcessDateTime(processDateTime);
				                        
				                        inventoryReportList.add(inventoryReport);
				                        
				                        //Insert to DB
				                        statement.executeUpdate("insert into pricing values(" + SuccessObjecttoString(inventoryReport) + ")");
				                		
			                		} catch (Exception ex) {
			                			//Error occurred in Selenium
			                			
			                			inventoryReport.setProductID(productID_Trim2_SKU);
			                			
				                        if(cellHS2 != null) {
				                        	inventoryReport.setASIN(cellHS2.getStringCellValue());
				                        }
				                        
			                			inventoryReport.setURL(driver.getCurrentUrl());
			                			
			                			inventoryReport.setReason("Selenium error occurred");
			                			inventoryReport.setLine(String.valueOf(j+1));
			                			
			                			inventoryReport.setFileName(fileName);
				                        inventoryReport.setProcessDateTime(processDateTime);
			                			
			                			inventoryReportFailList.add(inventoryReport);
			                			
			                			//Insert to DB
				                        statement.executeUpdate("insert into pricing_fail values(" + FailObjecttoString(inventoryReport) + ")");
			                			
			                			continue;
			                		}
			                		
		                		}
	                		}
	                	}
                	}
                	
                	
            	}
            	
        	}
        	
        	file.close();
            driver.close();
            driver.quit();
            
        } catch(SQLException ioe) {
            ioe.printStackTrace();
        } catch(Exception ex) {
        	ex.printStackTrace();
        } finally {
            result.put("SUCCESS", inventoryReportList);
            result.put("FAIL", inventoryReportFailList);
            result.put("SHEET", new ArrayList<String>(Arrays.asList(sheetName)));
            
            try
            {
              if(connection != null)
                connection.close();
            }
            catch(SQLException e)
            {
              // connection close failed.
              System.err.println(e);
            }
        }
        
        return result;
        
    }
	
	public String SuccessObjecttoString(AmazonInventoryReport dto) {
		String result = Joiner.on("', '").join(Arrays.asList(dto.getProductID(), dto.getAssignedAmazonSku(), dto.getASIN(), dto.getCostFromAE(), dto.getShippingCostEpacket(), dto.getAmazonFee(), dto.getCOGS(), dto.getMarginTargetPercent(), dto.getMarginTargetDolar(), dto.getAmzListingPriceMin(), dto.getMinAbsoluteMargin(), dto.getSuspectListPrice(), dto.getAbsoluteListPrice(), dto.getLine(), dto.getURL(), dto.getFileName(), dto.getProcessDateTime()));
		if (!result.isEmpty()) {
		  result = "'" + result + "'";
		}
		
		return result;
	}
	
	public String FailObjecttoString(AmazonInventoryReport dto) {
		String result = Joiner.on("', '").join(Arrays.asList(dto.getProductID(), dto.getASIN(), dto.getURL(), dto.getReason(), dto.getLine(), dto.getFileName(), dto.getProcessDateTime()));
		if (!result.isEmpty()) {
		  result = "'" + result + "'";
		}
		
		return result;
	}
	
	
	
}

