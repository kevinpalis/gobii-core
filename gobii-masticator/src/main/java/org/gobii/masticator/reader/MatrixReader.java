package org.gobii.masticator.reader;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.gobii.masticator.AspectMapper;
import org.gobii.masticator.reader.result.Break;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.Val;
import org.gobii.masticator.reader.transform.types.NucleotideSeparatorSplitter;


public class MatrixReader implements Reader {


	private File file;
	private int row;
	private int col;
	private String datasetType;

	private RandomAccessFile raf;

	private boolean lineBreak = false;

	private boolean hitEoF = false;

	private NucleotideSeparatorSplitter splitter;

	public MatrixReader(File file, int row, int col, String datasetType) throws IOException {
		this.file = file;
		this.row = row;
		this.col = col;

		this.raf = new RandomAccessFile(file, "r");

		for (int i = 0 ; i < row ; i++) {
			raf.readLine();
		}

		skipLineBeginning();

		if(datasetType.equals("NUCLEOTIDE_2_LETTER")){
			splitter = new NucleotideSeparatorSplitter(2);
		}
		else{
			splitter=null;
		}

	}

	/*Skips ahead one less tab character than the column number, indexing on the correct column
	* (given the relatively safe assumption all tabs are singular and structural)
	*/
	private void skipLineBeginning() throws IOException {
		for (int i = 0 ; i < col ; i++) {
			while (raf.readByte() != '\t') ;
		}
	}

	@Override
	public int dimension() {
		return 2;
	}

	@Override
	public ReaderResult read() throws IOException {

		if(hitEoF){
			return End.inst;
		} else if (lineBreak) {
			lineBreak = false;
			return Break.inst;
		} else if (raf.getFilePointer() == raf.length() - 1) {
			return End.inst;
		}

		StringBuilder sb = new StringBuilder();

		for (;;) {
			char c;
			try {
				c = (char) raf.readByte();
			} catch (EOFException eof) {
				hitEoF=true;
				break;
			}
			//Note, removed special handling of tab characters, as internal tabs should be preserved on 'matrix' calls
			if (c == '\n' || c == '\r') {
				try {
					skipLineBeginning();
				}
				catch(EOFException e){
					hitEoF=true;
				}
				break;
			} else {
				sb.append(c);
			}
		}


		String ret = sb.toString();
		if(splitter != null){
			ret = Arrays.stream(ret.split("\t")).map(s->splitter.process(s)).collect(Collectors.joining("\t"));
		}

		return Val.of(ret);
	}
}
