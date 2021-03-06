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
package com.wwm.attrs.byteencoding;


import com.wwm.attrs.Score;
import com.wwm.attrs.Score.Direction;
import com.wwm.attrs.bool.BooleanConstraint;
import com.wwm.attrs.bool.IBooleanValue;
import com.wwm.attrs.internal.Attribute;
import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.attrs.internal.TwoAttrScorer;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.ByteArray;



public class CompactBooleanScorer extends TwoAttrScorer {

	private static final long serialVersionUID = 1L;
	
	
	public CompactBooleanScorer(int scoreAttrId, int otherAttrId) {
		super(scoreAttrId, otherAttrId);
	}

	
	/**
	 * Implementation that avoids creating any objects.  Directly inspects the byte array
	 */
    @Override
    public void scoreItemToItem(Score score, Direction d, IAttributeMap<IAttribute> otherAttrs, IAttributeMap<IAttribute> scoreAttrs) {
    	// We assume that both are CompactAttrMap
    	ByteArray scoreBytes = ((CompactAttrMap<?>)scoreAttrs).getByteArray();
    	ByteArray otherBytes = ((CompactAttrMap<?>)otherAttrs).getByteArray();
    	
    	int scoreIndex = CompactAttrCodec.findAttrInBuf(scoreBytes, scorerAttrId);
		if (scoreIndex == CompactAttrCodec.NOT_FOUND) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}

    	int otherIndex = CompactAttrCodec.findAttrInBuf(otherBytes, otherAttrId);

        if (otherIndex == CompactAttrCodec.NOT_FOUND) {
            score.addNull(this, d);
            return;
        }

        score.add(this, calcScore(scoreBytes, scoreIndex, otherBytes, otherIndex), d);
    }
    

	@Override
    public void scoreSearchToNode(Score score, Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> scoreAttrs) {
        Attribute attr = (Attribute)scoreAttrs.findAttr(scorerAttrId);
		if (attr == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}
        IBooleanValue bAttr = (IBooleanValue) attr;
        IAttributeConstraint na = c.findAttr(otherAttrId);
        
        // If there is no Node Data then we only score null 
        if (na == null ) { // || !na.hasValue()) {
            score.addNull(this, d);
            return;
        }
        
        float result = 0.0f;

        if (na.isIncludesNotSpecified() ) {
            if (isScoreNull()) {
                result = getScoreOnNull();
            }
        }
        assert(na instanceof BooleanConstraint);
        result = Math.max(result, calcScore((BooleanConstraint)na, bAttr));
        score.add(this, result, d);
    }
    
    @Override
    public void scoreNodeToSearch(Score score, Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
    	IAttributeConstraint na = c.findAttr(scorerAttrId);
		if (na == null) {
			return; // If we do not have the scorer attr present in the search direction, we do not score - it wasn't 'wanted'
		}
		IAttributeConstraint bNa = na;
        IBooleanValue otherAttr = (IBooleanValue) searchAttrs.findAttr(otherAttrId);

        // If some nulls under this node then Score 1 so 
        // as not to push this node down in score 
        if (bNa.isIncludesNotSpecified() ) {
            score.add(this, maxScore, d);
            return;
        }

//        // This should never happen
//        if (!bNa.hasValue()) {
//            assert(false);
//            return;
//        }

        // If there is no Attr Data then we only score null 
        if (otherAttr == null) {
            score.addNull(this, d);
            return;
        }
        
        float result = calcScore((BooleanConstraint)bNa, otherAttr);
        score.add(this, result, d);
    }
    
    private float calcScore(ByteArray scoreBytes, int scoreIndex, ByteArray otherBytes, int otherIndex) {
		boolean isMatch = BooleanCodec.getValue(scoreBytes, scoreIndex) == BooleanCodec.getValue(otherBytes, otherIndex);
		return isMatch ? maxScore : minScore;
	}

    
    private float calcScore(BooleanConstraint bc, IBooleanValue attr) {
        switch (bc.getState()) {
	        case hasTrue:
	            return attr.isTrue() ? maxScore : minScore;
	        case hasFalse:
	            return attr.isTrue() ? minScore : maxScore;
	        case hasBoth:
	            return maxScore;
	        default: 
	            assert false; // Should handle all cases
	        	return minScore;
        }
    }

}
