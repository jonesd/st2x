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
import info.dgjones.st2x.javatoken.JavaCallEnd;
import info.dgjones.st2x.javatoken.JavaCallKeywordStart;
import info.dgjones.st2x.javatoken.JavaCallStart;
import info.dgjones.st2x.javatoken.JavaComment;
import info.dgjones.st2x.javatoken.JavaIdentifier;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaParenthesisEnd;
import info.dgjones.st2x.javatoken.JavaToken;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



public class TransformNewBecome extends AbstractMethodBodyTransformation {


	public TransformNewBecome() {
		super();
	}
	public TransformNewBecome(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
//				factory.token(JavaParenthesisStart.class),
//				factory.token(JavaIdentifier.class), 
				factory.token(JavaCallKeywordStart.class, "newBecome|newAllocType"),
				factory.token(JavaToken.class),
				factory.token(JavaCallEnd.class),
				factory.token(JavaParenthesisEnd.class),
				factory.token(JavaKeyword.class, "new"), 
				factory.token(JavaCallStart.class));
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaCallKeywordStart newType = (JavaCallKeywordStart)tokens.get(i);
		JavaCallStart call = (JavaCallStart)tokens.get(i+5);
		
		tokens.remove(i+3);
		tokens.remove(i+2);
		tokens.remove(i+1);
		tokens.remove(i);
		tokens.add(i, new JavaComment("TODO "+newType.value));
		
		if (i > 0) {
			if (tokens.get(i-1) instanceof JavaIdentifier) {
				JavaIdentifier type = (JavaIdentifier)tokens.get(i-1);
				call.value = type.value;
				tokens.remove(i-1);
				tokens.remove(i-2);
			} else {
				tokens.remove(i-1);
			}
		}

		return i;
	}
}
