package org.gobiiproject.gobiimodel.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.gobiiproject.gobiimodel.utils.error.Logger;

public class FileSystemInterface {

	private static boolean keepAllFiles=false;
	private static boolean keepInTemp=false;
	/**
	 * As unix RM command, except when flags 'keep all files' (-kaf) or 'keep temporary files' (-ktf) are invoked.
	 * In this case, works as in rmIfExist(described below)
	 * @param file
	 */
	public static void rm(String file){
		if(keepAllFiles||keepInTemp){
			rmIfExist(file);//Deals with 'move to temporary directory' better in the first place
			return;
		}

		HelperFunctions.tryExec("rm "+file);
	}

	/**
	 * Removes a file only if the file already exists. Note: This method has two global flags that affect its behavior.
	 * One prevents the removal, and the other moves the file to a \temp directory under the level the file was at.
	 *
	 * This behavior is described in the -kaf and -ktf flags.
	 * @param file File object to be 'removed' as if the RM command was executed on it, but safely in regard to existance of the file.
	 */
	public static void rmIfExist(File file){
		if(file==null){
			return;
		}

		if(keepAllFiles){
			return;
		}

		if(!file.exists()){
			return;
		}

		if(keepInTemp){
			File tmpDir=new File(file.getParent(),"temp");
			tmpDir.mkdir(); // returns false if directory exists or is file (if it's a file, whoops).
			File tempFile=new File(tmpDir,file.getName());
			try {
				Files.move(file.toPath(),tempFile.toPath(),(CopyOption)StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Logger.logError("FileSystemInterface","Unable to move file to a temporary directory",e);
			}
		}

		file.delete();
	}
	/**
	 * Removes a file only if the file already exists. Note: This method has two global flags that affect its behavior.
	 * One prevents the removal, and the other moves the file to a \temp directory under the level the file was at.
	 *
	 * This behavior is described in the -kaf and -ktf flags.
	 * @param file File object to be 'removed' as if the RM command was executed on it, but safely in regard to existance of the file.
	 */
	public static void rmIfExist(String file){
		if(file==null)return;
		File f=new File(file);
		rmIfExist(f);
	}
	/**
	 * Moves files, like the unix command, (but less powerful, see below).
	 * @param from file to move
	 * @param to file location to move to
	 * @return true if move succeeded
	 */
	public static boolean 	mv(String from, String to) {
			return mv(from,to,false);
	}

	/**
	 * Moves a file to a new file location in the 'to' folder
	 * @param from file to move
	 * @param toFolder folder to move file to
	 * @return true if move succeeded
	 */
	public static boolean mvToFolder(String from, String toFolder, boolean ignore) {
		String toFilePath = String.format("%s/%s", toFolder,new File(from).getName());//Convert the folder path to a file path
		return mv(from, toFilePath, ignore);
	}
	/**
	 * As unix MV command, but only 'warns' on error
	 * @param from Location of file to move
	 * @param to Location of place to move to
	 * @param ignore if true, only 'warn' on exception
	 * @return true if move succeeded
	 */
	public static boolean mv(String from, String to,boolean ignore) {
		try{
			Files.move(new File(from).toPath(),new File(to).toPath(),(CopyOption)StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException e){
			String reason = "Failed move of " + from + " to " + to;

			if(ignore){

				Logger.logWarning("FileSystemInterface",reason,e);
			}
			else {
				Logger.logError("FileSystemInterface", reason, e);
			}
			return false;
		}

		return true;
	}




	public static int lineCount(String file){
		if(!new File(file).exists()){
			Logger.logDebug("FileSystemInterface","File " + file + " being WC'd does not exist");
			return 0;
		}
		String [] exec={"wc","-l",file};
		int retVal=-1;
		try {
			ProcessBuilder builder = new ProcessBuilder(exec);
			Process p = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));//What terrible person makes 'InputStream' the type of the output of a process
			p.waitFor();
			retVal=Integer.parseInt(reader.readLine().split(" ")[0]);

		}
		catch(Exception e){
			Logger.logError("FileSystemInterface","Unable to call wc",e);
		}
		return retVal;
	}

	/**
	 * Determines whether or not to keep temporary files. Useful for debuggins
	 * @param keep
	 */
	public static void keepAllFiles(boolean keep){
		keepAllFiles=keep;
	}
	/**
	 * Determines whether 'deleted' temporary files will instead be stored in *path*\temp\*filename*
	 * Only matters if keepAllFiles = false.
	 * @param keep
	 */
	public static void keepTempFiles(boolean keep){
		keepInTemp=keep;
	}
}
