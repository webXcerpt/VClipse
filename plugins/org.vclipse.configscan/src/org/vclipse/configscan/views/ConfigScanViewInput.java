package org.vclipse.configscan.views;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.impl.model.TestCase;

public class ConfigScanViewInput {

	private String configurationName;
	
	private String date;
	
	private List<TestCase> testCases;
	
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}
	
	public void setConfigurationName(String name) {
		this.configurationName = name;
	}
	
	public List<TestCase> getTestCases() {
		return Collections.unmodifiableList(testCases);
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
