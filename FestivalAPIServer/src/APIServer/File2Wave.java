package APIServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import Util.Util;

public class File2Wave extends ServerResource {
	String errorWave = FestivalAPIServer.errorWave;
	String wavePath = FestivalAPIServer.wavePath;
	String festivalHome = FestivalAPIServer.festivalHome;

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
	                    String newWaveFinePath = waveFilePath.substring(0, waveFilePath.length()-4) + Util.generateUid() + ".wav";	                    
	                    String txt = sb.toString();
	                    txt = txt.replaceAll(waveFilePath, newWaveFinePath);
	                    
	                    File newFile = new File(wavePath + Util.generateUid() + ".scm");
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
		if(Util.excuteCommand(command))
		{
			result = new FileRepresentation(waveFilePath, MediaType.AUDIO_WAV);			
		}
		return result;
	}	
	
}
