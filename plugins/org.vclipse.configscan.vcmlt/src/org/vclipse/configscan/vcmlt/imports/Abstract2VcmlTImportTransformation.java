package org.vclipse.configscan.vcmlt.imports;

import java.util.Iterator;

import org.vclipse.configscan.imports.AbstractConfigScanImportTransformation;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.TestCase;
import org.vclipse.configscan.vcmlt.vcmlT.VcmlTFactory;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public abstract class Abstract2VcmlTImportTransformation extends AbstractConfigScanImportTransformation {
	
	protected static final VcmlFactory VCML = VcmlFactory.eINSTANCE;
	protected static final VcmlTFactory VCMLT = VcmlTFactory.eINSTANCE;
	
	public void init() {
		if(referencedModel != null) {
			VcmlModel vcmlModel = (VcmlModel)referencedModel;
			String materialNumber = "";
			for(Option option : vcmlModel.getOptions()) {
				if(OptionType.UPS == option.getName()) {
					materialNumber = option.getValue();
				}
			}
			if(materialNumber.isEmpty()) {
				throw new IllegalArgumentException("Can not create a test case without a material number");
			}
			final String searchString = materialNumber;
			Iterable<Material> materialObjects = Iterables.filter(vcmlModel.getObjects(), Material.class);
			Iterator<Material> foundNamedMaterial = Iterables.filter(materialObjects, new Predicate<Material>() {
				@Override
				public boolean apply(Material input) {
					return input.getName().equals(searchString);
				}
			}).iterator();
			
			if(foundNamedMaterial.hasNext()) {
				TestCase testCase = VcmlTFactory.eINSTANCE.createTestCase();
				testCase.setItem(foundNamedMaterial.next());
				((Model)targetModel).setTestcase(testCase);
			}
			
		}
	}

	public String getReferencedModelExtension() {
		return "vcml";
	}

	public String getTargetModelExtension() {
		return "vcmlt";
	}
}
