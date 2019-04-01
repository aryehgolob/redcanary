package com.redcanary.tools.telemetry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.redcanary.tools.telemetry.components.TelemetryProber;
import com.redcanary.tools.telemetry.xml.TelemetryDefinitionXml;

public class TelemetryValidator {
	// logger
	private static Logger log = Logger.getLogger(TelemetryValidator.class);
	
	// macro's
	private static final String CONFIG_FILE = "etc/telemetry_validator.properties";
	private static final String VALIDATE_CONFIG_XML = "etc/telemetry_definition.xml";
	
	// class and instance variable
	private String mode = null;
	private int threadCount = -1;
	public static String OPERATING_SYSTEM = null;
	
	public TelemetryValidator( ) {
		this.init();
	}

	private void init() {
		InputStream input = null;

		// initialize properties file
		try {
			// load propeties
			Properties prop = new Properties();
			input = new FileInputStream(CONFIG_FILE);
			prop.load(input);

			// set instance variables
			this.mode = prop.getProperty("telemetry_validator.mode");
			this.threadCount = Integer.parseInt(prop.getProperty("telemetry_validator.thread_count"));
			
			// print mode to log
			log.debug("mode: "+mode);
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// detect OS and define macro
		String os = System.getProperty("os.name").toLowerCase();
		if(os.matches("win.*")) {
			OPERATING_SYSTEM = "windows";
		}
		else if(os.matches("mac.*")) {
			OPERATING_SYSTEM = "mac";
		}
		else if(os.matches("linux.*")) {
			OPERATING_SYSTEM = "linux";
		}
		
	}

	public static void main(String[] args) {
		// set up logging
		BasicConfigurator.configure();
        log.debug("Red Canary Telemetry Validator ...");

        // create application and kick off
        TelemetryValidator validator = new TelemetryValidator();
        validator.validateTelemetry();
	}
	
	private void validateTelemetry() {
		// get an object representation of XML configuration file
		TelemetryDefinitionXml definition = new TelemetryDefinitionXml(VALIDATE_CONFIG_XML);
		
		// create a prober to probe metrics
		TelemetryProber prober = new TelemetryProber(definition, this.threadCount);
		
		// probe metrics
		prober.probeMetrics();
		
		// generate report
		prober.generateReport();

	}
	

}
