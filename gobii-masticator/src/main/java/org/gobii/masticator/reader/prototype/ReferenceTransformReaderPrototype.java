package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.Data;
import org.gobii.masticator.reader.TransformReader;
import org.gobii.masticator.reader.transform.Namespace;

@Data
public class ReferenceTransformReaderPrototype extends TransformReaderPrototype{

	private String fname;

	public ReferenceTransformReaderPrototype(String fname, List<Object> args, ReaderPrototype<?> readerPrototype) {
		super(args, readerPrototype);
		this.fname = fname;
	}

	@Override
	public TransformReader build(File file) throws IOException {
		return new TransformReader(Namespace.resolve(fname).build(super.getArgs()), super.getReaderPrototype().build(file));
	}
}
