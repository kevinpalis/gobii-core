package org.gobii.masticator.aspects;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransformAspect extends ElementAspect {

	private Aspect aspect;
	private List<Object> args = new ArrayList<>();

}
