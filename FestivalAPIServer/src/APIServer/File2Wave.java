package APIServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class File2Wave extends ServerResource {
	String errorWave = Main.errorWave;
	String wavePath = Main.wavePath;
	String festivalHome = Main.festivalHome;

	@Get
	//TODO
	//Need to get rid of this later
	//Need to refactor the file2wave to use "Get" directly
	public FileRepresentation getResource() throws IOException {
		FileRepresentation result = new FileRepresentation(getLatestFilefromDir(wavePath),MediaType.AUDIO_WAV);		
		return result; 
    }

	@Post
	public FileRepresentation accept(Representation entity) throws Exception {
		FileRepresentation result = null;
	    if (entity != null) {
	        if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
	            // 1/ Create a factory for disk-based file items
	            DiskFileItemFactory factory = new DiskFileItemFactory();
	            factory.setSizeThreshold(1000240);

	            // 2/ Create a new file upload handler based on the Restlet
	            // FileUpload extension that will parse Restlet requests and
	            // generates FileItems.
	            RestletFileUpload upload = new RestletFileUpload(factory);

	            // 3/ Request is parsed by the handler which generates a
	            // list of FileItems
	            FileItemIterator fileIterator = upload.getItemIterator(entity);

	            // Process only the uploaded item called "fileToUpload"
	            // and return back
	            boolean found = false;
	            while (fileIterator.hasNext() && !found) {
	                FileItemStream fi = fileIterator.next();
	                if (fi.getFieldName().equals("fileToUpload")) {
	                    found = true;
	                    // consume the stream immediately, otherwise the stream
	                    // will be closed.
	                    StringBuilder sb = new StringBuilder();
	                    BufferedReader br = new BufferedReader(
	                            new InputStreamReader(fi.openStream()));
	                    String line = null;
	                    while ((line = br.readLine()) != null) {
	                        sb.append(line);
	                    }
	                    //Get the original file name
	                    String waveFilePath = "";
	                    String[] matches = sb.toString().split(" ");
	                    String match = matches[matches.length-1]; 
	                    waveFilePath = match.split("\"")[1];
	                    
	                    //Replace the original file name with unique ID
	                    String newWaveFinePath = waveFilePath.substring(0, waveFilePath.length()-4) + GenerateUid() + ".wav";	                    
	                    String txt = sb.toString();
	                    txt = txt.replaceAll(waveFilePath, newWaveFinePath);
	                    
	                    File newFile = new File(wavePath + GenerateUid() + ".scm");
	                    BufferedWriter out = new BufferedWriter (new FileWriter(newFile));
	                    out.write(txt);
	                    out.close();
	                    result = Process(newFile.getName(), newWaveFinePath);
	                }
	            }
	        } else {
	            // POST request with no entity.
	            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        }
	    }
	    return result;
	}
	
	private FileRepresentation Process(String fileName, String filePath){		
		FileRepresentation result = new FileRepresentation(errorWave,MediaType.AUDIO_WAV);
		String waveFilePath = "";
		String scmFile = fileName;		
		waveFilePath = filePath;
		//String command = festivalHome + "festival -b " + wavePath + scmFile;
		String[] command = {"/bin/sh", "-c", "cd " + festivalHome +"; ./" + "festival -b " + wavePath + scmFile};
		if(ExcuteCommand(command))
		{
			result = new FileRepresentation(waveFilePath, MediaType.AUDIO_WAV);			
		}
		return result;
	}
	
	private String GenerateUid()
	{
		return UUID.randomUUID().toString();
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
	
	private boolean ExcuteCommand(String command) 
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
	
	private File getLatestFilefromDir(String dirPath) throws IOException{
	    File dir = new File(dirPath);
	    File[] files = dir.listFiles();
	    if (files == null || files.length == 0) {
	        return null;
	    }

	    File lastModifiedFile = files[0];
	    for (int i = 1; i < files.length; i++) {
	       if (lastModifiedFile.lastModified() < files[i].lastModified() && files[i].getName().toLowerCase().endsWith(".wav")) {
	           lastModifiedFile = files[i];
	       }
	    }
	    	return lastModifiedFile;

	}

}
