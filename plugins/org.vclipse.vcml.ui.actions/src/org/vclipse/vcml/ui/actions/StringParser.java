package org.vclipse.vcml.ui.actions;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.util.PolymorphicDispatcher;
import org.eclipse.xtext.util.PolymorphicDispatcher.ErrorHandler;
import org.vclipse.vcml.parser.antlr.VCMLParser;
import org.vclipse.vcml.parser.antlr.lexer.InternalVCMLLexer;
import org.vclipse.vcml.services.VCMLGrammarAccess;
import org.vclipse.vcml.services.VCMLGrammarAccess.ConditionSourceElements;
import org.vclipse.vcml.services.VCMLGrammarAccess.ConstraintSourceElements;
import org.vclipse.vcml.services.VCMLGrammarAccess.ProcedureSourceElements;
import org.vclipse.vcml.ui.actions.characteristic.CharacteristicReader;
import org.vclipse.vcml.ui.actions.classes.ClassReader;
import org.vclipse.vcml.ui.actions.variantfunction.VariantFunctionReader;
import org.vclipse.vcml.ui.actions.varianttable.VariantTableReader;
import org.vclipse.vcml.ui.internal.VCMLActivator;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConditionalStatement;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.IsSpecified_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.ProcedureSource;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.Statement;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.sap.conn.jco.JCoException;

public class StringParser {

	private final PolymorphicDispatcher<Void> parserDispatcher = new PolymorphicDispatcher<Void>("parse", 3, 3,
			Collections.singletonList(this), new ErrorHandler<Void>() {
				public Void handle(Object[] params, Throwable e) {
					return null;
				}
			});
	
	private final PolymorphicDispatcher<EObject> creationDispatcher = new PolymorphicDispatcher<EObject>("create", 5, 6,
			Collections.singletonList(this), new ErrorHandler<EObject>() {
				public EObject handle(Object[] params, Throwable e) {
					return null;
				}
			});
	
	private InternalVCMLLexer internalVcmlLexer; 
	
	private CharacteristicReader csticReader;
	private VariantFunctionReader variantFunctionReader;
	private VariantTableReader variantTableReader;
	private ClassReader classReader;
	
	private VCMLParser vcmlParser;
	
	private VCMLGrammarAccess vcmlGrammarAccess;
	
	public StringParser() {
		Injector injector = VCMLActivator.getInstance().getInjector("org.vclipse.vcml.VCML");
    	internalVcmlLexer = injector.getInstance(InternalVCMLLexer.class);
    	vcmlGrammarAccess = injector.getInstance(VCMLGrammarAccess.class);
    	vcmlParser = injector.getInstance(VCMLParser.class);
    	
    	csticReader = new CharacteristicReader();
    	variantFunctionReader = new VariantFunctionReader();
    	variantTableReader = new VariantTableReader();
    	classReader = new ClassReader();
	}
	
	public void parse(EObject parent, String textRepresentation, Model vcmlModel) {
		parserDispatcher.invoke(parent, textRepresentation, vcmlModel);
	}
	
	public EObject create(EObject object, String text, Model vcmlModel, Set<String> seenObjects, int tokenType) {
		return creationDispatcher.invoke(object, text, vcmlModel, seenObjects, tokenType);
	}
	
	public EObject create(EObject object, String text, Model vcmlModel, Set<String> seenObjects, List<Token> seenTokens, int tokenType) {
		return creationDispatcher.invoke(object, text, vcmlModel, seenObjects, seenTokens, tokenType);
	}
	
	protected void parse(SelectionCondition condtion, String textRepresentation, Model vcmlModel) {
		parseText(condtion, textRepresentation, vcmlModel);
	}
	
	protected void parse(Procedure procedure, String textRepresentation, Model vcmlModel) {
		ProcedureSource source = procedure.getSource();
		if(source == null) {
			ProcedureSourceElements elements = vcmlGrammarAccess.getProcedureSourceAccess();
	    	IParseResult result = vcmlParser.parse(elements.getRule(), new StringReader(textRepresentation));
	    	EObject rootASTElement = result.getRootASTElement();
	    	if(rootASTElement instanceof ProcedureSource) {
	    		source = (ProcedureSource)rootASTElement;
	    	}
		} 
		for(Statement statement : source.getStatements()) {
			parseText(statement, textRepresentation, vcmlModel);
		}
	}
	
	protected void parse(Precondition procedure, String textRepresentation, Model vcmlModel) {
		ConditionSource source = procedure.getSource();
		if(source == null) {
			ConditionSourceElements elements = vcmlGrammarAccess.getConditionSourceAccess();
	    	IParseResult result = vcmlParser.parse(elements.getRule(), new StringReader(textRepresentation));
	    	EObject rootASTElement = result.getRootASTElement();
	    	if(rootASTElement instanceof ConditionSource) {
	    		source = (ConditionSource)rootASTElement;
	    	}
		} 
		parseText(source.getCondition(), textRepresentation, vcmlModel);
	}
	
	protected void parse(Constraint constraint, String textRepresentation, Model vcmlModel) {
		ConstraintSource source = constraint.getSource();
		if(source == null) {
			ConstraintSourceElements elements = vcmlGrammarAccess.getConstraintSourceAccess();
			IParseResult result = vcmlParser.parse(elements.getRule(), new StringReader(textRepresentation));
	    	EObject rootASTElement = result.getRootASTElement();
	    	if(rootASTElement instanceof ConstraintSource) {
	    		source = (ConstraintSource)rootASTElement;
	    	}
		}
		parseText(source, textRepresentation, vcmlModel);
	}
	
	protected void parse(PFunction function, String textRepresentation, Model vcmlModel) {
		parseText(function, textRepresentation, vcmlModel);
	}
	
