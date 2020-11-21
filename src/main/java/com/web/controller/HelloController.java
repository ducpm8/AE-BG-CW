package com.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.web.entity.*;
import com.web.util.BulkImportFile;
import com.web.util.BulkImportOrder;
import com.web.util.CSVUtils;
import com.web.util.FileUpload;
import com.web.util.Recaptcha;
import com.web.util.SendAttachmentInEmail;

import org.springframework.web.multipart.MultipartFile;

import com.web.util.config.CommonProperties;
import com.web.data.AsinGrab;
import com.web.data.BanggoodOrderPlace;
import com.web.data.DataGrab;
import com.web.data.DataGrabBG;
import com.web.data.DataGrabBGByName;
import com.web.data.DataGrabP2;
import com.web.data.DataGrabP2_DBC;
import com.web.data.DataGrabP3;
import com.web.data.DataGrabP4;
import com.web.data.FeaturesScraper;
import com.web.data.P2SubmitData;

@Controller
@RequestMapping(value = "/home")
public class HelloController {
	
	public DataGrab dataGrab;
	public DataGrabBG dataGrabBG;
	public DataGrabP2 dataGrabP2;
	public DataGrabP3 dataGrabP3;
	public DataGrabP4 dataGrabP4;
	public AsinGrab asinGrab;
	public BanggoodOrderPlace bgorder;
	
