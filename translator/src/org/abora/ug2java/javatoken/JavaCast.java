/*
 * Udanax-Gold2Java - Translator
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003, 2005 David G Jones
 */
package org.abora.ug2java.javatoken;

public class JavaCast extends JavaToken {

	public JavaCast(String value) {
		super(value);
	}
	public void write(StringBuffer buffer) {
		buffer.append("(");
		super.write(buffer);
		buffer.append(") ");
	}

}
