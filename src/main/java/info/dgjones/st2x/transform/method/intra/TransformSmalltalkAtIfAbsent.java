/**
 * The MIT License
 * Copyright (c) 2003 David G Jones
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.dgjones.st2x.transform.method.intra;

import java.util.List;

import info.dgjones.st2x.JavaMethod;
import info.dgjones.st2x.javatoken.JavaAssignment;
import info.dgjones.st2x.javatoken.JavaBlockEnd;
import info.dgjones.st2x.javatoken.JavaBlockStart;
import info.dgjones.st2x.javatoken.JavaCallArgumentSeparator;
import info.dgjones.st2x.javatoken.JavaCallEnd;
import info.dgjones.st2x.javatoken.JavaCallKeywordStart;
import info.dgjones.st2x.javatoken.JavaIdentifier;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaParenthesisEnd;
import info.dgjones.st2x.javatoken.JavaParenthesisStart;
import info.dgjones.st2x.javatoken.JavaStatementTerminator;
import info.dgjones.st2x.javatoken.JavaType;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



public class TransformSmalltalkAtIfAbsent extends AbstractMethodBodyTransformation {


	public TransformSmalltalkAtIfAbsent() {
		super();
	}
	public TransformSmalltalkAtIfAbsent(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class),
				factory.token(JavaAssignment.class),
				factory.token(JavaIdentifier.class, "Smalltalk"),
				factory.token(JavaCallKeywordStart.class, "ifAbsent"),
				factory.token(JavaIdentifier.class),
				factory.token(JavaCallArgumentSeparator.class)
				);
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaIdentifier varAssign = (JavaIdentifier)tokens.get(i);
		JavaIdentifier varSymbol = (JavaIdentifier)tokens.get(i+4);
		int callEnd = javaMethod.methodBody.findClosingCallEnd(i+3);

		javaMethod.methodBody.removeShouldMatch(callEnd+1, JavaStatementTerminator.class);
		javaMethod.methodBody.removeShouldMatch(callEnd, JavaCallEnd.class);

		for (int j = i+ 5; j >= i; j--) {
			tokens.remove(j);
		}
		int j = i;
		tokens.add(j++, new JavaType("Category"));
		tokens.add(j++, new JavaIdentifier("ifAbsent"));
		tokens.add(j++, new JavaAssignment());
		tokens.add(j++, new JavaIdentifier("Smalltalk"));
		tokens.add(j++, new JavaCallKeywordStart("at"));
		tokens.add(j++, new JavaIdentifier(varSymbol.value));
		tokens.add(j++, new JavaCallEnd());
		tokens.add(j++, new JavaStatementTerminator());
		tokens.add(j++, new JavaKeyword("if"));
		tokens.add(j++, new JavaParenthesisStart());
		tokens.add(j++, new JavaIdentifier("ifAbsent"));
		tokens.add(j++, new JavaKeyword("!="));
		tokens.add(j++, new JavaIdentifier("null"));
		tokens.add(j++, new JavaParenthesisEnd());
		tokens.add(j++, new JavaBlockStart());
		tokens.add(j++, new JavaIdentifier(varAssign.value));
		tokens.add(j++, new JavaAssignment());
		tokens.add(j++, new JavaIdentifier("ifAbsent"));
		tokens.add(j++, new JavaStatementTerminator());
		tokens.add(j++, new JavaBlockEnd());
		tokens.add(j++, new JavaKeyword("else"));
		
		return i;

	}
}
