/*
 * Copyright(C) 2015
 * NEC Corporation All rights reserved.
 * 
 * No permission to use, copy, modify and distribute this software
 * and its documentation for any purpose is granted.
 * This software is provided under applicable license agreement only.
 */
package com.web.util.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * CommonProperties class.
 *
 * @author sondn
 */
public class CommonProperties {
	
    private static final String DEFAULT_PROPERTIES_NAME = "dropship.properties";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    
    private static CommonProperties instance = null;
    
    private Properties properties;

    private String shipmentMessNotFound = null;
    private String shipmentMessHeader = null;
    private String shipmentMessThank = null;
    private String shipmentMessContent = null;
    private String shipmentMessEnd = null;
    private String downloadPath = null;
    private String storeName = null;
    
    private String emailSender = null;
    private String emailSenderPass = null;
    private String emailRecipient = null;
    private String emailTitle = null;
    
    private String connectionString = null;
    
    private CommonProperties() throws IOException, URISyntaxException {
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource(DEFAULT_PROPERTIES_NAME);
		 
        Properties properties = new Properties();
        BufferedReader reader = Files.newBufferedReader(Paths.get(url.toURI()), UTF_8);
        properties.load(reader);

        this.properties = properties;
        
    }

    private void initialize() {
    	// Page Title settings
    	shipmentMessNotFound = properties.getProperty("shipment.status.not.found");
    	shipmentMessHeader = properties.getProperty("shipment.status.header");
    	shipmentMessThank = properties.getProperty("shipment.status.thank");
    	shipmentMessContent = properties.getProperty("shipment.status.content");
    	shipmentMessEnd = properties.getProperty("shipment.status.end");
    	downloadPath = properties.getProperty("download.path");
    	storeName = properties.getProperty("store.name");
    	
    	emailSender = properties.getProperty("email.send.adress");
    	emailSenderPass = properties.getProperty("email.send.password");
    	emailRecipient = properties.getProperty("email.recipient");
    	emailTitle = properties.getProperty("email.title");
    	
    	connectionString = properties.getProperty("db.driver");
    	
    }
    
    public static String getShipmentMessNotFound() {
		return instance.shipmentMessNotFound;
	}
    
    /**
	 * @return the emailSender
	 */
	public static String getEmailSender() {
		return instance.emailSender;
	}

	/**
	 * @return the emailSenderPass
	 */
	public static String getEmailSenderPass() {
		return instance.emailSenderPass;
	}

	/**
	 * @return the emailRecipient
	 */
	public static String getEmailRecipient() {
		return instance.emailRecipient;
	}

	/**
	 * @return the emailTitle
	 */
	public static String getEmailTitle() {
		return instance.emailTitle;
	}

	/**
	 * @return the shipmentMessHeader
	 */
	public static String getShipmentMessHeader() {
		return instance.shipmentMessHeader;
	}

	/**
	 * @return the shipmentMessThank
	 */
	public static String getShipmentMessThank() {
		return instance.shipmentMessThank;
	}

	/**
	 * @return the shipmentMessContent
	 */
	public static String getShipmentMessContent() {
		return instance.shipmentMessContent;
	}

	/**
	 * @return the shipmentMessEnd
	 */
	public static String getShipmentMessEnd() {
		return instance.shipmentMessEnd;
	}
	
	public static String getDownloadPath() {
		return instance.downloadPath;
	}
	
	public static String getStoreName() {
		return instance.storeName;
	}
	
	public static String getConnectionString() {
		return instance.connectionString;
	}

	public static synchronized void loadProperties() throws IOException, URISyntaxException {
        if (instance == null){
        	instance = new CommonProperties();  
        	instance.initialize();
        }
    }

    public static boolean isInitialized() {
        return (instance != null);
    }

}
