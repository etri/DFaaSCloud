package org.faas.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JVMUtil {

	public static int exec(Class klass,String args) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					String javaHome = System.getProperty("java.home");
					String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
					String classpath = System.getProperty("java.class.path");
					String className = klass.getCanonicalName();

					ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className, args, "ATTACH_GUI");

					// Merge System.err and System.out
					//builder.redirectErrorStream(true);
			        // Inherit System.out as redirect output stream
					//builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			        
					Process process = builder.start();

				    StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
				    StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), System.err::println);

				    new Thread(outputGobbler).start();
				    new Thread(errorGobbler).start();

					System.out.println(className+" successfully executed!");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		
		(new Thread(r)).start();
		

		
		return 0;
	}
	
	static class StreamGobbler implements Runnable {
	    private InputStream inputStream;
	    private Consumer<String> consumeInputLine;

	    public StreamGobbler(InputStream inputStream, Consumer<String> consumeInputLine) {
	        this.inputStream = inputStream;
	        this.consumeInputLine = consumeInputLine;
	    }

	    public void run() {
	        new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumeInputLine);
	    }
	}
}