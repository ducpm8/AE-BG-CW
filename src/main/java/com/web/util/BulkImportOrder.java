package com.web.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
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

import com.web.data.BanggoodOrderPlace;
import com.web.data.P2SubmitData;
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

public class BulkImportOrder {
	
	private AmazonOrderReport orderReport;
	
	public static String homepage = "http://www.banggood.com";
	public static String loginPage = "https://www.banggood.com/login.html";
	
	public Map<String, List> doPlaceOrder(P2SubmitData submitData)
    {	
		String sheetName = "result";
		if (submitData.getSheetName() != null && !submitData.getSheetName().isEmpty()) {
			sheetName = submitData.getSheetName();
		} 
		
		File serverFile = null;
		
		String account =  submitData.getAccount();
		String password = submitData.getPassword();
		
		Map<String, List> result = new HashMap<String, List>();
		Map<String, List> resultSub = new HashMap<String, List>();
		
		
		List<AmazonOrderReport> orderReportList = new ArrayList<AmazonOrderReport>();
		
		List<AmazonOrderReport> orderReportFailList = new ArrayList<AmazonOrderReport>();
		AmazonOrderReport tmpOrderRp = new AmazonOrderReport();
		
		List<AmazonOrderReport> dto = new ArrayList<AmazonOrderReport>();
		List<AmazonOrderReport> normalList = new ArrayList<AmazonOrderReport>();
		
		WebDriver driver = new ChromeDriver();
		
		driver.manage().window().maximize();
		
        try {
        	
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
    			//driver.findElement(By.id("login_image_code")).sendKeys("");
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
        	        
        	        if (h>30000){
        	        	throw new IllegalArgumentException("Captcha require");
        	        }
        	        
        	        //Button Login
        	        try {
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
        	        } catch (Exception e) {
        	        	System.out.println("error in middle_signIn_button_20161124");
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
                	} catch (Exception e) {
                		System.out.println("error in login url");
                	} finally {
                		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
                	}
                	
                	Thread.sleep(5000);
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
        	
        	
        	driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        	
        	try {
        		driver.navigate().to("https://www.banggood.com/index.php?com=account&t=dropshipImportOrder");
        	} catch (TimeoutException e) {
        		driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
        	} finally {
        		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        	}
    		
    		result = new HashMap<String, List>();
    		
    		BulkImportFile bif = new BulkImportFile();
    		
    		byte[] bytes = submitData.getFile().getBytes();
    		
			// Creating the directory to store file
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath + File.separator + "tmpFiles");
			if (!dir.exists())
				dir.mkdirs();
			
			String filePath = "";
			
			filePath = dir.getAbsolutePath() + File.separator + submitData.getFile().getOriginalFilename();

			// Create the file on server
			serverFile = new File(filePath);
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(serverFile));
			stream.write(bytes);
			stream.close();
    		
    		resultSub = bif.createInput(filePath);
    		
    		orderReportFailList = resultSub.get("FAIL");

    		WebElement browseButton = driver.findElement(By.id("import_file"));
    		browseButton.sendKeys(resultSub.get("CSV").get(0).toString());
    		
    		driver.findElement(By.id("title")).sendKeys("test1");
    		
    		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        	
        	try {
        		driver.findElement(By.xpath("//form[@id='importOrderSubmit']//input[@type='submit']")).click();
        	} catch (TimeoutException e) {
        		driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
        	} finally {
        		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        	}
        	
        	List<WebElement> shipList = driver.findElements(By.xpath("//form[@id='dropship_import_from_d']/div[@class='acc_item']/div"));
        	
        	String divAdd ="";
        	String selectAdd ="";
        	int divIdx = 0;
        	boolean trackidExistFlg = false;
        	for (int k=0; k<shipList.size()-2; k++) {
        		trackidExistFlg = false;
        		divIdx = divIdx + 1;
        		divAdd = "//form[@id='dropship_import_from_d']/div[@class='acc_item']/div[" + divIdx + "]//td[1]//span[@class='select_text']";
        		driver.findElement(By.xpath(divAdd)).click();
        		
        		selectAdd = "//form[@id='dropship_import_from_d']/div[@class='acc_item']/div[" + divIdx + "]//td[1]//div[@class='select_box']//li";
        		List<WebElement> shipType = driver.findElements(By.xpath(selectAdd));
        		
        		for (int j=0; j<shipType.size(); j++) {
        			if (shipType.get(j).getText().contains("Air Parcel Register")) {
        				shipType.get(j).click();
        				trackidExistFlg = true;
        				break;
        			}
        		}
        		
        		if (!trackidExistFlg) {
        			String tmpRecord  = "";
        			//form[@id='dropship_import_from_d']/div[@class='acc_item']/div[1]//div[@class='box']
        			tmpRecord = driver.findElement(By.xpath("//form[@id='dropship_import_from_d']/div[@class='acc_item']/div[" + divIdx + "]//div[@class='box']")).getText();
        			tmpOrderRp = new AmazonOrderReport();
        			
        			tmpOrderRp.setSku(tmpRecord);
        			tmpOrderRp.setReason("Air Parcel Shipment method does not exist");
        			tmpOrderRp.setLine("");
        			
        			orderReportFailList.add(tmpOrderRp);
        			
        			driver.findElement(By.xpath("//form[@id='dropship_import_from_d']/div[@class='acc_item']/div[" + divIdx + "]//div[@class='title']//a[@class='delect_btn']")).click();
        			
        			divIdx = divIdx - 1;
        		}
        	}
        	
