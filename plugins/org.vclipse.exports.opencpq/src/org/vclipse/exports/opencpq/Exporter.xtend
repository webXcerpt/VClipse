package org.vclipse.exports.opencpq

import com.google.common.collect.Sets
import com.google.inject.Inject
import java.util.Set
import org.eclipse.xtext.util.Strings
import org.vclipse.vcml.utils.DependencySourceUtils
import org.vclipse.vcml.vcml.BOMItem
import org.vclipse.vcml.vcml.BOMItem_Class
import org.vclipse.vcml.vcml.BOMItem_Material
import org.vclipse.vcml.vcml.BillOfMaterial
import org.vclipse.vcml.vcml.BinaryCondition
import org.vclipse.vcml.vcml.BinaryExpression
import org.vclipse.vcml.vcml.Characteristic
import org.vclipse.vcml.vcml.CharacteristicGroup
import org.vclipse.vcml.vcml.CharacteristicReference_P
import org.vclipse.vcml.vcml.CharacteristicValue
import org.vclipse.vcml.vcml.Classification
import org.vclipse.vcml.vcml.Comparison
import org.vclipse.vcml.vcml.ComparisonOperator
import org.vclipse.vcml.vcml.Condition
import org.vclipse.vcml.vcml.ConfigurationProfile
import org.vclipse.vcml.vcml.DateType
import org.vclipse.vcml.vcml.Description
import org.vclipse.vcml.vcml.Expression
import org.vclipse.vcml.vcml.InCondition_P
import org.vclipse.vcml.vcml.InterfaceDesign
import org.vclipse.vcml.vcml.IsSpecified_P
import org.vclipse.vcml.vcml.Language
import org.vclipse.vcml.vcml.List
import org.vclipse.vcml.vcml.Material
import org.vclipse.vcml.vcml.MultiLanguageDescriptions
import org.vclipse.vcml.vcml.NumberList
import org.vclipse.vcml.vcml.NumberListEntry
import org.vclipse.vcml.vcml.NumericCharacteristicValue
import org.vclipse.vcml.vcml.NumericInterval
import org.vclipse.vcml.vcml.NumericLiteral
import org.vclipse.vcml.vcml.NumericType
import org.vclipse.vcml.vcml.Precondition
import org.vclipse.vcml.vcml.SelectionCondition
import org.vclipse.vcml.vcml.SimpleDescription
import org.vclipse.vcml.vcml.SymbolList
import org.vclipse.vcml.vcml.SymbolicLiteral
import org.vclipse.vcml.vcml.SymbolicType
import org.vclipse.vcml.vcml.UnaryCondition
import org.vclipse.vcml.vcml.UnaryExpression
import org.vclipse.vcml.vcml.UnaryExpressionOperator
import org.vclipse.vcml.vcml.VCObject
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlModel

class Exporter {
	
	val VCML = VcmlFactory.eINSTANCE
	
	@Inject
	DependencySourceUtils dependencySourceUtils
	
	/* top-level entry point - export all interface designs */
	def CharSequence export(VcmlModel model) {
		val kmats = model.objects.filter(Material).filter[!configurationprofiles.empty];
		'''
		var {
			CSideEffect,
			CNameSpace, CNamed,
			CString, CInteger, CDate, CBoolean, CUnit,
			CHtml,
			CGroup, cmember,
			CSelect, ccase, cdefault, csel,
			CValidate,
		} = require("opencpq");
		
		var {cmemberNV, cmemberTOC} = require("../lib/utils");

		«FOR kmat: kmats»
		«kmat.export»
		«ENDFOR»
		
		«FOR c: model.objects.filter(SelectionCondition)»
		«c.export»
		«ENDFOR»
		«FOR c: model.objects.filter(Precondition)»
		«c.export»
		«ENDFOR»
		
		function in(v, list) {
			return list.indexOf(v) >= 0;
		}
		
		module.exports = {
			«FOR m: kmats»
				«m.jsId»,
			«ENDFOR»
		}
		'''
	}
	
	// configurable material
	def export(Material m) {
		val cgs = m.configurationprofiles.map[uidesign].filter[it | it != null].map[characteristicGroups].flatten
		'''
		var «m.jsId» = CNameSpace("productProps", CGroup(			
			function({productProps: p}) {return [
				«exportUI(cgs, m.classifications)»
				«FOR bom: m.billofmaterials»
					«exportBOM(bom)»
				«ENDFOR»
		]}));
		'''
	}
	
