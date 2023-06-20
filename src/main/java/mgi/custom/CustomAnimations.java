package mgi.custom;

import mgi.types.config.AnimationDefinitions;
import lombok.val;

/**
 * @author Kris | 29/03/2019 19:07
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CustomAnimations {

    public static final void pack() {
        val def = AnimationDefinitions.get(7403);
        def.setId(10000);
        def.setRightHandItem(/*12100*/10485);
        def.pack();
    }
}
