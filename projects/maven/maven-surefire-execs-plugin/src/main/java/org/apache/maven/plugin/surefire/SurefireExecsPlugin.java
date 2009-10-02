package org.apache.maven.plugin.surefire;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


/**
 * Run tests using Surefire.
 * 
 * @author Jason van Zyl
 * @version $Id: SurefirePlugin.java 652773 2008-05-02 05:58:54Z dfabulich $
 * @requiresDependencyResolution test
 * @goal test
 * @phase test
 */
public class SurefireExecsPlugin
    extends AbstractMojo
{

    /**
     * Set this to 'true' to skip running tests, but still compile them. Its use is NOT RECOMMENDED, but quite
     * convenient on occasion.
     * 
     * @parameter expression="${skipTests}"
     * @since 2.4
     */
    private boolean skipTests;
    
    /**
     * DEPRECATED This old parameter is just like skipTests, but bound to the old property maven.test.skip.exec.
     * Use -DskipTests instead; it's shorter.
     * 
     * @deprecated
     * @parameter expression="${maven.test.skip.exec}"
     * @since 2.3
     */
    private boolean skipExec;
    
    /**
     * Set this to 'true' to bypass unit tests entirely. Its use is NOT RECOMMENDED, especially if you
     * enable it using the "maven.test.skip" property, because maven.test.skip disables both running the
     * tests and compiling the tests.  Consider using the skipTests parameter instead.
     * 
     * @parameter expression="${maven.test.skip}"
     */
    private boolean skip;

    /**
     * Set this to true to ignore a failure during testing. Its use is NOT RECOMMENDED, but quite convenient on
     * occasion.
     * 
     * @parameter expression="${maven.test.failure.ignore}"
     */
    private boolean testFailureIgnore;

    /**
     * The base directory of the project being tested. This can be obtained in your unit test by
     * System.getProperty("basedir").
     * 
     * @parameter expression="${basedir}"
     * @required
     */
    private File basedir;

    /**
     * The directory containing generated test classes of the project being tested.
     * 
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    private File testClassesDirectory;

    /**
     * The directory containing generated classes of the project being tested.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;

    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The classpath elements of the project being tested.
     * 
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readonly
     */
    private List classpathElements;

    /**
     * Additional elements to be appended to the classpath.
     * 
     * @parameter
     * @since 2.4
     */
    private List additionalClasspathElements;
    
    /**
     * Base directory where all reports are written to.
     * 
     * @parameter expression="${project.build.directory}/surefire-reports"
     */
    private File reportsDirectory;

    /**
     * The test source directory containing test class sources.
     * 
     * @parameter expression="${project.build.testSourceDirectory}"
     * @required
     * @since 2.2
     */
    private File testSourceDirectory;

    /**
     * Specify this parameter to run individual tests by file name, overriding the <code>includes/excludes</code>
     * parameters.  Each pattern you specify here will be used to create an 
     * include pattern formatted like <code>**&#47;${test}.java</code>, so you can just type "-Dtest=MyTest"
     * to run a single test called "foo/MyTest.java".  This parameter will override the TestNG suiteXmlFiles
     * parameter.
     * 
     * @parameter expression="${test}"
     */
    private String test;

    /**
     * List of patterns (separated by commas) used to specify the tests that should be included in testing. When not
     * specified and when the <code>test</code> parameter is not specified, the default includes will be
     * <code>**&#47;Test*.java   **&#47;*Test.java   **&#47;*TestCase.java</code>.  This parameter is ignored if
     * TestNG suiteXmlFiles are specified.
     * 
     * @parameter
     */
    private List includes;

    /**
     * List of patterns (separated by commas) used to specify the tests that should be excluded in testing. When not
     * specified and when the <code>test</code> parameter is not specified, the default excludes will be
     * <code>**&#47;*$*</code> (which excludes all inner classes).  This parameter is ignored if
     * TestNG suiteXmlFiles are specified.
     * 
     * @parameter
     */
    private List excludes;

    /**
     * ArtifactRepository of the localRepository. To obtain the directory of localRepository in unit tests use
     * System.setProperty( "localRepository").
     * 
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * List of System properties to pass to the JUnit tests.
     * 
     * @parameter
     */
    private Properties systemProperties;

    /**
     * List of properties for configuring all TestNG related configurations. This is the new
     * preferred method of configuring TestNG.
     *
     * @parameter
     * @since 2.4
     */
    private Properties properties;

    /**
     * Map of of plugin artifacts.
     * 
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    private Map pluginArtifactMap;

    /**
     * Map of of project artifacts.
     * 
     * @parameter expression="${project.artifactMap}"
     * @required
     * @readonly
     */
    private Map projectArtifactMap;

    /**
     * Option to print summary of test suites or just print the test cases that has errors.
     * 
     * @parameter expression="${surefire.printSummary}" default-value="true"
     */
    private boolean printSummary;

    /**
     * Selects the formatting for the test report to be generated. Can be set as brief or plain.
     * 
     * @parameter expression="${surefire.reportFormat}" default-value="brief"
     */
    private String reportFormat;

    /**
     * Option to generate a file test report or just output the test report to the console.
     * 
     * @parameter expression="${surefire.useFile}" default-value="true"
     */
    private boolean useFile;

    /**
     * When forking, set this to true to redirect the unit test standard output to a file (found in
     * reportsDirectory/testName-output.txt).
     * 
     * @parameter expression="${maven.test.redirectTestOutputToFile}" default-value="false"
     * @since 2.3
     */
    private boolean redirectTestOutputToFile;

    /**
     * Set this to "true" to cause a failure if there are no tests to run. Defaults to false.
     * 
     * @parameter expression="${failIfNoTests}"
     * @since 2.4
     */
    private Boolean failIfNoTests;
    
    /**
     * Option to specify the forking mode. Can be "never", "once" or "always". "none" and "pertest" are also accepted
     * for backwards compatibility.
     * 
     * @parameter expression="${forkMode}" default-value="once"
     * @since 2.1
     */
    private String forkMode;

    /**
     * Option to specify the jvm (or path to the java executable) to use with the forking options. For the default, the
     * jvm will be the same as the one used to run Maven.
     * 
     * @parameter expression="${jvm}"
     * @since 2.1
     */
    private String jvm;

    /**
     * Arbitrary JVM options to set on the command line.
     * 
     * @parameter expression="${argLine}"
     * @since 2.1
     */
    private String argLine;

    /**
     * Attach a debugger to the forked JVM.  If set to "true", the process will suspend and 
     * wait for a debugger to attach on port 5005.  If set to some other string, that
     * string will be appended to the argLine, allowing you to configure arbitrary
     * debuggability options (without overwriting the other options specified in the argLine).
     * 
     * @parameter expression="${maven.surefire.debug}"
     * @since 2.4
     */
    private String debugForkedProcess;
    
    /**
     * Kill the forked test process after a certain number of seconds.  If set to 0,
     * wait forever for the process, never timing out.
     * 
     * @parameter expression="${surefire.timeout}"
     * @since 2.4
     */
    private int forkedProcessTimeoutInSeconds;
    
    /**
     * Additional environments to set on the command line.
     * 
     * @parameter
     * @since 2.1.3
     */
    private Map environmentVariables = new HashMap();

    /**
     * Command line working directory.
     * 
     * @parameter expression="${basedir}"
     * @since 2.1.3
     */
    private File workingDirectory;

    /**
     * When false it makes tests run using the standard classloader delegation instead of the default Maven isolated
     * classloader. Only used when forking (forkMode is not "none").<br/> Setting it to false helps with some problems
     * caused by conflicts between xml parsers in the classpath and the Java 5 provider parser.
     * 
     * @parameter expression="${childDelegation}" default-value="false"
     * @since 2.1
     */
    private boolean childDelegation;

    /**
     * (TestNG only) Groups for this test. Only classes/methods/etc decorated with one of the groups specified here will be included
     * in test run, if specified.  This parameter is overridden if suiteXmlFiles are specified.
     * 
     * @parameter expression="${groups}"
     * @since 2.2
     */
    private String groups;

    /**
     * (TestNG only) Excluded groups. Any methods/classes/etc with one of the groups specified in this list will specifically not be
     * run.  This parameter is overridden if suiteXmlFiles are specified.
     * 
     * @parameter expression="${excludedGroups}"
     * @since 2.2
     */
    private String excludedGroups;

    /**
     * (TestNG only) List of TestNG suite xml file locations, seperated by commas. Note that suiteXmlFiles is incompatible
     * with several other parameters on this plugin, like includes/excludes.  This parameter is ignored if
     * the "test" parameter is specified (allowing you to run a single test instead of an entire suite).
     * 
     * @parameter
     * @since 2.2
     */
    private File[] suiteXmlFiles;
    
    /**
     * Allows you to specify the name of the JUnit artifact. If not set, <code>junit:junit</code> will be used.
     * 
     * @parameter expression="${junitArtifactName}" default-value="junit:junit"
     * @since 2.3.1
     */
    private String junitArtifactName;
    
    /**
     * Allows you to specify the name of the TestNG artifact. If not set, <code>org.testng:testng</code> will be used.
     * 
     * @parameter expression="${testNGArtifactName}" default-value="org.testng:testng"
     * @since 2.3.1
     */
    private String testNGArtifactName;
    
    /**
     * (TestNG only) The attribute thread-count allows you to specify how many threads should be allocated for this execution. Only
     * makes sense to use in conjunction with parallel.
     * 
     * @parameter expression="${threadCount}"
     * @since 2.2
     */
    private int threadCount;

    /**
     * (TestNG only) When you use the parallel attribute, TestNG will try to run all your test methods in separate threads, except for
     * methods that depend on each other, which will be run in the same thread in order to respect their order of
     * execution.
     * 
     * @parameter expression="${parallel}"
     * @todo test how this works with forking, and console/file output parallelism
     * @since 2.2
     */
    private String parallel;

    /**
     * Whether to trim the stack trace in the reports to just the lines within the test, or show the full trace.
     * 
     * @parameter expression="${trimStackTrace}" default-value="true"
     * @since 2.2
     */
    private boolean trimStackTrace;

    /**
     * Resolves the artifacts needed.
     * 
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * Creates the artifact
     * 
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * The plugin remote repositories declared in the pom.
     * 
     * @parameter expression="${project.pluginArtifactRepositories}"
     * @since 2.2
     */
    private List remoteRepositories;
    
    /**
     * For retrieval of artifact's metadata.
     * 
     * @component
     */
    private ArtifactMetadataSource metadataSource;

    private static final String BRIEF_REPORT_FORMAT = "brief";

    private static final String PLAIN_REPORT_FORMAT = "plain";

    private Properties originalSystemProperties;

    /**
     * Flag to disable the generation of report files in xml format.
     * 
     * @parameter expression="${disableXmlReport}" default-value="false"
     * @since 2.2
     */
    private boolean disableXmlReport;

    /**
     * Option to pass dependencies to the system's classloader instead of using an isolated class loader when forking.
     * Prevents problems with JDKs which implement the service provider lookup mechanism by using the system's
     * classloader.  Default value is "true".
     * 
     * @parameter expression="${surefire.useSystemClassLoader}"
     * @since 2.3
     */
    private Boolean useSystemClassLoader;
    
    /**
     * By default, Surefire forks your tests using a manifest-only jar; set this parameter
     * to "false" to force it to launch your tests with a plain old Java classpath.
     * (See http://maven.apache.org/plugins/maven-surefire-plugin/examples/class-loading.html
     * for a more detailed explanation of manifest-only jars and their benefits.)
     * 
     * Default value is "true".  Beware, setting this to "false" may cause your tests to
     * fail on Windows if your classpath is too long.
     * 
     * @parameter expression="${surefire.useManifestOnlyJar}" default-value="true"
     * @since 2.4.3
     */
    private boolean useManifestOnlyJar;

    /**
     * By default, Surefire enables JVM assertions for the execution of your test cases. To disable the assertions, set
     * this flag to <code>false</code>.
     * 
     * @parameter expression="${enableAssertions}" default-value="true"
     * @since 2.3.1
     */
    private boolean enableAssertions;
    
    /**
     * The current build session instance. 
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;
    
    /**
     * @parameter
     */
    private List<TestExecution> testExecutions;
    
    /**
     * Property values for one single property
     * @parameter
     */
    private TestExecutionsPropertyValues testExecutionsPropertyValues;

    

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip || skipTests || skipExec )
        {
            getLog().info( "Tests are skipped." );
            return ;
        }
    	
    	
    	if(testExecutionsPropertyValues != null && testExecutions != null && !testExecutions.isEmpty()){
    		throw new MojoExecutionException("Cannot set both testExecutionsPropertyValues and testExecutions");
    	}
    	if((testExecutionsPropertyValues == null || testExecutionsPropertyValues.propertyValues == null) && (testExecutions == null || testExecutions.isEmpty())){
    		throw new MojoExecutionException("Both testExecutionsPropertyValues and testExecutions are not set");
    	}
    	
    	if(testExecutionsPropertyValues != null && testExecutionsPropertyValues.propertyValues != null){
    		testExecutions = new ArrayList<TestExecution>();
    		for(String propertyValue : Arrays.asList(testExecutionsPropertyValues.propertyValues.split(","))){
    			TestExecution execution = new TestExecution();
    			execution.id = propertyValue;
    			execution.systemProperties = new Properties();
    			execution.systemProperties.setProperty(testExecutionsPropertyValues.propertyName, propertyValue);
    			testExecutions.add(execution);
    		}
    	}
    	
    	if(testExecutions == null){
    		getLog().warn("No Test executions defined");
    	}
    	
    	for(TestExecution execution : testExecutions){
    		if(execution.skip){
    			getLog().info("TestExecution [" + execution + "] is skipped");
    			continue;
    		}
    		SurefirePlugin plugin = null;
			try {
				plugin = constructSurefirePlugin(execution);
			} catch (Exception e) {
				throw new MojoExecutionException("Cannot configure surefire plugin", e);
			}
    		getLog().info("Executing surefire testExecution : [" + execution.id + "]");
    		plugin.execute();
    	}
    	
    }


	private SurefirePlugin constructSurefirePlugin(TestExecution execution) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		SurefirePlugin surefire = new SurefirePlugin();
		surefire.setLog(getLog());
		surefire.setPluginContext(getPluginContext());
		for(Field field : getClass().getDeclaredFields()){
			if(Modifier.isFinal(field.getModifiers())){
				continue;
			}
			if("testExecutions".equals(field.getName())){
				continue;
			}
			if("testExecutionsPropertyValues".equals(field.getName())){
				continue;
			}
			Field surefireField = surefire.getClass().getDeclaredField(field.getName());
			surefireField.setAccessible(true);
			surefireField.set(surefire, field.get(this));
		}
		Field surefireSystemPropertiesF = surefire.getClass().getDeclaredField("systemProperties");
		surefireSystemPropertiesF.setAccessible(true);
		Properties executionProperties = execution.systemProperties;
		if(executionProperties != null){
			Properties surefireSystemProperties = (Properties) surefireSystemPropertiesF.get(surefire);
			Properties p = new Properties();
			if(surefireSystemProperties != null){
				p.putAll(surefireSystemProperties);
			}
			p.putAll(executionProperties);
			surefireSystemPropertiesF.set(surefire, p);
		}
		return surefire;
	}



}
