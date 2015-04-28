package sugar;


	import java.nio.*;
	import java.io.*;
	import java.nio.file.StandardWatchEventKinds.*;
	import java.nio.file.*;

	public class FileListener
	{
	   public static void main(String args[]) throws IOException, InterruptedException
	   {
	      watchDir("C:\\GlassSUGAR\\glassfish3\\glassfish\\domains\\domain1\\logs");		// Monitor changes to the files in E:\MyFolder
	   }
	   
	   public static String tail2( File file, int lines) {
		    java.io.RandomAccessFile fileHandler = null;
		    try {
		        fileHandler = 
		            new java.io.RandomAccessFile( file, "r" );
		        long fileLength = fileHandler.length() - 1;
		        StringBuilder sb = new StringBuilder();
		        int line = 0;

		        for(long filePointer = fileLength; filePointer != -1; filePointer--){
		            fileHandler.seek( filePointer );
		            int readByte = fileHandler.readByte();

		             if( readByte == 0xA ) {
		                if (filePointer < fileLength) {
		                    line = line + 1;
		                }
		            } else if( readByte == 0xD ) {
		                if (filePointer < fileLength-1) {
		                    line = line + 1;
		                }
		            }
		            if (line >= lines) {
		                break;
		            }
		            sb.append( ( char ) readByte );
		        }

		        String lastLine = sb.reverse().toString();
		        return lastLine;
		    } catch( java.io.FileNotFoundException e ) {
		        e.printStackTrace();
		        return null;
		    } catch( java.io.IOException e ) {
		        e.printStackTrace();
		        return null;
		    }
		    finally {
		        if (fileHandler != null )
		            try {
		                fileHandler.close();
		            } catch (IOException e) {
		            }
		    }
		}
	   
	   public static void watchDir(String dir) throws IOException, InterruptedException
	   {
	       WatchService service = FileSystems.getDefault().newWatchService();	// Create a WatchService
	       Path path = Paths.get(dir);	// Get the directory to be monitored
	       path.register(service , StandardWatchEventKinds.ENTRY_MODIFY);	// Register the directory
	       while(true)
	       {
	          WatchKey key = service.take();	// retrieve the watchkey
	          for (WatchEvent event : key.pollEvents())
	          {
	             System.out.println(event.kind() + ": "+ event.context());// Display event and file name
	             
	             //BufferedReader br = new BufferedReader(new FileReader("C:\\GlassSUGAR\\glassfish3\\glassfish\\domains\\domain1\\logs\\"+(String)event.context()));
	             File file = new File("C:\\GlassSUGAR\\glassfish3\\glassfish\\domains\\domain1\\logs\\server.log");
	             //System.out.println(tail(file));
	             String lastrow= tail2(file,3); 
	             System.out.println(lastrow);
	             int fin = lastrow.lastIndexOf("|");
	             String lastrowcut = lastrow.substring(1, fin); 
	             int start = lastrowcut.lastIndexOf("|");
	             String account = lastrow.substring(start+2, fin);
	             System.out.println("Account saved: " +account);
	          }
	          boolean valid = key.reset();
	          if (!valid)
	          {
	             break;	// Exit if directory is deleted
	          }
	       }
	   }
	}
