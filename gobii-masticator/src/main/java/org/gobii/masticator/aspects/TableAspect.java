package org.gobii.masticator.aspects;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableAspect extends Aspect {

	private String table;
	private Map<String, ElementAspect> aspects;

}
