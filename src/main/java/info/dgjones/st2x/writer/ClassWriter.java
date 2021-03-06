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
package info.dgjones.st2x.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import info.dgjones.st2x.JavaClass;
import info.dgjones.st2x.JavaField;
import info.dgjones.st2x.JavaMethod;
import info.dgjones.st2x.MethodBody;
import info.dgjones.st2x.javatoken.JavaToken;
import info.dgjones.st2x.stscanner.ChunkDetails;



public class ClassWriter {

	public static final String DEFAULT_FILE_COMMENT = "Abora-Gold\n"
					+ "Part of the Abora hypertext project: http://www.abora.org\n"
					+ "Copyright 2003, 2005 David G Jones\n"
					+ " \n"
					+ "Translated from Udanax-Gold source code: http://www.udanax.com\n"
					+ "Copyright 1979-1999 Udanax.com. All rights reserved";
	
	private final JavaClass javaClass;
	private final String fileComment;
	public boolean quoteSmalltalk = true;
	public boolean shouldIndent = true;

	private int JAVADOC_MARGIN = 90;
	
	private static final boolean INCLUDE_METHOD_BODIES = true;

	public ClassWriter(JavaClass javaClass) {
		//TODO file comment should come from a general configuration object
		this(javaClass, DEFAULT_FILE_COMMENT);
	}
	
	public ClassWriter(JavaClass javaClass, String fileComment) {
		this.javaClass = javaClass;
		this.fileComment = fileComment;
	}
	
	protected void writeVariables(PrintWriter writer) {
		for (Iterator iter = javaClass.getFields().iterator(); iter.hasNext();) {
			JavaField javaField = (JavaField) iter.next();
			writer.println("\tprotected " + javaField.modifiers + javaField.type + " " + javaField.name + ";");
		}
	}

	protected void writeMethodBody(MethodBody methodBody, PrintWriter writer) {
		Indentation indentation = getIndenter();
		indentation.increase();
		StringWriter stringWriter = new StringWriter();
		JavaWriter javaWriter = new JavaWriter(new PrintWriter(stringWriter), indentation);
		
			for (Iterator e = methodBody.tokens.iterator(); e.hasNext();) {
				JavaToken token = (JavaToken) e.next();
				token.write(javaWriter);
			}
			javaWriter.flush();
			writer.print(stringWriter.toString());
	}

	private Indentation getIndenter() {
		Indentation indentation;
		if (shouldIndent) {
			indentation = new SimpleIndenter();
		} else {
			indentation = new FlushIndentation();
		}
		return indentation;
	}

	private void writeStaticBlocks(PrintWriter writer) {
		for (Iterator iter = javaClass.getStaticBlocks().iterator(); iter.hasNext();) {
			JavaMethod javaMethod = (JavaMethod) iter.next();
			writeStaticBlock(javaMethod, writer);
		}
	}
	
	private void writeMethods(PrintWriter writer) {
		for (Iterator iter = javaClass.methods.iterator(); iter.hasNext();) {
			JavaMethod javaMethod = (JavaMethod) iter.next();
			writeMethod(javaMethod, writer);
		}
	}

	
	public void writeStaticBlock(JavaMethod javaMethod, PrintWriter writer) {
		writer.println("static {");
		writeMethodBody(javaMethod.methodBody, writer);
		if (quoteSmalltalk) {
			writeAsQuote(writer, javaMethod.smalltalkSource.context, javaMethod.smalltalkSource.text);
		}
		writer.println("}");
		writer.println();
	}

		public void writeMethod(JavaMethod javaMethod, PrintWriter writer) {
		if (javaMethod.shouldInclude) {
			writeMethodJavaDoc(javaMethod, writer);
		
			writeMethodSignature(javaMethod, writer);
			
			if (INCLUDE_METHOD_BODIES) {
				writeMethodBody(javaMethod.methodBody, writer);
			} else {
				writer.write("throw new UnsupportedOperationException();");
			}
		}
		if (quoteSmalltalk) {
			writeAsQuote(writer, javaMethod.smalltalkSource.context, javaMethod.smalltalkSource.text);
		}
		if (javaMethod.shouldInclude) {
			writer.println("}");
		}
	}