	final static Logger logger = Logger.getLogger(HelloController.class);
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) throws IOException, URISyntaxException {
		
		CommonProperties.loadProperties();
		
		//System.out.println(CommonProperties.getHom001Title());
		
		return "project1";
	}
	
	@RequestMapping(value = "/wm", method = RequestMethod.GET)
	public String wmURLGather(ModelMap model) throws IOException, URISyntaxException {
		
		FeaturesScraper featureGrab = new FeaturesScraper();
		featureGrab.walmartURL();
		
		return "project1";
	}
	
	@RequestMapping(value = "/bgbn", method = RequestMethod.GET)
	public String bgByNameInit(ModelMap model) throws IOException, URISyntaxException {
		return "bgbn";
	}
	
	@RequestMapping(value = "/dbc", method = RequestMethod.GET)
	public String dbc(ModelMap model) throws IOException, URISyntaxException, InterruptedException {
		
		Recaptcha dbc = new Recaptcha();
		
		dbc.getPixel("D:\\1.jpg");
		
		return "bgbn";
	}
	
	@RequestMapping(value = "/bgbn", method = RequestMethod.POST)
	public String bgByName(ModelMap model, @ModelAttribute P2SubmitData submitData) throws IOException, URISyntaxException, JSONException {
		
		DataGrabBGByName bgbn = new DataGrabBGByName();
		
		for (MultipartFile pricingFile : submitData.getFilePricing()) {
			
			if (pricingFile == null || pricingFile.isEmpty())
				continue;
			
				String rootPath = System.getProperty("catalina.home");
				File dir = new File(rootPath + File.separator + "tmpFiles");
				if (!dir.exists())
					dir.mkdirs();

				logger.info("Create temp folder");
				File fileIn = new File(pricingFile.getOriginalFilename());
				pricingFile.transferTo(fileIn);
				bgbn.doPullPrice(fileIn);
		}
		
		return "bgbn";
	}
	
	@RequestMapping(value = "/banggoodorder", method = RequestMethod.GET)
	public String project5(ModelMap model) {
		return "bgorder";
	}
	
	@RequestMapping(value = "/pj5", method = RequestMethod.GET)
	public String project6(ModelMap model) {
		return "project5";
	}
	
	@RequestMapping(value = "/pj4", method = RequestMethod.GET)
	public String project4(ModelMap model) {
		return "project4";
	}
	
	@RequestMapping(value = "/pj3", method = RequestMethod.GET)
	public String project3(ModelMap model) {
		return "project3";
	}
	
	@RequestMapping(value = "/pj2", method = RequestMethod.GET)
	public String project2(ModelMap model) {
		return "project2";
	}
	
	@RequestMapping(value = "/pj1", method = RequestMethod.GET)
	public String project1(ModelMap model) {
		return "project1";
	}

	@RequestMapping(value = "/hello/{name:.+}", method = RequestMethod.GET)
	public ModelAndView hello(@PathVariable("name") String name) {

		ModelAndView model = new ModelAndView();
		model.setViewName("hello");
		model.addObject("msg", name);

		return model;

	}
	
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody String uploadFileHandler(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute P2SubmitData submitData) throws IOException {
		
		if ((submitData.getFile() != null && !submitData.getFile().isEmpty()) 
				|| (submitData.getFilePricing() != null && submitData.getFilePricing().size() > 0)) {
			if (submitData.getProjectId().equals("1")){
				
				List<File> fileDownList = new ArrayList<File>();
				List<File> fileUpList = new ArrayList<File>();
				
				for (MultipartFile pricingFile : submitData.getFilePricing()) {
					
					if (pricingFile == null || pricingFile.isEmpty())
						continue;
					
					try {
						
						// Creating the directory to store file
						String rootPath = System.getProperty("catalina.home");
						File dir = new File(rootPath + File.separator + "tmpFiles");
						if (!dir.exists())
							dir.mkdirs();
		
						logger.info("Create temp folder");
						File fileIn = new File(pricingFile.getOriginalFilename());
						pricingFile.transferTo(fileIn);
						
						fileUpList.add(fileIn);
						
						Map<String, List> result = new HashMap<String, List>();
						
						List<AmazonInventoryReport> dto = new ArrayList<AmazonInventoryReport>();
						
						dataGrab = new DataGrab();
						dataGrabBG =  new DataGrabBG();
						
						if (submitData.getVendor().equals("AE")) {
							logger.info("AE process");
							result = dataGrab.doPullPrice(fileIn, submitData.getSheetName());
						} else if (submitData.getVendor().equals("BG")) {
							logger.info("BG process");
							result = dataGrabBG.doPullPrice(fileIn, submitData.getSheetName());
						}
						
						dto = result.get("SUCCESS");
						
						//Write to CSV file
						final String csvFile = dir.getAbsolutePath() + File.separator +  fileIn.getName().replace(".", "") + "_" + result.get("SHEET").get(0)  + ".csv";
				        FileWriter writer = new FileWriter(csvFile);
				        String mailBody = "";
				        
				        logger.info("Order finish, success csv prepare.");
				        
				        CSVUtils.writeLine(writer, Arrays.asList("SKU ","Assigned Amazon Sku","ASIN","Cost From AE","Shipping Cost (Epacket)","Amazon Fee","COGS","Margin Target (%)","Margin Target ($)","Amz Listing Price Min","Min Absolute Margin","Suspect List Price","Absolute List Price","Pull Numbers","URL"));
				        
				        if (dto != null) {
				        	mailBody = "Success : " + dto.size() + "\r\n";
					        for (int i=0; i < dto.size(); i++) {
					        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getProductID(), dto.get(i).getAssignedAmazonSku(), dto.get(i).getASIN(), dto.get(i).getCostFromAE(), dto.get(i).getShippingCostEpacket(), dto.get(i).getAmazonFee(), dto.get(i).getCOGS(), dto.get(i).getMarginTargetPercent(), dto.get(i).getMarginTargetDolar(), dto.get(i).getAmzListingPriceMin(), dto.get(i).getMinAbsoluteMargin(), dto.get(i).getSuspectListPrice(), dto.get(i).getAbsoluteListPrice(), dto.get(i).getLine(), dto.get(i).getURL()), ',', '"');
					        }
				        }
		
				        writer.flush();
				        writer.close();
				        
				        //Download
				        File fileDown = new File(csvFile);
						if(!fileDown.exists()){
							throw new ServletException("File doesn't exists on server.");
						}
						System.out.println("File location on server::" + fileDown.getAbsolutePath());
						
						fileDownList.add(fileDown);
						
						dir = new File(rootPath + File.separator + "tmpFiles" + File.separator + "Fail");
						if (!dir.exists())
							dir.mkdirs();
				        
				        dto = new ArrayList<AmazonInventoryReport>();
				        
				        dto = result.get("FAIL");
				        
				        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss");//dd/MM/yyyy
				        Date now = new Date();
				        String strDate = sdfDate.format(now);
				        
				        if (dto != null && dto.size() > 0) {
				        	logger.info("Fail csv prepare.");
				        	mailBody = mailBody + " Failed : " + dto.size();
				        	
				        	final String csvFileFail = dir.getAbsolutePath() + File.separator + "Fail_" + strDate + ".csv";
					        writer = new FileWriter(csvFileFail);
					        
					        CSVUtils.writeLine(writer, Arrays.asList("SKU ","ASIN","URL","Reason","Input Line"));
					        
					        for (int i=0; i < dto.size(); i++) {
					        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getProductID(), dto.get(i).getASIN(), dto.get(i).getURL(), dto.get(i).getReason(), dto.get(i).getLine()), ',', '"');
					        }
					        
					        writer.flush();
					        writer.close();
					        
					        logger.info("Write fail csv to disk.");
					        SendAttachmentInEmail.sendMail("Inventory Process", mailBody, new ArrayList<String>() {{add(csvFileFail);}});
				        	
					        logger.info("Email sent.");
				        } else {
				        	SendAttachmentInEmail.sendMail("Inventory Process", mailBody, new ArrayList<String>());
				        	logger.info("Email sent. success all.");
				        }
				        
						//return "You successfully uploaded file=" + pricingFile.getOriginalFilename();
					} catch(RuntimeException re) {
						logger.error("RuntimeException " + re.getMessage());
						//return "Email send fail";
					} catch (Exception e) {
						logger.error("Exception " + e.getMessage());
						//return "You failed to upload " + pricingFile.getOriginalFilename() + " => " + e.getMessage();
					}
				}
				
				response.setContentType("Content-type: text/zip");
				response.setHeader("Content-Disposition","attachment; filename=pricing.zip");
				// List of files to be downloaded
				
				ServletOutputStream out = response.getOutputStream();
				ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out));

				for (File file : fileDownList) {

					System.out.println("Adding " + file.getName());
					zos.putNextEntry(new ZipEntry(file.getName()));

					// Get the file
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);

					} catch (FileNotFoundException fnfe) {
						// If the file does not exists, write an error entry instead of
						// file
						// contents
						zos.write(("ERROR not find file " + file.getName()).getBytes());
						zos.closeEntry();
						System.out.println("Couldfind file " + file.getAbsolutePath());
						continue;
					}

					BufferedInputStream fif = new BufferedInputStream(fis);

					// Write the contents of the file
					int data = 0;
					while ((data = fif.read()) != -1) {
						zos.write(data);
					}
					fif.close();

					zos.closeEntry();
					System.out.println("Finished file " + file.getName());
				}

				zos.close();
				
				for (File fileDown : fileDownList) {
					try {
				        boolean delResult = Files.deleteIfExists(fileDown.toPath()); 
			        } catch (IOException ioe) {
			        	logger.error("Can not delete file " + fileDown.toPath());
			        	//return "You successfully uploaded file=" + pricingFile.getOriginalFilename();
			        }
				}
				
				for (File fileUp : fileUpList) {
					try {
				        boolean delResult = Files.deleteIfExists(fileUp.toPath()); 
			        } catch (IOException ioe) {
			        	logger.error("Can not delete file " + fileUp.toPath());
			        }
				}
				
				return "You successfully uploaded file";
			} else if (submitData.getProjectId().equals("2")) {
				
				try {
					byte[] bytes = submitData.getFile().getBytes();
	
					// Creating the directory to store file
					String rootPath = System.getProperty("catalina.home");
					File dir = new File(rootPath + File.separator + "tmpFiles");
					if (!dir.exists())
						dir.mkdirs();
					
					String filePath = "";
					
					filePath = dir.getAbsolutePath() + File.separator + submitData.getFile().getOriginalFilename();
	
					// Create the file on server
					File serverFile = new File(filePath);
					BufferedOutputStream stream = new BufferedOutputStream(
							new FileOutputStream(serverFile));
					stream.write(bytes);
					stream.close();
	
					Map<String, List> result = new HashMap<String, List>();
					List<AmazonOrderReport> dto = new ArrayList<AmazonOrderReport>();
					String mailBody = "";
					
					dataGrabP2 = new DataGrabP2();
					
					//DataGrabP2_DBC dataGrabP2DBC = new DataGrabP2_DBC();
					
					result = dataGrabP2.doPlaceOrder(filePath, submitData);
					
					//result = dataGrabP2.doPlaceOrder(filePath, submitData);
					
					String profit = "";
					
					dto = result.get("SUCCESS");
					
					if (result.get("PROFIT") != null) {
						profit =(String) result.get("PROFIT").get(0);
						if (!profit.isEmpty()) {
							mailBody = "Profit : " + profit + "\r\n";
						}
					}
					
					//Write to CSV file
					String csvFile = dir.getAbsolutePath() + File.separator +  result.get("SHEET").get(0)  + ".csv";
			        FileWriter writer = new FileWriter(csvFile);
			        
			        CSVUtils.writeLine(writer, Arrays.asList("order-id",
			        		"order-item-id",
			        		"Product Name",
			        		"Total Pruchase Price",
			        		"Total Customer Spend on Order",
			        		"Margin","Margin (%)",
			        		"Vendor Order number",
			        		"Tracking Number",
			        		"payments-date",
			        		"purchase-date",
			        		"reporting-date",
			        		"promise-date",
			        		"days-past-promise",
			        		"buyer-email",
			        		"buyer-name",
			        		"buyer-phone-number",
			        		"sku",
			        		"product-name",
			        		"quantity-purchased",
			        		"quantity-shipped",
			        		"quantity-to-ship",
			        		"ship-service-level",
			        		"recipient-name",
			        		"ship-address-1",
			        		"ship-address-2",
			        		"ship-address-3",
			        		"ship-city",
			        		"ship-state",
			        		"ship-postal-code",
			        		"ship-country"));
			        
			        if (dto != null) {
			        	mailBody = mailBody + "Success : " + dto.size() + "\r\n";
				        for (int i=0; i < dto.size(); i++) {
				        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getOrderId(), 
				        			dto.get(i).getOrderItemId(),
				        			dto.get(i).getProductName(),
				        			dto.get(i).getTotalPruchasePrice(),
				        			dto.get(i).getTotalCustomerSpendonOrder(),
				        			dto.get(i).getMarginDollar(),
				        			dto.get(i).getMarginPercent(),
				        			dto.get(i).getVendorOrdernumber(),
				        			dto.get(i).getTrackingNumber(),
				        			dto.get(i).getPaymentsDate(),
				        			dto.get(i).getPurchaseDate(),
				        			dto.get(i).getReportingDate(),
				        			dto.get(i).getPromiseDate(),
				        			dto.get(i).getDaysPastPromise(),
				        			dto.get(i).getBuyerEmail(),
				        			dto.get(i).getBuyerName(),
				        			dto.get(i).getBuyerPhoneNumber(),
				        			dto.get(i).getSku(),
				        			dto.get(i).getProductName2(),
				        			dto.get(i).getQuantityPurchased(),
				        			dto.get(i).getQuantityShipped(),
				        			dto.get(i).getQuantityToShip(),
				        			dto.get(i).getShipServiceLevel(),
				        			dto.get(i).getRecipientName(),
				        			dto.get(i).getShipAddress1(),
				        			dto.get(i).getShipAddress2(),
				        			dto.get(i).getShipAddress3(),
				        			dto.get(i).getShipCity(),
				        			dto.get(i).getShipState(),
				        			dto.get(i).getShipPostalCode(),
				        			dto.get(i).getShipCountry()), ',', '"');
				        }
			        }
	
			        writer.flush();
			        writer.close();
			        
			        //Download
			        File fileDown = new File(csvFile);
					if(!fileDown.exists()){
						throw new ServletException("File doesn't exists on server.");
					}
					System.out.println("File location on server::" + fileDown.getAbsolutePath());
					//ServletContext ctx = getServletContext();
					InputStream fis = new FileInputStream(fileDown);
					//String mimeType = ctx.getMimeType(fileDown.getAbsolutePath());
					response.setContentType("application/octet-stream");
					response.setContentLength((int) fileDown.length());
					response.setHeader("Content-Disposition", "attachment; filename=\"" + result.get("SHEET").get(0)  + ".csv" + "\"");
	
					ServletOutputStream os = response.getOutputStream();
					byte[] bufferData = new byte[1024];
					int read=0;
					while((read = fis.read(bufferData))!= -1){
						os.write(bufferData, 0, read);
					}
					os.flush();
					os.close();
					fis.close();
					
					dir = new File(rootPath + File.separator + "tmpFiles" + File.separator + "Fail");
					if (!dir.exists())
						dir.mkdirs();
			        
			        dto = new ArrayList<AmazonOrderReport>();
			        
			        dto = result.get("FAIL");
			        
			        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss");//dd/MM/yyyy
			        Date now = new Date();
			        String strDate = sdfDate.format(now);
			        
			        if (dto != null && dto.size() > 0) {
			        	mailBody = mailBody + " Failed : " + dto.size();
			        	final String csvFileFail = dir.getAbsolutePath() + File.separator + "Fail_" + strDate + ".csv";
				        writer = new FileWriter(csvFileFail);
				        
				        CSVUtils.writeLine(writer, Arrays.asList("SKU ","Product Name","URL","Reason","Input Line"));
				        
				        for (int i=0; i < dto.size(); i++) {
				        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getSku(),dto.get(i).getProductName(), dto.get(i).getURL(), dto.get(i).getReason(), dto.get(i).getLine()), ',', '"');
				        }
				        
				        writer.flush();
				        writer.close();
				        SendAttachmentInEmail.sendMail("Inventory Process", mailBody, new ArrayList<String>() {{add(csvFileFail);}});
			        	
			        } else {
			        	SendAttachmentInEmail.sendMail("Inventory Process", mailBody, new ArrayList<String>());
			        }
			        
			        try {
				        boolean delResult = Files.deleteIfExists(fileDown.toPath()); 
				        delResult = Files.deleteIfExists(serverFile.toPath());
			        } catch (IOException ioe) {
			        	System.out.println("Can not delete file." + ioe);
			        	return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
			        }
	
					return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
				} catch (Exception e) {
					return "You failed to upload " + submitData.getFile().getOriginalFilename() + " => " + e.getMessage();
				}
				
			}
		} else {
			return "You failed to upload " + submitData.getFile().getOriginalFilename()
					+ " because the file was empty.";
		}
		
		return "OK";
	}
	
	@RequestMapping(value = "/uploadFileP3", method = RequestMethod.POST)
	public @ResponseBody String uploadFileHandlerP3(HttpServletRequest request, HttpServletResponse response, @ModelAttribute P2SubmitData submitData) {
		if (!submitData.getFile().isEmpty()) {
			if (submitData.getProjectId().equals("3")) {
				
				try {
					byte[] bytes = submitData.getFile().getBytes();
	
					// Creating the directory to store file
					String rootPath = System.getProperty("catalina.home");
					File dir = new File(rootPath + File.separator + "tmpFiles");
					if (!dir.exists())
						dir.mkdirs();
					
					String filePath = "";
					
					filePath = dir.getAbsolutePath() + File.separator + submitData.getFile().getOriginalFilename();
	
					// Create the file on server
					File serverFile = new File(filePath);
					BufferedOutputStream stream = new BufferedOutputStream(
							new FileOutputStream(serverFile));
					stream.write(bytes);
					stream.close();
	
	//				logger.info("Server File Location="
	//						+ serverFile.getAbsolutePath());
					
					
					Map<String, List> result = new HashMap<String, List>();
					
					List<AmazonOrderReport> dto = new ArrayList<AmazonOrderReport>();
					
					dataGrabP3 = new DataGrabP3();
					
					result = dataGrabP3.doCollectTrackingNo(filePath, submitData);
					
					dto = result.get("SUCCESS");
					
					//Write to CSV file
					String csvFile = dir.getAbsolutePath() + File.separator +  result.get("SHEET").get(0)  + ".csv";
			        FileWriter writer = new FileWriter(csvFile);
			        
			        CSVUtils.writeLine(writer, Arrays.asList("order-id",
			        		"order-item-id",
			        		"Product Name",
			        		"Total Pruchase Price",
			        		"Total Customer Spend on Order",
			        		"Margin","Margin (%)",
			        		"Vendor Order number",
			        		"Tracking Number",
			        		"payments-date",
			        		"purchase-date",
			        		"reporting-date",
			        		"promise-date",
			        		"days-past-promise",
			        		"buyer-email",
			        		"buyer-name",
			        		"buyer-phone-number",
			        		"sku",
			        		"product-name",
			        		"quantity-purchased",
			        		"quantity-shipped",
			        		"quantity-to-ship",
			        		"ship-service-level",
			        		"recipient-name",
			        		"ship-address-1",
			        		"ship-address-2",
			        		"ship-address-3",
			        		"ship-city",
			        		"ship-state",
			        		"ship-postal-code",
			        		"ship-country"));
			        
			        if (dto != null) {
				        for (int i=0; i < dto.size(); i++) {
				        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getOrderId(), 
				        			dto.get(i).getOrderItemId(),
				        			dto.get(i).getProductName(),
				        			dto.get(i).getTotalPruchasePrice(),
				        			dto.get(i).getTotalCustomerSpendonOrder(),
				        			dto.get(i).getMarginDollar(),
				        			dto.get(i).getMarginPercent(),
				        			dto.get(i).getVendorOrdernumber(),
				        			dto.get(i).getTrackingNumber(),
				        			dto.get(i).getPaymentsDate(),
				        			dto.get(i).getPurchaseDate(),
				        			dto.get(i).getReportingDate(),
				        			dto.get(i).getPromiseDate(),
				        			dto.get(i).getDaysPastPromise(),
				        			dto.get(i).getBuyerEmail(),
				        			dto.get(i).getBuyerName(),
				        			dto.get(i).getBuyerPhoneNumber(),
				        			dto.get(i).getSku(),
				        			dto.get(i).getProductName2(),
				        			dto.get(i).getQuantityPurchased(),
				        			dto.get(i).getQuantityShipped(),
				        			dto.get(i).getQuantityToShip(),
				        			dto.get(i).getShipServiceLevel(),
				        			dto.get(i).getRecipientName(),
				        			dto.get(i).getShipAddress1(),
				        			dto.get(i).getShipAddress2(),
				        			dto.get(i).getShipAddress3(),
				        			dto.get(i).getShipCity(),
				        			dto.get(i).getShipState(),
				        			dto.get(i).getShipPostalCode(),
				        			dto.get(i).getShipCountry()), ',', '"');
				        }
			        }
	
			        writer.flush();
			        writer.close();
			        
			        //Download
			        File fileDown = new File(csvFile);
					if(!fileDown.exists()){
						throw new ServletException("File doesn't exists on server.");
					}
					System.out.println("File location on server::" + fileDown.getAbsolutePath());
					//ServletContext ctx = getServletContext();
					InputStream fis = new FileInputStream(fileDown);
					//String mimeType = ctx.getMimeType(fileDown.getAbsolutePath());
					response.setContentType("application/octet-stream");
					response.setContentLength((int) fileDown.length());
					response.setHeader("Content-Disposition", "attachment; filename=\"" + result.get("SHEET").get(0)  + ".csv" + "\"");
	
					ServletOutputStream os = response.getOutputStream();
					byte[] bufferData = new byte[1024];
					int read=0;
					while((read = fis.read(bufferData))!= -1){
						os.write(bufferData, 0, read);
					}
					os.flush();
					os.close();
					fis.close();
					
					dir = new File(rootPath + File.separator + "tmpFiles" + File.separator + "Fail");
					if (!dir.exists())
						dir.mkdirs();
			        
			        dto = new ArrayList<AmazonOrderReport>();
			        
			        dto = result.get("FAIL");
			        
			        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss");//dd/MM/yyyy
			        Date now = new Date();
			        String strDate = sdfDate.format(now);
			        
			        if (dto != null && dto.size() > 0) {
			        	csvFile = dir.getAbsolutePath() + File.separator + "Fail_" + strDate + ".csv";
				        writer = new FileWriter(csvFile);
				        
				        CSVUtils.writeLine(writer, Arrays.asList("Order Number","URL","Reason","Input Line"));
				        
				        for (int i=0; i < dto.size(); i++) {
				        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getVendorOrdernumber(), dto.get(i).getURL(), dto.get(i).getReason(), dto.get(i).getLine()), ',', '"');
				        }
				        
				        writer.flush();
				        writer.close();
			        	
			        }
			        
			        try {
				        boolean delResult = Files.deleteIfExists(fileDown.toPath()); 
				        delResult = Files.deleteIfExists(serverFile.toPath());
			        } catch (IOException ioe) {
			        	System.out.println("Can not delete file." + ioe);
			        	return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
			        }
	
					return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
				} catch (Exception e) {
					return "You failed to upload " + submitData.getFile().getOriginalFilename() + " => " + e.getMessage();
				}
				
			}
		} else {
			return "You failed to upload " + submitData.getFile().getOriginalFilename()
					+ " because the file was empty.";
		}
		
		return "OK";
	}
	
	@RequestMapping(value = "/uploadFileP4", method = RequestMethod.POST)
	public @ResponseBody String uploadFileHandlerP4(HttpServletRequest request, HttpServletResponse response, @ModelAttribute P2SubmitData submitData) {
		if (!submitData.getFile().isEmpty()) {
			if (submitData.getProjectId().equals("3")) {
				
				try {
					byte[] bytes = submitData.getFile().getBytes();
	
					// Creating the directory to store file
					String rootPath = System.getProperty("catalina.home");
					File dir = new File(rootPath + File.separator + "tmpFiles");
					if (!dir.exists())
						dir.mkdirs();
					
					String filePath = "";
					
					filePath = dir.getAbsolutePath() + File.separator + submitData.getFile().getOriginalFilename();
	
					// Create the file on server
					File serverFile = new File(filePath);
					BufferedOutputStream stream = new BufferedOutputStream(
							new FileOutputStream(serverFile));
					stream.write(bytes);
					stream.close();
	
	//				logger.info("Server File Location="
	//						+ serverFile.getAbsolutePath());
					
					
					Map<String, List> result = new HashMap<String, List>();
					
					List<AmazonOrderReport> dto = new ArrayList<AmazonOrderReport>();
					
					dataGrabP4 = new DataGrabP4();
					
					result = dataGrabP4.doCollectTrackingDetail(filePath, submitData);
					
					dto = result.get("SUCCESS");
					
					//Write to CSV file
					String csvFile = dir.getAbsolutePath() + File.separator +  result.get("SHEET").get(0)  + ".csv";
			        FileWriter writer = new FileWriter(csvFile);
			        
			        CSVUtils.writeLine(writer, Arrays.asList("order-id",
			        		"order-item-id",
			        		"Product Name",
			        		"Total Pruchase Price",
			        		"Total Customer Spend on Order",
			        		"Margin","Margin (%)",
			        		"Vendor Order number",
			        		"Tracking Number",
			        		"payments-date",
			        		"purchase-date",
			        		"reporting-date",
			        		"promise-date",
			        		"days-past-promise",
			        		"buyer-email",
			        		"buyer-name",
			        		"buyer-phone-number",
			        		"sku",
			        		"product-name",
			        		"quantity-purchased",
			        		"quantity-shipped",
			        		"quantity-to-ship",
			        		"ship-service-level",
			        		"recipient-name",
			        		"ship-address-1",
			        		"ship-address-2",
			        		"ship-address-3",
			        		"ship-city",
			        		"ship-state",
			        		"ship-postal-code",
			        		"ship-country",
			        		"ShipDetailDateTime",
			        		"Email Content"));
			        
			        if (dto != null) {
				        for (int i=0; i < dto.size(); i++) {
				        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getOrderId(), 
				        			dto.get(i).getOrderItemId(),
				        			dto.get(i).getProductName(),
				        			dto.get(i).getTotalPruchasePrice(),
				        			dto.get(i).getTotalCustomerSpendonOrder(),
				        			dto.get(i).getMarginDollar(),
				        			dto.get(i).getMarginPercent(),
				        			dto.get(i).getVendorOrdernumber(),
				        			dto.get(i).getTrackingNumber(),
				        			dto.get(i).getPaymentsDate(),
				        			dto.get(i).getPurchaseDate(),
				        			dto.get(i).getReportingDate(),
				        			dto.get(i).getPromiseDate(),
				        			dto.get(i).getDaysPastPromise(),
				        			dto.get(i).getBuyerEmail(),
				        			dto.get(i).getBuyerName(),
				        			dto.get(i).getBuyerPhoneNumber(),
				        			dto.get(i).getSku(),
				        			dto.get(i).getProductName2(),
				        			dto.get(i).getQuantityPurchased(),
				        			dto.get(i).getQuantityShipped(),
				        			dto.get(i).getQuantityToShip(),
				        			dto.get(i).getShipServiceLevel(),
				        			dto.get(i).getRecipientName(),
				        			dto.get(i).getShipAddress1(),
				        			dto.get(i).getShipAddress2(),
				        			dto.get(i).getShipAddress3(),
				        			dto.get(i).getShipCity(),
				        			dto.get(i).getShipState(),
				        			dto.get(i).getShipPostalCode(),
				        			dto.get(i).getShipCountry(),
				        			dto.get(i).getShipmentStatusDate(),
				        			dto.get(i).getEmailContent()), ',', '"');
				        }
			        }
	
			        writer.flush();
			        writer.close();
			        
			        //Download
			        File fileDown = new File(csvFile);
					if(!fileDown.exists()){
						throw new ServletException("File doesn't exists on server.");
					}
					System.out.println("File location on server::" + fileDown.getAbsolutePath());
					//ServletContext ctx = getServletContext();
					InputStream fis = new FileInputStream(fileDown);
					//String mimeType = ctx.getMimeType(fileDown.getAbsolutePath());
					response.setContentType("application/octet-stream");
					response.setContentLength((int) fileDown.length());
					response.setHeader("Content-Disposition", "attachment; filename=\"" + result.get("SHEET").get(0)  + ".csv" + "\"");
	
					ServletOutputStream os = response.getOutputStream();
					byte[] bufferData = new byte[1024];
					int read=0;
					while((read = fis.read(bufferData))!= -1){
						os.write(bufferData, 0, read);
					}
					os.flush();
					os.close();
					fis.close();
					
					dir = new File(rootPath + File.separator + "tmpFiles" + File.separator + "Fail");
					if (!dir.exists())
						dir.mkdirs();
			        
			        dto = new ArrayList<AmazonOrderReport>();
			        
			        dto = result.get("FAIL");
			        
			        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss");//dd/MM/yyyy
			        Date now = new Date();
			        String strDate = sdfDate.format(now);
			        
			        if (dto != null && dto.size() > 0) {
			        	csvFile = dir.getAbsolutePath() + File.separator + "Fail_" + strDate + ".csv";
				        writer = new FileWriter(csvFile);
				        
				        CSVUtils.writeLine(writer, Arrays.asList("Order Number","URL","Reason","Input Line"));
				        
				        for (int i=0; i < dto.size(); i++) {
				        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getVendorOrdernumber(), dto.get(i).getURL(), dto.get(i).getReason(), dto.get(i).getLine()), ',', '"');
				        }
				        
				        writer.flush();
				        writer.close();
			        	
			        }
			        
			        try {
				        boolean delResult = Files.deleteIfExists(fileDown.toPath()); 
				        delResult = Files.deleteIfExists(serverFile.toPath());
			        } catch (IOException ioe) {
			        	System.out.println("Can not delete file." + ioe);
			        	return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
			        }
	
					return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
				} catch (Exception e) {
					return "You failed to upload " + submitData.getFile().getOriginalFilename() + " => " + e.getMessage();
				}
				
			}
		} else {
			return "You failed to upload " + submitData.getFile().getOriginalFilename()
					+ " because the file was empty.";
		}
		
		return "OK";
	}
	
	@RequestMapping(value = "/amazonasin", method = RequestMethod.POST)
	public @ResponseBody String getASIN(HttpServletRequest request, HttpServletResponse response, @ModelAttribute P2SubmitData submitData) {
		if (!submitData.getFile().isEmpty()) {
			if (submitData.getProjectId().equals("5")) {
				
				try {
					byte[] bytes = submitData.getFile().getBytes();
	
					// Creating the directory to store file
					String rootPath = System.getProperty("catalina.home");
					File dir = new File(rootPath + File.separator + "tmpFiles");
					if (!dir.exists())
						dir.mkdirs();
					
					String filePath = "";
					
					filePath = dir.getAbsolutePath() + File.separator + submitData.getFile().getOriginalFilename();
	
					// Create the file on server
					File serverFile = new File(filePath);
					BufferedOutputStream stream = new BufferedOutputStream(
							new FileOutputStream(serverFile));
					stream.write(bytes);
					stream.close();
	
					Map<String, List> result = new HashMap<String, List>();
					
					List<AmazonOrderReport> dtos = new ArrayList<AmazonOrderReport>();
					
					FeaturesScraper featureGrab = new FeaturesScraper();
					
					result = featureGrab.collectASIN(filePath, submitData);
					
					dtos = result.get("SUCCESS");
					
					//Write to CSV file
					String csvFile = dir.getAbsolutePath() + File.separator +  result.get("SHEET").get(0)  + ".csv";
					
					OutputStream outputStream = new FileOutputStream(csvFile);
					Writer outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
					
			        CSVUtils.writeLine(outputStreamWriter, Arrays.asList("URL","ProductName", "ASIN", "Brand", "Model", "flag" ));
			        
			        if (dtos != null) {
				        for (AmazonOrderReport dto : dtos) {
				        	CSVUtils.writeLine(outputStreamWriter, Arrays.asList(dto.getProductName(), 
				        			dto.getBuyerName(), dto.getSku()
				        			,dto.getProductName2(), dto.getOrderId(), dto.getShipmentStatus()), ',', '"');
				        }
			        }
	
			        outputStreamWriter.close();
			        
			        //Download
			        File fileDown = new File(csvFile);
					if(!fileDown.exists()){
						throw new ServletException("File doesn't exists on server.");
					}
					System.out.println("File location on server::" + fileDown.getAbsolutePath());
					InputStream fis = new FileInputStream(fileDown);
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/octet-stream");
					response.setContentLength((int) fileDown.length());
					response.setHeader("Content-Disposition", "attachment; filename=\"" + result.get("SHEET").get(0)  + ".csv" + "\"");
	
					ServletOutputStream os = response.getOutputStream();
					byte[] bufferData = new byte[1024];
					int read=0;
					while((read = fis.read(bufferData))!= -1){
						os.write(bufferData, 0, read);
					}
					os.flush();
					os.close();
					fis.close();
			        
			        try {
				        Files.deleteIfExists(fileDown.toPath()); 
				        Files.deleteIfExists(serverFile.toPath());
			        } catch (IOException ioe) {
			        	System.out.println("Can not delete file." + ioe);
			        	return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
			        }
	
					return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
				} catch (Exception e) {
					return "You failed to upload " + submitData.getFile().getOriginalFilename() + " => " + e.getMessage();
				}
				
			}
		} else {
			return "You failed to upload " + submitData.getFile().getOriginalFilename()
					+ " because the file was empty.";
		}
		
		return "OK";
	}
	
	@RequestMapping(value = "/banggoodorder", method = RequestMethod.POST)
	public @ResponseBody String banggoodOrderPlace(HttpServletRequest request, HttpServletResponse response, @ModelAttribute P2SubmitData submitData) {
		if (!submitData.getFile().isEmpty()) {
			try {
				byte[] bytes = submitData.getFile().getBytes();

				// Creating the directory to store file
				String rootPath = System.getProperty("catalina.home");
				File dir = new File(rootPath + File.separator + "tmpFiles");
				if (!dir.exists())
					dir.mkdirs();
				
				String filePath = "";
				
				filePath = dir.getAbsolutePath() + File.separator + submitData.getFile().getOriginalFilename();

				// Create the file on server
				File serverFile = new File(filePath);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				Map<String, List> result = new HashMap<String, List>();
				
				List<AmazonOrderReport> dto = new ArrayList<AmazonOrderReport>();
				
				bgorder = new BanggoodOrderPlace();
				
				result = bgorder.doPlaceOrder(filePath, submitData);
				
				dto = result.get("SUCCESS");
				
				//Write to CSV file
				String csvFile = dir.getAbsolutePath() + File.separator +  result.get("SHEET").get(0)  + ".csv";
		        FileWriter writer = new FileWriter(csvFile);
		        
		        CSVUtils.writeLine(writer, Arrays.asList("order-id",
		        		"order-item-id",
		        		"Product Name",
		        		"Total Pruchase Price",
		        		"Total Customer Spend on Order",
		        		"Margin","Margin (%)",
		        		"Vendor Order number",
		        		"Tracking Number",
		        		"payments-date",
		        		"purchase-date",
		        		"reporting-date",
		        		"promise-date",
		        		"days-past-promise",
		        		"buyer-email",
		        		"buyer-name",
		        		"buyer-phone-number",
		        		"sku",
		        		"product-name",
		        		"quantity-purchased",
		        		"quantity-shipped",
		        		"quantity-to-ship",
		        		"ship-service-level",
		        		"recipient-name",
		        		"ship-address-1",
		        		"ship-address-2",
		        		"ship-address-3",
		        		"ship-city",
		        		"ship-state",
		        		"ship-postal-code",
		        		"ship-country",
		        		"ShipDetailDateTime",
		        		"Email Content"));
		        
		        if (dto != null) {
			        for (int i=0; i < dto.size(); i++) {
			        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getOrderId(), 
			        			dto.get(i).getOrderItemId(),
			        			dto.get(i).getProductName(),
			        			dto.get(i).getTotalPruchasePrice(),
			        			dto.get(i).getTotalCustomerSpendonOrder(),
			        			dto.get(i).getMarginDollar(),
			        			dto.get(i).getMarginPercent(),
			        			dto.get(i).getVendorOrdernumber(),
			        			dto.get(i).getTrackingNumber(),
			        			dto.get(i).getPaymentsDate(),
			        			dto.get(i).getPurchaseDate(),
			        			dto.get(i).getReportingDate(),
			        			dto.get(i).getPromiseDate(),
			        			dto.get(i).getDaysPastPromise(),
			        			dto.get(i).getBuyerEmail(),
			        			dto.get(i).getBuyerName(),
			        			dto.get(i).getBuyerPhoneNumber(),
			        			dto.get(i).getSku(),
			        			dto.get(i).getProductName2(),
			        			dto.get(i).getQuantityPurchased(),
			        			dto.get(i).getQuantityShipped(),
			        			dto.get(i).getQuantityToShip(),
			        			dto.get(i).getShipServiceLevel(),
			        			dto.get(i).getRecipientName(),
			        			dto.get(i).getShipAddress1(),
			        			dto.get(i).getShipAddress2(),
			        			dto.get(i).getShipAddress3(),
			        			dto.get(i).getShipCity(),
			        			dto.get(i).getShipState(),
			        			dto.get(i).getShipPostalCode(),
			        			dto.get(i).getShipCountry(),
			        			dto.get(i).getShipmentStatusDate(),
			        			dto.get(i).getEmailContent()), ',', '"');
			        }
		        }

		        writer.flush();
		        writer.close();
		        
		        //Download
		        File fileDown = new File(csvFile);
				if(!fileDown.exists()){
					throw new ServletException("File doesn't exists on server.");
				}
				System.out.println("File location on server::" + fileDown.getAbsolutePath());
				//ServletContext ctx = getServletContext();
				InputStream fis = new FileInputStream(fileDown);
				//String mimeType = ctx.getMimeType(fileDown.getAbsolutePath());
				response.setContentType("application/octet-stream");
				response.setContentLength((int) fileDown.length());
				response.setHeader("Content-Disposition", "attachment; filename=\"" + result.get("SHEET").get(0)  + ".csv" + "\"");

				ServletOutputStream os = response.getOutputStream();
				byte[] bufferData = new byte[1024];
				int read=0;
				while((read = fis.read(bufferData))!= -1){
					os.write(bufferData, 0, read);
				}
				os.flush();
				os.close();
				fis.close();
				
				dir = new File(rootPath + File.separator + "tmpFiles" + File.separator + "Fail");
				if (!dir.exists())
					dir.mkdirs();
		        
		        dto = new ArrayList<AmazonOrderReport>();
		        
		        dto = result.get("FAIL");
		        
		        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss");//dd/MM/yyyy
		        Date now = new Date();
		        String strDate = sdfDate.format(now);
		        
		        if (dto != null && dto.size() > 0) {
		        	csvFile = dir.getAbsolutePath() + File.separator + "Fail_" + strDate + ".csv";
			        writer = new FileWriter(csvFile);
			        
			        CSVUtils.writeLine(writer, Arrays.asList("Order Number","URL","Reason","Input Line"));
			        
			        for (int i=0; i < dto.size(); i++) {
			        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getVendorOrdernumber(), dto.get(i).getURL(), dto.get(i).getReason(), dto.get(i).getLine()), ',', '"');
			        }
			        
			        writer.flush();
			        writer.close();
		        	
		        }
		        
		        try {
			        boolean delResult = Files.deleteIfExists(fileDown.toPath()); 
			        delResult = Files.deleteIfExists(serverFile.toPath());
		        } catch (IOException ioe) {
		        	System.out.println("Can not delete file." + ioe);
		        	return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
		        }

				return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
			} catch (Exception e) {
				return "You failed to upload " + submitData.getFile().getOriginalFilename() + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + submitData.getFile().getOriginalFilename()
					+ " because the file was empty.";
		}
	}
	
	@RequestMapping(value = "/bgbulkorder", method = RequestMethod.POST)
	public @ResponseBody String bulkOrderImport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute P2SubmitData submitData) throws IOException, ServletException {
		
		if (submitData.getFile().isEmpty()) {
			return "Browser input file";
		}
		
		BulkImportOrder bio = new BulkImportOrder();
		List<AmazonOrderReport> dto = new ArrayList<AmazonOrderReport>();
		Map<String, List> result = new HashMap<String, List>();
		
		result = bio.doPlaceOrder(submitData);
		
		dto = result.get("SUCCESS");
		
		//Write to CSV file
		String rootPath = System.getProperty("catalina.home");
		File dir = new File(rootPath + File.separator + "tmpFiles");
		if (!dir.exists())
			dir.mkdirs();
		String csvFile = dir.getAbsolutePath() + File.separator +  result.get("SHEET").get(0)  + ".csv";
        FileWriter writer = new FileWriter(csvFile);
        
        CSVUtils.writeLine(writer, Arrays.asList("order-id",
        		"order-item-id",
        		"Product Name",
        		"Total Pruchase Price",
        		"Total Customer Spend on Order",
        		"Margin","Margin (%)",
        		"Vendor Order number",
        		"Tracking Number",
        		"payments-date",
        		"purchase-date",
        		"reporting-date",
        		"promise-date",
        		"days-past-promise",
        		"buyer-email",
        		"buyer-name",
        		"buyer-phone-number",
        		"sku",
        		"product-name",
        		"quantity-purchased",
        		"quantity-shipped",
        		"quantity-to-ship",
        		"ship-service-level",
        		"recipient-name",
        		"ship-address-1",
        		"ship-address-2",
        		"ship-address-3",
        		"ship-city",
        		"ship-state",
        		"ship-postal-code",
        		"ship-country",
        		"ShipDetailDateTime",
        		"Email Content"));
        
        if (dto != null) {
	        for (int i=0; i < dto.size(); i++) {
	        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getOrderId(), 
	        			dto.get(i).getOrderItemId(),
	        			dto.get(i).getProductName(),
	        			dto.get(i).getTotalPruchasePrice(),
	        			dto.get(i).getTotalCustomerSpendonOrder(),
	        			dto.get(i).getMarginDollar(),
	        			dto.get(i).getMarginPercent(),
	        			dto.get(i).getVendorOrdernumber(),
	        			dto.get(i).getTrackingNumber(),
	        			dto.get(i).getPaymentsDate(),
	        			dto.get(i).getPurchaseDate(),
	        			dto.get(i).getReportingDate(),
	        			dto.get(i).getPromiseDate(),
	        			dto.get(i).getDaysPastPromise(),
	        			dto.get(i).getBuyerEmail(),
	        			dto.get(i).getBuyerName(),
	        			dto.get(i).getBuyerPhoneNumber(),
	        			dto.get(i).getSku(),
	        			dto.get(i).getProductName2(),
	        			dto.get(i).getQuantityPurchased(),
	        			dto.get(i).getQuantityShipped(),
	        			dto.get(i).getQuantityToShip(),
	        			dto.get(i).getShipServiceLevel(),
	        			dto.get(i).getRecipientName(),
	        			dto.get(i).getShipAddress1(),
	        			dto.get(i).getShipAddress2(),
	        			dto.get(i).getShipAddress3(),
	        			dto.get(i).getShipCity(),
	        			dto.get(i).getShipState(),
	        			dto.get(i).getShipPostalCode(),
	        			dto.get(i).getShipCountry(),
	        			dto.get(i).getShipmentStatusDate(),
	        			dto.get(i).getEmailContent()), ',', '"');
	        }
        }

        writer.flush();
        writer.close();
        
        //Download
        File fileDown = new File(csvFile);
		if(!fileDown.exists()){
			throw new ServletException("File doesn't exists on server.");
		}
		System.out.println("File location on server::" + fileDown.getAbsolutePath());
		InputStream fis = new FileInputStream(fileDown);
		response.setContentType("application/octet-stream");
		response.setContentLength((int) fileDown.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + result.get("SHEET").get(0)  + ".csv" + "\"");

		ServletOutputStream os = response.getOutputStream();
		byte[] bufferData = new byte[1024];
		int read=0;
		while((read = fis.read(bufferData))!= -1){
			os.write(bufferData, 0, read);
		}
		os.flush();
		os.close();
		fis.close();
		
		dir = new File(rootPath + File.separator + "tmpFiles" + File.separator + "Fail");
		if (!dir.exists())
			dir.mkdirs();
        
        dto = new ArrayList<AmazonOrderReport>();
        
        dto = result.get("FAIL");
        
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        
        if (dto != null && dto.size() > 0) {
        	csvFile = dir.getAbsolutePath() + File.separator + "Fail_" + strDate + ".csv";
	        writer = new FileWriter(csvFile);
	        
	        CSVUtils.writeLine(writer, Arrays.asList("Order Number","URL","Reason","Input Line"));
	        
	        for (int i=0; i < dto.size(); i++) {
	        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getVendorOrdernumber(), dto.get(i).getURL(), dto.get(i).getReason(), dto.get(i).getLine()), ',', '"');
	        }
	        
	        writer.flush();
	        writer.close();
        	
        }
        
        try {
	        boolean delResult = Files.deleteIfExists(fileDown.toPath()); 
	        //delResult = Files.deleteIfExists(serverFile.toPath());
        } catch (IOException ioe) {
        	System.out.println("Can not delete file." + ioe);
        	return "You successfully uploaded file=" + submitData.getFile().getOriginalFilename();
        }
		
		return "";
	}
	
	
	@RequestMapping(value = "/trace", method = RequestMethod.GET)
	public ModelAndView traceFail() {
		
		List<String> fileLst = new ArrayList<String>();
		String rootPath = System.getProperty("catalina.home");
		
		final File folder = new File(rootPath + File.separator + "tmpFiles" + File.separator + "Fail");
		fileLst = CSVUtils.listFilesForFolder(folder);
		
		ModelAndView model = new ModelAndView();
		model.setViewName("trace");
		//model.addObject("msg", name);
		model.addObject("fileList", fileLst);

		return model;

	}
	
	@RequestMapping(value = "/download/{fileName:.+}", method = RequestMethod.GET)
	public void getErrorTrace(@PathVariable("fileName") String name, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		ModelAndView model = new ModelAndView();
		model.setViewName("trace");
		model.addObject("msg", name);
		
		String rootPath = System.getProperty("catalina.home");
		File fileDown = new File(rootPath + File.separator + "tmpFiles" + File.separator + "Fail" + File.separator +  name);
		
		if(!fileDown.exists()){
			throw new ServletException("File doesn't exists on server.");
		}
		System.out.println("File location on server::" + fileDown.getAbsolutePath());
		//ServletContext ctx = getServletContext();
		ServletOutputStream os = null;
		InputStream fis = null;
		try {
			fis = new FileInputStream(fileDown);
			//String mimeType = ctx.getMimeType(fileDown.getAbsolutePath());
			response.setContentType("application/octet-stream");
			response.setContentLength((int) fileDown.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
	
			os = response.getOutputStream();
			byte[] bufferData = new byte[1024];
			int read=0;
			while((read = fis.read(bufferData))!= -1){
				os.write(bufferData, 0, read);
			}
			
		} catch (Exception ex) {
			System.out.println("Error");
		} finally {
			os.flush();
			os.close();
			fis.close();
		}

		//return model;

	}
	
	public void writeCSVForBGOrderTool(String path, List<AmazonOrderReport> dto) throws IOException {
		
		FileWriter writer = new FileWriter(path);
        
        CSVUtils.writeLine(writer, Arrays.asList("Sale Record Id",
        		"Buyer Country",
        		"Buyer Fullname",
        		"Buyer Address 1",
        		"Buyer Address 2",
        		"Buyer State",
        		"Buyer City",
        		"Buyer Zip",
        		"Buyer Phone Number",
        		"Remark",
        		"Warehouse",
        		"Product ID",
        		"Quantity"));
        
        if (dto != null) {
	        for (int i=0; i < dto.size(); i++) {
	        	CSVUtils.writeLine(writer, Arrays.asList(dto.get(i).getOrderId(), 
	        			"UNITED STATES",
	        			dto.get(i).getBuyerName(),
	        			dto.get(i).getShipAddress1(),
	        			dto.get(i).getShipAddress2() + " " + dto.get(i).getShipAddress3(),
	        			dto.get(i).getShipState(),
	        			dto.get(i).getShipCity(),
	        			dto.get(i).getShipPostalCode(),
	        			dto.get(i).getBuyerPhoneNumber(),
	        			//CN US EU
	        			"CN",
	        			dto.get(i).getSku(),
	        			dto.get(i).getQuantityPurchased()), ',', '"');
	        }
        }

        writer.flush();
        writer.close();
	}
	
	
	
}