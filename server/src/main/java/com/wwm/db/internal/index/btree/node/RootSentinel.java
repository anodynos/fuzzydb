/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.db.internal.index.btree.node;

import java.io.Serializable;

import com.wwm.db.Ref;
import com.wwm.db.internal.index.btree.NodeW;

public class RootSentinel extends Node implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	final Ref<NodeW> root;
	
	public RootSentinel(Ref<NodeW> root) {
		this.root = root;
	}

	@Override
	public Node clone() {
		throw new UnsupportedOperationException();
	}

	public Ref<NodeW> getRoot() {
		return root;
	}
}