	// TODO use superclasses
	def exportUI(Iterable<CharacteristicGroup> cgs, Iterable<Classification> classifications) {
		val general = VCML.createCharacteristicGroup => [
			name = "General"
			description = VCML.createSimpleDescription => [value = "General"]
		];
		val Set<Characteristic> cstics = Sets.newHashSet;
		cgs.forEach[cstics.addAll(characteristics)];
		for (clf: classifications) {
			for (cstic: clf.cls.characteristics) {
				if (cstics.add(cstic)) {
					general.characteristics.add(cstic);
				}
			}
		}
		'''
		«FOR cg: cgs»
			«cg.export»
		«ENDFOR»
		«IF !general.characteristics.empty»
			«general.export»
		«ENDIF»
		'''
	}
	
	def exportBOM(BillOfMaterial bom) {
		'''
		«FOR item: bom.items»
		«export(item)»
		«ENDFOR»
		'''
	}
	
	def export(BOMItem item) {
		switch item {
			BOMItem_Class: '''// ### TODO «item»'''
			BOMItem_Material: {
				if (!item.material.configurationprofiles.empty)
					'''cmemberTOC(«item.material.nameAndDescription», «item.material.jsId»),'''
			}
		}
	}
	
	def export(BillOfMaterial bom) {
		'''
		var «bom.jsId» = CGroup([
			«FOR item: bom.items»
			«ENDFOR»
		]);
		'''
	}
	
	def export(ConfigurationProfile cp) {
		'''
		var «cp.jsId» = CGroup([
		]);
		'''
	}
	
	def export(InterfaceDesign id) {
		'''
		«FOR cg: id.characteristicGroups»
			«cg.export»
		«ENDFOR»
		'''
	}
	
	def export(CharacteristicGroup cg) {
		'''
		cmemberTOC(«cg.nameAndDescription», CGroup([
			«FOR cstic: cg.characteristics»
				«cstic.export»
			«ENDFOR»
		])),
		'''
	}
	
	def getPreconditionPrefix(Characteristic it) {
		val pre = dependencies?.dependencies?.filter(Precondition)?.head;
		if (pre!=null)
			'''
			() => «pre.jsId»(p) ?
			'''
	}
	def getPreconditionSuffix(Characteristic it) {
		val pre = dependencies?.dependencies?.filter(Precondition)?.head;
		if (pre!=null)
			''' : undefined'''
	}
	
	def export(Characteristic cstic) {
		val prefix = '''«cstic.preconditionPrefix»cmemberNV(«cstic.nameAndDescription»'''; 
		val suffix = ''')«cstic.preconditionSuffix»,'''
		if (cstic.multiValue) {
			switch t: cstic.type {
				SymbolicType:
					if (t.values.empty)
						'''
						«prefix», CString()«suffix», // TODO ### multivalue
						'''
					else if (t.isBoolean)
						'''
						«prefix», CBoolean({})«suffix» // TODO ### multivalue
						'''
					else
						'''
						«prefix», CGroup([
							«FOR v: t.values»
								cmemberNV(«v.nameAndDescription», CBoolean({})),
							«ENDFOR»
						])«suffix»
						'''
				NumericType:
					if (t.values.empty)
						'''
						«prefix», CInteger({defaultValue: 0})«suffix» // TODO ### multivalue
						'''
					else
						'''
							«FOR v: t.values»
								cmemberNV(«v.nameAndDescription», CBoolean({})),
							«ENDFOR»
						])),
						'''
				default:
					'''
					«prefix», ### unhandled CharacteristicType «t» ###«suffix»
					'''
			}
		} else 
			switch t: cstic.type {
				SymbolicType:
					if (t.values.empty)
						'''
						«prefix», CString()«suffix»
						'''
					else if (t.isBoolean)
						'''
						«prefix», CBoolean({})«suffix»
						'''
					else
						'''
						«prefix», CSelect([
							«FOR v: t.values»
								«IF v.^default»
									cdefault(ccase(«v.nameAndDescription»)),
								«ELSE»
									ccase(«v.nameAndDescription»),
								«ENDIF»
							«ENDFOR»
						])«suffix»
						'''
				NumericType:
					if (t.values.empty)
						'''
						«prefix», CInteger({defaultValue: 0})«suffix»
						'''
					else
						'''
						«prefix», CSelect([
							«FOR v: t.values»
								«IF v.^default»
									cdefault(ccase(«v.nameAndDescription»)),
								«ELSE»
									ccase(«v.nameAndDescription»),
								«ENDIF»
							«ENDFOR»
						])«suffix»
						'''
				DateType:
						'''
						«prefix», CDate({defaultValue: new Date().toDateString()})«suffix»
						'''
				default:
					'''
					«prefix», ### unhandled CharacteristicType «t» ###«suffix»
					'''
			}
	}

