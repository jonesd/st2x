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
import info.dgjones.st2x.javatoken.JavaCallEnd;
import info.dgjones.st2x.javatoken.JavaCallStart;
import info.dgjones.st2x.javatoken.JavaIdentifier;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaParenthesisEnd;
import info.dgjones.st2x.javatoken.JavaParenthesisStart;
import info.dgjones.st2x.javatoken.JavaStatementTerminator;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



/**
 * TODO this class exists only to a possible bug in the GrandHashtable constructer where
 * the calculated nodIndexShift could be overestimated, so leading later code to generate
 * a collection index too high. The concern is that this code could be a single fix
 * for a more endemic problem of an incorrect // implementation...
 */
public class TransformGrandHashTableConstructor extends AbstractMethodBodyTransformation {

	public TransformGrandHashTableConstructor() {
		super();
	}
	public TransformGrandHashTableConstructor(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class, "ExponentialHashMap"),
				factory.token(JavaCallStart.class, "hashBits"),
				factory.token(JavaCallEnd.class),
				factory.token(JavaKeyword.class, "/"),
				factory.token(JavaIdentifier.class, "numNodes"),
				factory.token(JavaStatementTerminator.class));
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		if (!javaMethod.getQualifiedName().equals("GrandHashTable.GrandHashTable")) {
			return i;
		}
		tokens.add(i+4, new JavaParenthesisStart());
		tokens.add(i+6, new JavaKeyword("-"));
		tokens.add(i+7, new IntegerLiteral(1));
		tokens.add(i+8, new JavaParenthesisEnd());
		
		return i;
	}
}
