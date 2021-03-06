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
import info.dgjones.st2x.javatoken.JavaCallEnd;
import info.dgjones.st2x.javatoken.JavaCallKeywordStart;
import info.dgjones.st2x.javatoken.JavaComment;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaParenthesisEnd;
import info.dgjones.st2x.javatoken.JavaParenthesisStart;
import info.dgjones.st2x.javatoken.JavaToken;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



public class TransformAndOrs extends AbstractMethodBodyTransformation {

	public TransformAndOrs() {
		super();
	}
	public TransformAndOrs(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.any(
						factory.token(JavaCallKeywordStart.class, "and"), 
						factory.token(JavaCallKeywordStart.class, "or")), 
				factory.token(JavaBlockStart.class));
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaCallKeywordStart call = (JavaCallKeywordStart)tokens.get(i);
		int closingIndex = javaMethod.methodBody.findEndOfBlock(i + 1);
		//TODO automatically handle comments, rather than working around them here...
		int postClosingIndex = closingIndex+1;
		JavaToken postClosing = (JavaToken)tokens.get(closingIndex+1);
		if (postClosing instanceof JavaComment) {
			postClosingIndex += 1;
		}
		javaMethod.methodBody.removeShouldMatch(postClosingIndex, JavaCallEnd.class);
		javaMethod.methodBody.removeShouldMatch(closingIndex, JavaBlockEnd.class);
		tokens.add(closingIndex, new JavaParenthesisEnd());
		javaMethod.methodBody.removeShouldMatch(i + 1, JavaBlockStart.class);
		javaMethod.methodBody.removeShouldMatch(i, JavaCallKeywordStart.class);
		String value;
		if (call.value.equals("and")) {
			value = "&&";
		} else {
			value = "||";
		}
		tokens.add(i, new JavaKeyword(value));
		tokens.add(i + 1, new JavaParenthesisStart());
		return i;

	}
}
