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

public class EmotionalWave extends ServerResource {
	String txt = "";
	String errorWave = Main.errorWave;
	String noAuthorityWave = Main.noAuthorityWave;
	String wavePath = Main.wavePath;
	String festivalHome = Main.festivalHome;
	String voice = Main.defaultVoice;
	String emotion = Main.defaultEmotion;
	String level = Main.defaultLevel;
	String token = "";
	
	@Get
    public FileRepresentation getResource() {
		FileRepresentation result = new FileRepresentation(errorWave,MediaType.AUDIO_WAV);
		Request request = getRequest();
		Form form = request.getResourceRef().getQueryAsForm();
		if(form.getValues("txt") != null)
		{
			txt = form.getValues("txt");
		}
		else
		{
			System.out.println("query string txt is null");
		}
		if(form.getValues("voice") != null)
		{
			voice = form.getValues("voice");
		}
		if(form.getValues("emotion") != null)
		{
			emotion = form.getValues("emotion");
		}
		if(form.getValues("level") != null)
		{
			level = form.getValues("level");
		}
		if(form.getValues("token") != null)
		{
			token = form.getValues("token");
			if (token.equals(Main.token))
			{		
				result = Process(txt, voice, emotion, level);		
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

	private FileRepresentation Process(String txt, String voice, String emotion, String level){		
		FileRepresentation result = new FileRepresentation(errorWave,MediaType.AUDIO_WAV);
		String waveFilePath = "";
		String scmFile = generateScmFile(txt, voice, emotion, level);		
		waveFilePath = wavePath + GenerateUid() + ".wav";
		String[] Command = {"/bin/sh", "-c", "cd " + festivalHome +"; ./" + "text2wave " + scmFile + " -o "  + waveFilePath};
		if(ExcuteCommand(Command))
		{
			result = new FileRepresentation(waveFilePath,MediaType.AUDIO_WAV);		
		}
		else
		{
			System.out.println("Generate wav file error");	
		}
		return result;
	}
	
	private String GenerateUid()
	{
		return UUID.randomUUID().toString();
	}
	
	private String generateScmFile(String txt, String voice, String emotion, String level)
	{
		String fileName = "";
		String uniqueID = GenerateUid();
		fileName = wavePath + uniqueID + ".scm";
		try {
			File scmFile = new File(fileName);
			FileWriter fw;

			fw = new FileWriter(scmFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String content = "(utt.save.wave (SayEmotional \'" + emotion + "\"" + txt + "\") \"" + level + "abc.wav\")";
			bw.write(content);
			bw.close();
			
			if (!scmFile.exists()){
				System.out.println("Generate txt file error");	
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (SecurityException e) {
			   e.printStackTrace();
			  }

		return fileName;
	}
	
	private boolean ExcuteCommand(String[] command) 
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
