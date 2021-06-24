package org.gobii.masticator.reader.transform;

import java.util.List;
import java.util.function.Function;

public interface Transformer<T, R> extends Function<List<T>,R> {

	R apply(List<T> t) throws TransformException;

}
