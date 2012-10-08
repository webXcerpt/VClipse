package org.vclipse.vcml.ui;

import org.eclipse.xtext.builder.builderState.IBuilderState;
import org.vclipse.vcml.ui.builder.VCMLClusteringBuilderState;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

@SuppressWarnings("restriction")
public class VCMLSharedModule implements Module {

    public void configure(Binder binder) {
		binder.bind(IBuilderState.class).to(VCMLClusteringBuilderState.class).in(Scopes.SINGLETON);
    }

}
