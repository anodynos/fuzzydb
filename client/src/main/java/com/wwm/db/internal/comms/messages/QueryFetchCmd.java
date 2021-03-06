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
package com.wwm.db.internal.comms.messages;


public class QueryFetchCmd extends TransactionCommand {
	private static final long serialVersionUID = 1L;

	private int qid;
	
    /** Default ctor for serialization libraries */
    private QueryFetchCmd() {
        super(-1, -1, -1);
    }

	public QueryFetchCmd(int storeId, int cid, int tid, int qid) {
		super(storeId, cid, tid);
		this.qid = qid;
	}

	public int getQid() {
		return qid;
	}
}