	protected void parseText(EObject object, String textRepresentation, Model vcmlModel) {
		internalVcmlLexer.setCharStream(new ANTLRStringStream(textRepresentation));
		HashSet<String> seenObjects = new HashSet<String>(); 
		List<Token> tokens = Lists.newArrayList();
    	for(Token currentToken=internalVcmlLexer.nextToken(); currentToken.getText()!=null; currentToken=internalVcmlLexer.nextToken()) {
    		tokens.add(currentToken);
    		if(currentToken.getType() == InternalVCMLLexer.RULE_ID) {
    			String text = currentToken.getText();
    			for(int i=tokens.size()-1; i>-1; i--) {
    				Token token = tokens.get(i);
    				if(canSkip(token)) {
    					continue;
    				} else {
    					create(object, text, vcmlModel, seenObjects, tokens, token.getType());
    				}
    			}
    		}
    	}
	}
	
	protected EObject create(SelectionCondition selectionCondition, String text, Model vcmlModel, Set<String> seenObjects, List<Token> seenTokens, int tokenType) {
		return createCharacteristic(text, vcmlModel, seenObjects);
	}
	
	protected EObject create(ConditionalStatement object, String text, Model vcmlModel, Set<String> seenObjects, List<Token> seenTokens, int tokenType) {
		if(tokenType == InternalVCMLLexer.KEYWORD_100) {
			return createCharacteristic(text, vcmlModel, seenObjects);
		}
		return null;
	}
	
	protected EObject create(PFunction object, String text, Model vcmlModel, Set<String> seenObjects, List<Token> seenTokens, int tokenType) {
		switch(tokenType) {
			case InternalVCMLLexer.KEYWORD_2 :
			case InternalVCMLLexer.KEYWORD_6 :
				return createCharacteristic(text, vcmlModel, seenObjects);
			case InternalVCMLLexer.KEYWORD_13 :
				Iterator<Literal> filter = Iterables.filter(object.getValues(), new Predicate<Literal>() {
					@Override
					public boolean apply(Literal input) {
						if(input instanceof CharacteristicReference_P) {
							return ((CharacteristicReference_P)input).getCharacteristic() == null;
						}
						return false;
					}
				}).iterator();
				if(filter.hasNext()) {
					return createCharacteristic(text, vcmlModel, seenObjects);
				}
			case InternalVCMLLexer.KEYWORD_138 :
				return createVariantFunction(text, vcmlModel, seenObjects);
			default :
				return null;
		}
	}
	
	protected EObject create(Condition condition, String text, Model vcmlModel, Set<String> seenObjects, List<Token> seenTokens, int tokenType) {
		switch(tokenType) {
			case InternalVCMLLexer.KEYWORD_13 :
				return createCharacteristic(text, vcmlModel, seenObjects);
			case InternalVCMLLexer.KEYWORD_94 :
				if(condition instanceof IsSpecified_C) {
					return createClass("(300)" + text, vcmlModel, seenObjects);	
				}
			case InternalVCMLLexer.KEYWORD_100 :
				return createCharacteristic(text, vcmlModel, seenObjects);
			default :
				return createCharacteristic(text, vcmlModel, seenObjects);
		}
	}
	
	protected EObject create(ConstraintSource source, String text, Model vcmlModel, Set<String> seenObjects, List<Token> seenTokens, int tokenType) {
		switch(tokenType) {
			case InternalVCMLLexer.KEYWORD_2 : 
			case InternalVCMLLexer.KEYWORD_6 : 
			case InternalVCMLLexer.KEYWORD_13 :
			case InternalVCMLLexer.KEYWORD_100 :
				return createCharacteristic(text, vcmlModel, seenObjects);
			case InternalVCMLLexer.KEYWORD_94 :
				for(int i=seenTokens.size()-1; i>-1; i--) {
					int type = seenTokens.get(i).getType();
					if(type == InternalVCMLLexer.KEYWORD_15) {
						return null;
					} else if(type == InternalVCMLLexer.KEYWORD_94) {
						break;
					}
				}
				return createClass("(300)" + text, vcmlModel, seenObjects);
			case InternalVCMLLexer.KEYWORD_108 :
				return createVariantTable(text, vcmlModel, seenObjects);
			default :
				return null;
		}
	}
	
	protected VariantTable createVariantTable(String text, Model vcmlModel, Set<String> seenObjects) {
		try {
			VariantTable variantTable = variantTableReader.read(text, vcmlModel, new NullProgressMonitor(), seenObjects, true);
			seenObjects.add("VariantTable/" + variantTable.getName());
			return variantTable;
		} catch(JCoException exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	protected VariantFunction createVariantFunction(String text, Model vcmlModel, Set<String> seenObjects) {
		try {
			VariantFunction variantFunction = variantFunctionReader.read(text, vcmlModel, new NullProgressMonitor(), seenObjects, true);
			seenObjects.add("VariantFunction/" + variantFunction.getName());
			return variantFunction;
		} catch (JCoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Characteristic createCharacteristic(String text, Model vcmlModel, Set<String> seenObjects) {
		try {
			Characteristic characteristic = csticReader.read(text, vcmlModel, new NullProgressMonitor(), seenObjects, true);
			if(characteristic != null) {
				seenObjects.add("Characteristic/" + characteristic.getName());								
			}
			return characteristic;
		} catch (JCoException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected org.vclipse.vcml.vcml.Class createClass(String text, Model vcmlModel, Set<String> seenObjects) {
		try {
			return classReader.read(text, vcmlModel, new NullProgressMonitor(), seenObjects, true);
		} catch (JCoException exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	private boolean canSkip(Token token) {
		return token.getType() == InternalVCMLLexer.RULE_WS 
				|| token.getType() == InternalVCMLLexer.RULE_DEPENDENCY_COMMENT;
	}
}
