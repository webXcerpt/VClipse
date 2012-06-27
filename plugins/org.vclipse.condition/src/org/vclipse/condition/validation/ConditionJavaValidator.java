package org.vclipse.condition.validation;

import org.eclipse.xtext.validation.Check;
import org.vclipse.condition.validation.AbstractConditionJavaValidator;
import org.vclipse.vcml.vcml.ConditionSource;
 
public class ConditionJavaValidator extends AbstractConditionJavaValidator {

	@Check
	public void checkConditionSource(ConditionSource source) {
		checkSource(source);
	}

}
