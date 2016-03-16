package com.ensoftcorp.open.cg.analysis;

import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;

public abstract class CGAnalysis {

	private boolean hasRun = false;
	
	protected boolean libraryCallGraphConstructionEnabled;
	
	protected CGAnalysis(boolean libraryCallGraphConstructionEnabled){
		this.libraryCallGraphConstructionEnabled = libraryCallGraphConstructionEnabled;
	}
	
	/**
	 * Returns true if library call graph construction is enabled
	 * @return
	 */
	public boolean isLibraryCallGraphConstructionEnabled(){
		return libraryCallGraphConstructionEnabled;
	}
	
	/**
	 * Returns the call graph produced by the algorithm
	 * @return
	 */
	public Q getCallGraph(){
		if(!hasRun()){
			run();
		}
		return Common.universe().edgesTaggedWithAny(getCallEdgeTags());
	}
	
	/**
	 * Returns the set of tags applied to edges during call graph construction
	 * @return
	 */
	public abstract String[] getCallEdgeTags();
	
	/**
	 * Returns true if the call graph construction has completed
	 * @return
	 */
	public boolean hasRun(){
		return hasRun || graphHasEvidenceOfPreviousRun();
	}
	
	/**
	 * Not ideal, but implementing this method guards against multiple instances
	 * floating around due to class loader issues.  Ideally we could just use a
	 * singleton and everything would be hunky-dory.
	 * 
	 * @return
	 */
	public boolean graphHasEvidenceOfPreviousRun(){
		return Common.universe().edgesTaggedWithAny(getCallEdgeTags()).eval().edges().size() > 0;
	}
	
	/**
	 * Runs the call graph construction (if it hasn't been run already)
	 * and returns the time in milliseconds to complete the analysis
	 * @return
	 */
	public double run(){
		if(hasRun()){
			Log.info(getClass().getSimpleName() + " Call Graph construction has already completed.");
			return 0;
		} else {
			try {
				long start = System.nanoTime();
				Log.info("Starting " + getClass().getSimpleName() + " Call Graph Construction");
				runAnalysis();
				Log.info("Finished " + getClass().getSimpleName() + " Call Graph Construction");
				hasRun = true;
				long stop = System.nanoTime();
				return (stop - start)/1000.0/1000.0;
			} catch (Exception e){
				Log.error("Error constructing call graph.", e);
				hasRun = false;
				return -1;
			}
		}
	}
	
	/**
	 * Runs the call graph construction algorithm
	 */
	protected abstract void runAnalysis();
	
}
