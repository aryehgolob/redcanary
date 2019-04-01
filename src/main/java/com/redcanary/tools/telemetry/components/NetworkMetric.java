package com.redcanary.tools.telemetry.components;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.redcanary.tools.etc.ProcessUtil;

public class NetworkMetric extends GenericMetric {
	private static Logger log = Logger.getLogger(NetworkMetric.class);

	// define instance variables
	private String srcIp = null;
	private int srcPort = -1;
	private String destIp = null;
	private int destPort = -1;
	private String data = null;
	private long dataSize = -1;
	private ProcessHandle.Info procInfo = null;
	private long pid = -1;


	// constructor
	public NetworkMetric(String ip, String port, String data) {
		this.destIp = ip;
		this.destPort = Integer.parseInt(port);
		this.data = data;
		this.dataSize = data.getBytes().length;
	}

	// run in thread and set results
	public Boolean call() {
		this.procInfo = ProcessUtil.getProcessInfo();
		this.pid = ProcessUtil.getPid();

		try {
			Socket socket = new Socket(this.destIp, this.destPort);
			this.srcIp = socket.getLocalAddress().toString().replace("/", "");
			this.srcPort = socket.getLocalPort();
			OutputStream outstream = socket.getOutputStream(); 
			PrintWriter out = new PrintWriter(outstream);
			out.print(data);
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public String toJson() {
		String commandLine = ProcessUtil.stripOptional(this.procInfo.command().toString());
		String procName = commandLine.substring(commandLine.lastIndexOf("\\") + 1);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("pid", pid);
		jsonObj.put("start_time", ProcessUtil.stripOptional(procInfo.startInstant().toString()));
		jsonObj.put("user", ProcessUtil.stripOptional(procInfo.user().toString()));
		jsonObj.put("process_name", procName);
		jsonObj.put("command_line", commandLine);
		jsonObj.put("source_ip", srcIp);
		jsonObj.put("source_port", srcPort);
		jsonObj.put("destination_ip", destIp);
		jsonObj.put("destination_port", destPort);
		jsonObj.put("data_size", dataSize);
		jsonObj.put("protocol", "TCP");
		jsonObj.put("element_type", "network_validate");
		return jsonObj.toString();
	}
}
