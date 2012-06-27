package org.vclipse.vcml.formatting;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.Provider;

public class OptionsProvider implements Provider<Map<String, String>>{

	public static final String VCML_FILE_NAME = "vcml_file_name";
	
	public static final String VCML_FILE_URI = "vcml_file_uri";
	
	private Map<String, String> options;
	
	public OptionsProvider() {
		options = Maps.newHashMap();
	}
	
	public Map<String, String> get() {
		return options;
	}
}
