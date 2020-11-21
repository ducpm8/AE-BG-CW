package com.web.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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

public class YoshidaKaban {
	
	private AmazonInventoryReport inventoryReport;
	private AmazonFeeCal amazonCalculate;
	
	public static int pageMaster = 20;
	public static String homepage = "https://www.yoshidakaban.com/product/search_result.html?p=s&p_lisize=100&p_lisort=&p_online=1&pno=20";
	private static final String FILENAME = "F:\\Personal\\Upwork\\Suzuki\\Scraper\\Yoshida\\URL_Kids.txt";
	private static final String Detail = "F:\\Personal\\Upwork\\Suzuki\\Scraper\\Yoshida\\workbook.xls";
	
	public static void main1(String[] args) {

		BufferedWriter bw = null;
		FileWriter fw = null;
		ChromeOptions op = new ChromeOptions();
//    	op.addExtensions(new File("C:\\wd\\Block-image_v1.1.crx"));
    	
    	op.addArguments("headless");
    	op.addArguments("window-size=1200x600");
    	int eleCount = 0;
    	
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
	    			
//	    			while (products.size()<98 && pageCount<pageMaster) {
	    				retry++;
	    				Thread.sleep(1000);
	    				
	    				//if (retry > 10) {
	    					products = driver.findElements(By.className("img_ast"));
//	    				} else {
//	    					products = driver.findElements(By.xpath("//div[@class='photo']//a"));
//	    				}
//	    			}
	    			
	        		for (int i=0; i<products.size(); i++) {
	        			eleCount++;
	        			URLs.add(products.get(i).getAttribute("href"));
	        			bw.write(products.get(i).getAttribute("href"));
	        			bw.write(System.lineSeparator());
	        		}
	        		
	        		System.out.println("Grep "+eleCount);
	        		
	        		List<WebElement> nextPage = driver.findElements(By.id("productHeadPageNext"));
	        		
	        		if (nextPage.size() > 0) {
	        			driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
	                	
	                	try {
	                		int retryPage = 0;
	                		while (true) {
	                			try {
	                				retryPage++;
	                				Thread.sleep(200);
	                				
                					nextPage.get(0).click();
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
	
	public static void main(String[] args) {
		ChromeOptions op = new ChromeOptions();
//    	op.addExtensions(new File("C:\\wd\\Block-image_v1.1.crx"));
    	
    	op.addArguments("headless");
    	op.addArguments("window-size=1200x600");
    	int eleCount = 0;
    	
    	WebDriver driver = new ChromeDriver(op);

		try {
			List<String> URLs = new ArrayList<String>();
			
			Workbook wb = new HSSFWorkbook();
		    //Workbook wb = new XSSFWorkbook();
		    CreationHelper createHelper = wb.getCreationHelper();
		    Sheet sheet = wb.createSheet("new sheet");
		    
			//WebDriver driver = new ChromeDriver();
			
			driver.manage().window().maximize();
			int pageCount = 0;
	    	try {
	    		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
            	
            	try {
            		File file = new File(FILENAME);
        			FileReader fileReader = new FileReader(file);
        			BufferedReader bufferedReader = new BufferedReader(fileReader);
        			StringBuffer stringBuffer = new StringBuffer();
        			String line;
        			while ((line = bufferedReader.readLine()) != null) {
        				pageCount++;
        				driver.navigate().to(line);
        				
        				Row row = sheet.createRow((short)pageCount-1);
        				
        				while (true) {
        	    			
        	    			String topcate = driver.findElement(By.id("topicPath")).getText();
        	    			String subCate = driver.findElement(By.className("snms")).getText();
        	    			String productName = driver.findElement(By.xpath("//dl[@id='productDetailName']//dd")).getText();
        	    			String price = driver.findElement(By.id("productDetailPrice")).getText();
        	    			
        	    			List<WebElement> colors = driver.findElements(By.className("itemColor"));
        	    			String[] color = new String[colors.size()];
        	    			for (int h=0; h<colors.size(); h++) {
        	    				color[h] = colors.get(h).getAttribute("data-cn");
        	    			}
        	    			
        	    			String prdNo = driver.findElement(By.xpath("//div[@class='num_text']/p[1]")).getText();
        	    			String material = driver.findElement(By.id("productDetailMaterial")).getText();
        	    			
        	    			String size = driver.findElement(By.xpath("//div[@id='productDetailSize']//dl")).getText();
        	    			size = size.replace("機内持込み可能手荷物サイズについて", "");
        	    			String weight = driver.findElement(By.xpath("//dl[@id='productDetailWeight']//dd")).getText();
        	    			String detail = driver.findElement(By.id("productDetailComment")).getText();
        	    			
        	    			row.createCell(0).setCellValue(pageCount);
        	    			row.createCell(1).setCellValue(topcate.replace("PAGE BACK", ""));
    	        			row.createCell(2).setCellValue(subCate);
    	        			row.createCell(3).setCellValue(productName);
    	        			row.createCell(4).setCellValue(price);
    	        			row.createCell(5).setCellValue(Arrays.toString(color));
    	        			row.createCell(6).setCellValue(prdNo);
    	        			row.createCell(7).setCellValue(material);
    	        			row.createCell(8).setCellValue(size);
    	        			row.createCell(9).setCellValue(weight);
    	        			row.createCell(10).setCellValue(detail);
    	        			row.createCell(11).setCellValue(line);
    	        			
    	        			break;
        	    		}
        				
        				System.out.println(pageCount + " : " + line);
        				
        				
        			}
        			fileReader.close();
            		
            	} catch (TimeoutException e) {
        		  	driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
            	} finally {
            		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            	}
	    		
	    	} catch (Exception e) {
	    		System.out.println("Error");
	    	} finally {
	    		FileOutputStream fileOut = new FileOutputStream(Detail);
			    wb.write(fileOut);	
			    fileOut.close();
			    wb.close();
			    
	    		driver.close();
	    		driver.quit();
	    	}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

