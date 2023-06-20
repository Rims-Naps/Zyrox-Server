package com.zenyte.game.util;

import java.io.FileNotFoundException;

public class Script {

    //private static final List<NPCDefinition> toDump = new ArrayList<>();
    //private static final List<NPCDefinition> toMerge = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
/*
        BufferedReader br = new BufferedReader(new FileReader(new File("./data/npcs/definitions.json")));
        NPCDefinition[] defs = new Gson().fromJson(br, NPCDefinition[].class);
        int[] levels = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        for (NPCDefinition def : defs) {
            def.setLevels(levels);
            toMerge.enqueue(def);
        }
		/*
		 * int offset = templates.length; for (int i = offset; i <
		 * toMerge.size(); i++) { NPCDefinition def = toMerge.get(i);
		 * toDump.enqueue(def); }
		 

        PrintWriter pw = null;
        String to = new GsonBuilder().setPrettyPrinting().create().toJson(toMerge);
        try {
            pw = new PrintWriter("./data/npc_definitions.json", "UTF-8");
            pw.println(to);
            pw.flush();
            pw.close();
        } catch (Exception e) {

        }
        System.out.println("Total dumped =" + toMerge.size());*/
    }

    public static class Template {

        private int id;
        private String name;
        private String examine;
        private int respawn;

        public Template(int id, String name, String examine, int respawn, int combat, int hitpoints, int maxHit, int size, int attackSpeed, int attackAnim, int blockAnim, int deathAnim, boolean attackable, boolean aggressive, boolean poisonous, String combatType, int attackBonus, int defenceMelee, int defenceRange, int defenceMage, int slayerLevelRequired) {
            super();
            this.id = id;
            this.name = name;
            this.examine = examine;
            this.respawn = respawn;
            this.combat = combat;
            this.hitpoints = hitpoints;
            this.maxHit = maxHit;
            this.size = size;
            this.attackSpeed = attackSpeed;
            this.attackAnim = attackAnim;
            this.blockAnim = blockAnim;
            this.deathAnim = deathAnim;
            this.attackable = attackable;
            this.aggressive = aggressive;
            this.poisonous = poisonous;
            this.combatType = combatType;
            this.attackBonus = attackBonus;
            this.defenceMelee = defenceMelee;
            this.defenceRange = defenceRange;
            this.defenceMage = defenceMage;
            this.slayerLevelRequired = slayerLevelRequired;
        }

        private int combat;
        private int hitpoints;
        private int maxHit;
        private int size;
        private int attackSpeed;
        private int attackAnim;
        private int blockAnim;
        private int deathAnim;
        private boolean attackable;
        private boolean aggressive;
        private boolean poisonous;
        private String combatType;
        private int attackBonus;
        private int defenceMelee;
        private int defenceRange;
        private int defenceMage;
        private int slayerLevelRequired;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExamine() {
            return examine;
        }

        public void setExamine(String examine) {
            this.examine = examine;
        }

        public int getRespawn() {
            return respawn;
        }

        public void setRespawn(int respawn) {
            this.respawn = respawn;
        }

        public int getCombat() {
            return combat;
        }

        public void setCombat(int combat) {
            this.combat = combat;
        }

        public int getHitpoints() {
            return hitpoints;
        }

        public void setHitpoints(int hitpoints) {
            this.hitpoints = hitpoints;
        }

        public int getMaxHit() {
            return maxHit;
        }

        public void setMaxHit(int maxHit) {
            this.maxHit = maxHit;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getAttackSpeed() {
            return attackSpeed;
        }

        public void setAttackSpeed(int attackSpeed) {
            this.attackSpeed = attackSpeed;
        }

        public int getAttackAnim() {
            return attackAnim;
        }

        public void setAttackAnim(int attackAnim) {
            this.attackAnim = attackAnim;
        }

        public int getBlockAnim() {
            return blockAnim;
        }

        public void setBlockAnim(int blockAnim) {
            this.blockAnim = blockAnim;
        }

        public int getDeathAnim() {
            return deathAnim;
        }

        public void setDeathAnim(int deathAnim) {
            this.deathAnim = deathAnim;
        }

        public boolean isAttackable() {
            return attackable;
        }

        public void setAttackable(boolean attackable) {
            this.attackable = attackable;
        }

        public boolean isAggressive() {
            return aggressive;
        }

        public void setAggressive(boolean aggressive) {
            this.aggressive = aggressive;
        }

        public boolean isPoisonous() {
            return poisonous;
        }

        public void setPoisonous(boolean poisonous) {
            this.poisonous = poisonous;
        }

        public String getCombatType() {
            return combatType;
        }

        public void setCombatType(String combatType) {
            this.combatType = combatType;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAttackBonus() {
            return attackBonus;
        }

        public void setAttackBonus(int attackBonus) {
            this.attackBonus = attackBonus;
        }

        public int getDefenceMelee() {
            return defenceMelee;
        }

        public void setDefenceMelee(int defenceMelee) {
            this.defenceMelee = defenceMelee;
        }

        public int getDefenceRange() {
            return defenceRange;
        }

        public void setDefenceRange(int defenceRange) {
            this.defenceRange = defenceRange;
        }

        public int getDefenceMage() {
            return defenceMage;
        }

        public void setDefenceMage(int defenceMage) {
            this.defenceMage = defenceMage;
        }

        public int getSlayerLevelRequired() {
            return slayerLevelRequired;
        }
    }
}
