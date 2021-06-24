package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.Reader;
import org.gobii.masticator.reader.result.Val;

@Data
@AllArgsConstructor
public class RangeReaderPrototype implements ReaderPrototype {

	private int from;

	@Override
	public Reader build(File file) throws IOException {
		AtomicInteger i = new AtomicInteger(from);
		return () -> new Val(i.getAndIncrement() + "");
	}
}
