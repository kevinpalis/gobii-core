package org.gobii;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Util {

	public static <K, V, W> Map<K, W> mapVals(Map<K, V> m, Function<V, W> f) {
		Map<K, W> n = new HashMap<>();
		for (Map.Entry<K, V> kv : m.entrySet()) {
			n.put(kv.getKey(), f.apply(kv.getValue()));
		}

		return n;
	}

	public static <S, T> List<T> map(Function<S, T> f, List<S> xs) {
		return xs.stream().map(f).collect(Collectors.toList());
	}

	public static <S, T> List<T> map(Function<S, T> f, S[] xs) {
		return map(f, Arrays.asList(xs));
	}

	public static String slurp(InputStream in) throws IOException {

		StringBuilder sb = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader
				(in))) {
			String line = reader.readLine();
			while (line !=null) {
				sb.append(line);
				line=reader.readLine();
			}
		}
		return sb.toString();
	}

	public static String slurp(File file) throws IOException {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines( Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8))  {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		}
		catch (IOException e) {
			throw e;
		}
		return contentBuilder.toString();
	}

	public static String slurp(String path) throws IOException {

		File file = new File(path);

		return slurp(file);
	}

	public static String slurpResource(String path) throws IOException {

		ClassLoader classLoader = Util.class.getClassLoader();
		File file = new File(classLoader.getResource(path).getFile());

		return slurp(file);
	}


	public static <S, T> Map<S, T> zipmap(Map<S, T> map, List<S> ks, List<T> vs) {
		for (int i = 0 ; i < ks.size() ; i++) {
			map.put(ks.get(i), vs.get(i));
		}
		return map;
	}

	public static <S, T> Map<S, T> zipmap(List<S> ks, List<T> vs) {
		return zipmap(new HashMap<S, T>(), ks, vs);
	}

	public static <S, T> Map<S, T> zipmap(S[] ks, T[] vs) {
		return zipmap(new HashMap<S, T>(), Arrays.asList(ks), Arrays.asList(vs));
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> takeNth(int skip, int n, List<T> list) {
		return IntStream.range(0, list.size())
				.filter(i -> i % n == skip)
				.mapToObj(i -> list.get(i))
				.collect(Collectors.toList());
	}

	public static <T> List<T> takeNth(int n, List<T> list) {
		return takeNth(0, n, list);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> takeNth(int skip, int n, T[] arr) {
		return takeNth(skip, n, Arrays.asList(arr));
	}

	public static <T> List<T> takeNth(int n, T[] arr) {
		return takeNth(0, n, arr);
	}
}
