package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.gobii.masticator.reader.TransformReader;
import org.gobii.masticator.reader.transform.compilers.Compilers;

public class InlineTransformReaderPrototype extends TransformReaderPrototype {

	private String lang;
	private String script;

	public InlineTransformReaderPrototype(String lang, String script, List<Object> args, ReaderPrototype<?> readerPrototype) {
		super(args, readerPrototype);
		this.lang = lang;
		this.script = script;
	}

	@Override
	public TransformReader build(File file) throws IOException {
		return new TransformReader(Compilers.resolve(lang).compile(script).build(super.getArgs()), super.getReaderPrototype().build(file));
	}

}
