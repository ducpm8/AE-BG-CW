package com.web.data;

import java.io.File;
import java.util.Formatter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import java.util.concurrent.TimeUnit;

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

public class FeaturesScraper {
	
	private AmazonOrderReport orderReport;
	
	public static String homepage = "https://www.walmart.com";
	
	public Map<String, List> collectASIN(String fileName, P2SubmitData sbData) throws IOException, URISyntaxException
    {
		CommonProperties.loadProperties();
		
		String sheetName = sbData.getSheetName();
		
		Map<String, List> result = new HashMap<String, List>();
		
		List<AmazonOrderReport> orderReportList = new ArrayList<AmazonOrderReport>();
		
		ChromeOptions op = new ChromeOptions();
	    op.addExtensions(new File("C:\\usr\\Block-image_v1.1.crx"));
		
		WebDriver driver = new ChromeDriver(op);
		
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
        	
        	List<String> brand;
        	String model;
        	String ASIN = StringUtils.EMPTY; 
        	String amazonProductURL = "";
        	String amazonProductName = "";
        	String query = "";
        	String tmp = "";
        	String tableDetailId = "";
        	char extention;
        	boolean detailInformation;
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
            	
            	List<WebElement> eles;
            	
            	WebElement element;
            	
            	for (int i=0; i < workbook.getNumberOfSheets(); i++) {
                	HSSFSheet sheet = workbook.getSheetAt(i);
                	
                	if (sheetName == null || StringUtils.isEmpty(sheetName)) {
                		sheetName = workbook.getSheetAt(i).getSheetName();
                	}
                	
                	if (sheet.getSheetName().equals(sheetName)) {
                		
	                	for (int j=1; j <= sheet.getLastRowNum(); j++) {
	                		eles = new ArrayList<WebElement>();
	                		brand = new ArrayList<String>();
	                		model = "";
	                		amazonProductURL = "";
	                		amazonProductName = "";
	                		ASIN = "";
	                		query = "";
	                		tmp = "";
	                		tableDetailId = "";
	                		detailInformation = false;
	                		orderReport = new AmazonOrderReport();
	                		rowHS = sheet.getRow(j);
	                		if (rowHS != null) {
	                			
	                			try {
	                				cellHS = rowHS.getCell(0);
	                				if (cellHS != null) {
			                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
			                			amazonProductURL = cellHS.getStringCellValue();
			                			
			                			driver.navigate().to(amazonProductURL);
	                				} else {
	                					continue;
	                				}
	                				
	                				cellHS = rowHS.getCell(4);
	                				if (cellHS != null) {
			                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
			                			ASIN = cellHS.getStringCellValue();
	                				}
	                				
	                				cellHS = rowHS.getCell(2);
	                				if (cellHS != null) {
			                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
			                			amazonProductName = cellHS.getStringCellValue();
	                				}
	                				
	                				//productDetails_techSpec_section_1
	                				
	                				eles = driver.findElements(By.xpath("//table[@id='productDetails_techSpec_section_1']/tbody/tr/th"));
	                				tableDetailId = "productDetails_techSpec_section_1";
	                				
	                				if (eles.size() <= 0) {
	                					eles = driver.findElements(By.xpath("//table[@id='productDetails_detailBullets_sections1']/tbody/tr/th"));
	                					tableDetailId = "productDetails_detailBullets_sections1";
	                				}
	                				
	                				if (eles.size() > 0) {
	                					for (int m=0; m< eles.size(); m++) {
	                						if (eles.get(m).getText().toLowerCase().contains("manufacturer")
	                								&& !eles.get(m).getText().toLowerCase().contains("number")) {
	                							tmp = "//table[@id='" + tableDetailId + "']/tbody/tr[" + (m+1) + "]/td";
	                							brand.add(driver.findElement(By.xpath(tmp)).getText());
	                							detailInformation = true;
	                						}
	                						if (eles.get(m).getText().toLowerCase().contains("brand")) {
	                							tmp = "//table[@id='" + tableDetailId + "']/tbody/tr[" + (m+1) + "]/td";
	                							brand.add(driver.findElement(By.xpath(tmp)).getText());
	                							detailInformation = true;
	                						}
	                						if (eles.get(m).getText().toLowerCase().contains("model number")) {
	                							tmp = "//table[@id='" + tableDetailId + "']/tbody/tr[" + (m+1) + "]/td";
	                							model = driver.findElement(By.xpath(tmp)).getText();
	                							
	                							if (brand.size() > 0) {
	                								detailInformation = true;
	                								break;
	                							}
	                						}
	                					}
	                				}
		                    	    
		                			orderReport.setProductName(amazonProductURL);
		                			orderReport.setSku(ASIN);
		                			orderReport.setBuyerName(amazonProductName);
		                			
		                			orderReport.setProductName2(StringUtils.join(brand, ','));
		                			orderReport.setOrderId(model);
		                			orderReport.setShipmentStatus(String.valueOf(detailInformation));
		                			
		                			query = "insert into amazon_feature values(" + SuccessObjecttoString(orderReport) + ")";
		                			query = new String(query.getBytes("ISO-8859-1"), "UTF-8");
		                			
		                			statement.executeUpdate(query);
		                			
		                			orderReportList.add(orderReport);
			                			
		                		
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
	
	public void walmartURL() throws IOException, URISyntaxException
    {
		CommonProperties.loadProperties();
		ChromeOptions op = new ChromeOptions();
	    op.addExtensions(new File("C:\\usr\\Block-image_v1.1.crx"));
	    
	    System.setProperty("webdriver.chrome.driver", "C:\\wd\\chromedriver.exe");
		
		WebDriver driver = new ChromeDriver(op);
		List<WebElement> eles = new ArrayList<WebElement>();
		
		driver.manage().window().maximize();
		Connection connection = null;
		
        try {
        	String brand;
         	String model;
         	String ASIN = StringUtils.EMPTY; 
         	String amazonProductName = "";
         	String query = "";
         	String tmp = "";
         	String tmpURL = "";
         	
         	driver.navigate().to(homepage);
        	
        	final String connectionString = CommonProperties.getConnectionString();
        	
        	Class.forName("org.sqlite.JDBC");
    	    connection = DriverManager.getConnection(connectionString);
    	    Statement statement = connection.createStatement();
    	    
    	    ResultSet rs = statement.executeQuery("SELECT * FROM amazon_feature;");
    	    while (rs.next()) {
    	    	try {
					brand = "";
					model = "";
					amazonProductName = "";
					ASIN = "";
					query = "";
					tmp = "";
					tmpURL = "";
					eles = new ArrayList<WebElement>();
					  
					amazonProductName = rs.getString("ProductName");
					brand = rs.getString("Brand");
					model = rs.getString("Model");
					ASIN = rs.getString("ASIN");
					tmpURL = rs.getString("walmartURL");
					
					if (!StringUtils.isEmpty(tmpURL)) continue;
					
					brand = brand.isEmpty() == true ? " " : " " + brand + " ";
					tmp = amazonProductName + brand + model;
					
					driver.findElement(By.id("global-search-input")).clear();
					driver.findElement(By.id("global-search-input")).sendKeys(tmp);
					
					driver.findElement(By.xpath("//button[@class='header-GlobalSearch-submit btn']")).click();
					
					eles = driver.findElements(By.xpath("//span[@class='zero-results-message alert active alert-warning']"));
					
					if (eles.size()>0) {
						
						if (tmp.indexOf(",") <= 0) {
							continue;
						}
						
						driver.findElement(By.id("global-search-input")).clear();
						tmp = tmp.substring(0, tmp.indexOf(","));
						driver.findElement(By.id("global-search-input")).sendKeys(tmp);
						driver.findElement(By.xpath("//button[@class='header-GlobalSearch-submit btn']")).click();
						eles = driver.findElements(By.xpath("//span[@class='zero-results-message alert active alert-warning']"));
						
						if (eles.size()>0) {
							continue;
						}
					}
					
					query = "UPDATE amazon_feature set walmartURL = ? WHERE ASIN = ?";
					
					PreparedStatement preparedStatement = connection.prepareStatement(query);
	
					preparedStatement.setString(1, driver.getCurrentUrl());
					preparedStatement.setString(2, ASIN);
					
					preparedStatement.executeUpdate();
					preparedStatement.close();
    	    	} catch (Exception e) {
    	    		continue;
    	    	}
    	    }
    	      
			rs.close();
			statement.close();
        	
            driver.close();
            driver.quit();
            
        } catch(Exception ioe) {
            ioe.printStackTrace();
        } finally {
            try
            {
              if(connection != null)
                connection.close();
            }
            catch(SQLException e)
            {
              System.err.println(e);
            }
        }
        
    }
	
	public String SuccessObjecttoString(AmazonOrderReport dto) {
		String result = Joiner.on("', '").join(Arrays.asList(dto.getProductName(), 
    			dto.getBuyerName(), dto.getSku()
    			,dto.getProductName2(), dto.getOrderId(), dto.getShipmentStatus()));
		if (!result.isEmpty()) {
		  result = "'" + result + "'";
		}
		
		return result;
	}
}

