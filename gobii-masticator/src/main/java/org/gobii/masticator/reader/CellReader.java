package org.gobii.masticator.reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import lombok.Data;

import org.gobii.masticator.AspectMapper;
import org.gobii.masticator.reader.result.Val;

@Data
public class CellReader implements Reader {

	private File file;
	private RandomAccessFile raf;
	private int row;
	private int col;

	private boolean closed = false;

	private String val;

	public CellReader(File file, int row, int col) throws IOException {

		char delimitter = AspectMapper.delimitter;
		raf = new RandomAccessFile(file, "r");

		for (int i = 0 ; i < row ; i++) {
			raf.readLine();
		}

		for (int i = 0 ; i < col ; i++) {
			while (raf.readByte() != '\t') ;
		}

		StringBuilder s = new StringBuilder();
		for (char c = (char) raf.readByte() ; c != delimitter ; c = (char) raf.readByte()) {
			if (c == '\n') {
				this.closed = true;
				break;
			}
			s.append(c);
		}

		this.val = s.toString();
		raf.close();
	}

	@Override
	public ReaderResult read() throws IOException {
		return Val.of(val);
	}
}
