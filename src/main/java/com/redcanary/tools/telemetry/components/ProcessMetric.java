package com.redcanary.tools.telemetry.components;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.redcanary.tools.etc.ProcessUtil;

public class ProcessMetric extends GenericMetric {
	private static Logger log = Logger.getLogger(ProcessMetric.class);

	private String procName = null;
	private String procArgs = null;
	private ProcessHandle.Info procInfo = null;
	private long pid = -1;


	
	public ProcessMetric(String procName, String procArgs) {
		this.procName = procName;
		if(procArgs == null) {
			this.procArgs = "";
		}
		else {
			this.procArgs = procArgs;
		}
	}
	
	// run in thread and set results
	public Boolean call() {
        ProcessBuilder pb = new ProcessBuilder(this.procName, this.procArgs);
        
        try {
			Process proc = pb.start();
			this.pid = proc.pid();
			procInfo = proc.info();
		} 
        catch (IOException e) {
			e.printStackTrace();

		}

		return true;
	}
	
	public String toJson() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("pid", pid);
		jsonObj.put("start_time", ProcessUtil.stripOptional(procInfo.startInstant().toString()));
		jsonObj.put("user", ProcessUtil.stripOptional(procInfo.user().toString()));
		jsonObj.put("process_name", procName+" "+procArgs);
		jsonObj.put("command_line", ProcessUtil.stripOptional(procInfo.command().toString()));
		jsonObj.put("element_type", "process_validate");
		return jsonObj.toString();
	}
}
