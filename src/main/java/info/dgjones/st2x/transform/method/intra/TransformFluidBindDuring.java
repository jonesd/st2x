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
import info.dgjones.st2x.javatoken.JavaCallStart;
import info.dgjones.st2x.javatoken.JavaComment;
import info.dgjones.st2x.javatoken.JavaIdentifier;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaStatementTerminator;
import info.dgjones.st2x.javatoken.JavaType;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



public class TransformFluidBindDuring extends AbstractMethodBodyTransformation {
	

	public TransformFluidBindDuring() {
		super();
	}
	public TransformFluidBindDuring(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class),
				factory.token(JavaCallStart.class, "fluidBindDuring"));
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaIdentifier identifier = (JavaIdentifier)tokens.get(i);
		//TODO library way to lowercase first letter
		String oldValueVariable = String.valueOf(Character.toLowerCase(identifier.value.charAt(0)))+identifier.value.substring(1)+"OldValue";
		int argumentStart = javaMethod.methodBody.findNextTokenOfType(i+1, JavaCallArgumentSeparator.class);
		int startOfBlack = argumentStart+1;
		if (tokens.get(startOfBlack) instanceof JavaComment) {
			//TODO should be filtering out comments implicitly
			++startOfBlack;
		}
		if (!(tokens.get(startOfBlack) instanceof JavaBlockStart)) {
			System.out.println("--Failed JavaBlockStart match for fluidBindDuring");
			return i;
		}
		int endOfBlock = javaMethod.methodBody.findEndOfBlock(startOfBlack);
		if (!(tokens.get(endOfBlock+1) instanceof JavaCallEnd)) {
			System.out.println("--Failed JavaBlockEnd match for fluidBindDuring");
			return i;
		}
		int endOfCall = endOfBlock+1;// javaMethod.methodBody.findClosingCallEnd(i+1);

		tokens.remove(endOfCall+1);
		tokens.remove(endOfCall);
		tokens.add(endOfCall, new JavaKeyword("finally"));
		tokens.add(endOfCall+1, new JavaBlockStart());
		tokens.add(endOfCall+2, new JavaIdentifier("AboraBlockSupport"));
		tokens.add(endOfCall+3, new JavaCallKeywordStart("exitFluidBindDuring"));
		tokens.add(endOfCall+4, new JavaIdentifier(identifier.value));
		tokens.add(endOfCall+5, new JavaCallArgumentSeparator());
		tokens.add(endOfCall+6, new JavaIdentifier(oldValueVariable));
		tokens.add(endOfCall+7, new JavaCallEnd());
		tokens.add(endOfCall+8, new JavaStatementTerminator());
		tokens.add(endOfCall+9, new JavaBlockEnd());
		
		tokens.remove(argumentStart);
		tokens.add(argumentStart, new JavaCallEnd());
		tokens.add(argumentStart+1, new JavaStatementTerminator());
		tokens.add(argumentStart+2, new JavaKeyword("try"));
		
		tokens.remove(i+1);
		tokens.remove(i);
		
		tokens.add(i, new JavaType("Object"));
		tokens.add(i+1, new JavaIdentifier(oldValueVariable));
		tokens.add(i+2, new JavaAssignment());
		tokens.add(i+3, new JavaIdentifier("AboraBlockSupport"));
		tokens.add(i+4, new JavaCallKeywordStart("enterFluidBindDuring"));
		tokens.add(i+5, new JavaIdentifier(identifier.value));
		tokens.add(i+6, new JavaCallArgumentSeparator());
		
		return i;
	}
}
