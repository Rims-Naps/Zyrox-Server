package mgi.custom.halloween;

import com.zenyte.Constants;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.MapUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static mgi.GhettoPacker.packMap;

public class HalloweenMapPacker {

    public void pack() throws IOException {
        if (!Constants.HALLOWEEN) {
            return;
        }
        packMap(12340, Files.readAllBytes(Paths.get("assets/halloween/draynor/620l.dat")), MapUtils.inject(Files.readAllBytes(Paths.get("assets/halloween/draynor/621m.dat")), o -> {
            if (o.getId() == 21743 && o.getXInRegion() == 49 && o.getYInRegion() == 18) {
                o.setId(1289);
                return false;
            }
            o.setId(HalloweenObject.redirectedIds.getOrDefault(o.getId(), o.getId()));
            return false;
        }));

        packMap(6473, Files.readAllBytes(Paths.get("assets/halloween/dungeon/532l.dat")), MapUtils.inject(Files.readAllBytes(Paths.get("assets/halloween/dungeon/533m.dat")), o -> {
            if (o.getId() == 18061 || o.getId() == 27093 || o.getId() == 9568) {//Remove a trapdoor, light and bucket.
                return true;
            }
            o.setId(HalloweenObject.redirectedIds.getOrDefault(o.getId(), o.getId()));
            return false;
        }, new WorldObject(16665, 10, 2, 1613, 4695, 0)));

        packMap(6985, Files.readAllBytes(Paths.get("assets/halloween/mansion/1404l.dat")), MapUtils.inject(Files.readAllBytes(Paths.get("assets/halloween/mansion/1405m.dat")), o -> {
                    if (o.getId() == HalloweenObject.FIRE_WALL.getOriginalObject()) {
                        return true;
                    }
                    if (o.getId() == HalloweenObject.CAGE.getOriginalObject() || o.getId() == HalloweenObject.CAGE.getRepackedObject()) {
                        if ((o.getX() == (1764 & 0x3F) && o.getY() == (4724 & 0x3F)) || (o.getX() == (1752 & 0x3F) && o.getY() == (4724 & 0x3F))) {
                            return true;
                        }
                        if (o.getX() == (1743 & 0x3F) && o.getY() == (4711 & 0x3F)) {
                            o.setRotation(1);
                        }
                        if (o.getX() == (1767 & 0x3F) && o.getY() == (4709 & 0x3F)) {
                            o.setRotation(1);
                        }
                        if (o.getX() == (1768 & 0x3F) && o.getY() == (4709 & 0x3F)) {
                            o.setId(1116);
                            o.setRotation(2);
                            o.setType(10);
                        }
                        if ((o.getX() == (1760 & 0x3F) && o.getY() == (4707 & 0x3F)) || (o.getX() == (1759 & 0x3F) && o.getY() == (4706 & 0x3F))
                                || (o.getX() == (1768 & 0x3F) && o.getY() == (4708 & 0x3F))) {
                            return true;
                        }
                    }
                    if (o.getId() == HalloweenObject.BARRIER.getOriginalObject() || o.getId() == HalloweenObject.GATE.getOriginalObject()) {
                        //Incorrect rotation objects, "breaks" code.
                        if ((o.getX() == (1748 & 0x3F) && o.getY() == (4714 & 0x3F))
                                || (o.getX() == (1757 & 0x3F) && o.getY() == (4725 & 0x3F))
                                || (o.getX() == (1765 & 0x3F) && o.getY() == (4724 & 0x3F))
                                || (o.getX() == (1758 & 0x3F) && o.getY() == (4721 & 0x3F))
                                || (o.getX() == (1753 & 0x3F) && o.getY() == (4724 & 0x3F))
                                || (o.getX() == (1748 & 0x3F) && o.getY() == (4722 & 0x3F))) {
                            o.setRotation(2);
                            o.setLocation(o.getX() - 1, o.getY(), o.getPlane());
                        }
                    }
                    if ((o.getX() == (1752 & 0x3F) && o.getY() == (4724 & 0x3F))) {
                        o.setLocation(o.getX() - 1, o.getY(), o.getPlane());
                    }
                    if (o.getId() == HalloweenObject.CAGE.getRepackedObject()) {
                        if (o.getX() == (1757 & 0x3F) && o.getY() == (4721 & 0x3F)) {
                            o.setLocation(o.getX(), o.getY() + 1, o.getPlane());
                        }
                    }
                    if (o.getId() == HalloweenObject.WEB.getOriginalObject()) {
                        if (o.getX() == (1758 & 0x3F) && o.getY() == (4730 & 0x3F)) {
                            o.setRotation(2);
                        }
                    }
                    if (o.getId() == 29487) {
                        o.setId(HalloweenObject.BARRIER.getRepackedObject());
                        o.setRotation(2);
                        o.setLocation(o.getX() - 1, o.getY(), o.getPlane());
                    }
                    o.setId(HalloweenObject.redirectedIds.getOrDefault(o.getId(), o.getId()));
                    return false;
                }, new WorldObject(14977, 0, 1, 1768, 4709, 0),
                new WorldObject(1116, 10, 0, 1768, 4709, 0),
                new WorldObject(14977, 0, 0, 1739, 4730, 0),

                new WorldObject(14977, 0, 0, 1743, 4729, 0),
                new WorldObject(14977, 0, 0, 1743, 4728, 0),

                new WorldObject(HalloweenObject.CHEST.getRepackedObject(), 10, 2, 1749, 4708, 0),
                new WorldObject(HalloweenObject.CHEST.getRepackedObject(), 10, 2, 1766, 4715, 0),
                new WorldObject(HalloweenObject.CHEST.getRepackedObject(), 10, 0, 1751, 4731, 0),
                new WorldObject(HalloweenObject.CHEST.getRepackedObject(), 10, 3, 1752, 4718, 0)
        ));
        packMap(6729, new byte[4 * 64 * 64], MapUtils.inject(new byte[1], null));
    }

}
