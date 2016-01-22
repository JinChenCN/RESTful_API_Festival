package Util;

import java.util.UUID;

public class Util {
	
	public static String generateUid()
	{
		return UUID.randomUUID().toString();
	}
	
	public static boolean excuteCommand(String[] command) 
	{
		boolean result = false;
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return result;
	}

}
