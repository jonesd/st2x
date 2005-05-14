/*
 * Udanax-Gold2Java - Translator
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003, 2005 David G Jones
 */
package org.abora.ug2java.transform.method.intra;

import java.util.List;

import org.abora.ug2java.JavaMethod;
import org.abora.ug2java.javatoken.JavaCallEnd;
import org.abora.ug2java.javatoken.JavaCallStart;
import org.abora.ug2java.javatoken.JavaIdentifier;
import org.abora.ug2java.transform.method.AbstractMethodBodyTransformation;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcher;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcherFactory;



public class TransformCharacterStaticCall extends AbstractMethodBodyTransformation {

	public TransformCharacterStaticCall() {
		super();
	}
	public TransformCharacterStaticCall(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class, "Character"),
			factory.token(JavaCallStart.class),
			factory.token(JavaCallEnd.class)
			);
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaIdentifier call = (JavaIdentifier)tokens.get(i);
		call.value = "AboraCharacterSupport";
		return i;
	}
}
