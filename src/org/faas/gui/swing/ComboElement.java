package org.faas.gui.swing;

import java.util.List;

import org.faas.utils.ReflectionUtil;

public class ComboElement {

	private Object value;
	private Object name;
	
	public ComboElement(Object value) {
		this(value, "");
	}
	public ComboElement(Object value,Object name) {
		this.value = value;
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public Object getName() {
		return name;
	}
	
	public String toString() {
		return name.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ComboElement) {
			return this.value.equals(((ComboElement)obj).value);
		}
		return super.equals(obj);
	}

	// CWE-581 add hashCode()
	@Override
	public int hashCode() {
		return this.value.hashCode();
	}

	public static ComboElement[] toArray(List list,String keyName) {
		return toArray(list, keyName, false);
	}
	
	public static ComboElement[] toArray(List list,String keyName, boolean addInitElement) {
		ComboElement[] array = new ComboElement[list.size() + (addInitElement?1:0)];
		
		int ii=0;
		if (addInitElement) {
			array[ii] = new ComboElement("","N/A");
			ii++;
		}
		
		for (int i=0;i<list.size();i++,ii++) {
			Object obj = list.get(i);
			array[ii] = new ComboElement(ReflectionUtil.callGetMethod(obj, keyName),obj);
		}
		
		return array;
	}

//	public static ComboElement[] toArray(List list,String keyName, boolean addInitElement) {
//		ComboElement[] array = new ComboElement[list.size() + (addInitElement?1:0)];
//		
//		int i;
//		for (i=0;i<list.size();i++) {
//			Object obj = list.get(i);
//			array[i] = new ComboElement(ReflectionUtil.callGetMethod(obj, keyName),obj);
//		}
//		if (addInitElement) {
//			array[i] = new ComboElement("","N/A");
//		}
//		
//		
//		return array;
//	}

}
