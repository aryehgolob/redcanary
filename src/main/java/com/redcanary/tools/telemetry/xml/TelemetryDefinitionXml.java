package com.redcanary.tools.telemetry.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.redcanary.tools.etc.StringUtil;
import com.redcanary.tools.telemetry.TelemetryValidator;
import com.redcanary.tools.telemetry.components.FileMetric;
import com.redcanary.tools.telemetry.components.GenericMetric;
import com.redcanary.tools.telemetry.components.NetworkMetric;
import com.redcanary.tools.telemetry.components.ProcessMetric;

import org.apache.log4j.Logger;

public class TelemetryDefinitionXml {
	private static Logger log = Logger.getLogger(TelemetryDefinitionXml.class);
	
	private String fileName = null;
	private List<GenericMetric> metricList = new ArrayList<GenericMetric>();

	public TelemetryDefinitionXml(String fileName) {
		this.fileName = fileName;
		this.parse();
	}
	
	public List<GenericMetric> getMetricList() {
		return metricList;
	}
	
	private void parse() {
		SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(this.fileName);
        
       
        try {

            Document document = (Document) builder.build(xmlFile);
            Element rootNode = document.getRootElement();
            List<Element> platformList = rootNode.getChildren("platform");
            for(Element platformElem : platformList) {
            	String os = platformElem.getAttributeValue("os");
            	if(!os.equals(TelemetryValidator.OPERATING_SYSTEM)) {
            		continue;
            	}
            	
            	List<Element> metricGroupList = platformElem.getChildren("metric_element_group");
            	for(Element metricGroupElem : metricGroupList) {
            		String metricType = metricGroupElem.getAttributeValue("type");
            		//log.debug("metricType: "+metricType);
            		
            		if(metricType.equals("file")) {
            			List<Element> fileList = metricGroupElem.getChildren("file");
            			for(Element fileElem : fileList) {
							String fileName = fileElem.getAttributeValue("name");
							String fileDir = fileElem.getAttributeValue("dir");
							FileMetric fileMetric = new FileMetric(fileName, fileDir);
							
							Element textElem = fileElem.getChild("text");
							String random = textElem.getAttributeValue("random");
							String numberOfBytes = textElem.getAttributeValue("size_bytes");
							
							String content = null;
							if(random != null && random.toLowerCase().equals("true")) {
								content = StringUtil.generateRandomString(numberOfBytes);
							}
							else {
								content = textElem.getTextTrim();
							}
							
							fileMetric.setContent(content);
							
							metricList.add(fileMetric);
            			}
            		}
            		else if(metricType.equals("process")) {
            			List<Element> procList = metricGroupElem.getChildren("process");
            			for(Element procElem : procList) {
            				String procName = procElem.getAttributeValue("name");
            				String procArgs = procElem.getAttributeValue("args");
            				ProcessMetric procMetric = new ProcessMetric(procName, procArgs);
							metricList.add(procMetric);
            			}
            		}
            		else if(metricType.equals("network")) {
            			List<Element> netList = metricGroupElem.getChildren("network_connection");
            			for(Element netElem : netList) {
            				String ip = netElem.getAttributeValue("ip");
            				String port = netElem.getAttributeValue("port");
            				String data = netElem.getChildTextTrim("data");
            				NetworkMetric netMetric = new NetworkMetric(ip, port, data);
							metricList.add(netMetric);
            			}

            		}
            	}
            }

        }
        catch (IOException io) {
            System.out.println(io.getMessage());
        }
        catch (JDOMException jdomex) {
            System.out.println(jdomex.getMessage());
        }
	}
}
