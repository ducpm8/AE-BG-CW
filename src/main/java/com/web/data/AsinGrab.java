package com.web.data;

import java.io.File;
import java.util.Formatter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

import com.google.common.base.Joiner;
import com.web.entity.*;
import com.web.util.ConstVal;
import com.web.util.config.CommonProperties;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class AsinGrab {
	
	private AmazonOrderReport orderReport;
	
	public static String homepage = "https://www.amazon.com";
	
	public Map<String, List> collectASIN(String fileName, P2SubmitData sbData) throws IOException, URISyntaxException
    {
		CommonProperties.loadProperties();
		
		String sheetName = sbData.getSheetName();
		
		Map<String, List> result = new HashMap<String, List>();
		
		List<AmazonOrderReport> orderReportList = new ArrayList<AmazonOrderReport>();
		
		ChromeOptions op = new ChromeOptions();
	    //op.addExtensions(new File("C:\\usr\\Block-image_v1.1.crx"));
		
		WebDriver driver = new ChromeDriver();
		
		driver.manage().window().maximize();
		Connection connection = null;
		
		String processDateTime;
    	
    	DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        processDateTime = sdf.format(date);
		
        try {
        	
        	final String connectionString = CommonProperties.getConnectionString();
        	
        	Class.forName("org.sqlite.JDBC");
    	    connection = DriverManager.getConnection(connectionString);
    	    Statement statement = connection.createStatement();
    	    statement.setQueryTimeout(30);  // set timeout to 30 sec.
    	    
    	    statement.executeUpdate("PRAGMA encoding='UTF-8';");
        	
        	String productName = StringUtils.EMPTY;
        	String ASIN = StringUtils.EMPTY; 
        	String productId = "";
        	String amazonProductName = "";
        	String query = "";
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
            	
            	driver.navigate().to(homepage);
            	
            	WebElement element;
            	
            	for (int i=0; i < workbook.getNumberOfSheets(); i++) {
                	HSSFSheet sheet = workbook.getSheetAt(i);
                	
                	if (sheetName == null || StringUtils.isEmpty(sheetName)) {
                		sheetName = workbook.getSheetAt(i).getSheetName();
                	}
                	
                	if (sheet.getSheetName().equals(sheetName)) {
                		
	                	for (int j=1; j <= sheet.getLastRowNum(); j++) {
	                		productName = "";
	                		productId = "";
	                		ASIN = "";
	                		query = "";
	                		orderReport = new AmazonOrderReport();
	                		rowHS = sheet.getRow(j);
	                		if (rowHS != null) {
	                			
	                			try {
	                				cellHS = rowHS.getCell(0);
	                				if (cellHS != null) {
			                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
			                			productId = cellHS.getStringCellValue();
	                				}
	                				
		                			//Order Number
			                		cellHS = rowHS.getCell(1);
			                		if (cellHS != null) {
			                			
			                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
			                			productName = cellHS.getStringCellValue();
			                			
				                		if (StringUtils.isEmpty(productName)) {
				                			continue;
				                		}
				                		
				                		//Input Order Number
			                			element = driver.findElement(By.xpath("//input[contains(@id,'twotabsearchtextbox')]"));
			                			
			                			element.clear();
			                			element.sendKeys(productName);
		    	                		
		    	                		//Click Search
			                			element = driver.findElement(By.xpath("//input[contains(@type,'submit')]"));
			                			element.click();
			                			
			                			boolean doTheLoop = true;
			                    	    int h = 0;
			                    	    while (doTheLoop){ 
			                    	        h = h+200;
			                    	        Thread.sleep(1000);
			                    	        if (h>1000){
			                    	        	throw new IllegalArgumentException("");
			                    	        }
			                    	        if (driver.findElements(By.xpath("//li[@id='result_0']")).size() > 0 ){
			                    	            doTheLoop = false;
			                    	        }      
			        	            	}
			                			
			                			List<WebElement> notFound = driver.findElements(By.id("noResultsTitle"));
			                			
			                			if (notFound.size() > 0) {
			                				continue;
			                			}
			                			
			                			(new WebDriverWait(driver, 10))
		                        		   .until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id='result_0']/div/div/div/div/div/div/a"))).click();
				                		
			                			doTheLoop = true;
			                    	    h = 0;
			                    	    while (doTheLoop){ 
			                    	        h = h+200;
			                    	        Thread.sleep(500);
			                    	        if (h>1000){
			                    	        	boolean doTheLoopSub = true;
					                    	    int m = 0;
					                    	    while (doTheLoopSub){ 
					                    	        m = m+200;
					                    	        Thread.sleep(500);
					                    	        if (m>1000){
					                    	        	throw new IllegalArgumentException("Size or Type or Color required");
					                    	        }
					                    	        if (driver.findElements(By.xpath("//input[@id='ASIN']")).size() > 0 ){
					                    	        	doTheLoopSub = false;
					                    	        	doTheLoop = false;
					                    	            ASIN = driver.findElement(By.xpath("//input[@id='ASIN']")).getAttribute("value");
					                    	        }      
					        	            	}
			                    	        }
			                    	        if (driver.findElements(By.xpath("//form[@class='askQuestionForm']/input[@name='askAsin']")).size() > 0 ){
			                    	            doTheLoop = false;
			                    	            ASIN = driver.findElement(By.xpath("//form[@class='askQuestionForm']/input[@name='askAsin']")).getAttribute("value");
			                    	        }      
			        	            	}
			                    	    
			                    	    
			                			amazonProductName = driver.findElement(By.id("productTitle")).getText();
			                    	    
			                			orderReport.setProductName(productName);
			                			orderReport.setSku(ASIN);
			                			orderReport.setOrderId(productId);
			                			orderReport.setProductName2(amazonProductName);
			                			orderReport.setURL(driver.getCurrentUrl());
			                			orderReport.setReportingDate(processDateTime);
			                			
			                			query = "insert into amazon_asin values(" + SuccessObjecttoString(orderReport) + ")";
			                			query = new String(query.getBytes("ISO-8859-1"), "UTF-8");
			                			
			                			statement.executeUpdate(query);
			                			
			                			orderReportList.add(orderReport);
			                		}	
		                		
	                			} catch (Exception e) {
	                				continue;
	                			}
	                		}
	                	}
                	}
            	}
        	}
        	
        	file.close();
            driver.close();
            driver.quit();
            
        } catch(Exception ioe) {
            ioe.printStackTrace();
        } finally {
            result.put("SUCCESS", orderReportList);
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
	
	public String SuccessObjecttoString(AmazonOrderReport dto) {
		String result = Joiner.on("', '").join(Arrays.asList(dto.getOrderId(), 
    			dto.getProductName(), dto.getSku()
    			,dto.getProductName2(), dto.getURL(), dto.getReportingDate()));
		if (!result.isEmpty()) {
		  result = "'" + result + "'";
		}
		
		return result;
	}
}

