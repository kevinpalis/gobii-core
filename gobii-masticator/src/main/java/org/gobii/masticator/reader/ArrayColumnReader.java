package org.gobii.masticator.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.StringJoiner;

import lombok.Data;

import org.gobii.masticator.AspectMapper;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.Val;

@Data
public class ArrayColumnReader implements Reader {

	private String delimiter = "\t";

	private File file;
	private int row;
	private int col;
	private String sep;

	private Iterator<String> lines;

	public ArrayColumnReader(File file, int row, int col, String sep) throws IOException {
		this.delimiter = String.valueOf(AspectMapper.delimitter);
		this.file = file;
		this.row = row;
		this.col = col;
		this.sep = sep;
		this.lines = Files.lines(file.toPath()).iterator();
		for(int i = 0 ; i < row ; i++) {
			lines.next();
		}
	}

	@Override
	public ReaderResult read() throws IOException {

		if (! lines.hasNext()) {
			return End.inst;
		}			

		String columnValue = lines.next().split(delimiter)[col];
		String[] arrayValues = columnValue.split(this.sep);
		
		StringJoiner joiner = new StringJoiner(",");
		for (String arrayValue : arrayValues) {
			joiner.add(String.format("\"\"%s\"\"", arrayValue));
		}

		return new Val(String.format("{%s}", joiner.toString()));
	}
}
