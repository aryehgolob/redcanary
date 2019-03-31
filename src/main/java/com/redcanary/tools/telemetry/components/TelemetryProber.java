package com.redcanary.tools.telemetry.components;

import com.redcanary.tools.etc.FileUtil;
import com.redcanary.tools.telemetry.TelemetryValidator;
import com.redcanary.tools.telemetry.xml.TelemetryDefinitionXml;

import java.text.SimpleDateFormat;
import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class TelemetryProber {
	private static Logger log = Logger.getLogger(TelemetryValidator.class);
	
	private TelemetryDefinitionXml definition = null;
	private ExecutorService executor = null;
	
	public TelemetryProber(TelemetryDefinitionXml definition, int threadCount) {
		this.definition = definition;
		this.executor = Executors.newFixedThreadPool(threadCount);
	}

	public void probeMetrics() {
		List<GenericMetric> metricList = this.definition.getMetricList();
		
		List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
		
		for(GenericMetric metric : metricList) {
			Future<Boolean> future = executor.submit(metric);
			futureList.add(future);
		}

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
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String fileName = "telemetry_report." + timeStamp + ".json";
		if(!FileUtil.fileExists(fileName)) {
			FileUtil.createFile(fileName);
		}

		List<GenericMetric> metricList = this.definition.getMetricList();
		StringBuffer sb = new StringBuffer("[");
		//for(GenericMetric metric : metricList) {
		log.debug("metric list size: "+metricList.size());
		int stopIndex = metricList.size() - 1;
		for(int i = 0; i < metricList.size(); i++) {
			String json = metricList.get(i).toJson();
			sb.append(json);
			log.debug("i: "+i);
			log.debug("stop index: "+stopIndex);
			if(i  < stopIndex) {
				sb.append(",");
				log.debug("json: "+json);
			}
		}
		sb.append("]");
		
		FileUtil.appendToFile(fileName, sb.toString());
	}
}