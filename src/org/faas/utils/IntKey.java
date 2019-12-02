package org.faas.utils;

public class IntKey {
	int key1;
	int key2;
	
	public IntKey(int key1,int key2) {
		this.key1 = key1;
		this.key2 = key2;
	}
	
	public int getKey1() {
		return key1;
	}
	
	public int getKey2() {
		return key2;
	}
	
	@Override
    public int hashCode() {
        //System.out.println("hashCode");
        final int prime = 31;
        int result = 1;
        result = prime * result + key1;
        result = prime * result + key2;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
	    // CWE 595 == -> hashCode() == hashCode()
        //System.out.println("equals");
        if (this.hashCode() == obj.hashCode())
            return true;
        if (getClass() != obj.getClass())
            return false;
        IntKey other = (IntKey) obj;
        if (key1 != other.key1)
            return false;
        return true;
    }
}