package com.web.util;

import java.io.File;
import java.io.FileFilter;

public class UtilFunction {
	public static long sleepRandom() {
		return (long)(Math.random() * 10000);
	}
	public static long sleepRandom1Sec() {
		return (long)(Math.random() * 1000);
	}
	
	public static File lastFileModified(String dir) {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {          
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }
}
