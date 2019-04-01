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
	private static Logger log = Logger.getLogger(TelemetryValidator.class);
	
	private static final String CONFIG_FILE = "etc/telemetry_validator.properties";
	private static final String VALIDATE_CONFIG_XML = "etc/telemetry_definition.xml";
	
	private String mode = null;
	private int threadCount = -1;
	public static String OPERATING_SYSTEM = null;
	
	public TelemetryValidator( ) {
		this.init();
	}

	private void init() {
		InputStream input = null;

		try {
			Properties prop = new Properties();

			input = new FileInputStream(CONFIG_FILE);

			// load a properties file
			prop.load(input);

			this.mode = prop.getProperty("telemetry_validator.mode");
			this.threadCount = Integer.parseInt(prop.getProperty("telemetry_validator.thread_count"));
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
		BasicConfigurator.configure();
        log.debug("Red Canary Telemetry Validator ...");

        TelemetryValidator validator = new TelemetryValidator();
        validator.validateTelemetry();
	}
	
	private void validateTelemetry() {
		TelemetryDefinitionXml definition = new TelemetryDefinitionXml(VALIDATE_CONFIG_XML);
		TelemetryProber prober = new TelemetryProber(definition, this.threadCount);
		prober.probeMetrics();
		prober.generateReport();

	}
	

}
