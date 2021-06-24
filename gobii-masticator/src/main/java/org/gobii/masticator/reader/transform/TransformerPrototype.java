package org.gobii.masticator.reader.transform;

import java.util.List;

public interface TransformerPrototype<T,R> {

	Transformer<T,R> build(List<T> args);

}
