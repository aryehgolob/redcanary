package com.redcanary.tools.telemetry.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.redcanary.tools.etc.FileUtil;
import com.redcanary.tools.etc.ProcessUtil;

public class FileMetric extends GenericMetric {
	private static Logger log = Logger.getLogger(FileMetric.class);

	private String fileName = null;
	private String fileDir = null;
	private boolean createSuccess = false;
	private boolean modifySuccess = false;
	private boolean deleteSuccess = false;
	private String fqFileName = null;
	private String content = null;
	private long pid = -1;
	private ProcessHandle.Info procInfo = null;

	
	public FileMetric(String fileName, String fileDir) {
		this.fileName = fileName;
		this.fileDir = fileDir;
	}

	public Boolean call() {
		this.procInfo = ProcessUtil.getProcessInfo();
		this.pid = ProcessUtil.getPid();
		String fileName = this.getFileName();
		this.fqFileName = FileUtil.getFullyQualifiedName(fileName);
		this.createSuccess = FileUtil.createFile(fileName);
		this.modifySuccess = FileUtil.canModifyFile(fileName, content);
		this.deleteSuccess = FileUtil.deleteFile(fileName);
		return true;
	}
	
	private String getFileName() {
		if(fileDir == null) {
			return this.fileName;
		}
		return this.fileDir + "/" + this.fileName;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public String toJson() {
		Map<String, String> commonMap = new HashMap<String, String>();
		commonMap.put("start_time", ProcessUtil.stripOptional(procInfo.startInstant().toString()));
		commonMap.put("user", ProcessUtil.stripOptional(procInfo.user().toString()));
		String commandLine = ProcessUtil.stripOptional(procInfo.command().toString());
		String procName = commandLine.substring(commandLine.lastIndexOf("\\") + 1);
		commonMap.put("command_line", commandLine);
		commonMap.put("process_name", procName);
		commonMap.put("file_name", fqFileName);
		commonMap.put("element_type", "file_validate");

		JSONObject createObj = new JSONObject();
		JSONObject modifyObj = new JSONObject();
		JSONObject deleteObj = new JSONObject();
		addCommonToJson(createObj, commonMap);
		addCommonToJson(modifyObj, commonMap);
		addCommonToJson(deleteObj, commonMap);

		createObj.put("pid", pid);
		createObj.put("activity_descriptor", "create");
		createObj.put("status", this.createSuccess == true ? "pass" : "fail");

		modifyObj.put("pid", pid);
		modifyObj.put("activity_descriptor", "modify");
		modifyObj.put("status", this.modifySuccess == true ? "pass" : "fail");

		deleteObj.put("pid", pid);
		deleteObj.put("activity_descriptor", "delete");
		createObj.put("status", this.deleteSuccess == true ? "pass" : "fail");
		
		return createObj.toString() + "," + modifyObj.toString() + "," + deleteObj.toString();
	}

	private static void addCommonToJson(JSONObject jsonObj, Map<String, String> commonMap) {
		Iterator<String> iter = commonMap.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			String val = commonMap.get(key);
			jsonObj.put(key, val);
		}
		
		
	}
}
