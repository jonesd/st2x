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
package info.dgjones.st2x;

import java.util.HashMap;
import java.util.Map;



public class Annotation {
	
	
	private final Map<String,Object> annotations = new HashMap<String,Object>();

	//TODO convert to bloch enumeration
	public static final String PROBLEM_SIGNALS = "Signals";
	public static final String REQUIRES = "Requires";

	public Annotation() {
		super();
	}
	
	public Object get(String annotationKey) {
		return annotations.get(annotationKey);
	}
	
	public Object getIfNone(String annotationKey, Object defaultValue) {
		if (annotations.containsKey(annotationKey)) {
			return get(annotationKey);
		} else {
			return defaultValue;
		}
	}
	
	public void put(String annotationKey, Object value) {
		annotations.put(annotationKey, value);
	}

}