	def export(SelectionCondition c) {
		exportCondition(dependencySourceUtils.getSelectionConditionSource(c)?.condition, c);
	}
	
	def export(Precondition c) {
		exportCondition(dependencySourceUtils.getPreconditionSource(c)?.condition, c);
	}
	
	def exportCondition(Condition c, VCObject o) {
		'''
		function «o.jsId»(p) {
			return «translate(c)»;
		}
		'''
	}
	
	def boolean isBoolean(SymbolicType t) {
		val vals = t.values.map[name].map[toLowerCase].sortBy[it]
		(	vals == #["f", "t"] || vals == #["false", "true"] || 
		 	vals == #["n", "y"] || vals == #["no", "yes"]
		)
	}

	def nameAndDescription(VCObject it) {
		nameAndDescription(name, description)
	}
	
	def nameAndDescription(CharacteristicValue it) {
		nameAndDescription(name, description)
	}
	
	def nameAndDescription(NumericCharacteristicValue it) {
		switch e: entry {
			NumericLiteral: e.value
			NumericInterval: '''### TODO «e»###'''
		}
	}
	
	def nameAndDescription(CharacteristicGroup it) {
		nameAndDescription(name, description)
	}
	
	def nameAndDescription(String name, Description description) {
		val d = description?.english ?: name;
		'''«name.jsString», «d.jsString»'''
	}
	
	def jsId(VCObject obj) {
		if (obj!=null)
			'''«obj.eClass.name»_«obj.name»'''
		else
			'''### null (jsId) ###'''
	}
	
	def jsString(String s) {
		if (s!=null)
			'''"«Strings.convertToJavaString(s, false)»"'''
		else
			'''### null (jsString) ###'''
	}
	
	def english(Description d) {
		switch d {
			MultiLanguageDescriptions: d.descriptions.filter[language == Language.EN].head?.value
			SimpleDescription: d.value 
		}
	}
	
	// conditions and expressions
	
	def CharSequence translate(Condition c) {
		switch c {
			case null: '''/* null */ true'''
			BinaryCondition: '''(«c.left.translate» «c.operator.translateOp» «c.right.translate»)'''
			UnaryCondition: '''!(«c.condition.translate»)'''
			Comparison: '''(«c.left.translate» «c.operator.translate» «c.right.translate»)'''
			IsSpecified_P: '''«c.characteristic.characteristic.value» != null'''
			InCondition_P: '''in(«c.characteristic.characteristic.value», «c.list.translate»)'''
			default: '''unknown condition «c»'''
		}
	}
	
	def CharSequence translate(List l) {
		switch l {
			NumberList: '''[«l.entries.map[translate].join(", ")»]'''
			SymbolList: '''[«l.entries.map[translate].join(", ")»]'''
		}
	}
	
	def CharSequence translate(NumberListEntry e) {
		switch e {
			NumericInterval: '''### «e» ###''' 
			NumericLiteral: e.value
		}
	}
	
	def CharSequence translate(Expression e) {
		switch e {
			case null: '''/* null */ true'''
			BinaryExpression: '''(«e.left.translate» «e.operator.translateOp» «e.right.translate»)'''
			UnaryExpression: '''«e.operator.translate»(«e.expression.translate»)'''
			SymbolicLiteral: '''"«Strings.convertToJavaString(e.value, false)»"'''
			NumericLiteral: e.value
			CharacteristicReference_P: e.characteristic.value
			default: '''unknown expression «e»'''
		}
	}
	
	def CharSequence translate(UnaryExpressionOperator op) {
		switch op {
			case PLUS: "+"
			case MINUS: "-"
			case UC: "### UC ###"
			case LC: "### LC ###"
		}
	}
	
	def CharSequence translate(ComparisonOperator op) {
		switch op {
			case NE: "!=="
			case EQ:  "==="
			case LT:  "<"
			case LE:  "<="
			case GT:  ">"
			case GE:  ">="
		}
	}
	
	def CharSequence translateOp(String op) {
		switch op.toLowerCase {
			case "and": "&&"
			case "or": "||"
			case "||": "### concatenate ###"
			default: op
		}
	}
	
	def value(Characteristic it) {
		if (it == null)
			'''### value(Characteristic) == null ###'''
		else
			'''p["«Strings.convertToJavaString(name ?: "### name = null ###", false)»"]'''
	}
	
}
