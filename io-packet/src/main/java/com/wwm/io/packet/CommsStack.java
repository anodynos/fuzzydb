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
package com.wwm.io.packet;

import com.wwm.io.core.MessageInterface;
import com.wwm.io.packet.layer1.SocketDriver;

public interface CommsStack {
	public SocketDriver getDriver();
	public MessageInterface getMessageInterface();
}