package com.zenyte.game.world.entity.npc.spawns;

import com.zenyte.game.util.Direction;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public final class NPCSpawn {

	@Getter @Setter 
	private int id;
	@Getter @Setter
	private int x;
	@Getter @Setter
	private int y;
	@Getter @Setter
	private int z;
	@Getter @Setter
	private Direction direction = Direction.SOUTH;
	@Getter @Setter
	private Integer radius = 0;
	//@Getter @Setter private int knownIndex;

}
