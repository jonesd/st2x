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
import info.dgjones.st2x.javatoken.JavaCallEnd;
import info.dgjones.st2x.javatoken.JavaCallKeywordStart;
import info.dgjones.st2x.javatoken.JavaCallStart;
import info.dgjones.st2x.javatoken.JavaIdentifier;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaStatementTerminator;
import info.dgjones.st2x.javatoken.JavaType;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



public class TransformWriteStreamOnString extends AbstractMethodBodyTransformation {


	public TransformWriteStreamOnString() {
		super();
	}
	public TransformWriteStreamOnString(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class),
				factory.token(JavaAssignment.class),
				factory.token(JavaIdentifier.class, "WriteStream"),
				factory.token(JavaCallKeywordStart.class, "on"));
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaIdentifier identifier = (JavaIdentifier)tokens.get(i);
		
		int callEnd = javaMethod.methodBody.findClosingCallEnd(i+3);
		javaMethod.methodBody.remove(i+2, callEnd+1);

		String stringWriterVar = generateStringWriterName(identifier.value);
		
		tokens.add(i, new JavaType("StringWriter"));
		tokens.add(i+1, new JavaIdentifier(stringWriterVar));
		tokens.add(i+2, new JavaAssignment());
		tokens.add(i+3, new JavaKeyword("new"));
		tokens.add(i+4, new JavaCallStart("StringWriter"));
		tokens.add(i+5, new JavaCallEnd());
		tokens.add(i+6, new JavaStatementTerminator());
		tokens.add(i+9, new JavaKeyword("new"));
		tokens.add(i+10, new JavaCallKeywordStart("PrintWriter"));
		tokens.add(i+11, new JavaIdentifier(stringWriterVar));
		tokens.add(i+12, new JavaCallEnd());
		
		return i;
	}
	
	public static String generateStringWriterName(String printWriterVarName) {
		return printWriterVarName+"String";
	}
}
