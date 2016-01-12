package APIServer;

import java.io.File;
import java.util.TimerTask;

public class DeleteFile extends TimerTask  {

	int DaysBack;
	String DirWay;
	public DeleteFile(int daysBack, String dirWay){
		this.DaysBack = daysBack;
		this.DirWay = dirWay;
	}

   public void run() {
    try {
    	int daysBack = this.DaysBack;
    	final File directory = new File(this.DirWay );
	    if(directory.exists()){	        
	        final File[] listFiles = directory.listFiles();          
	        final long purgeTime = 
	            System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000);
	        for(File listFile : listFiles) {
	            if(listFile.lastModified() < purgeTime) {
	            	listFile.delete();               
	            }
	        }
	    } 
        } catch (Exception ex) {

        System.out.println("error running thread " + ex.getMessage());
        }
   }
}
