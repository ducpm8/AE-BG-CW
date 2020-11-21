package com.web.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;

import com.google.common.base.Joiner;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DataGrabBGByName {
	
	private AmazonInventoryReport bgProductInfo;
	private AmazonFeeCal amazonCalculate;
	
	public static String homepage = "http://www.banggood.com";
	
	public void doPullPrice(File fileIn) throws IOException, JSONException
    {
		
		List<AmazonInventoryReport> bgProductInfoList = new ArrayList<AmazonInventoryReport>();
		
		amazonCalculate = new AmazonFeeCal();
		
		ChromeOptions op = new ChromeOptions();
    	op.addExtensions(new File("C:\\wd\\Block-image_v1.1.crx"));
    	WebDriver driver = new ChromeDriver(op);
		
		driver.manage().window().maximize();

        try {
        	
        	String productName = StringUtils.EMPTY;
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
            	
            	//driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
            	
            	try {
            		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
            		driver.navigate().to(homepage);
            	    //driver.findElement(By.xpath("//div[@class='country']//div[@class='active']"));
            	} catch (Exception e) {
        		  	//driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
            		  	
            	} finally {
            		driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
            	}
            	
            	String js = "";
    			js = "ship();";
    			if (driver instanceof JavascriptExecutor) {
    			    ((JavascriptExecutor)driver).executeScript(js);
    			} else {
    			    throw new IllegalArgumentException("This driver does not support JavaScript!");
    			}
            	
    			Thread.sleep(2000);
            	//ship()
            	//$(".country .active").trigger("click");
            	//Select US
    			driver.findElement(By.xpath("//div[@class='country_list']//div[@class='active']")).click();
    			
            	List<WebElement> country;
            	country = driver.findElements(By.xpath("//div[@class='country']//div[@class='country_box']//li"));
            	Thread.sleep(1000);
            	for (int o=0; o<country.size(); o++) {
            		System.out.println(country.get(o).getText());
            		if (country.get(o).getText().equals("United States")) {
            			country.get(o).click();
            			break;
            		}
            	}
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
            	int insetCount = 0;
            	
            	for (int i=0; i < workbook.getNumberOfSheets(); i++) {
                	HSSFSheet sheet = workbook.getSheetAt(i);
                	
                	for (int j=1; j <= sheet.getLastRowNum(); j++) {
                		rowHS = sheet.getRow(j);
                		if (rowHS != null) {
	                		cellHS = rowHS.getCell(0);
	                		if (cellHS != null && cellHS.getCellType() != Cell.CELL_TYPE_BLANK) {
	                			
	                			cellHS.setCellType(Cell.CELL_TYPE_STRING);
	                			productName = cellHS.getStringCellValue();
	                			
	                			cellHS2 = rowHS.getCell(1);
	                			
	                			cellHS2.setCellType(Cell.CELL_TYPE_STRING);
	                			
	                			String ASIN = cellHS2.getStringCellValue();
	                			
		                		try {
		                			
		                			//Check search result
		                			
		                			HashMap<String, String> listProduct = searchByName(driver, productName);
		                			
		                			if (listProduct.size() < 1) {
		                				//Product not found
		                				//Rename and research 2nd
		                				
		                				String newProductWONum = ProcessNameWithoutNumber(productName);
		                				listProduct = searchByName(driver, newProductWONum);
		                				
		                				if (listProduct.size() < 1) {
		                					//Rename and research 3rd
		                					String newProductWOFor = ProcessNameWithoutPurpose(productName);
			                				listProduct = searchByName(driver, newProductWOFor);
		                				}
		                				
		                				if (listProduct.size() < 1) {
		                					//no luck
		                					continue;
		                				}
		                			}
		                			
		                			//Click to first image
		                			//listProduct.get(0).click();
		                			//driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		                        	
		                			//Collect 3 first Product
		                			int limit = 0;
		                			if (listProduct.size() > 3) {
		                				limit = 3;
		                			} else {
		                				limit = listProduct.size();
		                			}
		                			
		                			int loopCount = 0;
		                			
		                			//for (int urlIndex=0; urlIndex<limit; urlIndex++) {
	                				for(Map.Entry<String, String> productURL_Name : listProduct.entrySet()) {
	                					try {
		                					loopCount++;
		                					if (loopCount > limit) {
		                						break;
		                					}
		                				    String productURL = productURL_Name.getKey();
		                				    String product_bg_name = productURL_Name.getValue();	
			                				
			                				bgProductInfo = new AmazonInventoryReport();
			                				try {
				                        		//Go to product
			                					driver.manage().timeouts().pageLoadTimeout(3, TimeUnit.MINUTES);
					                			driver.navigate().to(productURL);
				                        	} catch (TimeoutException e) {
				                        		  	//driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
				                        	} finally {
				                        		//driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
				                        	}
			                				
			                				String rating = driver.findElement(By.xpath("//li[@class='review']")).getText();
				                			
				                			price = driver.findElement(By.className("now")).getText();
				                			
				                			//Banggood ProductID
				                			String productID = "";
				        		    	    	
				        	    	    	if (driver instanceof JavascriptExecutor) {
				        		    	        productID =(String) ((JavascriptExecutor)driver).executeScript("return $('#products_id').val();");
				        		    	    } else {
				        		    	        throw new IllegalStateException("This driver does not support JavaScript!");
				        		    	    }
				        	    	    	bgProductInfo.setProductID(productID);
				                			
				                			//Button order click
				        	    	    	try {
				        	    	    		
				        	    	    		if (driver.findElements(By.className("buynow")).size() < 1) {
				        	    	    			continue;
				        	    	    		}
				        	    	    		
				                        		//Buy now
				        	    	    		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
				        	    	    		driver.findElement(By.className("buynow")).click();
				                        	} catch (Exception e) {
				                        		//driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
				                        	} finally {
				                        		driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
				                        	}
				                			
				        	    	    	try {
				        	    	    		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
					                			boolean doTheLoop = true;
					                    	    int h = 0;
					                    	    while (doTheLoop) {
					                    	        h = h+1;
					                    	        Thread.sleep(1000);
					                    	        if (h>3){
					                    	        	 throw new IllegalArgumentException("This product require size or color select"); 
					                    	        }
					                    	        if (driver.getCurrentUrl().indexOf("shopping_cart") > 0 ){
					                    	            break;
					                    	        }      
					        	            	}
				        	    	    	} catch (Exception e) {
				                        		//driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
				                        	} finally {
				                        		driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
				                        	}
				                    	    
				                    	    String shipment = "0";
				                    	    boolean urlRequestFailed = true;
				                    	    try {
					                    	    Set<Cookie> cookies = driver.manage().getCookies();
					                            //System.out.println("Size: " + cookies.size());
					                            String cookieStr = "";
		
					                            Iterator<Cookie> itr = cookies.iterator();
					                            while (itr.hasNext()) {
					                                Cookie cookie = itr.next();
		//			                                System.out.println(cookie.getName() + "\n" + cookie.getPath()
		//			                                        + "\n" + cookie.getDomain() + "\n" + cookie.getValue()
		//			                                        + "\n" + cookie.getExpiry());
					                                cookieStr = cookieStr + ";" + cookie.getName() + "=" + cookie.getValue();
					                            }
					                    	    
					                            cookieStr = cookieStr.substring(1);
					                    	    
					                    	    String warehouseID = "722";
					                    	    if (driver instanceof JavascriptExecutor) {
					                    	    	warehouseID =(String) ((JavascriptExecutor)driver).executeScript("return $('.warehouseCheckbox.middle_selectWarehouse_tapbar_170504.checkbox_on_active').attr('warehouse');");
					        		    	    } else {
					        		    	        throw new IllegalStateException("This driver does not support JavaScript!");
					        		    	    }
					                    	    
					                    	    StringBuilder responseText = new StringBuilder();
					                    	    URL url = new URL("https://www.banggood.com/index.php?com=shopcart&t=changeShipment&warehouse="+warehouseID+"&shipcode=hkairmail_hkairmail");
					                    	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					                    	    conn.setRequestMethod("GET");
					                    	    conn.setRequestProperty("Cookie",cookieStr);
					                    	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					                    	    String line;
					                    	    while ((line = rd.readLine()) != null) {
					                    	    	responseText.append(line);
					                    	    }
					                    	    rd.close();
					                    	    //System.out.println(responseText.toString());
					                    	    
					                    	    JSONObject jsonObject = new JSONObject(responseText.toString());
					                    	    String shipmentInfo = (String)jsonObject.get("shipments");
					                    	    
					                    	    Document doc = Jsoup.parse(shipmentInfo);
					                    		Elements lis = doc.getElementsByTag("li");
					                    		
					                    		for (Element li : lis) {
					                    			if (li.text().contains("Tracking number")) {
					                    				shipment = li.text().replaceAll("[^\\d.]", "");
					                    				urlRequestFailed = false;
					                    				break;
					                    			}
					                    		}
					                    		System.out.println("Request tracking fee " + urlRequestFailed);
				                    	    } catch (Exception ex) {
				                    	    	System.out.println("Get tracking fee failed 1st");
				                    	    	urlRequestFailed = true;
				                    	    }
	//			                    		System.out.println(shipment);
	//			                    	    
				                    	    //Click to selectbox Shipment
				                    	    if (urlRequestFailed) {
				                    	    	try {
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
								                			
							                    	    	Set<Cookie> allCookies = driver.manage().getCookies();
									                        for (Cookie cookie : allCookies) {
									                        	if (!cookie.getName().contains("default_ship_country") && !cookie.getName().contains("currency")) {
									                        		driver.manage().deleteCookieNamed(cookie.getName());	
									                        	}
									                        }
								                			driver.navigate().to(homepage);
								                			continue;
							                    	    }
							                    	    
							                    	    Thread.sleep(1000);
							                    	    
							                    	    String pDecide = "";
							                    	    pDecide = "//span[@class='td_shipping_list']/p[" + tmpI + "]";
							                    	    driver.findElement(By.xpath(pDecide)).click();
						                    	    }
						                    	    
						                    	  //Radio Tracking ID exist check
						                    	    Thread.sleep(500);
						                    	    if (driver.findElements(By.xpath("//span[@class='tracking_info']/ol/li[2]")).size() < 1) {
			
						                    	    	Set<Cookie> allCookies = driver.manage().getCookies();
								                        for (Cookie cookie : allCookies) {
								                        	if (!cookie.getName().contains("default_ship_country") && !cookie.getName().contains("currency")) {
								                        		driver.manage().deleteCookieNamed(cookie.getName());	
								                        	}
								                        }
							                			driver.navigate().to(homepage);
							                			continue;
						                    	    }
						                    	    
						                    	    String radioName = "";
						                    	    
						                    	    radioName = driver.findElement(By.xpath("//span[@class='tracking_info']/ol/li[2]")).getText();
						                    	    if (radioName.indexOf("Tracking number") >= 0) {
						                    	    	shipment = radioName.replaceAll("[^\\d.]", "");
						                    	    }
						                    	    
				                    	    	} catch (Exception ee) {
				                    	    		System.out.println("Get tracking fee failed 2nd");
				                    	    		continue;
				                    	    	}
				                    	    }
				                    	    
				                    	    bgProductInfo.setShippingCostEpacket(shipment);
				                    	    bgProductInfo.setURL(productURL);
				                    	    
					                        bgProductInfo.setMinAbsoluteMargin("3");
					                        bgProductInfo.setMarginTargetPercent("50");
					                		
				                        	bgProductInfo.setASIN(ASIN);
					                        
					                        bgProductInfo.setCostFromAE(price);
					                        bgProductInfo.setProductName(productName);
					                        
					                        bgProductInfo.setAssignedAmazonSku(productName + "-BG");
					                        bgProductInfo.setLine(String.valueOf(j+1));
					                        
					                        bgProductInfo.setBg_product_name(product_bg_name);
					                        
					                        //Price calculation
					                        bgProductInfo = amazonCalculate.priceCalculate(bgProductInfo);
					                        
					                        //Rating
					                        bgProductInfo.setRating(rating);
					                        
					                        bgProductInfoList.add(bgProductInfo);
					                        insetCount++;
					                        if (insetCount == 10) {
					                        	updateDB(bgProductInfoList);
					                        	insetCount=0;
					                        	bgProductInfoList = new ArrayList<AmazonInventoryReport>();
					                        }
					                        
					                        //driver.manage().deleteAllCookies();
					                        Set<Cookie> allCookies = driver.manage().getCookies();
					                        for (Cookie cookie : allCookies) {
					                        	if (!cookie.getName().contains("default_ship_country") && !cookie.getName().contains("currency")) {
					                        		driver.manage().deleteCookieNamed(cookie.getName());	
					                        	}
					                        }
				                    	    
			                			} catch (Exception iee) {
			                				System.out.println("error in loop " + iee.getMessage());
			                			}
	                				} 
			                    
		                		} catch (IllegalArgumentException iae) {
		                			
		                			updateDBFail(ASIN);
		                			
		                			Set<Cookie> allCookies = driver.manage().getCookies();
			                        for (Cookie cookie : allCookies) {
			                        	if (!cookie.getName().contains("default_ship_country") && !cookie.getName().contains("currency")) {
			                        		driver.manage().deleteCookieNamed(cookie.getName());	
			                        	}
			                        }
		                			
			                        try {
			                        	driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
			                        	driver.navigate().to(homepage);
			                        } catch (Exception e) {
			                        	driver.navigate().refresh();
			                        }
			                        
			                        System.out.println("Exception " + iae.getMessage());
		                			
		                			continue;
		                			
		                		} catch (Exception ex) {
		                			//Error occurred in Selenium
		                			updateDBFail(ASIN);
		                			
		                			Set<Cookie> allCookies = driver.manage().getCookies();
			                        for (Cookie cookie : allCookies) {
			                        	if (!cookie.getName().contains("default_ship_country") && !cookie.getName().contains("currency")) {
			                        		driver.manage().deleteCookieNamed(cookie.getName());	
			                        	}
			                        }
		                			
			                        try {
			                        	driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
			                        	driver.navigate().to(homepage);
			                        } catch (Exception e) {
			                        	driver.navigate().refresh();
			                        }
			                        
			                        System.out.println("Exception " + ex.getMessage());
		                			continue;
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
        }
    }
	
	public String ProcessNameWithoutNumber(String oriName) {
		String newName = "";
		String[] oriNameArr = oriName.split(" ");
		
		for (int i=0; i<oriNameArr.length; i++) {
			
			//Check if Part doesn't contain any number
			if (!oriNameArr[i].matches(".*\\d+.*")) {
				newName = newName + " " + oriNameArr[i];
			}
			
		}
		
		if (!newName.isEmpty())
		newName = newName.substring(1);
		
		return newName;
	}
	
	public String ProcessNameWithoutPurpose(String oriName) {
		String newName = "";
		
		if (oriName.toLowerCase().contains("for")) {
			newName = oriName.substring(0, oriName.toLowerCase().indexOf("for") - 1);
		}
		
		return newName;
	}
	
	public HashMap<String, String> searchByName(WebDriver driver, String productName) throws InterruptedException {
		//List<String> listURL = new ArrayList<String>();
		HashMap<String, String> listURL = new HashMap<String, String>();
		
		try {
    		driver.manage().timeouts().pageLoadTimeout(3, TimeUnit.MINUTES);
    		driver.navigate().to(homepage);
    	    //driver.findElement(By.xpath("//div[@class='country']//div[@class='active']"));
    	} catch (Exception e) {
		  	//driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
    		  	
    	} finally {
    		//driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
    	}
		
		try {
			boolean loopF = true;
			int loopCnt = 0;
			while (loopF) {
				loopCnt++;
				if (loopCnt > 10) {
					return listURL;
				}
				try {
					//Input productID
					driver.findElement(By.xpath("//input[@name='keywords']")).clear();
					driver.findElement(By.xpath("//input[@name='keywords']")).sendKeys(productName);
					break;
				} catch (Exception e) {
					System.out.println(e.getMessage());
					driver.navigate().to(homepage);
					Thread.sleep(1000);
				}
			}
	    	
	    	try {
	    		//Click Search
	    		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
				driver.findElement(By.xpath("//input[@type='submit']")).click();
	    	} catch (TimeoutException e) {
	    		  	//driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
	    	} finally {
	    		driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
	    	}
			
			//Check search result
			List<WebElement> listProduct = driver.findElements(By.xpath("//div[@class='good_box_min '][1]/ul[@class='goodlist_1 ']/li/span[@class='title']/a"));
			for (int i=0; i<listProduct.size(); i++) {
				//listURL.add(listProduct.get(i).getAttribute("href"));
				listURL.put(listProduct.get(i).getAttribute("href"), listProduct.get(i).getText());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return listURL;
	}
	
	public void updateDB(List<AmazonInventoryReport> objLst) throws SQLException, UnsupportedEncodingException {
		
		Connection connection = null;
		Statement statement = null;
		
		try {
		
			Class.forName("org.sqlite.JDBC");
		    connection = DriverManager.getConnection("jdbc:sqlite::resource:dropship.s3db");
		    statement = connection.createStatement();
			
			String result;
			String sum = "";
			
			for (int k=0; k<objLst.size(); k++) {
				result = Joiner.on("', '").join(Arrays.asList(
						objLst.get(k).getProductID(),
						objLst.get(k).getAbsoluteListPrice(),
						"30",
						objLst.get(k).getASIN(), 
						"ASIN",
						"New"));
				if (!result.isEmpty()) {
				  result = "('" + result + "'),";
				  sum = sum + result;
				}
			}
			
			sum = sum.substring(0, sum.length() -1);
			sum = "insert into pricing_bg_new VALUES " + sum;
			
			statement.executeUpdate(sum);
			
			System.out.println("Updated to DB 20 records");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			try { statement.close(); } catch (Exception e) { /* ignored */ }
            try { connection.close(); } catch (Exception e) { /* ignored */ }
		}
	}
	
	public void updateDBFail(String ASIN) throws SQLException, UnsupportedEncodingException {
		
		Connection connection = null;
		Statement statement = null;
		
		try {
		
			Class.forName("org.sqlite.JDBC");
		    connection = DriverManager.getConnection("jdbc:sqlite::resource:dropship.s3db");
		    statement = connection.createStatement();
			
			String sum = "";
			sum = "insert into pricing_bg_new_fail VALUES ('" + ASIN + "')";
			
			statement.executeUpdate(sum);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			try { statement.close(); } catch (Exception e) { /* ignored */ }
            try { connection.close(); } catch (Exception e) { /* ignored */ }
		}
	}
	
	private static String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (String key : params.keySet())
	    {
	        if (first) {
	            first = false;
	            result.append("?");
	        } else {
	            result.append("&");
	        }

	        result.append(URLEncoder.encode(key, "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(params.get(key), "UTF-8"));
	    }

	    return result.toString();
	}
}

