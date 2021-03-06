package com.wwm.postcode;
/******************************************************************************
 * Copyright (c) 2005-2009 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/


import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import com.wwm.db.core.Settings;
import com.wwm.geo.GeoInformation;
import com.wwm.postcode.PostcodeConvertor;

/**
 * These tests assume the postcode data has been built and installed into the correct location.
 * 
 * They test functionality where only the basic "outward part" postcode database is available.
 */
public class PostcodeConverterTest {
    private PostcodeConvertor convertor;

    @Before
    public void setUp() throws Exception {
        Settings.getInstance().setPostcodeRoot("data");
        convertor = new PostcodeConvertor();
    }


    @Test
    public void testJibbleSimple() {
        GeoInformation r = convertor.lookupShort("CB4");
        assertCB4(r);
    }

    @Test
    public void testJibbleSimpleSpaced() {
        GeoInformation r = convertor.lookupShort("CB 4");
        assertCB4(r);
    }

    private void assertCB4(GeoInformation r) {
        Assert.assertNotNull(r);
        Assert.assertTrue (r.getLatitude() < 52.35);
        Assert.assertTrue (r.getLatitude() > 52.2);
        Assert.assertTrue (r.getLongitude() < 0.2);
        Assert.assertTrue (r.getLatitude() > -0.2);
    }

    @Test
    public void testJibbleSimpleCased() {
        GeoInformation r = convertor.lookupShort("cb4");
        assertCB4(r);
    }

    @Test
    public void testJibbleSimpleCasedSpaced() {
        GeoInformation r = convertor.lookupShort(" c B 4 ");
        assertCB4(r);
    }

    @Test
    public void testJibbleInvalid() {
        GeoInformation r = convertor.lookupShort("FOOBAR");
        Assert.assertNull(r);
    }

    @Test
    public void testJibbleEmpty() {
        GeoInformation r = convertor.lookupShort("");
        Assert.assertNull(r);
    }
}
