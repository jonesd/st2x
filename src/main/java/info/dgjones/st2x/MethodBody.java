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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import info.dgjones.st2x.javatoken.JavaAssignment;
import info.dgjones.st2x.javatoken.JavaBlockEnd;
import info.dgjones.st2x.javatoken.JavaBlockStart;
import info.dgjones.st2x.javatoken.JavaCallArgumentSeparator;
import info.dgjones.st2x.javatoken.JavaCallEnd;
import info.dgjones.st2x.javatoken.JavaCallKeywordStart;
import info.dgjones.st2x.javatoken.JavaCallStart;
import info.dgjones.st2x.javatoken.JavaComment;
import info.dgjones.st2x.javatoken.JavaKeyword;
import info.dgjones.st2x.javatoken.JavaParenthesisEnd;
import info.dgjones.st2x.javatoken.JavaParenthesisStart;
import info.dgjones.st2x.javatoken.JavaStatementTerminator;
import info.dgjones.st2x.javatoken.JavaToken;

public class MethodBody {
	public List<JavaToken> tokens;

	public MethodBody(List<JavaToken> tokens) {
		super();
		this.tokens = tokens;
	}
	
	public JavaToken get(int index) {
		return (JavaToken)tokens.get(index);
	}
	
	public void remove(int index) {
		tokens.remove(index);
	}
	
	public void remove(int startIndex, int endIndexExclusive) {
		for (int i = endIndexExclusive-1; i >= startIndex; --i) {
			remove(i);
		}
	}
	
	
	public void removeShouldMatch(int index, Class aClass) {
		removeShouldMatch(index, aClass, null);
	}
	
	public void removeShouldMatch(int index, String value) {
		removeShouldMatch(index, null, value);
	}

	public void removeShouldMatch(int index, Class aClass, String value) {
		shouldMatch(index, aClass, value);
		remove(index);
	}

	public void shouldMatch(int index, Class aClass) {
		shouldMatch(index, aClass, null);
	}
	
	public void shouldMatch(int index, String value) {
		shouldMatch(index, null, value);
	}
	public void shouldMatch(int index, Class aClass, String value) {
		JavaToken tokenToRemove = (JavaToken)tokens.get(index);
		if (aClass != null && !(aClass.isInstance(tokenToRemove))) {
			throw new IllegalStateException("Body index:"+index+" expected class:"+aClass+" but found:"+tokenToRemove);
		}
		if (value != null && !value.equals(tokenToRemove.value)) {
			throw new IllegalStateException("Body index:"+index+" expected value:"+value+" but found:"+tokenToRemove);
		}
	}

	public int findClosingCallEnd(int callStart) {
		int callEnd = findClosingCallEndQuietFail(callStart);
		if (callEnd == -1) {
			throw new IllegalStateException("Could not find closing callend");
		} else {
			return callEnd;
		}
	}
	
