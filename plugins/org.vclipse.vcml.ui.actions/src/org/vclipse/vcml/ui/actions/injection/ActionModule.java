package org.vclipse.vcml.ui.actions.injection;

import java.io.PrintStream;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.console.CMConsolePlugin;
import org.vclipse.console.CMConsolePlugin.Kind;
import org.vclipse.vcml.ui.extension.IExtensionPointUtilities;
import org.vclipse.vcml.ui.internal.VCMLActivator;
import org.vclipse.vcml.utils.DependencySourceUtils;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class ActionModule extends AbstractGenericModule {
	
	@Override
	public void configure(Binder binder) {
		Injector vcmlInjector = VCMLActivator.getInstance().getInjector(VCMLActivator.ORG_VCLIPSE_VCML_VCML);
		Injector connectionInjector = VClipseConnectionPlugin.getDefault().getInjector();
		
		binder.bind(DependencySourceUtils.class).toInstance(vcmlInjector.getInstance(DependencySourceUtils.class));
		binder.bind(IPreferenceStore.class).toInstance(vcmlInjector.getInstance(IPreferenceStore.class));
		binder.bind(IConnectionHandler.class).toInstance(connectionInjector.getInstance(IConnectionHandler.class));
		
		binder.bind(PrintStream.class).annotatedWith(Names.named("Task")).toInstance(
				new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Task)));
		
		binder.bind(PrintStream.class).annotatedWith(Names.named("Error")).toInstance(
				new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Error)));
		
		binder.bind(PrintStream.class).annotatedWith(Names.named("Warning")).toInstance(
				new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Warning)));
		
		binder.bind(PrintStream.class).annotatedWith(Names.named("Info")).toInstance(
				new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Info)));
		
		/**
		 * workaround to avoid error messages in the console provided by the guice, does not really seem to break the functionality 
		 */
		binder.bind(IGrammarAccess.class).toInstance(vcmlInjector.getInstance(IGrammarAccess.class));
		binder.bind(IExtensionPointUtilities.class).toInstance(vcmlInjector.getInstance(IExtensionPointUtilities.class));
		binder.bind(EPackage.Registry.class).toInstance(vcmlInjector.getInstance(EPackage.Registry.class));
	}
}
