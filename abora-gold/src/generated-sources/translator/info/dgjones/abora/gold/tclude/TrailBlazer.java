/*
 * Abora-Gold
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003, 2005 David G Jones
 * 
 * Translated from Udanax-Gold source code: http://www.udanax.com
 * Copyright 1979-1999 Udanax.com. All rights reserved
 */

package info.dgjones.abora.gold.tclude;

import info.dgjones.abora.gold.be.basic.BeEdition;
import info.dgjones.abora.gold.be.basic.BeRangeElement;
import info.dgjones.abora.gold.be.basic.ID;
import info.dgjones.abora.gold.collection.cache.HashSetCache;
import info.dgjones.abora.gold.java.AboraBlockSupport;
import info.dgjones.abora.gold.java.AboraSupport;
import info.dgjones.abora.gold.java.exception.AboraRuntimeException;
import info.dgjones.abora.gold.java.missing.smalltalk.Set;
import info.dgjones.abora.gold.snarf.Abraham;
import info.dgjones.abora.gold.spaces.basic.XnRegion;
import info.dgjones.abora.gold.spaces.unordered.IDSpace;
import info.dgjones.abora.gold.tclude.TrailBlazer;
import info.dgjones.abora.gold.xcvr.Rcvr;
import info.dgjones.abora.gold.xcvr.Xmtr;
import info.dgjones.abora.gold.xpp.basic.Heaper;

/**
 * The object responsible for recording results into a trail.
 */
public class TrailBlazer extends Abraham {

