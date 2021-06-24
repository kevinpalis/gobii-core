package org.gobiiproject.gobiiprocess.digester.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.gobiiproject.gobiimodel.dto.children.PropNameId;
import org.gobiiproject.gobiimodel.utils.error.Logger;

/**
 * Singleton that stores and exports extract summary information to a file.
 */
public class ExtractSummaryWriter {
	private List<Item> valueList = new LinkedList<Item>();

	public void addItem(String key, List<String> values){
		valueList.add(new Item(key,values));
	}
	public void addItem(String key, String value){
		ArrayList<String> singleItemList=new ArrayList<String>();
		if(value!=null && !value.equals("")){
			singleItemList.add(value);
		}
		this.addItem(key,singleItemList);
	}
	//Always choose name
	public void addItem(String key, PropNameId value){
		if(value!=null) {
			this.addItem(key, value.getName());
		}
	}
	public void addPropList(String key, List<PropNameId> values){
		if(values !=null) {
			List<String> svals = values.stream().map(PropNameId::getName).collect(Collectors.toList());
			this.addItem(key, svals);
		}
	}

	public void writeToFile(File outFile){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			for(Item cur:valueList){
				out.write(cur.getTabularLine());
			}
			out.close();
		}
		catch(Exception e){
			Logger.logError("Extract Summary Writer",e);
		}
	}

}

/**
 * Simple immutable 'line of the file' class.
 */
class Item{
	private String key;
	private List<String> values;
	Item(String key, List<String> values){
		this.key=key;
		this.values=values;
	}

	/**
	 * If both key and values are valid, returns a line of a table in the form key: value,value,value (newline)
	 */
	String getTabularLine(){
		if(key==null || values==null || values.size()==0){
			return "";
		}
		return key + "\t" + String.join(",",values)+"\n";
	}
}