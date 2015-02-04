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
import info.dgjones.st2x.javatoken.JavaCallArgumentSeparator;
import info.dgjones.st2x.javatoken.JavaCallKeywordStart;
import info.dgjones.st2x.javatoken.JavaCallStart;
import info.dgjones.st2x.javatoken.JavaIdentifier;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;

public class TransformSocket extends AbstractMethodBodyTransformation {


	public TransformSocket() {
		super();
	}

	public TransformSocket(TokenMatcherFactory factory) {
		super(factory);
	}
	
	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.token(JavaCallStart.class, "close|acceptNonBlock|listenFor");
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaCallStart call = (JavaCallStart)tokens.get(i);
		int expressionStart = javaMethod.methodBody.findStartOfExpressionMinimal(i-1);
		
		if (expressionStart == i-1 && tokens.get(i-1) instanceof JavaIdentifier) {
			String varName = ((JavaIdentifier)tokens.get(i-1)).value;
			String varType = javaMethod.findTypeOfVariable(varName);
			if (varType != null && !varType.equals("int")) {
				return i;
			}
		}
		
		tokens.remove(i);
		if (call instanceof JavaCallKeywordStart) {
			tokens.add(i, new JavaCallArgumentSeparator());
		}
		
		tokens.add(expressionStart, new JavaIdentifier("AboraSocketSupport"));
		tokens.add(expressionStart+1, new JavaCallKeywordStart(call.value));
				
		return i;
	}
}