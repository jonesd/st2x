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
import info.dgjones.st2x.javatoken.IntegerLiteral;
import info.dgjones.st2x.javatoken.JavaAssignment;
import info.dgjones.st2x.javatoken.JavaBlockStart;
import info.dgjones.st2x.javatoken.JavaCallEnd;
import info.dgjones.st2x.javatoken.JavaCallKeywordStart;
import info.dgjones.st2x.javatoken.JavaCallStart;
import info.dgjones.st2x.javatoken.JavaCast;
import info.dgjones.st2x.javatoken.JavaIdentifier;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaLoopTerminator;
import info.dgjones.st2x.javatoken.JavaParenthesisEnd;
import info.dgjones.st2x.javatoken.JavaParenthesisStart;
import info.dgjones.st2x.javatoken.JavaStatementTerminator;
import info.dgjones.st2x.javatoken.JavaType;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



public class TransformAllSubclassesDo extends AbstractMethodBodyTransformation {

	public TransformAllSubclassesDo() {
		super();
	}
	public TransformAllSubclassesDo(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class),
				factory.token(JavaCallKeywordStart.class, "allSubclassesDo|subclassesDo|allInstancesDo"),
				factory.token(JavaBlockStart.class)
//				factory.token(JavaType.class, "Character"),
//				factory.token(JavaIdentifier.class),
//				factory.token(JavaStatementTerminator.class)
				);
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaIdentifier sourceVar = (JavaIdentifier)tokens.get(i);
		JavaCallKeywordStart call = (JavaCallKeywordStart)tokens.get(i+1);
		
		String sourceVarName = call.value.substring(0, call.value.length() - 2);
				
		int blockEnd = javaMethod.methodBody.findEndOfBlock(i+2);
		javaMethod.methodBody.shouldMatch(blockEnd+1, JavaCallEnd.class);
		javaMethod.methodBody.shouldMatch(blockEnd+2, JavaStatementTerminator.class);
		
		String sizeCallName = "size";
		String elementTypeName = "AboraClass";
		if (call.value.equals("allInstancesDo")) {
			elementTypeName = sourceVar.value;
		}
		String elementAccessorName = "get";
		
		tokens.remove(blockEnd+2);
		tokens.remove(blockEnd+1);
		
		int j = i;
		tokens.add(j++, new JavaType("OrderedCollection"));
		tokens.add(j++, new JavaIdentifier(sourceVarName));
		tokens.add(j++, new JavaAssignment());
		tokens.add(j++, new JavaIdentifier("AboraSupport"));
		tokens.add(j++, new JavaCallKeywordStart(sourceVarName));
		tokens.add(j++, new JavaIdentifier(sourceVar.value));
		if (javaMethod.getJavaCodebase().getJavaClass(sourceVar.value) != null) {
			tokens.add(j++, new JavaIdentifier("class"));
		}
		tokens.add(j++, new JavaCallEnd());
		tokens.add(j++, new JavaStatementTerminator());

		tokens.add(j++, new JavaKeyword("for"));
		tokens.add(j++, new JavaParenthesisStart());
		tokens.add(j++, new JavaType("int"));
		tokens.add(j++, new JavaIdentifier("doIndex"));
		tokens.add(j++, new JavaAssignment());
		tokens.add(j++, new IntegerLiteral(0));
		tokens.add(j++, new JavaLoopTerminator());
		tokens.add(j++, new JavaIdentifier("doIndex"));
		tokens.add(j++, new JavaKeyword("<"));
		tokens.add(j++, new JavaIdentifier(sourceVarName));
		tokens.add(j++, new JavaCallStart(sizeCallName));
		tokens.add(j++, new JavaCallEnd());
		tokens.add(j++, new JavaLoopTerminator());
		tokens.add(j++, new JavaIdentifier("doIndex"));
		tokens.add(j++, new JavaKeyword("++"));
		tokens.add(j++, new JavaParenthesisEnd());
		tokens.remove(j);
		tokens.remove(j);
		j++;
		tokens.remove(j);
		tokens.add(j++, new JavaType(elementTypeName));
		j++;
		tokens.add(j++, new JavaAssignment());
		tokens.add(j++, new JavaCast(elementTypeName));
		tokens.add(j++, new JavaIdentifier(sourceVarName));
		tokens.add(j++, new JavaCallKeywordStart(elementAccessorName));
		tokens.add(j++, new JavaIdentifier("doIndex"));
		tokens.add(j++, new JavaCallEnd());
		
		return i;
	}
}
