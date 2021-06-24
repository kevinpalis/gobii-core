package org.gobii.masticator.aspects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlignAspect extends ElementAspect {

	private List<Aspect> aspects;

}
