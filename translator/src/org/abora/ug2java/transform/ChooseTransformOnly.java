/*
 * Udanax-Gold2Java - Translator
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003, 2005 David G Jones
 */
package org.abora.ug2java.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.abora.ug2java.JavaMethod;
import org.abora.ug2java.javatoken.JavaBlockEnd;
import org.abora.ug2java.javatoken.JavaCallEnd;
import org.abora.ug2java.javatoken.JavaCallStart;
import org.abora.ug2java.javatoken.JavaIdentifier;
import org.abora.ug2java.javatoken.JavaStatementTerminator;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcher;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcherFactory;



public class ChooseTransformOnly extends AbstractMethodBodyTransformation {

	private static final List TRANSLATE_METHODS;
	static {
		List list = new ArrayList();
		list.add("IntegerPos.actualHashForEqual");
		list.add("IntegerPos.integerHash");
		
		//TODO only for tests...
		list.add("translateOnly");

		TRANSLATE_METHODS = Collections.unmodifiableList(list);
	}

	private static final List SMALLTALK_METHODS;
	static {
		List list = new ArrayList();

		//TODO only for tests...
		list.add("smalltalkOnly");

		SMALLTALK_METHODS = Collections.unmodifiableList(list);
	}

	public ChooseTransformOnly() {
		super();
	}
	public ChooseTransformOnly(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaBlockEnd.class),
				factory.any(
						factory.token(JavaIdentifier.class, "translateOnly"),
						factory.token(JavaIdentifier.class, "smalltalkOnly")),
				factory.token(JavaStatementTerminator.class));
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaIdentifier onlyType = (JavaIdentifier)tokens.get(i+1);
		boolean isTranslateOnly = onlyType.value.equals("translateOnly");
		
		String shortName = javaMethod.name;
		String fullName = javaMethod.javaClass.className+"."+shortName;
		boolean shouldTranslate = TRANSLATE_METHODS.contains(shortName) || TRANSLATE_METHODS.contains(fullName);
		boolean shouldSmalltalk = SMALLTALK_METHODS.contains(shortName) || SMALLTALK_METHODS.contains(fullName);
		
		if (isTranslateOnly) {
			if (shouldTranslate) {
				return acceptOnlyBlock(javaMethod, tokens, i);
			} else if (shouldSmalltalk) {
				return rejectOnlyBlock(javaMethod, tokens, i);
			}
		} else {
			if (shouldTranslate) {
				return rejectOnlyBlock(javaMethod, tokens, i);
			} else if (shouldSmalltalk) {
				return acceptOnlyBlock(javaMethod, tokens, i);
			}
		}
		
		return simpleBlock(javaMethod, tokens, i, onlyType.value);
	}
		
	private int acceptOnlyBlock(JavaMethod javaMethod, List tokens, int i) {
		int blockStart = javaMethod.methodBody.findStartOfBlock(i);
		tokens.remove(i+2);
		tokens.remove(i+1);
		tokens.remove(i);
		tokens.remove(blockStart);
		
		return i - 1;
	}

	private int rejectOnlyBlock(JavaMethod javaMethod, List tokens, int i) {
		int blockStart = javaMethod.methodBody.findStartOfBlock(i);
		for (int j = i+2; j >= blockStart; --j) {
			tokens.remove(j);
		}
		return blockStart;
	}

	private int simpleBlock(JavaMethod javaMethod, List tokens, int i, String call) {
		int blockStart = javaMethod.methodBody.findStartOfBlock(i);
		tokens.remove(i+2);
		tokens.remove(i+1);

		tokens.add(blockStart, new JavaIdentifier("AboraSupport"));
		tokens.add(blockStart+1, new JavaCallStart(call));
		tokens.add(blockStart+2, new JavaCallEnd());
		tokens.add(blockStart+3, new JavaStatementTerminator());
		
		javaMethod.javaClass.includeImportForType("AboraSupport");
		
		return i;
	}
}
