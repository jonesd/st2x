/*
 * Udanax-Gold2Java - Translator
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003, 2005 David G Jones
 */
package org.abora.ug2java.transform.method.intra;

import java.util.List;

import org.abora.ug2java.JavaMethod;
import org.abora.ug2java.javatoken.JavaCallEnd;
import org.abora.ug2java.javatoken.JavaCallKeywordStart;
import org.abora.ug2java.javatoken.JavaIdentifier;
import org.abora.ug2java.javatoken.JavaKeyword;
import org.abora.ug2java.transform.method.AbstractMethodBodyTransformation;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcher;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcherFactory;



public class TransformIsKindOf extends AbstractMethodBodyTransformation {

	public TransformIsKindOf() {
		super();
	}
	public TransformIsKindOf(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class), 
				factory.token(JavaCallKeywordStart.class, "isKindOf"), 
				factory.token(JavaIdentifier.class), 
				factory.token(JavaCallEnd.class));
	}
	
	protected int transform(JavaMethod javaMethod, List methodBodyTokens, int i) {
		JavaIdentifier var = (JavaIdentifier)methodBodyTokens.get(i+2);
		String varType = javaMethod.findTypeOfVariable(var.value);
		if (!"Category".equals(varType)) {
			methodBodyTokens.remove(i + 3);
			methodBodyTokens.remove(i + 1);
			methodBodyTokens.add(i + 1, new JavaKeyword("instanceof"));
		}
		return i;
	}
}
