package org.vclipse.configscan.views;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.impl.model.TestCase;

import com.google.common.collect.Maps;

public class ConfigScanViewInput {

	private String configurationName;
	
	private String date;
	
	private List<TestCase> testCases;
	
	private Map<String, Object> options;
	
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
		options = Maps.newHashMap();
	}
	
	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}
	
	public void setConfigurationName(String name) {
		this.configurationName = name;
	}
	
	public List<TestCase> getTestCases() {
		return Collections.unmodifiableList(testCases);
	}
	
	public Map<String, Object> getOptions() {
		return Collections.unmodifiableMap(options);
	}
	
	public String getConfigurationName() {
		return configurationName;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		if(date == null) {
			this.date = new SimpleDateFormat(
					IConfigScanConfiguration.DATE_FORMAT).format(Calendar.getInstance().getTime());			
		} else {
			this.date = date;
		}
	}
}
