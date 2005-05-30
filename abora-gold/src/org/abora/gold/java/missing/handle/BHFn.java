/*
 * Abora-White
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003 David G Jones
 * 
 * Based on the Udanax-Gold source code: http://www.udanax.com
 * Copyright 1979-1999 Udanax.com. All rights reserved
 * 
 * $Id$
 */
package org.abora.gold.java.missing.handle;

import org.abora.gold.java.Fn;
import org.abora.gold.xcvr.Rcvr;
import org.abora.gold.xpp.basic.Heaper;

public class BHFn extends Fn {

	public BHFn(Rcvr rcvr) {
		super(rcvr);
	}

	public BHFn() {
		super();
	}

	public boolean invokeFunction(Heaper arg1) {
		throw new UnsupportedOperationException();
	}

}
