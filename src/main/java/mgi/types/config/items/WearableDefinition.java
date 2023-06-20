package mgi.types.config.items;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

public class WearableDefinition {

	@Getter @Setter private HashMap<Integer, Integer> requirements;
	@Getter @Setter private String bonuses;
	@Getter @Setter WieldableDefinition weaponDefinition;
	
}
