package org.vclipse.vcml.utils;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.ShortVarReference;

import com.google.common.collect.Lists;

public class ExpressionExtensions {

	private final PolymorphicDispatcher<List<Characteristic>> csticExtractor 
		= new PolymorphicDispatcher<List<Characteristic>>("getCharacteristics", 1, 1, Collections.singletonList(this),
			PolymorphicDispatcher.NullErrorHandler.<List<Characteristic>> get()) {
		@Override
		protected List<Characteristic> handleNoSuchMethod(final Object... params) {
			return Lists.newArrayList();
		}
	};
	
	public List<Characteristic> getUsedCharacteristics(EObject object) {
		return csticExtractor.invoke(object);
	}
	
	protected List<Characteristic> getCharacteristics(Comparison comparison) {
		return getUsedCharacteristics(comparison.getLeft());
	}
	
	protected List<Characteristic> getCharacteristics(ObjectCharacteristicReference ocr) {
		return Lists.newArrayList(ocr.getCharacteristic());
	}
	
	protected List<Characteristic> getCharacteristics(ShortVarReference svr) {
		return Lists.newArrayList(svr.getRef().getCharacteristic());
	}
}
