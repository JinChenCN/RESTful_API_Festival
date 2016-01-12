package APIServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class Main {
	static Integer port = 8183;
	static String wavePath = "";
	static String festivalHome = "";
	static String token = "";
	static String defaultVoice = "";
	static String defaultEmotion = "";
	static String defaultLevel = "";
	static String errorWave = "Error.wav";
	static String noAuthorityWave = "noAuthority.wav";
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0)
		{
			System.out.println("Please add parameter to indicate the path of the config file.");
			return;
		}
		getProperties(args[0]);
		
		// Initiate temporary folder for keeping auto generated files
		File folder = new File(wavePath);
		try {
			   if (!(folder.isDirectory())) {
			    new File(wavePath).mkdir();
			   }
			  } catch (SecurityException e) {
			   e.printStackTrace();
			  }
		
	    // Create a new Component.  
	    Component component = new Component(); 

	    // Add a new HTTP server listening on port configured, the default port is 8183.  
	    component.getServers().add(Protocol.HTTP, "0.0.0.0", port);  

	    // Attach the application.  
	    component.getDefaultHost().attach("/api",  
	            new APIServer());  

	    // Start the component.  
	    component.start();	
	    
	    //Delete auto generated .txt and .wav files every 1 hour
	    Timer time = new Timer(); 
	    time.schedule(new DeleteFile(1, wavePath), 0, 1000 * 60 * 60 * 1);	   
	   
	} 
	
	private static void getProperties(String configFilePath){
		Properties configFile = new Properties();
		FileInputStream file;
		try {
			file = new FileInputStream(configFilePath);
			configFile.load(file);
			file.close();
			port = Integer.parseInt(configFile.getProperty("port"));
			wavePath = configFile.getProperty("waveFilePath");
			festivalHome = configFile.getProperty("festivalHome");
			token = configFile.getProperty("APIToken");
			defaultVoice = configFile.getProperty("defaultVoice");
			defaultEmotion = configFile.getProperty("defaultEmotion");
			defaultLevel = configFile.getProperty("defaultLevel");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
