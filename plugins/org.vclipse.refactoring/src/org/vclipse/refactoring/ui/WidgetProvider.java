package org.vclipse.refactoring.ui;

import java.lang.reflect.Field;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.widgets.Widget;
import org.vclipse.refactoring.RefactoringPlugin;

public abstract class WidgetProvider extends UserInputWizardPage {

	public WidgetProvider(String name) {
		super(name);
	}

	@SuppressWarnings("unchecked")
	public <T extends Widget> T getWidget(String name, Class<T> type) {
		if(name == null || name.isEmpty()) {
			return null;
		}
		for(Field field : getClass().getDeclaredFields()) {
			if(field.getName().equals(name)) {
				field.setAccessible(true);
				Object fieldValue;
				try {
					fieldValue = field.get(this);
					if(type.isAssignableFrom(fieldValue.getClass())) {
						return (T)fieldValue;
					}
				} catch(Exception exception) {
					RefactoringPlugin.log(exception.getMessage(), exception);
				}
			}
		}
		return null;
	}
}
