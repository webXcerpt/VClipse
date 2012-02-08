package org.vclipse.configscan.injection;

import org.eclipse.core.resources.IFile;
import org.vclipse.configscan.IConfigScanRunner;

public interface ITestConfigScanRunner extends IConfigScanRunner {

	public void setFile(IFile file);
}
