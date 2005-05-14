/*
 * Udanax-Gold2Java - Translator
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003, 2005 David G Jones
 */
package org.abora.ug2java.transform.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.abora.ug2java.JavaMethod;
import org.abora.ug2java.MethodBody;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcher;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcherFactory;



public abstract class AbstractMethodBodyTransformation implements MethodTransformation {

	private final TokenMatcher tokenMatcher;
	
	public AbstractMethodBodyTransformation() {
		this(new TokenMatcherFactory());
	}
	
	public AbstractMethodBodyTransformation(TokenMatcherFactory factory) {
		tokenMatcher = matchers(factory);
	}
	
	public void transform(JavaMethod javaMethod) {
		MethodBody methodBody = javaMethod.methodBody;
		List methodBodyTokens = methodBody.tokens;
		for (int i = 0; i < methodBodyTokens.size(); i++) {
			if (tokenMatcher.doesMatch(methodBodyTokens, i)) {
				int nextI = transform(javaMethod, methodBodyTokens, i);
				i = nextI;
			}
		}
	}
	
	protected abstract TokenMatcher matchers(TokenMatcherFactory factory);
	protected abstract int transform(JavaMethod javaMethod, List methodBodyTokens, int indexOfMatch);

	protected String regularExpressionOr(Collection c1, Collection c2) {
		//TODO insanity for c1.copyWithAll(c2)...
		Object[] array1 = c1.toArray();
		Object[] array2 = c2.toArray();
		Object[] array12 = new Object[array1.length + array2.length];
		System.arraycopy(array1, 0, array12, 0, array1.length);
		System.arraycopy(array2, 0, array12, array1.length, array2.length);
		return regularExpressionOr(Arrays.asList(array12));
	}

	protected String regularExpressionOr(Collection c) {
		StringBuffer regularExpression = new StringBuffer();
		Iterator iterator = c.iterator();
		while (iterator.hasNext()) {
			regularExpression.append((String)iterator.next());
			if (iterator.hasNext()) {
				regularExpression.append("|");
			}
		}
		return regularExpression.toString();
	}

}