package com.redcanary.tools.telemetry.components;

import com.redcanary.tools.etc.FileUtil;
import com.redcanary.tools.telemetry.xml.TelemetryDefinitionXml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

public class TelemetryProber {
	private static Logger log = Logger.getLogger(TelemetryProber.class);
	
	// define instance variables
	private TelemetryDefinitionXml definition = null;
	private ExecutorService executor = null;
	
	public TelemetryProber(TelemetryDefinitionXml definition, int threadCount) {
		this.definition = definition;
		this.executor = Executors.newFixedThreadPool(threadCount);
	}

	public void probeMetrics() {
		// get a list of all defined metrics
		List<GenericMetric> metricList = this.definition.getMetricList();
		
		// get future results
		List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
		
		// execute each individual metric instance telemetry collection in it's own thread
		for(GenericMetric metric : metricList) {
			Future<Boolean> future = executor.submit(metric);
			futureList.add(future);
		}

		// wait to complete and shutdown
		boolean isComplete = false;
		while(!isComplete) {
		    for(Future<Boolean> future : futureList) {
				try {
					boolean futureComplete = future.get();
					if(!futureComplete) {
						break;
					}
				}
				catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
		    }
		    isComplete = true;
        }
		executor.shutdown();
	}


	public void generateReport() {
		// create file
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String fileName = "telemetry_report." + timeStamp + ".json";
		if(!FileUtil.fileExists(fileName)) {
			FileUtil.createFile(fileName);
		}

		// iterate through metrics, and build json output string
		List<GenericMetric> metricList = this.definition.getMetricList();
		StringBuffer sb = new StringBuffer("[");
		int stopIndex = metricList.size() - 1;
		for(int i = 0; i < metricList.size(); i++) {
			String json = metricList.get(i).toJson();
			sb.append(json);
			if(i  < stopIndex) {
				sb.append(",");
			}
		}
		sb.append("]");
		
		// append output to file
		FileUtil.appendToFile(fileName, sb.toString());
	}
}