	private void writeMethodSignature(JavaMethod javaMethod, PrintWriter writer) {
		writer.print("public ");
		writer.print(javaMethod.modifiers);
		writer.print(javaMethod.returnType);
		if (javaMethod.modifiers.length() > 0 || javaMethod.returnType.length() > 0) {
			writer.print(" ");
		}
		writer.print(javaMethod.name + "(");
		for (Iterator iter = javaMethod.parameters.iterator(); iter.hasNext();) {
			JavaField element = (JavaField) iter.next();
			if (element.modifiers != null && element.modifiers.length() > 0) {
				writer.print(element.modifiers);
				writer.print(" ");
			}
			writer.print(element.type);
			writer.print(" ");
			writer.print(element.name);
			if (iter.hasNext()) {
				writer.print(", ");
			}
		}
		writer.println(") {");
	}

	private void writeMethodJavaDoc(JavaMethod javaMethod, PrintWriter writer) {
		String comment = "";
		if (javaMethod.comment != null) {
			comment += javaMethod.comment;
		}
		if (javaMethod.isDeprecated) {
			comment += "\n@deprecated";
		}
		if (comment.length() > 0) {
			writeAsJavadocComment(writer, comment);			
		}
	}

	private void writeImports(PrintWriter writer) {
		for (Iterator iterator = javaClass.getImports().iterator(); iterator.hasNext();) {
			String importPackage = (String) iterator.next();
			if (!importPackage.equals(javaClass.getPackage())) {
				writer.println("import " + importPackage + ";");
			}
			if (!iterator.hasNext()) {
				writer.println();
			}
		}
	}

	protected void writeFileComment(PrintWriter writer) {
		if (fileComment != null) {
			writeAsComment(writer, fileComment);
			writer.println();
		}
	}

	public String writeClassDefinition()  {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		try {
			if (javaClass.getComment() != null) {
				writeAsJavadocComment(writer, javaClass.getComment());
			}
			writer.println("public class " + javaClass.className + " extends " + javaClass.superclassName + " {");
			writer.println();
	
			writeVariables(writer);
	
			for (Enumeration e = javaClass.classQuotes.elements(); e.hasMoreElements();) {
				ChunkDetails comment = (ChunkDetails) e.nextElement();
				writeAsQuote(writer, comment.context, comment.contents);
			}
			writeStaticBlocks(writer);
			writeMethods(writer);
			writer.println("}");
		} finally {
			writer.close();
		}
		return stringWriter.toString();
	}

	protected void writeAsQuote(PrintWriter writer, String context, String comment) {
		comment = stringReplaceWith(comment, "/*", "/-");
		comment = stringReplaceWith(comment, "*/", "-/");
	
		writer.println("/*");
		writer.println(context);
		for (StringTokenizer tokenizer = new StringTokenizer(comment, "\n"); tokenizer.hasMoreTokens();) {
			writer.println(tokenizer.nextToken());
		}
		writer.println("*/");
	}

	protected void writeAsJavadocComment(PrintWriter writer, String comment) {
		writer.println("/**");
		for (StringTokenizer tokenizer = new StringTokenizer(comment, "\n"); tokenizer.hasMoreTokens();) {
			String line = tokenizer.nextToken().trim();
			int start = 0;
			while (start < line.length()) {
				int end = line.length() - 1;
				if (start + JAVADOC_MARGIN < line.length()) {
					end = Math.min(start + JAVADOC_MARGIN, end);
					while (end > start && !Character.isWhitespace(line.charAt(end))) {
						end -= 1;
					}
					while (end > start && Character.isWhitespace(line.charAt(end))) {
						end -= 1;
					}
					if (end == start) {
						end = line.length() - 1;
					}
				}
				writer.println(" * " + line.substring(start, end + 1));
				start = end + 1;
				while (start < line.length() - 1 && Character.isWhitespace(line.charAt(start))) {
					start += 1;
				}
			}
		}
		writer.println(" */");
	}

