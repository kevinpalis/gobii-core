package org.gobii.masticator.reader;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gobii.masticator.AspectMapper;
import org.gobii.masticator.reader.result.Break;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.Val;


public class MatrixReader implements Reader {


	private File file;
	private int row;
	private int col;

	private RandomAccessFile raf;

	private boolean lineBreak = false;

	private boolean hitEoF = false;

	public MatrixReader(File file, int row, int col) throws IOException {
		this.file = file;
		this.row = row;
		this.col = col;

		this.raf = new RandomAccessFile(file, "r");

		for (int i = 0 ; i < row ; i++) {
			raf.readLine();
		}

		skipLineBeginning();

	}

	/*Skips ahead one less tab character than the column number, indexing on the correct column
	* (given the relatively safe assumption all tabs are singular and structural)
	*/
	private void skipLineBeginning() throws IOException {
		for (int i = 0 ; i < col ; i++) {
			while (raf.readByte() != AspectMapper.delimitter) ;
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
				skipLineBeginning();
				break;
			} else {
				sb.append(c);
			}
		}

		return Val.of(sb.toString());
	}
}
