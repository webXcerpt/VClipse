package org.vclipse.bapi.actions.handler;

import java.util.List;
import java.util.Set;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.ContributionReader;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.connection.IConnectionHandler;

import com.google.inject.Inject;

public class BAPIActionPropertyTester extends PropertyTester {

	public static final String CONNECTED = "connected";
	public static final String HANDLER_AVAILABLE = "handlerType";
	
	public static final String SEPARATOR = ":";
	public static final String EXISTS_STRING = "exists";
	
	@Inject
	protected IConnectionHandler connectionHandler;
	
	@Inject
	protected ContributionReader contributionReader;
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if(CONNECTED.equals(property)) {
			return connectionHandler.getCurrentConnection() != null;
		} else if(HANDLER_AVAILABLE.equals(property)) {
			if(expectedValue instanceof String) {
				return handlerExists(receiver, (String)expectedValue);
			}
		}
		return false;
	}
	
	protected boolean handlerExists(Object receiver, String expectedValue) {
		String[] parts = ((String)expectedValue).split(SEPARATOR);
		if(parts.length == 2) {
			for(IBAPIActionRunner<?> handler : contributionReader.getHandler(parts[1])) {
				try {
					handler.getClass().getMethod("run", new Class[]{receiver.getClass().getInterfaces()[0], Resource.class, IProgressMonitor.class, Set.class, List.class});
					return EXISTS_STRING.equals(parts[0]);
				} catch (Exception exception) {
					// ignore
				} 
			}
		}
		return false;
	}
}
