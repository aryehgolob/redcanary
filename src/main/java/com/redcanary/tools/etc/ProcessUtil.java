package com.redcanary.tools.etc;

import org.apache.log4j.Logger;

public class ProcessUtil {
	private static Logger log = Logger.getLogger(ProcessUtil.class);

    /*
     * Generic Process methods            				
     */
	
	public static ProcessHandle.Info getProcessInfo() {
		ProcessHandle pHandle = ProcessHandle.current();
		return pHandle.info();
	}
	
	public static long getPid() {
		return ProcessHandle.current().pid();
	}
	
	public static String stripOptional(String s) {
		return s.replace("Optional[", "").replace("]", "");
	}

}
