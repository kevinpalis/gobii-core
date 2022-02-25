package org.gobii.masticator.reader;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.gobii.masticator.AspectMapper;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.Val;

@Data
@AllArgsConstructor
public class RowReader implements Reader {


	private File file;
	private RandomAccessFile raf;
	private int row;
	private int col;

	private boolean closed = false;

	public RowReader(File file, int row, int col) throws IOException {
		raf = new RandomAccessFile(file, "r");

		for (int i = 0 ; i < row ; i++) {
			raf.readLine();
		}

		for (int i = 0 ; i < col ; i++) {
			while (raf.readByte() != '\t') ;
		}
	}

	@Override
	public ReaderResult read() throws IOException {

		if (closed) {
			return End.inst;
		}

		StringBuilder s = new StringBuilder();
		try {
			for (char c = (char) raf.readByte(); c != '\t'; c = (char) raf.readByte()) {
				if (c == '\n' || c == '\r') {
					this.closed = true;
					break;
				}
				s.append(c);
			}
		} catch (EOFException eof) {
			this.closed = true;
		}

		return new Val(s.toString());
	}
}
