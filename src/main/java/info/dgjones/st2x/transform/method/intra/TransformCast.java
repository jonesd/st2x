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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.dgjones.st2x.JavaClass;
import info.dgjones.st2x.JavaMethod;
import info.dgjones.st2x.javatoken.JavaCallEnd;
import info.dgjones.st2x.javatoken.JavaCallKeywordStart;
import info.dgjones.st2x.javatoken.JavaCast;
import info.dgjones.st2x.javatoken.JavaIdentifier;
import info.dgjones.st2x.transform.method.AbstractMethodBodyTransformation;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcher;
import info.dgjones.st2x.transform.tokenmatcher.TokenMatcherFactory;



public class TransformCast extends AbstractMethodBodyTransformation {

	private static final Map OVERRIDE_CAST;
	static {
		Map map = new HashMap();
		map.put("XnBufferedReadStream.getBytes.String", "");
		map.put("XnReadStream.getBytes.String", "");
		map.put("Converter.CONVERT.to", "");
		OVERRIDE_CAST = Collections.unmodifiableMap(map);
	}
	
	public TransformCast() {
		super();
	}
	public TransformCast(TokenMatcherFactory factory) {
		super(factory);
	}

	protected TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.any(
						factory.token(JavaCallKeywordStart.class, "cast"),
						factory.token(JavaCallKeywordStart.class, "basicCast"),
						factory.token(JavaCallKeywordStart.class, "quickCast")), 
				factory.token(JavaIdentifier.class),
				factory.token(JavaCallEnd.class));
	}

	protected int transform(JavaMethod javaMethod, List tokens, int i) {
		JavaIdentifier type = (JavaIdentifier)tokens.get(i + 1);
		String castTypeName = findCastType(javaMethod, type);
		int start = javaMethod.methodBody.findStartOfExpression(i - 1);
		tokens.remove(i + 2);
		tokens.remove(i + 1);
		tokens.remove(i);
		if (i > 0 && tokens.get(i-1) instanceof JavaIdentifier) {
			JavaIdentifier var = (JavaIdentifier)tokens.get(i-1);
			String varTypeName = javaMethod.findTypeOfVariable(var.value);
			if (varTypeName != null && javaMethod.methodBody.findStartOfExpressionMinimal(i-1) == i-1) {
				if (varTypeName.equals(castTypeName)) {
//				if (!javaMethod.getJavaCodebase().shouldDowncast(varType, castType)) {
					castTypeName = "";
				} else {
					JavaClass castType = javaMethod.getJavaCodebase().getJavaClass(castTypeName);
					JavaClass varType = javaMethod.getJavaCodebase().getJavaClass(varTypeName);
					if (castType != null && varType != null && varType.isSubclassOf(castType)) {
						castTypeName = "";
					}
				}
			}
		}
		if (!castTypeName.equals("")) {
			tokens.add(start, new JavaCast(castTypeName));
		}
		return i;
	}
	
	private String findCastType(JavaMethod javaMethod, JavaIdentifier type) {
		String name = javaMethod.javaClass.className+"."+javaMethod.name+"."+type.value;
		String castType = (String)OVERRIDE_CAST.get(name);
		if (castType == null) {
			castType = type.value;
		}
		return castType;
	}
}
