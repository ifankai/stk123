package com.stk123.common.ik;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class MySimilarity extends SimilarityBase {


	@Override
	public SimScorer simScorer(SimWeight arg0, AtomicReaderContext arg1)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected float score(BasicStats arg0, float arg1, float arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