	public int findClosingCallEndQuietFail(int callStart) {
		int earlyCalls = 0;
		for (int i = callStart + 1; i < tokens.size(); i++) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaCallStart) {
				earlyCalls++;
			} else if (token instanceof JavaCallEnd) {
				earlyCalls--;
				if (earlyCalls < 0) {
					return i;
				}
			}
		}
		return -1;
	}

	public JavaToken findExistingJavaCallKeyword(Vector<JavaToken> expression) {
		JavaToken existingKeyword = null;
		for (Enumeration<JavaToken> e = expression.elements(); e.hasMoreElements();) {
			JavaToken token = e.nextElement();
			if (token instanceof JavaCallKeywordStart) {
				existingKeyword = token;
				break;
			}
		}
		return existingKeyword;
	}

	public int findEndOfBlockQuietFail(int blockStart) {
		int earlyParentheses = 0;
		int earlyBlocks = 0;
		for (int i = blockStart + 1; i < tokens.size(); i++) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaParenthesisStart) {
				earlyParentheses++;
			} else if (token instanceof JavaParenthesisEnd) {
				earlyParentheses--;
			} else if (token instanceof JavaBlockStart) {
				earlyBlocks++;
			} else if (token instanceof JavaBlockEnd) {
				if (earlyParentheses > 0 || earlyBlocks > 0) {
					earlyBlocks--;
				} else {
					return i;
				}
			}
		}
		return -1;
	}

	public int findEndOfBlock(int blockStart) {
		int end = findEndOfBlockQuietFail(blockStart);
		if (end == -1) {
			throw new IllegalStateException("Could not find closing block");
		} else {
			return end;
		}
	}

	public int findStartOfBlock(int blockEnd) {
		int earlyBlocks = 0;
		for (int i = blockEnd - 1; i >= 0; i--) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaBlockEnd) {
				earlyBlocks++;
			} else if (token instanceof JavaBlockStart) {
				if (earlyBlocks > 0) {
					earlyBlocks--;
				} else {
					return i;
				}
			}
		}
		throw new IllegalStateException("Could not find starting block");
	}

	public int findStartOfExpression(int endIndex) {
		int laterParentheses = 0;
		int laterBlocks = 0;
		for (int i = endIndex; i >= 0; i--) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaParenthesisStart || token instanceof JavaCallStart) {
				laterParentheses--;
			} else if (token instanceof JavaParenthesisEnd || token instanceof JavaCallEnd) {
				laterParentheses++;
			} else if (token instanceof JavaBlockStart) {
				laterBlocks--;
			} else if (token instanceof JavaBlockEnd) {
				laterBlocks--;
//				laterBlocks++;
			} else if (
				(token instanceof JavaAssignment)
					|| (token instanceof JavaStatementTerminator)
					|| ((token instanceof JavaKeyword) && token.value.equals("return"))) {
				if (laterParentheses == 0 && laterBlocks == 0) {
					return i + 1;
				}
			}
			if ((laterParentheses < 0 && laterBlocks == 0) || (laterParentheses == 0 && laterBlocks < 0)) {
				return i + 1;
			}
			if (laterParentheses == 0 && laterBlocks == 0 && i == 0) {
				return 0;
			}
		}
		throw new IllegalStateException("Could not find start of expression");
	}

	public int findStartOfExpressionMinimal(int endIndex) {
		int laterParentheses = 0;
		int laterBlocks = 0;
		for (int i = endIndex; i >= 0; i--) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaParenthesisStart || token instanceof JavaCallStart) {
				laterParentheses--;
			} else if (token instanceof JavaParenthesisEnd || token instanceof JavaCallEnd) {
				laterParentheses++;
			} else if (token instanceof JavaBlockStart) {
				laterBlocks--;
			} else if (token instanceof JavaBlockEnd) {
				laterBlocks--;
//				laterBlocks++;
			} else if (
				(token instanceof JavaAssignment)
					|| (token instanceof JavaStatementTerminator)
					|| ((token instanceof JavaKeyword) && token.value.equals("return"))
					|| (token instanceof JavaKeyword)
					|| (token instanceof JavaCallArgumentSeparator)) {
				if (laterParentheses == 0 && laterBlocks == 0) {
					return i + 1;
				}
			}
			if ((laterParentheses < 0 && laterBlocks == 0) || (laterParentheses == 0 && laterBlocks < 0)) {
				return i + 1;
			}
			if (laterParentheses == 0 && laterBlocks == 0 && i == 0) {
				return 0;
			}
		}
		throw new IllegalStateException("Could not find start of expression");
	}

	public int findNextTokenOfTypeQuietFail(int startIndex, Class aClass) {
		for (int i = startIndex; i < tokens.size(); i++) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (aClass.isAssignableFrom(token.getClass())) {
				return i;
			}
		}
		return -1;
	}

	public int findNextTokenOfType(int startIndex, Class aClass) {
		int i = findNextTokenOfTypeQuietFail(startIndex, aClass);
		if (i == -1) {
			throw new IllegalStateException("Could not find any more " + aClass);
		} else {
			return i;
		}
	}

	public int findPreviousTokenOfTypeQuietFail(int startIndex, Class aClass) {
		for (int i = startIndex; i >= 0; --i) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (aClass.isAssignableFrom(token.getClass())) {
				return i;
			}
		}
		return -1;
	}

	public int findPreviousTokenOfType(int startIndex, Class aClass) {
		int i = findPreviousTokenOfTypeQuietFail(startIndex, aClass);
		if (i == -1) {
			throw new IllegalStateException("Could not find earlier  " + aClass);
		} else {
			return i;
		}
	}

	public int findEndOfExpression(int startIndex) {
		int laterParentheses = 0;
		int laterBlocks = 0;
		for (int i = startIndex; i < tokens.size(); i++) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaParenthesisStart) {
				laterParentheses++;
			} else if (token instanceof JavaParenthesisEnd) {
				laterParentheses--;
			} else if (token instanceof JavaBlockStart) {
				laterBlocks++;
			} else if (token instanceof JavaBlockEnd) {
				laterBlocks--;
			} else if (token instanceof JavaStatementTerminator) {
				if (laterParentheses == 0 && laterBlocks == 0) {
					return i - 1;
				}
			}
			if ((laterParentheses < 0 && laterBlocks == 0) || (laterParentheses == 0 && laterBlocks < 0)) {
				return i - 1;
			}
			if (laterParentheses == 0 && laterBlocks == 0 && i == tokens.size() - 1) {
				return tokens.size() - 1;
			}
		}
		throw new IllegalStateException("Could not find end of expression");
	}

	public int findEndOfImmediateExpression(int startIndex) {
		int laterParentheses = 0;
		int laterBlocks = 0;
		for (int i = startIndex; i < tokens.size(); i++) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaParenthesisStart) {
				laterParentheses++;
			} else if (token instanceof JavaParenthesisEnd) {
				laterParentheses--;
			} else if (token instanceof JavaBlockStart) {
				laterBlocks++;
			} else if (token instanceof JavaBlockEnd) {
				laterBlocks--;
			} else if (token instanceof JavaStatementTerminator || token instanceof JavaKeyword) {
				if (laterParentheses == 0 && laterBlocks == 0) {
					return i - 1;
				}
			}
			if ((laterParentheses < 0 && laterBlocks == 0) || (laterParentheses == 0 && laterBlocks < 0)) {
				return i - 1;
			}
			if (laterParentheses == 0 && laterBlocks == 0 && i == tokens.size() - 1) {
				return tokens.size() - 1;
			}
		}
		throw new IllegalStateException("Could not find end of expression");
	}

	public int findClosingTokenOfType(int startIndex, Class closingType) {
		Class openingType = tokens.get(startIndex).getClass();
		int depth = 0;
		for (int i = startIndex; i < tokens.size(); i++) {
			JavaToken token = (JavaToken)tokens.get(i);
			if (token.getClass() == openingType) {
				++depth;
			} else if (token.getClass() == closingType) {
				--depth;
				if (depth == 0) {
					return i;
				} else if (depth < 0) {
					throw new IllegalStateException("Close before start: "+closingType);
				}
			}
		}
		throw new IllegalStateException("Could not find matching close: "+closingType);
	}

	public int findOpeningTokenOfType(int endIndex, Class openingType) {
		Class closingType = tokens.get(endIndex).getClass();
		int depth = 0;
		for (int i = endIndex; i >= 0; --i) {
			JavaToken token = (JavaToken)tokens.get(i);
			if (token.getClass() == closingType) {
				++depth;
			} else if (token.getClass() == openingType) {
				--depth;
				if (depth == 0) {
					return i;
				} else if (depth < 0) {
					throw new IllegalStateException("Close before open: "+openingType);
				}
			}
		}
		throw new IllegalStateException("Could not find matching open: "+openingType);
	}
	
	public int findNumberOfCallArgs(int callStart) {
		shouldMatch(callStart, JavaCallStart.class);
		int args = 0;
		
		int earlyCalls = 0;
		for (int i = callStart + 1; i < tokens.size(); i++) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaCallStart) {
				earlyCalls++;
			} else if (token instanceof JavaCallEnd) {
				earlyCalls--;
				if (earlyCalls < 0) {
					return args;
				}
			} else if (token instanceof JavaCallArgumentSeparator && earlyCalls == 0) {
				args += 1;
			} else if (!(token instanceof JavaComment) && args == 0) {
				args = 1;
			}
		}
		throw new IllegalStateException("Could not find closing callend while calculating number of args");
	}

	public List<MethodBody> extractCallArgExpressions(int callStart) {
		shouldMatch(callStart, JavaCallStart.class);
		List<MethodBody> args = new ArrayList<MethodBody>();
		
		List<JavaToken> expression = new ArrayList<JavaToken>();
		int earlyCalls = 0;
		for (int i = callStart + 1; i < tokens.size(); i++) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaCallStart) {
				earlyCalls++;
				expression.add(token);
			} else if (token instanceof JavaCallEnd) {
				earlyCalls--;
				if (earlyCalls < 0) {
					args.add(new MethodBody(expression));
					return args;
				} else {
					expression.add(token);
				}
			} else if (token instanceof JavaCallArgumentSeparator && earlyCalls == 0) {
				args.add(new MethodBody(expression));
				expression = new ArrayList<JavaToken>();
			} else {
				expression.add(token);
			}
			
		}
		throw new IllegalStateException("Could not find closing callend while calculating number of args");
	}

	public List extractCallArgStarts(int callStart) {
		shouldMatch(callStart, JavaCallStart.class);
		List args = new ArrayList();

		int argumentStart = callStart+1;
		int earlyCalls = 0;
		for (int i = callStart + 1; i < tokens.size(); i++) {
			JavaToken token = tokens.get(i);
			if (token instanceof JavaCallStart) {
				earlyCalls++;
			} else if (token instanceof JavaCallEnd) {
				earlyCalls--;
				if (earlyCalls < 0) {
					args.add(new Integer(argumentStart));
					return args;
				}
			} else if (token instanceof JavaCallArgumentSeparator && earlyCalls == 0) {
				args.add(new Integer(argumentStart));
				argumentStart = i+1;
			}
			
		}
		throw new IllegalStateException("Could not find closing callend while calculating number of args");
	}

	public void copy(int sourceStartInclusive, int sourceEndExclusive, int newStart) {
		List<JavaToken> subList = new ArrayList<JavaToken>(tokens.subList(sourceStartInclusive, sourceEndExclusive));
		tokens.addAll(newStart, subList);
	}

	public int findStartOfStatement(int endIndex) {
		int laterParentheses = 0;
		int laterBlocks = 0;
		for (int i = endIndex; i >= 0; i--) {
			JavaToken token = (JavaToken) tokens.get(i);
			if (token instanceof JavaParenthesisStart || token instanceof JavaCallStart) {
				laterParentheses--;
			} else if (token instanceof JavaParenthesisEnd || token instanceof JavaCallEnd) {
				laterParentheses++;
			} else if (token instanceof JavaBlockStart) {
				laterBlocks--;
			} else if (token instanceof JavaBlockEnd) {
				laterBlocks--;
//				laterBlocks++;
			} else if (token instanceof JavaStatementTerminator) {
				if (laterParentheses == 0 && laterBlocks == 0) {
					return i + 1;
				}
			}
			if ((laterParentheses < 0 && laterBlocks == 0) || (laterParentheses == 0 && laterBlocks < 0)) {
				return i + 1;
			}
			if (laterParentheses == 0 && laterBlocks == 0 && i == 0) {
				return 0;
			}
		}
		return 0;
	}

}
