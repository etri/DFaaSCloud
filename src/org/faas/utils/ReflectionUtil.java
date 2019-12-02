package org.faas.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;

public class ReflectionUtil {

	private static Map<Class, Class> builtInMap = new HashMap<Class, Class>();

	static {
		builtInMap.put(Integer.class, Integer.TYPE);
		builtInMap.put(Long.class, Long.TYPE);
		builtInMap.put(Double.class, Double.TYPE);
		builtInMap.put(Float.class, Float.TYPE);
		builtInMap.put(Boolean.class, Boolean.TYPE);
		builtInMap.put(Character.class, Character.TYPE);
		builtInMap.put(Byte.class, Byte.TYPE);
		builtInMap.put(Void.class, Void.TYPE);
		builtInMap.put(Short.class, Short.TYPE);
	}

	private static Class getClass(Class clazz) {
		Class foundClass = builtInMap.get(clazz);
		if (foundClass == null) {
			return clazz;
		}

		return foundClass;
	}

	public static Object createObject(String className, Object[] initArgs) {

		Object object = null;
		Class classDefinition;
		Class[] argsClass = new Class[initArgs.length];
		Object[] args = new Object[initArgs.length];

		for (int i = 0; i < initArgs.length; i++) {
			argsClass[i] = getClass(initArgs[i].getClass());
			args[i] = initArgs[i];
		}

		Constructor constructor;

		try {
			classDefinition = Class.forName(className);
			constructor = classDefinition.getConstructor(argsClass);
			object = createObject(constructor, args);
		} catch (ClassNotFoundException e) {
			System.err.println(className+" args:"+initArgs);
			// CWE-209 add code
			Logger.error("ReflectionUtil:createObject","Exception: " + e);
			//e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.err.println(className+" args:"+initArgs);
			// CWE-209 add code
			Logger.error("ReflectionUtil:createObject","Exception: " + e);
			//e.printStackTrace();
		}
		return object;
	}

	private static Object createObject(Constructor constructor, Object[] arguments) {

		//System.out.println("ReflectionUtil.createObject: Constructor: " + constructor.toString());
		Object object = null;

		try {
			object = constructor.newInstance(arguments);
			//System.out.println("ReflectionUtil.createObject:Object: " + object.toString());
			return object;
		} catch (Exception e) {
			System.err.print(constructor.getName()+" args:");
			for (int i=0;i<arguments.length;i++) {
				System.err.print(arguments[i]+" ");
			}
			System.err.println();
			e.printStackTrace();
		}
		return object;
	}

	public static Object callGetMethod(Object obj,String vname) {
		Object value = null;
		try {
			Class clazz = obj.getClass();
			
			Class[] params = {};
			Method method = clazz.getMethod("get"+StringUtils.capitalise(vname), params);
			value = method.invoke(obj, new Object[]{});
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("ReflectionUtil:callGetMethod","Exception: " + e);
			//e.printStackTrace();
		}
		
		return value;
	}
	
	public static Constructor[] getConstructors(Class aClass) {
		return aClass.getConstructors();
	}
}
