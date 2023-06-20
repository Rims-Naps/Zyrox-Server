package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 11. veebr 2018 : 2:18.42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SpiritualMage extends KillcountNPC implements CombatScript {

    private static final Graphics castGraphics = new Graphics(164, 20, 60), impactGraphics = new Graphics(166, 0, 92);
    private static final Projectile projectile = new Projectile(165, 20, 25, 57, 15, 18, 64, 5);
    private static final Graphics saradominImpactGraphics = new Graphics(76, 0, 96), zamorakImpactGraphics = new Graphics(78);
	private static final Projectile thrownAxe = new Projectile(1193, 85, 30, 30, 15, 8, 0, 5);
	private static final SoundEffect spiritualMageSound = new SoundEffect(1655, 10, 0);
	private static final SoundEffect priestMageSound = new SoundEffect(1659, 10, 0);

    public SpiritualMage(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
    }

    @Override
    public GodType type() {
        return id == 2244 ? GodType.BANDOS : id == 2212 || id == 2209 ? GodType.SARADOMIN : id == 3168 ? GodType.ARMADYL :
                GodType.ZAMORAK;
    }

    @Override
    public int attack(final Entity target) {
        switch (id) {
            case 2244:
                setGraphics(castGraphics);
                setAnimation(getCombatDefinitions().getAttackAnim());
                delayHit(this, World.sendProjectile(this, target, projectile), target,
                        new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MAGIC, target), HitType.MAGIC).onLand(hit -> target.setGraphics(impactGraphics)));
                break;
            case 2212:
            case 2209:
                World.sendSoundEffect(getMiddleLocation(), id == 2209 ? priestMageSound : spiritualMageSound);
                setAnimation(getCombatDefinitions().getAttackAnim());
                delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MAGIC, target), HitType.MAGIC).onLand(hit -> target.setGraphics(saradominImpactGraphics)));
                break;
            case 3161:
                World.sendSoundEffect(getMiddleLocation(), spiritualMageSound);
                setAnimation(getCombatDefinitions().getAttackAnim());
                delayHit(this, 1, target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MAGIC, target), HitType.MAGIC).onLand(hit -> target.setGraphics(zamorakImpactGraphics)));
                break;
            case 3168:
                setAnimation(getCombatDefinitions().getAttackAnim());
                delayHit(this, World.sendProjectile(this, target, thrownAxe), target, new Hit(this, getRandomMaxHit(this, combatDefinitions.getMaxHit(), RANGED, target), HitType.RANGED));
                break;
        }
        return getCombatDefinitions().getAttackSpeed();
    }

    @Override
    public void onDeath(Entity source) {
        super.onDeath(source);
        if (source instanceof Player) {
            val player = (Player) source;
            player.getAchievementDiaries().update(FremennikDiary.SLAY_A_SPIRITUAL_MAGE);
            player.getAchievementDiaries().update(WildernessDiary.SLAY_A_SPIRITRUAL_MAGE);
        }
    }

    @Override
    public boolean validate(int id, String name) {
        return id == 2244 || id == 2212 || id == 2209 || id == 3161 || id == 3168;
    }
}
