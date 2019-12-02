package org.faas.utils;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonUtil {

	public static boolean write(Object obj, String fileName) {
		ObjectMapper mapper = new ObjectMapper();

		//Staff staff = createDummyObject();

		try {
			// Convert object to JSON string and save into a file directly
			//mapper.writeValue(new File(fileName), obj);
			
			ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
			writer.writeValue(new File(fileName), obj);
		} catch (JsonGenerationException e) {
			// CWE-209 add code
			Logger.error("JsonUtil:write","Exception: " + e);
			//e.printStackTrace();
			return false;
		} catch (JsonMappingException e) {
			// CWE-209 add code
			Logger.error("JsonUtil:write","Exception: " + e);
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			// CWE-209 add code
			Logger.error("JsonUtil:write","Exception: " + e);
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	public static Object read(Class className, String fileName) {
		return read(className,new File(fileName));
	}
	public static Object read(Class className, File jsonFile) {
		Object object = null;
		ObjectMapper mapper = new ObjectMapper();

		try {

			// Convert JSON string from file to Object
			object = mapper.readValue(jsonFile, className);
			//System.out.println(toPrettyJsonString(object));
			
		} catch (JsonGenerationException e) {
			// CWE-209 add code
			Logger.error("JsonUtil:read","Exception: " + e);
			//e.printStackTrace();
		} catch (JsonMappingException e) {
			// CWE-209 add code
			Logger.error("JsonUtil:read","Exception: " + e);
			//e.printStackTrace();
		} catch (IOException e) {
			// CWE-209 add code
			Logger.error("JsonUtil:read","Exception: " + e);
			//e.printStackTrace();
		}
		return object;
	}
	
	public static String toPrettyJsonString(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		
		String jsonInString = null;
		try {
			// Convert object to JSON string
			//String jsonInString = mapper.writeValueAsString(obj);

			// Convert object to JSON string and pretty print
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

		} catch (JsonGenerationException e) {
			// CWE-209 add code
			Logger.error("JsonUtil:toPrettyJsonString","Exception: " + e);
			//e.printStackTrace();
		} catch (JsonMappingException e) {
			// CWE-209 add code
			Logger.error("JsonUtil:toPrettyJsonString","Exception: " + e);
			//e.printStackTrace();
		} catch (IOException e) {
			// CWE-209 add code
			Logger.error("JsonUtil:toPrettyJsonString","Exception: " + e);
			//e.printStackTrace();
		}

		return jsonInString;
	}
}
