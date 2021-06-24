package org.gobii.masticator.aspects;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileAspect extends Aspect {

	private Map<String, TableAspect> aspects;

}
