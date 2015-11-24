package com.venkat.restaurant.backend.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class FileUtils 
{

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static Properties loadFileIntoProperties(String file)
	{
		System.out.println("Config file to load :- "+file);
		Properties props = new Properties();
		File fs = new File(file);
		try {
			props.load(new FileReader(fs));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file : " + fs.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not load file : " + fs.getAbsolutePath());
			e.printStackTrace();
		}
		return props;
	}
}
