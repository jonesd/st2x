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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.dgjones.st2x.JavaMethod;
import info.dgjones.st2x.MethodBody;
import info.dgjones.st2x.javatoken.JavaCallEnd;
import info.dgjones.st2x.javatoken.JavaCallKeywordStart;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaStatementTerminator;
import info.dgjones.st2x.transform.method.MethodTransformation;



public class TransformPasseMethodCategory implements MethodTransformation {

	private static final Set EXCLUDE;
	static {
		Set set = new HashSet();
		set.add("IntegerRegion.chooseOne");
		EXCLUDE = Collections.unmodifiableSet(set);
	}
	
	public void transform(JavaMethod javaMethod) {
		if (EXCLUDE.contains(javaMethod.getName()) || EXCLUDE.contains(javaMethod.getQualifiedName()) || EXCLUDE.contains(javaMethod.getQualifiedSignature())) {
			return;
		}
		
		if (javaMethod.methodCategory.indexOf("passe") != -1) {
			// TODO duplicated behaviour from TransformPasse...
			List tokens = new ArrayList();
			tokens.add(new JavaKeyword("throw"));
			tokens.add(new JavaKeyword("new"));
			tokens.add(new JavaCallKeywordStart("PasseException"));
			tokens.add(new JavaCallEnd());
			tokens.add(new JavaStatementTerminator());
			javaMethod.methodBody = new MethodBody(tokens);

			javaMethod.isDeprecated = true;
		}
	}

}
