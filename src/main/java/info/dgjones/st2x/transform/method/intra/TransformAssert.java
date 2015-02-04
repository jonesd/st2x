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
import info.dgjones.st2x.javatoken.JavaBlockEnd;
import info.dgjones.st2x.javatoken.JavaBlockStart;
import info.dgjones.st2x.javatoken.JavaCallStart;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaParenthesisEnd;
import info.dgjones.st2x.javatoken.JavaParenthesisStart;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



public class TransformAssert extends AbstractMethodBodyTransformation {


	public TransformAssert() {
		super();
	}
	public TransformAssert(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.token(JavaCallStart.class, "assert");
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaCallStart call = (JavaCallStart)tokens.get(i);
		int callEnd = javaMethod.methodBody.findClosingCallEnd(i);
		tokens.add(callEnd+2, new JavaBlockEnd());
		
		int expressionStart = i;
		if (i > 0) {
			expressionStart = javaMethod.methodBody.findStartOfExpression(i - 1);
		}
		tokens.add(i, new JavaParenthesisEnd());
		tokens.add(i+1, new JavaBlockStart());
		tokens.add(i+2, new JavaKeyword("throw"));
		tokens.add(i+3, new JavaKeyword("new"));
		call.value = "AboraAssertionException";
		
		if (i > 0) {
			if (!(tokens.get(expressionStart) instanceof JavaParenthesisStart) || !(tokens.get(i - 1) instanceof JavaParenthesisEnd)) {
				tokens.add(i, new JavaParenthesisEnd());
				tokens.add(expressionStart, new JavaParenthesisStart());
			} else if (tokens.get(i-1) instanceof JavaParenthesisEnd) {
				int parenStart = javaMethod.methodBody.findOpeningTokenOfType(i-1, JavaParenthesisStart.class);
				if (parenStart > expressionStart) {
					tokens.add(i, new JavaParenthesisEnd());
					tokens.add(expressionStart, new JavaParenthesisStart());
				}

			}
		}
		tokens.add(expressionStart, new JavaKeyword("if"));
		tokens.add(expressionStart+1, new JavaParenthesisStart());
		tokens.add(expressionStart+2, new JavaKeyword("!"));
		return i;

	}
}
