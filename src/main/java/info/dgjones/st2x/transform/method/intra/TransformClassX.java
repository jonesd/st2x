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
import info.dgjones.st2x.javatoken.JavaIdentifier;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



public class TransformClassX extends AbstractMethodBodyTransformation {

	public TransformClassX() {
		super();
	}
	public TransformClassX(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class),
				factory.token(JavaCallStart.class, "classx"), 
				factory.token(JavaCallEnd.class));
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaIdentifier var = (JavaIdentifier)tokens.get(i);
		if (javaMethod.getJavaCodebase().getJavaClass(var.value) != null) {
			return i;
		}
		tokens.add(i, new JavaIdentifier("AboraSupport"));
		tokens.add(i+1, new JavaCallKeywordStart("findCategory"));
		tokens.remove(i+3);
		tokens.add(i+3, new JavaCallStart("getClass"));
		tokens.add(i+5, new JavaCallEnd());
		
		return i;
	}
}
