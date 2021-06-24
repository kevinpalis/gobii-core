package org.gobii.masticator.aspects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResolveAspect extends ElementAspect {

	private String table;
	private String column;

}
