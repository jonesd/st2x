package org.abora.ug2java.transform;

import java.util.List;

import org.abora.ug2java.JavaMethod;
import org.abora.ug2java.javatoken.JavaCallStart;
import org.abora.ug2java.javatoken.JavaIdentifier;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcher;
import org.abora.ug2java.transform.tokenmatcher.TokenMatcherFactory;



public class TransformSuperCreate extends MethodBodyTransformation {

	public TokenMatcher matchers(TokenMatcherFactory factory) {
		return factory.seq(
				factory.token(JavaIdentifier.class, "super"), 
				factory.token(JavaCallStart.class, "create"));
	}

	public void transform(JavaMethod javaMethod, List tokens, int i) {
		JavaCallStart call = (JavaCallStart) tokens.get(i + 1);
		call.value = "super";
		tokens.remove(i);
	}
}