package org.gobii.masticator.reader.prototype;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.TransformReader;
import org.gobii.masticator.reader.transform.Namespace;

@Data
@AllArgsConstructor
public abstract class TransformReaderPrototype implements ReaderPrototype<TransformReader> {

	private static Namespace namespace = new Namespace();

	private List<Object> args;
	private ReaderPrototype<?> readerPrototype;


}
