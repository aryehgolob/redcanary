package com.redcanary.tools.telemetry.components;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public abstract class GenericMetric implements Callable {
	private static Logger log = Logger.getLogger(GenericMetric.class);

	public abstract String toJson();
}
