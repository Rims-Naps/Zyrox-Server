package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

public class VerzikDialoguePhase extends VerzikPhase {


    public VerzikDialoguePhase(VerzikVitur verzik) {
        super(verzik, 1);
    }

    @Override
    public void onPhaseStart() {
        verzik.spawnPillars();
    }

    @Override
    public void onTick() {

    }

    @Override
    public boolean isPhaseComplete() {
        return false;
    }

    @Override
    public VerzikPhase advance() {
        return null;
    }

}
