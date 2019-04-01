package com.redcanary.tools.etc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class FileUtil {
	private static Logger log = Logger.getLogger(FileUtil.class);

    /*
     * Generic File methods            				
     */
	public static boolean createFile(String fileName) {
		try {
			File file = new File(fileName);
			return file.createNewFile();
		}
		catch(IOException e) {
			log.warn("could not create file "+fileName+" "+e.getMessage());
		}
		return false;
	}
	
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if(file.delete()) {
			return true;
		}
		return false;
	}
	
	public static boolean canModifyFile(String fileName, String content) {
		File file = new File(fileName);
		long fileSizeOrig = file.length();
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(content.getBytes());
			outputStream.close();
			long fileSizeNew = file.length();
			if(fileSizeOrig < fileSizeNew) {
				return true;
			}
		}
		catch(IOException e) {
			log.warn("could not write to file "+fileName+" "+e.getMessage());
		}
		return false;
	}
	
	public static boolean fileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}
	
	public String getFullyQualifiedPath(String file) {
		if(FileUtil.fileExists(file)) {
			return new File(file).getAbsolutePath();
		}
		return null;
	}
	
	public static void appendToFile(String fileName, String content) {
		try {
		    PrintWriter out = new PrintWriter(new PrintWriter(new FileWriter(fileName, true)));
		    out.println(content);
		    out.close();
		} catch (IOException e) {
		    log.error("error writing to file: "+fileName);
		}
	}

	public static String getFullyQualifiedName(String fileName) {
		return new File(fileName).getAbsolutePath();
	}

}
