package com.web.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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

public class Adidas {
	
	private AmazonInventoryReport inventoryReport;
	private AmazonFeeCal amazonCalculate;
	
	public static int pageMaster = 8;
	public static String homepage = "https://shop.adidas.jp/item/?gendId=k&limit=120";
	private static final String FILENAME = "F:\\Personal\\Upwork\\Suzuki\\Scraper\\Adidas\\URL_Kids.txt";
	
	public static void main(String[] args) {

		BufferedWriter bw = null;
		FileWriter fw = null;
		ChromeOptions op = new ChromeOptions();
//    	op.addExtensions(new File("C:\\wd\\Block-image_v1.1.crx"));
    	
    	op.addArguments("headless");
    	op.addArguments("window-size=1200x600");
    	
    	WebDriver driver = new ChromeDriver(op);

		try {
			//WebDriver driver = new ChromeDriver();
			
			driver.manage().window().maximize();
			int pageCount = 0;
	    	try {
	    		
	    		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
            	
            	try {
            		driver.navigate().to(homepage);
            	} catch (TimeoutException e) {
        		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
            	} finally {
            		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            	}
	    		
	    		List<String> URLs = new ArrayList<String>();
	    		fw = new FileWriter(FILENAME);
				bw = new BufferedWriter(fw);
				
	    		while (true) {
	    			pageCount++;
	    			List<WebElement> products = driver.findElements(By.xpath("//div[@class='photo']//a"));
	    			
	    			int retry = 0;
	    			
	    			while (products.size()<118 && pageCount<pageMaster) {
	    				retry++;
	    				Thread.sleep(1000);
	    				
	    				if (retry > 10) {
	    					products = driver.findElements(By.className("image_link"));
	    				} else {
	    					products = driver.findElements(By.xpath("//div[@class='photo']//a"));
	    				}
	    			}
	    			
	        		for (int i=0; i<products.size(); i++) {
	        			URLs.add(products.get(i).getAttribute("href"));
	        			bw.write(products.get(i).getAttribute("href"));
	        			bw.write(System.lineSeparator());
	        		}
	        		
	        		List<WebElement> nextPage = driver.findElements(By.className("pager_arrow_right"));
	        		List<WebElement> nextPage2 = driver.findElements(By.xpath("//a[@class='buttonArrow mod-next']"));
	        		
	        		if (nextPage.size() > 0) {
	        			if (!nextPage.get(0).getAttribute("class").contains("disable") 
	        					&& !nextPage.get(0).getAttribute("style").contains("none")) {
		        			driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		                	
		                	try {
		                		int retryPage = 0;
		                		while (true) {
		                			try {
		                				retryPage++;
		                				Thread.sleep(200);
		                				
		                				if (retryPage > 10) {
		                					driver.navigate().to(nextPage.get(0).getAttribute("href"));
		                				} else {
		                					nextPage.get(0).click();
		                				}
			                			break;
			                		} catch (Exception e) {
			                			
			                		}
		                		}
		                	} catch (TimeoutException e) {
		            		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
		                	} finally {
		                		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		                	}
		        		} else {
		        			break;
		        		}
	        		} else if (nextPage2.size() >0) {
	        			if (!nextPage2.get(0).getAttribute("class").contains("disable")
	        					&& !nextPage2.get(0).getAttribute("style").contains("none")) {
		        			driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		                	
		                	try {
		                		int retryPage = 0;
		                		while (true) {
		                			try {
		                				retryPage++;
		                				Thread.sleep(200);
		                				
		                				if (retryPage > 10) {
		                					driver.navigate().to(nextPage2.get(0).getAttribute("href"));
		                				} else {
		                					nextPage2.get(0).click();
		                				}
			                			
			                			break;
			                		} catch (Exception e) {
			                			
			                		}
		                		}
		                	} catch (TimeoutException e) {
		            		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
		                	} finally {
		                		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		                	}
		        		} else {
		        			break;
		        		}
	        		} else {
	        			break;
	        		}
	    		}
	    		
				System.out.println("Done " + URLs.size());
	    	} catch (Exception e) {
	    		System.out.println("Error");
	    	} finally {
	    		if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
	    		driver.close();
	    		driver.quit();
	    	}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doPullPrice(File fileIn, String sheetName)
    {
		
		amazonCalculate = new AmazonFeeCal();
		
		
            	
    }
}

