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
	
	public void setDate(String date, String formatString) {
		if(date == null) {
			this.date = new SimpleDateFormat(formatString == null ? 
					IConfigScanConfiguration.DATE_FORMAT : IConfigScanConfiguration.DATE_FORMAT_UI_ENTRIES).format(Calendar.getInstance().getTime());			
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configurationName == null) ? 0 : configurationName.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((testRuns == null) ? 0 : testRuns.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		} else if(object == null) {
			return false;
		} else if (getClass() != object.getClass()) {
			return false;
		}
		
		ConfigScanViewInput other = (ConfigScanViewInput) object;
		if(configurationName == null) {
			if(other.configurationName != null) {
				return false;
			}
		} else if(!configurationName.equals(other.configurationName)) {
			return false;
		}
		if(date == null) {
			if(other.date != null) {
				return false;
			}
		} else if(!date.equals(other.date)) {
			return false;
		} if(testRuns == null) {
			if(other.testRuns != null) {
				return false;
			}
		} else if(!testRuns.equals(other.testRuns)) {
			return false;
		}
		return true;
	}
}