	protected BeEdition myTrail;
	protected HashSetCache myRecorded;
	protected int myRefCount;
/*
udanax-top.st:11170:
Abraham subclass: #TrailBlazer
	instanceVariableNames: '
		myTrail {BeEdition}
		myRecorded {HashSetCache of: BeRangeElement}
		myRefCount {IntegerVar}'
	classVariableNames: ''
	poolDictionaries: ''
	category: 'Xanadu-tclude'!
*/
/*
udanax-top.st:11177:
TrailBlazer comment:
'The object responsible for recording results into a trail. '!
*/
/*
udanax-top.st:11179:
(TrailBlazer getOrMakeCxxClassDescription)
	attributes: ((Set new) add: #SHEPHERD.PATRIARCH; add: #COPY; add: #EQ; add: #LOCKED; add: #CONCRETE; yourself)!
*/
/*
udanax-top.st:11264:
TrailBlazer class
	instanceVariableNames: ''!
*/
/*
udanax-top.st:11267:
(TrailBlazer getOrMakeCxxClassDescription)
	attributes: ((Set new) add: #SHEPHERD.PATRIARCH; add: #COPY; add: #EQ; add: #LOCKED; add: #CONCRETE; yourself)!
*/
public static void initializeClassAttributes() {
	AboraSupport.findAboraClass(TrailBlazer.class).setAttributes( new Set().add("SHEPHERDPATRIARCH").add("COPY").add("EQ").add("LOCKED").add("CONCRETE"));
/*

Generated during transformation: AddMethod
*/
}
public TrailBlazer() {
	super();
	myTrail = null;
	myRecorded = HashSetCache.make();
	myRefCount = 0;
	newShepherd();
/*
udanax-top.st:11184:TrailBlazer methodsFor: 'create'!
create
	super create.
	myTrail := NULL.
	myRecorded := HashSetCache make.
	myRefCount := IntegerVarZero.
	self newShepherd.!
*/
}
public void setEdition(BeEdition trail) {
	myTrail = trail;
	diskUpdate();
/*
udanax-top.st:11194:TrailBlazer methodsFor: 'private:'!
{void} setEdition: trail {BeEdition}
	myTrail := trail.
	self diskUpdate.!
*/
}
/**
 * Whether this TrailBlazer was in fact successfully attached
 */
public boolean isAlive() {
	return myTrail != null;
/*
udanax-top.st:11201:TrailBlazer methodsFor: 'accessing'!
{BooleanVar} isAlive
	"Whether this TrailBlazer was in fact successfully attached"
	
	^myTrail ~~ NULL!
*/
}
/**
 * record the answer into my Edition, and keep only the partial part.
 * Should usually suppress redundant records of the same object.  (These are typically
 * generated by a race between the now and future parts of a backfollow, which are guaranteed
 * to err by overlapping rather than gapping.  They may also be generated by a crash/reboot
 * during AgendaItem processing.)
 */
public void record(BeRangeElement answer) {
	if ( ! (myRecorded.hasMember(answer))) {
		ID iD;
		BeEdition newTrail;
		iD = ((IDSpace) myTrail.coordinateSpace()).newID();
		try {
			(myTrail.get(iD)).makeIdentical((answer.makeFe(null)));
		}
		catch (AboraRuntimeException ex) {
			if (AboraRuntimeException.CANT_MAKE_IDENTICAL.equals(ex.getMessage()) || AboraRuntimeException.MUST_BE_OWNER.equals(ex.getMessage()) || AboraRuntimeException.NOT_IN_TABLE.equals(ex.getMessage())) {
				return ;
			}
			else {
				throw ex;
			}
		}
		myRecorded.store(answer);
		Ravi.thingToDo();
		/* This should not be an edit operation (?) */
		newTrail = myTrail.without(iD);
		Ravi.thingToDo();
		/* decrease refcount on old trail, increase on new one */
		AboraBlockSupport.enterConsistent(1);
		try {
			myTrail = newTrail;
			diskUpdate();
		}
		finally {
			AboraBlockSupport.exitConsistent();
		}
	}
/*
udanax-top.st:11206:TrailBlazer methodsFor: 'accessing'!
{void} record: answer {BeRangeElement}
	"record the answer into my Edition, and keep only the partial part.
	Should usually suppress redundant records of the same object.  (These are typically generated by a race between the now and future parts of a backfollow, which are guaranteed to err by overlapping rather than gapping.  They may also be generated by a crash/reboot during AgendaItem processing.)"
	(myRecorded hasMember: answer) ifFalse:
		[ | iD {ID} newTrail {BeEdition} |
		iD := (myTrail coordinateSpace cast: IDSpace) newID.
		TrailBlazer problems.RecordFailure
			handle: [ :ex | ^VOID]
			do: [(myTrail get: iD) makeIdentical: (answer makeFe: NULL)].
		myRecorded store: answer.
		Ravi thingToDo.  "This should not be an edit operation (?)"
		newTrail := myTrail without: iD.
		Ravi thingToDo. "decrease refcount on old trail, increase on new one"
		DiskManager consistent: 1 with:
			[myTrail := newTrail.
			self diskUpdate]]!
*/
}
/**
 * Increment the reference count
 */
public void addReference(Abraham object) {
	AboraBlockSupport.enterConsistent(1);
	try {
		myRefCount = myRefCount + 1;
		if (myRefCount == 1) {
			remember();
		}
		diskUpdate();
	}
	finally {
		AboraBlockSupport.exitConsistent();
	}
/*
udanax-top.st:11227:TrailBlazer methodsFor: 'storage'!
{void} addReference: object {Abraham unused} 
	"Increment the reference count"
	DiskManager consistent: 1
		with: 
			[myRefCount := myRefCount + 1.
			myRefCount = 1 ifTrue: [self remember].
			self diskUpdate]!
*/
}
/**
 * Decrement the reference count
 */
public void removeReference(Abraham object) {
	AboraBlockSupport.enterConsistent(1);
	try {
		myRefCount = myRefCount - 1;
		if (myRefCount == 0) {
			forget();
		}
		diskUpdate();
	}
	finally {
		AboraBlockSupport.exitConsistent();
	}
/*
udanax-top.st:11236:TrailBlazer methodsFor: 'storage'!
{void} removeReference: object {Abraham unused} 
	"Decrement the reference count"
	DiskManager consistent: 1
		with: 
			[myRefCount := myRefCount - 1.
			myRefCount = IntegerVarZero ifTrue: [self forget].
			self diskUpdate]!
*/
}
public int actualHashForEqual() {
	return asOop();
/*
udanax-top.st:11247:TrailBlazer methodsFor: 'generated:'!
actualHashForEqual ^self asOop!
*/
}
public TrailBlazer(Rcvr receiver) {
	super(receiver);
	myTrail = (BeEdition) receiver.receiveHeaper();
	myRecorded = (HashSetCache) receiver.receiveHeaper();
	myRefCount = receiver.receiveIntegerVar();
/*
udanax-top.st:11249:TrailBlazer methodsFor: 'generated:'!
create.Rcvr: receiver {Rcvr}
	super create.Rcvr: receiver.
	myTrail _ receiver receiveHeaper.
	myRecorded _ receiver receiveHeaper.
	myRefCount _ receiver receiveIntegerVar.!
*/
}
public boolean isEqual(Heaper other) {
	return this == other;
/*
udanax-top.st:11255:TrailBlazer methodsFor: 'generated:'!
isEqual: other ^self == other!
*/
}
public void sendSelfTo(Xmtr xmtr) {
	super.sendSelfTo(xmtr);
	xmtr.sendHeaper(myTrail);
	xmtr.sendHeaper(myRecorded);
	xmtr.sendIntegerVar(myRefCount);
/*
udanax-top.st:11257:TrailBlazer methodsFor: 'generated:'!
{void} sendSelfTo: xmtr {Xmtr}
	super sendSelfTo: xmtr.
	xmtr sendHeaper: myTrail.
	xmtr sendHeaper: myRecorded.
	xmtr sendIntegerVar: myRefCount.!
*/
}
/**
 * should only be called from Edition::getOrMakeTrailBlazer
 */
public static TrailBlazer make(BeEdition trail) {
	TrailBlazer result;
	XnRegion partial;
	BeEdition sub;
	AboraBlockSupport.enterConsistent(1);
	try {
		result = new TrailBlazer();
	}
	finally {
		AboraBlockSupport.exitConsistent();
	}
	partial = trail.attachTrailBlazer(result);
	sub = trail.copy(partial);
	AboraBlockSupport.enterConsistent(1);
	try {
		result.setEdition(sub);
	}
	finally {
		AboraBlockSupport.exitConsistent();
	}
	/* this makes the blazer be alive, once attached */
	return result;
/*
udanax-top.st:11272:TrailBlazer class methodsFor: 'create'!
make: trail {BeEdition} 
	"should only be called from Edition::getOrMakeTrailBlazer"
	| result {TrailBlazer} partial {XnRegion} sub {BeEdition} |
	DiskManager consistent: 1 with:
		[result := self create].
	partial := trail attachTrailBlazer: result.
	sub := trail copy: partial.
	DiskManager consistent: 1 with:
		[result setEdition: sub]. "this makes the blazer be alive, once attached"
	^result!
*/
}
/*
udanax-top.st:11286:TrailBlazer class methodsFor: 'exceptions:'!
problems.RecordFailure
	^Heaper signals: #(MustBeOwner CantMakeIdentical NotInTable)!
*/
}