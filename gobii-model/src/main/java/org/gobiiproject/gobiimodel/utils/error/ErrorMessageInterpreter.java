package org.gobiiproject.gobiimodel.utils.error;

import java.io.BufferedReader;
import java.io.FileReader;

public class ErrorMessageInterpreter {

	public static String gerErrorMessage(String processName, int retCode){
		return getErrorMessage(processName,retCode,null);
	}
	public static String getErrorMessage(String processName,int retCode, String errFile){
		String message="";
		if(processName.contains("gobii_ifl.py") && errFile!=null){
			try {
				BufferedReader br = new BufferedReader(new FileReader(errFile));
				while (br.ready()) {
					message += br.readLine() + "\n";
				}
				br.close();
			} catch (Exception e) {
				//Error file was empty - message ends
			}
		}
		else if(processName.contains("loadHDF5")){
			switch(retCode){
				case 0: message="Creating HDF5 file failed for unknown reason";
					break;
				case 1: message="HDF5 Creation failed";
					break;
				case 2: message="HDF5 Creation failed closing dataset";
				break;
				case 3: message="HDF5 Creation failed closing space";
				break;
				case 4: message="HDF5 Creation failed closing memspace";
				break;
				case 5: message="HDF5 Creation failed closing dataspace";
				break;
				case 6: message="HDF5 Creation failed closing file";
				break;
				default: message="Unexpected return code from loadHDF5 - " + retCode;
				break;
			}
		}
		else if(processName.contains("dumpdataset")){
			switch(retCode){
				case 0: message="Extracting dataset failed for unknown reason";
					break;
				case 1: message="Extract dataset failed - could not open file";
					break;
				default: message="Unexpected return code from dumpdataset - " + retCode;
					break;
			}
		}
		else if(processName.contains("fetchmarkerlist")){
			switch(retCode){
				case 0: message="Extracting marker list failed for unknown reason";
					break;
				case 1: message="fetchmarkerlist - Failure with HDF5 file";
					break;
				default: message="Unexpected return code from fetchmarkerlist - " + retCode;
					break;
			}
		}
		else{
			//Remove / from process name, if there are any.
			String readableProcName=processName;
			if(processName.contains("/")) {
				try {
					readableProcName = processName.substring(processName.lastIndexOf('/'));
				}
				catch(Exception e) {
				}
			}
			message= readableProcName + " exited with a return code of " + retCode;
		}
		return message;
	}

}
