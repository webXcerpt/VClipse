package org.vclipse.configscan.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.impl.model.TestRun;

import com.google.common.collect.Lists;

public class ConfigScanViewInput {

	private String configurationName;
	
	private String date;
	
	private List<TestRun> testRuns;
	
	public ConfigScanViewInput() {
		configurationName = "";
		date = "";
		testRuns = Lists.newArrayList();
	}
	
	public void setTestRuns(List<TestRun> testRuns) {
		this.testRuns = testRuns == null ? new ArrayList<TestRun>() : testRuns;
	}
	
	public void setConfigurationName(String name) {
		this.configurationName = name == null ? "" : name;
	}
	
	public void setDate(String date) {
		if(date == null) {
			this.date = new SimpleDateFormat(
					IConfigScanConfiguration.DATE_FORMAT).format(Calendar.getInstance().getTime());			
		} else {
			this.date = date;
		}
	}
	
	public List<TestRun> getTestRuns() {
		return Collections.unmodifiableList(testRuns);
	}
	
	public String getConfigurationName() {
		return configurationName;
	}
	
	public String getDate() {
		return date;
	}
	
	@Override
	public String toString() {
		return "Name={" + configurationName + 
				"} Date={" + date + "} Test cases amount={" + 
					testRuns.size() + "}";
	}

	
	@Override
	public boolean equals(Object object) {
		if(object instanceof ConfigScanViewInput) {
			ConfigScanViewInput compare = (ConfigScanViewInput)object;
			return configurationName.equals(compare.getConfigurationName()) 
					&& date.equals(compare.getDate()) 
						&& testRuns.size() == compare.getTestRuns().size();
						
		}
		return super.equals(object);
	}
}