        	driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        	
        	try {
        		driver.findElement(By.xpath("//form[@id='dropship_import_from_d']//input[@type='button']")).click();
        	} catch (TimeoutException e) {
        		driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
        	} finally {
        		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        	}
        	
        	Thread.sleep(8000);
        	
        	driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        	
        	try {
        		driver.navigate().to("https://www.banggood.com/index.php?ordersStatus=1&page=1&com=account&t=ordersList&search_type=c&pageSize=60&timeRange=0&warehouse=&keywords=&add_date_start=&add_date_end=&goPage=1");
        	} catch (TimeoutException e) {
        		driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
        	} finally {
        		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        	}
        	
        	dto = resultSub.get("SUCCESS");
        	Map<String, Integer> childOrderInfor = new HashMap<String, Integer>();
        	childOrderInfor = (Map<String, Integer>) resultSub.get("ORDERLIST").get(0);
        	
        	String htmlEle = "";
        	String bgOrderNo = "";
        	String saleId = "";
        	int eleNo = childOrderInfor.size();
        	int count = 0;
        	boolean fullFlg = false;
        	
        	List<WebElement> orderListCheckout = driver.findElements(By.xpath("//div[@class='order_num']"));
        	
        	for (int h=0; h<orderListCheckout.size(); h++) {
        		tmpOrderRp = new AmazonOrderReport();
        		
        		htmlEle = orderListCheckout.get(h).getAttribute("innerHTML");
        		saleId = htmlEle.substring(htmlEle.indexOf("<s>"), htmlEle.indexOf("</s>"));
        		saleId = saleId.replaceAll("[^\\d.]", "");
        		bgOrderNo = htmlEle.substring(htmlEle.indexOf("value="),htmlEle.indexOf("class=\"cbx\""));
        		bgOrderNo = bgOrderNo.replaceAll("[^\\d.]", "");
        		
        		if (childOrderInfor.containsKey(saleId)) {
        			tmpOrderRp = dto.get(childOrderInfor.get(saleId));
        			tmpOrderRp.setVendorOrdernumber(bgOrderNo);
        			//dto.set(childOrderInfor.get(saleId), tmpOrderRp);
        			normalList.add(tmpOrderRp);
        			count = count + 1;
        		}
        		if (count == eleNo) {
        			fullFlg = true;
        			break;
        		}
        	}
        	
        	boolean brkFlag = true;
        	if (!fullFlg) {
        	
	        	do {
		        	List<WebElement> pageList = driver.findElements(By.xpath("//a[@title='Next page']"));
		        	if (pageList.size() > 0) {
		        		
		        		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		            	
		            	try {
		            		pageList.get(0).click();
		            	} catch (TimeoutException e) {
		            		driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
		            	} finally {
		            		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		            	}
		        		
		        		orderListCheckout = driver.findElements(By.xpath("//div[@class='order_num']"));
		            	
		            	for (int h=0; h<orderListCheckout.size(); h++) {
		            		tmpOrderRp = new AmazonOrderReport();
		            		
		            		htmlEle = orderListCheckout.get(h).getAttribute("innerHTML");
		            		saleId = htmlEle.substring(htmlEle.indexOf("<s>"), htmlEle.indexOf("</s>"));
		            		saleId = saleId.replaceAll("[^\\d.]", "");
		            		bgOrderNo = htmlEle.substring(htmlEle.indexOf("value="),htmlEle.indexOf("class=\"cbx\""));
		            		bgOrderNo = bgOrderNo.replaceAll("[^\\d.]", "");
		            		
		            		if (childOrderInfor.containsKey(saleId)) {
		            			tmpOrderRp = dto.get(childOrderInfor.get(saleId));
		            			tmpOrderRp.setVendorOrdernumber(bgOrderNo);
		            			//dto.set(childOrderInfor.get(saleId), tmpOrderRp);
		            			normalList.add(tmpOrderRp);
		            			count = count + 1;
		            		}
		            		if (count == eleNo) {
		            			break;
		            		}
		            	}
		        		
		        	} else {
		        		brkFlag = false;
		        	}
	        	} while (brkFlag);
        	}
        	
            driver.close();
            driver.quit();
            
        } catch(Exception ioe) {
            ioe.printStackTrace();
        } finally {
            result.put("SUCCESS", normalList);
            result.put("FAIL", orderReportFailList);
            result.put("SHEET", new ArrayList<String>(Arrays.asList(sheetName)));
            
            try {
    	        Files.deleteIfExists(serverFile.toPath());
            } catch (IOException ioe) {
            	System.out.println("Can not delete file." + ioe);
            }
        }
        
        return result;
        
    }
	
	public WebElement findDynamicElement(By by, int timeOut, WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		return element;
	}
}

