/*
 * Udanax-Gold2Java - Translator
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003, 2005 David G Jones
 */
package org.abora.ug2java.transform.method.intra;

import java.util.List;

import org.abora.ug2java.JavaMethod;
import org.abora.ug2java.javatoken.JavaCallArgumentSeparator;
import org.abora.ug2java.javatoken.JavaCallEnd;
import org.abora.ug2java.javatoken.JavaCallKeywordStart;
import org.abora.ug2java.javatoken.JavaIdentifier;
import org.abora.ug2java.javatoken.JavaParenthesisEnd;
import org.abora.ug2java.javatoken.JavaParenthesisStart;
import org.abora.ug2java.transform.method.AbstractMethodBodyTransformation;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcher;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcherFactory;



public class TransformFeWrapperSpecPointerToStaticMemberCall extends AbstractMethodBodyTransformation {

	public TransformFeWrapperSpecPointerToStaticMemberCall() {
		super();
	}
	public TransformFeWrapperSpecPointerToStaticMemberCall(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaParenthesisStart.class),
				factory.token(JavaParenthesisStart.class),
				factory.token(JavaIdentifier.class, "Smalltalk"),
				factory.token(JavaCallKeywordStart.class, "at"),
				factory.token(JavaIdentifier.class),
				factory.token(JavaCallEnd.class),
				factory.token(JavaParenthesisEnd.class),
				factory.token(JavaCallKeywordStart.class, "pointerToStaticMember"));
	}
	

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		int callEnd = javaMethod.methodBody.findClosingTokenOfType(i, JavaParenthesisEnd.class);
		tokens.remove(callEnd);
		
		tokens.remove(i + 7);
		tokens.remove(i + 6);
		tokens.add(i + 6, new JavaCallArgumentSeparator());
		tokens.remove(i + 1);
		tokens.remove(i);
		tokens.add(i, new JavaIdentifier("AboraSupport"));
		tokens.add(i+1, new JavaCallKeywordStart("pointerToStaticMember"));

		return i;
	}
}
