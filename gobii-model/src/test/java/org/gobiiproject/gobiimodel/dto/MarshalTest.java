package org.gobiiproject.gobiimodel.dto;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderProcedure;
import org.junit.Test;


public class MarshalTest {

	@Test
	public void testUnmarshalGobiiLoaderProcedure() throws Exception {
		final String path = "loader_instruction_file.json";

		String instructionFileJson = slurp(path);

		GobiiLoaderProcedure proc = Marshal.unmarshalGobiiLoaderProcedure(instructionFileJson);

		assertNotNull(proc);
		assertNotNull(proc.getInstructions());
		assertNotNull(proc.getMetadata());
		assertNotNull(proc.getMetadata().getGobiiJobStatus());
		assertNotNull(proc.getMetadata().getGobiiFile());
		assertNotNull(proc.getMetadata().getGobiiFile().getDestination());
		assertNotNull(proc.getMetadata().getGobiiFile().getSource());
		assertNotNull(proc.getMetadata().getGobiiCropType());

		assertNotNull(proc.getInstructions());
		assertFalse(proc.getInstructions().isEmpty());
	}

	public String slurp(String path) {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(path).getFile());

		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines( Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8))  {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		}
		catch (IOException e) {
			return null;
		}
		return contentBuilder.toString();
	}

}
