/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.attrs.userobjects;

import java.io.Serializable;


import com.wwm.attrs.AttrsFactory;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.internal.AttributeCache;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;


public class TestWhirlwindClass extends AugmentedAttributeMap implements Serializable {


	private static final long serialVersionUID = 1L;

	CardinalAttributeMap<IAttribute> attrs = AttrsFactory.getCardinalAttributeMap();
	
	/**
	 * Create instance with single attribute with integer value of i
	 * @param f
	 */
	public TestWhirlwindClass( int attrId, float f ) {
		setFloat(attrId, f);
	}



	
	public IAttributeMap<IAttribute> getAttributeMap() {
		return attrs.getAttributeMap();  // for Server side. 
	}
	
	public void setAttributeMap(IAttributeContainer attrs) {
		throw new UnsupportedOperationException();
	}
	
	
	public Object getNominee() {
		throw new UnsupportedOperationException();
	}


	public void setNominee(Object o) {
		throw new UnsupportedOperationException();
	}

	public void mergeDuplicates(AttributeCache cache) {
		// FIXME: We should merge, as it is part of testing.
	}
}
