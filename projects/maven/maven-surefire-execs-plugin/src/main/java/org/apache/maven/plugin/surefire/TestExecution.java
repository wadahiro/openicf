package org.apache.maven.plugin.surefire;

import java.util.Properties;

public class TestExecution {
	/**
	 * @parameter 
	 */
	boolean skip;
	/**
	 * @parameter
	 * @required
	 */
	String id;
	/**
	 * @parameter
	 */
	Properties systemProperties;
	
	public String toString(){
		return "systemProperties = " + systemProperties; 
	}
}
