package org.vclipse.configscan.injection;

import com.google.inject.Provider;

public class ProviderAdapter<T> implements Provider<T> {

	@Override
	public T get() {
		return null;
	}

}
