package mgi.custom;

import com.zenyte.Game;
import com.zenyte.GameEngine;
import mgi.types.config.AnimationDefinitions;
import mgi.types.Definitions;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Kris | 08/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AnimationFrame {

    public static final void main(final String... args) {
        System.out.println("Loading cache.");
        Game.load();
        System.out.println("Loading sequence definitions.");
        Definitions.loadDefinitions(new Class<?>[] {AnimationDefinitions.class});
        val scanner = new Scanner(System.in);
        val yaml = new Yaml(new Constructor(AnimationDefinitions.class));
        do {
            System.out.println("Enter id of the sequence to extract, or type exit to close: ");
            try {
                val line = scanner.nextLine();
                if (line.toLowerCase().contains("exit")) {
                    System.exit(-1);
                    return;
                }
                val id = Integer.parseInt(line);
                try (val pw = new PrintWriter(id + ".seq", "UTF-8")) {
                    pw.println(yaml.dump(AnimationDefinitions.get(id)));
                } catch (final Exception e) {
                    GameEngine.logger.error(Strings.EMPTY, e);
                }
            } catch (Exception e) {
                GameEngine.logger.error(Strings.EMPTY, e);
            }
        } while(true);
    }

    public static final void pack(final String fileName, final AnimationBase base, String framesFolder) throws IOException {
        val file = new File("assets/animations/sequence/" + fileName + ".seq");
        val yaml = new Yaml(new Constructor(AnimationDefinitions.class));
        val anim = yaml.loadAs(new FileReader(file), AnimationDefinitions.class);
        val ids = anim.getFrameIds();
        for (int i = 0; i < ids.length; i++) {
            ids[i] = (ids[i] & 0xFFFF) | (base.getBaseId() << 16);
        }
        Arrays.sort(ids);
        /*for (int id : ids) {
            Game.getLibrary().getIndex(0).addArchive(id >> 16).addFile(id & 0xFFFF,
                    IOUtils.toByteArray(new FileInputStream(new File("assets/animations/frames/" + framesFolder + "/" + id + ".dat"))));
        }*/
        anim.pack();
    }

}
