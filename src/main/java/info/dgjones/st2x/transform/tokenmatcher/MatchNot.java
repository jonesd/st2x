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
package info.dgjones.st2x.transform.tokenmatcher;

import java.util.List;

import info.dgjones.st2x.javatoken.JavaToken;
import info.dgjones.st2x.util.ToStringGenerator;



public class MatchNot implements TokenMatcher {

	private final TokenMatcher tokenMatcher;
	
	public MatchNot(TokenMatcher tokenMatcher) {
		this.tokenMatcher = tokenMatcher;
	}
	
	public boolean doesMatch(List<JavaToken> tokens, int i) {
		return !tokenMatcher.doesMatch(tokens, i);
	}
	
	public String toString() {
		ToStringGenerator generator = new ToStringGenerator(this);
		generator.add("match", tokenMatcher);
		return generator.end();
	}


}
