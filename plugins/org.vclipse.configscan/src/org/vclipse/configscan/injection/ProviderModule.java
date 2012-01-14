package org.vclipse.configscan.injection;

import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.views.actions.ImportExportAction;

import com.google.inject.Provider;

public abstract class ProviderModule extends AbstractGenericModule {
	
	protected final ConfigScanPlugin plugin;

	public ProviderModule(ConfigScanPlugin plugin) {
		this.plugin = plugin;
	}
	
	public Provider<TestCase> registerTestCaseProvider() {
		return new Provider<TestCase>() {
			@Override
			public TestCase get() {
				return plugin.getInjector().getInstance(TestCase.class);
			}
		};
	}
	
	public Provider<TestRunAdapter> registerTestRunAdapterProvider() {
		return new Provider<TestRunAdapter>() {
			@Override
			public TestRunAdapter get() {
				return plugin.getInjector().getInstance(TestRunAdapter.class);
			}
		};
	}
	
	public Provider<ImportExportAction> registerFileActionProvider() {
		return new ProviderAdapter<ImportExportAction>() {
			public ImportExportAction get() {
				return plugin.getInjector().getInstance(ImportExportAction.class);
			}
		};
	}
}
