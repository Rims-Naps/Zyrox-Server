package mgi.types.config.items;

import com.zenyte.game.world.entity.masks.RenderAnimation;

import lombok.Getter;
import lombok.Setter;

public class WieldableDefinition {

	@Getter @Setter private boolean twoHanded;
	@Getter @Setter private int blockAnimation;
	@Getter @Setter private int standAnimation = RenderAnimation.STAND;
	@Getter @Setter private int walkAnimation = RenderAnimation.WALK;
	@Getter @Setter private int runAnimation = RenderAnimation.RUN;
	@Getter @Setter private int standTurnAnimation = RenderAnimation.STAND_TURN;
	@Getter @Setter private int rotate90Animation = RenderAnimation.ROTATE90;
	@Getter @Setter private int rotate180Animation = RenderAnimation.ROTATE180;
	@Getter @Setter private int rotate270Animation = RenderAnimation.ROTATE270;
	@Getter @Setter private int accurateAnimation;
	@Getter @Setter private int aggressiveAnimation;
	@Getter @Setter private int controlledAnimation;
	@Getter @Setter private int defensiveAnimation;
	@Getter @Setter private int attackSpeed;
	@Getter @Setter private int interfaceVarbit;
	@Getter @Setter private int normalAttackDistance;
	@Getter @Setter private int longAttackDistance;
	
}
