/*
 * Udanax-Gold2Java - Translator
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003, 2005 David G Jones
 */
package org.abora.ug2java.transform.method.intra;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.abora.ug2java.JavaMethod;
import org.abora.ug2java.javatoken.JavaCallEnd;
import org.abora.ug2java.javatoken.JavaCallStart;
import org.abora.ug2java.javatoken.JavaKeyword;
import org.abora.ug2java.transform.method.AbstractMethodBodyTransformation;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcher;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcherFactory;



public class TransformUnimplemented extends AbstractMethodBodyTransformation {

	private static final Set IGNORE;
	static {
		Set set = new HashSet();
		set.add("DiskTester.destroyTest");
		IGNORE = Collections.unmodifiableSet(set);
	}
	
public TransformUnimplemented() {
		super();
	}
	public TransformUnimplemented(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaCallStart.class, "unimplemented"),
				factory.token(JavaCallEnd.class));
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		if (IGNORE.contains(javaMethod.getName()) || IGNORE.contains(javaMethod.getQualifiedName()) || IGNORE. contains(javaMethod.getQualifiedSignature())) {
			return i;
		}
		
		JavaCallStart call = (JavaCallStart)tokens.get(i);
		tokens.add(i, new JavaKeyword("throw"));
		tokens.add(i + 1, new JavaKeyword("new"));
		call.value = "UnimplementedException";
		
		return i;
	}
}
