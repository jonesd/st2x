package org.abora.ug2java.transform;

import java.util.List;

import org.abora.ug2java.JavaMethod;
import org.abora.ug2java.javatoken.JavaCallEnd;
import org.abora.ug2java.javatoken.JavaCallKeywordStart;
import org.abora.ug2java.javatoken.JavaIdentifier;
import org.abora.ug2java.javatoken.JavaKeyword;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcher;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcherFactory;



public class TransformIsKindOf extends MethodBodyTransformation {

	public TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class), 
				factory.token(JavaCallKeywordStart.class, "isKindOf"), 
				factory.token(JavaIdentifier.class), 
				factory.token(JavaCallEnd.class));
	}
	
	public void transform(JavaMethod javaMethod, List methodBodyTokens, int i) {
		methodBodyTokens.remove(i + 3);
		methodBodyTokens.remove(i + 1);
		methodBodyTokens.add(i + 1, new JavaKeyword("instanceof"));
	}
}