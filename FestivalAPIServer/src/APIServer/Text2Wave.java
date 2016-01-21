package APIServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Return wave files generated by calling festival in Terminal.
 * We put all the wave files into folder GeneratedWave
 * the file will be deleted after having been sent to the client
 */

public class Text2Wave extends ServerResource{
	String txt = "";
	String errorWave = Main.errorWave;
	String noAuthorityWave = Main.noAuthorityWave;
	String wavePath = Main.wavePath;
	String festivalHome = Main.festivalHome;
	String voice = Main.defaultVoice;
	String token = "";
	
	@Get
    public FileRepresentation getResource() {
		FileRepresentation result = new FileRepresentation(errorWave,MediaType.AUDIO_WAV);
		Request request = getRequest();
		Form form = request.getResourceRef().getQueryAsForm();
		if(form.getValues("txt") != null && form.getValues("txt") != "")
		{
			txt = form.getValues("txt");
		}
		else
		{
			System.out.println("query string txt is null");
		}		
		if(form.getValues("voice") != null && form.getValues("voice") != "")
		{
			voice = form.getValues("voice");
		}
		if(form.getValues("token") != null && form.getValues("token") != "")
		{
			token = form.getValues("token");
			if (token.equals(Main.token))
			{		
				result = process(txt);		
			}
			else
			{
				result = new FileRepresentation(noAuthorityWave,MediaType.AUDIO_WAV);
			}	
		}
		else
		{
			System.out.println("query string token is null");
			result = new FileRepresentation(noAuthorityWave,MediaType.AUDIO_WAV);
		}		
		return result; 
    }

	private FileRepresentation process(String txt){		
		FileRepresentation result = new FileRepresentation(errorWave,MediaType.AUDIO_WAV);
		String waveFilePath = "";
		waveFilePath = wavePath + generateUid() + ".wav";
		String txtFile = generateTxtFile(txt);	
		String[] Command = new String[3];
		System.out.println("The current voice is: "+voice);
		if (voice.equals(Main.defaultVoice))
		{
	
			String[] CommandOnlyTxt = {"/bin/sh", "-c", "cd " + festivalHome +"; ./" + "text2wave " + txtFile + " -o "  + waveFilePath};
			Command = CommandOnlyTxt;
		}
		else
		{
			String scmFile = generateSCMFile(voice);
			String[] CommandChangeVoice = {"/bin/sh", "-c", "cd " + festivalHome +"; ./" + "text2wave " + "-eval " + scmFile + " " + txtFile + " -o "  + waveFilePath};	
			Command = CommandChangeVoice;
		}

		if(excuteCommand(Command))
		{
			result = new FileRepresentation(waveFilePath,MediaType.AUDIO_WAV);		
		}
		else
		{
			System.out.println("Generate wav file error");	
		}
		return result;
	}	
		
	private String generateUid()
	{
		return UUID.randomUUID().toString();
	}
	
	private String generateTxtFile(String txt)
	{
		String fileName = "";
		String uniqueID = generateUid();
		fileName = wavePath + uniqueID + ".txt";
		try {
			File txtFile = new File(fileName);
			FileWriter fw;

			fw = new FileWriter(txtFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(txt);
			bw.close();
			
			if (!txtFile.exists()){
				System.out.println("Generate txt file error");	
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}  catch (SecurityException e) {
			   e.printStackTrace();
			  }

		return fileName;
	}
	
	private String generateSCMFile(String voice)
	{
		String fileName = "";
		String uniqueID = generateUid();
		fileName = wavePath + uniqueID + ".scm";
		try {
			File scmFile = new File(fileName);
			FileWriter fw;

			fw = new FileWriter(scmFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("(" + voice + ")");
			bw.close();
			
			if (!scmFile.exists()){
				System.out.println("Generate scm file error");	
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}  catch (SecurityException e) {
			   e.printStackTrace();
			  }

		return fileName;
	}
	
	private boolean excuteCommand(String[] command) 
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