	protected void writeAsComment(PrintWriter writer, String comment) {
		writer.println("/*");
		for (StringTokenizer tokenizer = new StringTokenizer(comment, "\n"); tokenizer.hasMoreTokens();) {
			String line = tokenizer.nextToken().trim();
			writer.println(" * " + line);
		}
		writer.println(" */");
	}

	public void write(String baseDirectory) throws Exception {
	
		File dir = new File(baseDirectory, javaClass.getPackageDirectory());
		dir.mkdirs();
	
		File javaFile = new File(dir, javaClass.className + ".java");
		String generatedContents = generate();
		String existingContents = readExistingFile(javaFile);
		if (!generatedContents.equals(existingContents)) {
			System.out.println("Writing class: " + javaClass.getPackage() + "." + javaClass.className+" ["+summarizeDifference(existingContents, generatedContents)+"]");
//			javaFile.delete();
			writeContents(generatedContents, javaFile);
		}
	}
	
	private String summarizeDifference(String existingContents, String generatedContents) {
		if (existingContents == null) {
			return "New";
		} else {
			for (int i = 0; i < Math.min(existingContents.length(), generatedContents.length()); i++) {
				if (existingContents.charAt(i) != generatedContents.charAt(i)) {
					String shared = extract(existingContents, i-10, i);
					String existingAfter = extract(existingContents, i, i+10);
					String generatedAfter = extract(generatedContents, i, i+10);
					return shared+"<"+existingAfter+"/"+generatedAfter+"> "+i;
				}
			}
			if (existingContents.length() != generatedContents.length()) {
				return "Differente Size";
			}
		}
		return "";
	}

	private String extract(String text, int start, int endExclusive) {
		int starti = Math.min(start, text.length()-1);
		int endi = Math.min(endExclusive, text.length());
		if (starti >= 0 && endi >= 0) {
			String highlight = text.substring(starti, endi);
	
			highlight = highlight.replace('\n', '.');
			highlight = highlight.replace('\r', '.');
			highlight = highlight.replace('\t', '.');
			return highlight;
		} else {
			return "";
		}
	}

	private void writeContents(String contents, File javaFile) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(javaFile, false), "UTF-8");
		try {
			writer.write(contents);
		} finally {
			writer.close();
		}
	}

	private String readExistingFile(File javaFile) throws IOException {
		if (!javaFile.exists()) {
			return null;
		}
		InputStreamReader reader = new InputStreamReader(new FileInputStream(javaFile), "UTF-8");
		try {
			StringBuffer stringBuffer = new StringBuffer();
			char[] buffer = new char[2048];
			int read;
			
			while ((read = reader.read(buffer)) != -1) {
				stringBuffer.append(buffer, 0, read);
			}
			return stringBuffer.toString();
		} finally {
			reader.close();
		}
	}

	private String generate() throws Exception {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		try {
			write(printWriter);
			printWriter.flush();
			return stringWriter.toString();
		} finally {
			printWriter.close();
		}
	}

	public void write(PrintWriter writer) {
		String classDefinition = writeClassDefinition();

		writeFileComment(writer);
		writer.println("package " + javaClass.getPackage() + ";");
		writer.println();
		writeImports(writer);
		writer.print(classDefinition);
	}

	protected String stringReplaceWith(String s, String find, String replaceWith) {
		//FIXME use String.replaceAll() instead
		StringBuffer buffer = new StringBuffer();
		int start = 0;
		int match;
		while ((match = s.indexOf(find, start)) != -1) {
			buffer.append(s.substring(start, match));
			buffer.append(replaceWith);
			start = match + find.length();
		}
		buffer.append(s.substring(start));
		return buffer.toString();
	}

}
