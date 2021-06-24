package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import lombok.Getter;
import org.gobii.masticator.reader.Reader;
import org.gobii.masticator.reader.ReaderResult;
import org.gobii.masticator.reader.result.Val;

public class ConstantReaderPrototype implements ReaderPrototype {

	@Getter
	private final String constantValue;

	private final ReaderResult result;

	public ConstantReaderPrototype(String constantValue) {
		this.constantValue = constantValue;
		this.result = new Val(constantValue);
	}

	@Override
	public Reader build(File file) throws IOException {
		return () -> result;
	}
}
