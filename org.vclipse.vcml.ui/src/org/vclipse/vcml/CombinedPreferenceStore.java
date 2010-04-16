/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
/**
 * 
 */
package org.vclipse.vcml;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 *
 */
public class CombinedPreferenceStore implements IPreferenceStore {

	
	/**
	 * 
	 */
	private ScopedPreferenceStore uiScope;
	
	/**
	 * 
	 */
	private ScopedPreferenceStore coreScope;
	
	/**
	 * 
	 */
	public CombinedPreferenceStore(String uiQualifier, String coreQualifier) {
		uiScope = new ScopedPreferenceStore(new InstanceScope(), uiQualifier);
		coreScope = new ScopedPreferenceStore(new InstanceScope(), coreQualifier);
	}
	
	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#addPropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		uiScope.addPropertyChangeListener(listener);
		coreScope.addPropertyChangeListener(listener);
	}
	
	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#removePropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
	 */
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		uiScope.removePropertyChangeListener(listener);
		coreScope.removePropertyChangeListener(listener);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#contains(java.lang.String)
	 */
	public boolean contains(String name) {
		return uiScope.contains(name) ? true : coreScope.contains(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#firePropertyChangeEvent(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
		if(uiScope.contains(name)) {
			uiScope.firePropertyChangeEvent(name, oldValue, newValue);
		} else if(coreScope.contains(name)){
			coreScope.firePropertyChangeEvent(name, oldValue, newValue);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String name) {
		return uiScope.contains(name) ? uiScope.getBoolean(name) : coreScope.getBoolean(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultBoolean(java.lang.String)
	 */
	public boolean getDefaultBoolean(String name) {
		return uiScope.contains(name) ? uiScope.getDefaultBoolean(name) : coreScope.getDefaultBoolean(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultDouble(java.lang.String)
	 */
	public double getDefaultDouble(String name) {
		return uiScope.contains(name) ? uiScope.getDefaultDouble(name) : coreScope.getDefaultDouble(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultFloat(java.lang.String)
	 */
	public float getDefaultFloat(String name) {
		return uiScope.contains(name) ? uiScope.getDefaultFloat(name) : coreScope.getDefaultFloat(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultInt(java.lang.String)
	 */
	public int getDefaultInt(String name) {
		return uiScope.contains(name) ? uiScope.getDefaultInt(name) : coreScope.getDefaultInt(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultLong(java.lang.String)
	 */
	public long getDefaultLong(String name) {
		return uiScope.contains(name) ? uiScope.getDefaultLong(name) : coreScope.getDefaultLong(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultString(java.lang.String)
	 */
	public String getDefaultString(String name) {
		return uiScope.contains(name) ? uiScope.getDefaultString(name) : coreScope.getDefaultString(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDouble(java.lang.String)
	 */
	public double getDouble(String name) {
		return uiScope.contains(name) ? uiScope.getDouble(name) : coreScope.getDouble(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getFloat(java.lang.String)
	 */
	public float getFloat(String name) {
		return uiScope.contains(name) ? uiScope.getFloat(name) : coreScope.getFloat(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getInt(java.lang.String)
	 */
	public int getInt(String name) {
		return uiScope.contains(name) ? uiScope.getInt(name) : coreScope.getInt(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getLong(java.lang.String)
	 */
	public long getLong(String name) {
		return uiScope.contains(name) ? uiScope.getLong(name) : coreScope.getLong(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#getString(java.lang.String)
	 */
	public String getString(String name) {
		return uiScope.contains(name) ? uiScope.getString(name) : coreScope.getString(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#isDefault(java.lang.String)
	 */
	public boolean isDefault(String name) {
		return uiScope.contains(name) ? uiScope.isDefault(name) : coreScope.isDefault(name);
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#needsSaving()
	 */
	public boolean needsSaving() {
		return uiScope.needsSaving() ? true : coreScope.needsSaving();
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#putValue(java.lang.String, java.lang.String)
	 */
	public void putValue(String name, String value) {
		if(uiScope.contains(name)) {
			uiScope.putValue(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.putValue(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, double)
	 */
	public void setDefault(String name, double value) {
		if(uiScope.contains(name)) {
			uiScope.setDefault(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setDefault(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, float)
	 */
	public void setDefault(String name, float value) {
		if(uiScope.contains(name)) {
			uiScope.setDefault(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setDefault(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, int)
	 */
	public void setDefault(String name, int value) {
		if(uiScope.contains(name)) {
			uiScope.setDefault(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setDefault(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, long)
	 */
	public void setDefault(String name, long value) {
		if(uiScope.contains(name)) {
			uiScope.setDefault(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setDefault(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, java.lang.String)
	 */
	public void setDefault(String name, String defaultObject) {
		if(uiScope.contains(name)) {
			uiScope.setDefault(name, defaultObject);
		} else if(coreScope.contains(name)) {
			coreScope.setDefault(name, defaultObject);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, boolean)
	 */
	public void setDefault(String name, boolean value) {
		if(uiScope.contains(name)) {
			uiScope.setDefault(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setDefault(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setToDefault(java.lang.String)
	 */
	public void setToDefault(String name) {
		if(uiScope.contains(name)) {
			uiScope.setToDefault(name);
		} else if(coreScope.contains(name)) {
			coreScope.setToDefault(name);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, double)
	 */
	public void setValue(String name, double value) {
		if(uiScope.contains(name)) {
			uiScope.setValue(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setValue(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, float)
	 */
	public void setValue(String name, float value) {
		if(uiScope.contains(name)) {
			uiScope.setValue(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setValue(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, int)
	 */
	public void setValue(String name, int value) {
		if(uiScope.contains(name)) {
			uiScope.setValue(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setValue(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, long)
	 */
	public void setValue(String name, long value) {
		if(uiScope.contains(name)) {
			uiScope.setValue(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setValue(name, value);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, java.lang.String)
	 */
	public void setValue(String name, String value) {
		if(uiScope.contains(name)) {
			uiScope.setValue(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setValue(name, value);
		}
	}
	
	/**
	 * 
	 */
	public void storePreferences() {
		try {
			uiScope.save();
			coreScope.save();
		} catch (IOException e) {
			VCMLUiPlugin.log(e.getMessage(), e);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, boolean)
	 */
	public void setValue(String name, boolean value) {
		if(uiScope.contains(name)) {
			uiScope.setValue(name, value);
		} else if(coreScope.contains(name)) {
			coreScope.setValue(name, value);
		}
	}
}
