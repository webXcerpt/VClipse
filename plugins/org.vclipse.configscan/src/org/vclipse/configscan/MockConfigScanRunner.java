package org.vclipse.configscan;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.xtext.util.Files;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;

import com.sap.conn.jco.JCoException;

public class MockConfigScanRunner implements IConfigScanRunner {

	public String execute(IFile file, String xmlInput, RemoteConnection rc, String matNr) throws JCoException, CoreException {
		
		System.err.println("MockConfigScanRunner: executing " + file.getName()); 
		
//		return "TODO: dies ist zu ersetzen"; // TODO Inhalt der Datei file.getName() + ".log" zurückgeben
		
//		File f = new File(file.getFullPath().toPortableString() + ".log");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Files.readFileIntoString(file.getLocation().toPortableString() + ".xml.log");
		
//		return Files.readStreamIntoString(file.getContents());
	}
	
	/*
	
	1) No implementation for java.lang.String annotated with @com.google.inject.name.Named(value=file.extensions) was bound.
	  while locating java.lang.String annotated with @com.google.inject.name.Named(value=file.extensions)
	    for parameter 0 at org.eclipse.xtext.resource.FileExtensionProvider.setExtensions(FileExtensionProvider.java:27)
	  at org.eclipse.xtext.resource.FileExtensionProvider.setExtensions(FileExtensionProvider.java:27)
	  while locating org.eclipse.xtext.resource.FileExtensionProvider
	    for field at org.eclipse.xtext.resource.impl.DefaultResourceServiceProvider.fileExtensionProvider(DefaultResourceServiceProvider.java:25)
	  while locating org.eclipse.xtext.resource.IResourceServiceProvider
	    for field at org.eclipse.xtext.resource.XtextResource.resourceServiceProvider(XtextResource.java:131)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	2) No implementation for java.lang.String annotated with @com.google.inject.name.Named(value=languageName) was bound.
	  while locating java.lang.String annotated with @com.google.inject.name.Named(value=languageName)
	    for parameter 0 at org.eclipse.xtext.ui.editor.preferences.PreferenceStoreAccessImpl.setLanguageNameAsQualifier(PreferenceStoreAccessImpl.java:81)
	  at org.eclipse.xtext.ui.editor.preferences.PreferenceStoreAccessImpl.setLanguageNameAsQualifier(PreferenceStoreAccessImpl.java:81)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	3) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService.setGrammar(AbstractDeclarativeValueConverterService.java:50)
	  at org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService.setGrammar(AbstractDeclarativeValueConverterService.java:50)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	4) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.formatting.impl.DefaultNodeModelFormatter.hiddenTokenHelper(DefaultNodeModelFormatter.java:32)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	5) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.formatting.impl.NodeModelStreamer.hiddenTokenHelper(NodeModelStreamer.java:36)
	  while locating org.eclipse.xtext.formatting.INodeModelStreamer
	    for field at org.eclipse.xtext.formatting.impl.DefaultNodeModelFormatter.nodeModelStreamer(DefaultNodeModelFormatter.java:32)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	6) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.formatting.impl.NodeModelStreamer.tokenUtil(NodeModelStreamer.java:36)
	  while locating org.eclipse.xtext.formatting.INodeModelStreamer
	    for field at org.eclipse.xtext.formatting.impl.DefaultNodeModelFormatter.nodeModelStreamer(DefaultNodeModelFormatter.java:32)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	7) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer.tokenUtil(CrossReferenceSerializer.java:38)
	  while locating org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer
	    for field at org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider.crossRefSerializer(SequenceFeeder.java:42)
	  while locating org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider
	    for field at org.eclipse.xtext.serializer.sequencer.AbstractSemanticSequencer.feederProvider(AbstractSemanticSequencer.java:22)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	8) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.EnumLiteralSerializer.tokenUtil(EnumLiteralSerializer.java:28)
	  while locating org.eclipse.xtext.serializer.tokens.IEnumLiteralSerializer
	    for field at org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider.enumLiteralSerializer(SequenceFeeder.java:42)
	  while locating org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider
	    for field at org.eclipse.xtext.serializer.sequencer.AbstractSemanticSequencer.feederProvider(AbstractSemanticSequencer.java:22)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	9) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.ValueSerializer.tokenUtil(ValueSerializer.java:24)
	  while locating org.eclipse.xtext.serializer.tokens.IValueSerializer
	    for field at org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider.valueSerializer(SequenceFeeder.java:42)
	  while locating org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider
	    for field at org.eclipse.xtext.serializer.sequencer.AbstractSemanticSequencer.feederProvider(AbstractSemanticSequencer.java:22)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	10) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer.tokenUtil(CrossReferenceSerializer.java:38)
	  while locating org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer
	    for field at org.eclipse.xtext.serializer.sequencer.AssignmentFinder.crossRefSerializer(AssignmentFinder.java:41)
	  while locating org.eclipse.xtext.serializer.sequencer.IAssignmentFinder
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.assignmentFinder(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	11) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.EnumLiteralSerializer.tokenUtil(EnumLiteralSerializer.java:28)
	  while locating org.eclipse.xtext.serializer.tokens.IEnumLiteralSerializer
	    for field at org.eclipse.xtext.serializer.sequencer.AssignmentFinder.enumLiteralSerializer(AssignmentFinder.java:41)
	  while locating org.eclipse.xtext.serializer.sequencer.IAssignmentFinder
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.assignmentFinder(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	12) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.ValueSerializer.tokenUtil(ValueSerializer.java:24)
	  while locating org.eclipse.xtext.serializer.tokens.IValueSerializer
	    for field at org.eclipse.xtext.serializer.sequencer.AssignmentFinder.valueSerializer(AssignmentFinder.java:41)
	  while locating org.eclipse.xtext.serializer.sequencer.IAssignmentFinder
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.assignmentFinder(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	13) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer.tokenUtil(CrossReferenceSerializer.java:38)
	  while locating org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer
	    for field at org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider.crossRefSerializer(SequenceFeeder.java:42)
	  while locating org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider
	    for field at org.eclipse.xtext.serializer.sequencer.AbstractSemanticSequencer.feederProvider(AbstractSemanticSequencer.java:22)
	  at org.eclipse.xtext.service.DefaultRuntimeModule.configureGenericSemanticSequencer(DefaultRuntimeModule.java:219)

	14) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.EnumLiteralSerializer.tokenUtil(EnumLiteralSerializer.java:28)
	  while locating org.eclipse.xtext.serializer.tokens.IEnumLiteralSerializer
	    for field at org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider.enumLiteralSerializer(SequenceFeeder.java:42)
	  while locating org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider
	    for field at org.eclipse.xtext.serializer.sequencer.AbstractSemanticSequencer.feederProvider(AbstractSemanticSequencer.java:22)
	  at org.eclipse.xtext.service.DefaultRuntimeModule.configureGenericSemanticSequencer(DefaultRuntimeModule.java:219)

	15) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.ValueSerializer.tokenUtil(ValueSerializer.java:24)
	  while locating org.eclipse.xtext.serializer.tokens.IValueSerializer
	    for field at org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider.valueSerializer(SequenceFeeder.java:42)
	  while locating org.eclipse.xtext.serializer.acceptor.SequenceFeeder$Provider
	    for field at org.eclipse.xtext.serializer.sequencer.AbstractSemanticSequencer.feederProvider(AbstractSemanticSequencer.java:22)
	  at org.eclipse.xtext.service.DefaultRuntimeModule.configureGenericSemanticSequencer(DefaultRuntimeModule.java:219)

	16) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer.tokenUtil(CrossReferenceSerializer.java:38)
	  while locating org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer
	    for field at org.eclipse.xtext.serializer.sequencer.AssignmentFinder.crossRefSerializer(AssignmentFinder.java:41)
	  while locating org.eclipse.xtext.serializer.sequencer.IAssignmentFinder
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.assignmentFinder(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.DefaultRuntimeModule.configureGenericSemanticSequencer(DefaultRuntimeModule.java:219)

	17) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.EnumLiteralSerializer.tokenUtil(EnumLiteralSerializer.java:28)
	  while locating org.eclipse.xtext.serializer.tokens.IEnumLiteralSerializer
	    for field at org.eclipse.xtext.serializer.sequencer.AssignmentFinder.enumLiteralSerializer(AssignmentFinder.java:41)
	  while locating org.eclipse.xtext.serializer.sequencer.IAssignmentFinder
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.assignmentFinder(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.DefaultRuntimeModule.configureGenericSemanticSequencer(DefaultRuntimeModule.java:219)

	18) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  at org.eclipse.xtext.parsetree.reconstr.impl.DefaultHiddenTokenHelper.setGrammar(DefaultHiddenTokenHelper.java:20)
	  while locating org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper
	    for field at org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil.hiddenTokenHelper(TokenUtil.java:33)
	  while locating org.eclipse.xtext.parsetree.reconstr.impl.TokenUtil
	    for field at org.eclipse.xtext.serializer.tokens.ValueSerializer.tokenUtil(ValueSerializer.java:24)
	  while locating org.eclipse.xtext.serializer.tokens.IValueSerializer
	    for field at org.eclipse.xtext.serializer.sequencer.AssignmentFinder.valueSerializer(AssignmentFinder.java:41)
	  while locating org.eclipse.xtext.serializer.sequencer.IAssignmentFinder
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.assignmentFinder(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.DefaultRuntimeModule.configureGenericSemanticSequencer(DefaultRuntimeModule.java:219)

	19) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for parameter 0 at org.eclipse.xtext.validation.impl.ConcreteSyntaxConstraintProvider.setGrammar(ConcreteSyntaxConstraintProvider.java:540)
	  at org.eclipse.xtext.validation.impl.ConcreteSyntaxConstraintProvider.setGrammar(ConcreteSyntaxConstraintProvider.java:540)
	  while locating org.eclipse.xtext.validation.IConcreteSyntaxConstraintProvider
	    for field at org.eclipse.xtext.validation.impl.ConcreteSyntaxValidator.constraintProvider(ConcreteSyntaxValidator.java:44)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	20) No implementation for org.eclipse.xtext.parsetree.reconstr.IParseTreeConstructor was bound.
	  while locating org.eclipse.xtext.parsetree.reconstr.IParseTreeConstructor
	    for parameter 0 at org.eclipse.xtext.parsetree.reconstr.Serializer.<init>(Serializer.java:39)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	21) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for field at org.eclipse.xtext.conversion.impl.AbstractIDValueConverter.grammarAccess(AbstractIDValueConverter.java:118)
	  while locating org.eclipse.xtext.conversion.impl.AbstractIDValueConverter
	    for field at org.eclipse.xtext.common.services.DefaultTerminalConverters.idValueConverter(DefaultTerminalConverters.java:24)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	22) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for field at org.eclipse.xtext.formatting.impl.BaseFormatter.grammar(BaseFormatter.java:19)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	23) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for field at org.eclipse.xtext.linking.lazy.LazyLinker.grammarAccess(LazyLinker.java:260)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	24) No implementation for java.lang.String annotated with @com.google.inject.name.Named(value=languageName) was bound.
	  while locating java.lang.String annotated with @com.google.inject.name.Named(value=languageName)
	    for field at org.eclipse.xtext.resource.XtextResource.languageName(XtextResource.java:1)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	25) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for field at org.eclipse.xtext.serializer.diagnostic.SequencerDiagnosticProvider.grammarAccess(SequencerDiagnosticProvider.java:42)
	  while locating org.eclipse.xtext.serializer.diagnostic.ISemanticSequencerDiagnosticProvider
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.diagnosticProvider(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	26) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for field at org.eclipse.xtext.serializer.diagnostic.SequencerDiagnosticProvider.grammarAccess(SequencerDiagnosticProvider.java:42)
	  while locating org.eclipse.xtext.serializer.diagnostic.ISemanticSequencerDiagnosticProvider
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.diagnosticProvider(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.DefaultRuntimeModule.configureGenericSemanticSequencer(DefaultRuntimeModule.java:219)

	27) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for field at org.eclipse.xtext.serializer.sequencer.ContextFinder.grammar(ContextFinder.java:46)
	  while locating org.eclipse.xtext.serializer.sequencer.IContextFinder
	    for field at org.eclipse.xtext.serializer.sequencer.AssignmentFinder.contextFinder(AssignmentFinder.java:41)
	  while locating org.eclipse.xtext.serializer.sequencer.IAssignmentFinder
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.assignmentFinder(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	28) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for field at org.eclipse.xtext.serializer.sequencer.ContextFinder.grammar(ContextFinder.java:46)
	  while locating org.eclipse.xtext.serializer.sequencer.IContextFinder
	    for field at org.eclipse.xtext.serializer.diagnostic.SequencerDiagnosticProvider.contextFinder(SequencerDiagnosticProvider.java:42)
	  while locating org.eclipse.xtext.serializer.diagnostic.ISemanticSequencerDiagnosticProvider
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.diagnosticProvider(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.MethodBasedModule.configure(MethodBasedModule.java:55)

	29) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for field at org.eclipse.xtext.serializer.sequencer.ContextFinder.grammar(ContextFinder.java:46)
	  while locating org.eclipse.xtext.serializer.sequencer.IContextFinder
	    for field at org.eclipse.xtext.serializer.sequencer.AssignmentFinder.contextFinder(AssignmentFinder.java:41)
	  while locating org.eclipse.xtext.serializer.sequencer.IAssignmentFinder
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.assignmentFinder(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.DefaultRuntimeModule.configureGenericSemanticSequencer(DefaultRuntimeModule.java:219)

	30) No implementation for org.eclipse.xtext.IGrammarAccess was bound.
	  while locating org.eclipse.xtext.IGrammarAccess
	    for field at org.eclipse.xtext.serializer.sequencer.ContextFinder.grammar(ContextFinder.java:46)
	  while locating org.eclipse.xtext.serializer.sequencer.IContextFinder
	    for field at org.eclipse.xtext.serializer.diagnostic.SequencerDiagnosticProvider.contextFinder(SequencerDiagnosticProvider.java:42)
	  while locating org.eclipse.xtext.serializer.diagnostic.ISemanticSequencerDiagnosticProvider
	    for field at org.eclipse.xtext.serializer.sequencer.BacktrackingSemanticSequencer.diagnosticProvider(BacktrackingSemanticSequencer.java:48)
	  at org.eclipse.xtext.service.DefaultRuntimeModule.configureGenericSemanticSequencer(DefaultRuntimeModule.java:219)

	30 errors
*/
	
}
