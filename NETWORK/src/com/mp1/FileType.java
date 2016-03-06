package com.mp1;

public class FileType {
	
	public static String getExtension(String f) {
    	String ext = null;
        int i = f.lastIndexOf('.');

        if (i > 0 &&  i < f.length() - 1) {
            ext = f.substring(i+1).toLowerCase();
        }        
        return ext;
    }


}
