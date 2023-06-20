package mgi.tools.dumpers;

import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.Cache;
import mgi.types.config.items.ItemDefinitions;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter;

public class ItemModelInfoDumper {

    private static Cache cache;
    private static final File directory = new File("./dumps/201/items/");
    private static final String cacheDir = "./data/cache200V2/";
    private static final boolean dumpModels = true;
    private static final String itemName = "baby mole-rat";
    private static final boolean requireFullMatch = false;
    private static ItemDefinitions[] definitions;
    private static final boolean writeToToml = true;

    public static void main(String[] args) throws IOException {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        cache = Cache.openCache(cacheDir);

        val configs = cache.getArchive(ArchiveType.CONFIGS);
        val items = configs.findGroupByID(GroupType.ITEM);
        definitions = new ItemDefinitions[items.getHighestFileId()];

        for (int id = 0; id < items.getHighestFileId(); id++) {
            val file = items.findFileByID(id);
            if (file == null) {
                continue;
            }
            val buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            definitions[id] = new ItemDefinitions(id, buffer);
        }
        processDefinitions();
    }

    public static void processDefinitions() {
        for(ItemDefinitions def : definitions) {
            if(def.getName().equalsIgnoreCase(itemName) || (!requireFullMatch && def.getName().contains(itemName))) {
                printDefinition(def);
                if(dumpModels) {
                    dumpModels(def);
                }
            }
        }
    }

    public static String defToStringOutput(ItemDefinitions def) {
        String out = "";
        out += "[[item]]" + "\r\n";
        out += "id=" + def.getId() + "\r\n";
        out += "invmodel=" + def.getInventoryModelId() + "\r\n";
        out += "name=\"" + def.getName() + "\"\r\n";
        out += "zoom=" + def.getZoom() + "\r\n";
        out += "modelpitch=" + def.getModelPitch() + "\r\n";
        out += "modelroll=" + def.getModelRoll() + "\r\n";
        out += "offsetx=" + def.getOffsetX() + "\r\n";
        out += "offsety=" + def.getOffsetY() + "\r\n";
        out += "stackable=" + (def.isStackable() ? "1" : "0") + "\r\n";
        //out += "price=" + def.getPrice() + "\r\n";
        out += "members=" + def.isMembers() + "\r\n";
        out += "primarymalemodel=" + def.getPrimaryMaleModel() + "\r\n";
        out += "secondarymalemodel=" + def.getSecondaryMaleModel() + "\r\n";
        out += "primaryfemalemodel=" + def.getPrimaryFemaleModel() + "\r\n";
        out += "secondaryfemalemodel=" + def.getSecondaryFemaleModel() + "\r\n";

        out += "groundops=[";
        if (def.getGroundOptions() != null) {
            for (int i = 0; i < def.getGroundOptions().length; i++) {
                out += "\"" + (def.getGroundOptions()[i] != null ? def.getGroundOptions()[i] : "") + (i != def.getGroundOptions().length - 1 ? "\"," : "\"");
            }
        }
        out += "]" + "\r\n";

        out += "ops=[";
        if (def.getInventoryOptions() != null) {
            for (int i = 0; i < def.getInventoryOptions().length; i++) {
                out += "\"" + (def.getInventoryOptions()[i] != null ? def.getInventoryOptions()[i] : "") + (i != def.getInventoryOptions().length - 1 ? "\"," : "\"");
            }
        }
        out += "]" + "\r\n";

        out += "originalcolours=[";
        if(def.getOriginalColours() != null) {
            for (int i = 0; i < def.getOriginalColours().length; i++) {
                out += "\"" + (def.getOriginalColours()[i] != 0 ?  def.getOriginalColours()[i] : "") + (i != def.getOriginalColours().length - 1 ? "\"," : "\"");
            }
        }
        out += "]" + "\r\n";

        out += "replacementcolours=[";
        if(def.getReplacementColours() != null) {
            for (int i = 0; i < def.getReplacementColours().length; i++) {
                out += "\"" + (def.getReplacementColours()[i] != 0 ? def.getReplacementColours()[i] : "") + (i != def.getReplacementColours().length - 1 ? "\"," : "\"");
            }
        }
        out += "]" + "\r\n";

        out += "originaltextureids=[";
        if(def.getOriginalTextureIds() != null) {
            for (int i = 0; i < def.getOriginalTextureIds().length; i++) {
                out += "\"" + (def.getOriginalTextureIds()[i] != 0 ? def.getOriginalTextureIds() : "") + (i != def.getOriginalTextureIds().length - 1 ? "\"," : "\"");
            }
        }
        out += "]" + "\r\n";

        out += "replacementtextureids=[";
        if(def.getReplacementTextureIds() != null) {
            for (int i = 0; i < def.getReplacementTextureIds().length; i++) {
                out += "\"" + (def.getReplacementTextureIds()[i] != 0 ? def.getReplacementTextureIds() : "") + (i != def.getReplacementTextureIds().length - 1 ? "\"," : "\"");
            }
        }
        out += "]" + "\r\n";

        out += "shiftclickindex=" + def.getShiftClickIndex() + "\r\n";
        out += "grandexchange=" + def.isGrandExchange() + "\r\n";
        out += "tertiarymalemodel=" + def.getTertiaryMaleModel() + "\r\n";
        out += "tertiaryfemalemodel=" + def.getTertiaryFemaleModel() + "\r\n";
        out += "primarymaleheadmodelid=" + def.getPrimaryMaleHeadModelId() + "\r\n";
        out += "primaryfemaleheadmodelid=" + def.getPrimaryFemaleHeadModelId() + "\r\n";
        out += "secondarymaleheadmodelid=" + def.getSecondaryMaleHeadModelId() + "\r\n";
        out += "secondaryfemaleheadmodelid=" + def.getSecondaryFemaleHeadModelId() + "\r\n";
        out += "modelyaw=" + def.getModelYaw() + "\r\n";
        out += "notedid=" + def.getNotedId() + "\r\n";
        out += "notedtemplate=" + def.getNotedTemplate() + "\r\n";

        out += "stackids=[";
        if(def.getStackIds() != null) {
            for (int i = 0; i < def.getStackIds().length; i++) {
                out += "\"" + (def.getStackIds()[i] != 0 ? def.getStackIds()[i] : "") + (i != def.getStackIds().length - 1 ? "\"," : "\"");
            }
        }
        out += "]" + "\r\n";

        out += "stackamounts=[";
        if(def.getStackAmounts() != null)
            for(int i = 0; i < def.getStackAmounts().length; i++) {
                out += "\"" + (def.getStackAmounts()[i] != 0 ? def.getStackAmounts()[i] : "") + (i != def.getStackAmounts().length -1 ? "\"," : "\"");
            }
        out += "]" + "\r\n";

        out += "resizex=" + def.getResizeX() + "\r\n";
        out += "resizey=" + def.getResizeY() + "\r\n";
        out += "resizez=" + def.getResizeZ() + "\r\n";
        out += "ambient=" + def.getAmbient() + "\r\n";
        out += "contrast=" + def.getContrast() + "\r\n";
        out += "teamid=" + def.getTeamId() + "\r\n";
        out += "bindid=" + def.getBindId() + "\r\n";
        out += "bindtemplateid=" + def.getBindTemplateId() + "\r\n";
        out += "placeholderid=" + def.getPlaceholderId() + "\r\n";
        out += "placeholdertemplate=" + def.getPlaceholderTemplate() + "\r\n";
        out += "parameters={ clear=true }" + "\r\n"; // gonna keep this clear for now
        return out;
    }

    public static void printDefinition(ItemDefinitions def) {
        try {
            String out = "";
            out += "# " + def.getName() + "\r\n";
            out += defToStringOutput(def);
            out += "\r\n";
            out += "\r\n";

            if(def.getNotedId() != -1) {
                out += "# " + def.getName() + " (Noted)" + "\r\n";
                out += defToStringOutput(definitions[def.getNotedId()]);
                out += "\r\n";
                out += "\r\n";
            }

            if(def.getPlaceholderId() != -1) {
                out += "# " + def.getName() + " (Placeholder)" + "\r\n";
                out += defToStringOutput(definitions[def.getPlaceholderId()]);
                out += "\r\n";
                out += "\r\n";
            }

            if(writeToToml) {
                String nameWithUnderScores = def.getName().replaceAll(" ", "_");
                String tomlDir = def.getId() + "_" + nameWithUnderScores + "/";
                String fullDir = directory + "/" + tomlDir;
                File full = new File(fullDir);
                if(!full.exists()) {
                    full.mkdir();
                }

                FileWriter fileWriter = new FileWriter(directory.getPath() + "/" + def.getId() + "_" + def.getName().toLowerCase().replaceAll(" ", "_") + "/" + def.getName().toLowerCase().replaceAll(" ", "_") + ".toml");
                fileWriter.write(out);
                fileWriter.close();
            }

            System.out.print(out);
        } catch (Exception e) {
            System.err.println("Error while writing toml file! " + "writeToToml=" + writeToToml);
            e.printStackTrace();
        }
    }

    public static void dumpModels(ItemDefinitions def) {
        System.out.println("Paste these into type parser: ");
        if(def.getInventoryModelId() > 0) {
            dumpModel(def.getInventoryModelId(),  def.getId() + " " + def.getName().toLowerCase(), "inv");
        }

        if(def.getPrimaryMaleModel() > 0) {
            dumpModel(def.getPrimaryMaleModel(), def.getId() + " " + def.getName().toLowerCase(), "primary male");
        }

        if(def.getSecondaryMaleModel() > 0) {
            dumpModel(def.getSecondaryMaleModel(), def.getId() + " " + def.getName().toLowerCase(), "secondary male");
        }

        if(def.getPrimaryFemaleModel() > 0) {
            dumpModel(def.getPrimaryFemaleModel(), def.getId() + " " + def.getName().toLowerCase(), "primary female");
        }

        if(def.getSecondaryFemaleModel() > 0) {
            dumpModel(def.getSecondaryFemaleModel(), def.getId() + " " + def.getName().toLowerCase(), "secondary female");
        }

        if(def.getTertiaryMaleModel() > 0) {
            dumpModel(def.getTertiaryMaleModel(), def.getId() + " " + def.getName().toLowerCase(), "tertiary male");
        }

        if(def.getTertiaryFemaleModel() > 0) {
            dumpModel(def.getTertiaryFemaleModel(), def.getId() + " " + def.getName().toLowerCase(), "tertiary female");
        }

        if(def.getPrimaryMaleHeadModelId() > 0) {
            dumpModel(def.getPrimaryMaleHeadModelId(), def.getId() + " " + def.getName().toLowerCase(), "primary male head");
        }

        if(def.getPrimaryFemaleHeadModelId() > 0) {
            dumpModel(def.getPrimaryFemaleHeadModelId(), def.getId() + " " + def.getName().toLowerCase(), "primary female head");
        }

        if(def.getSecondaryMaleHeadModelId() > 0) {
            dumpModel(def.getSecondaryMaleHeadModelId(), def.getId() + " " + def.getName().toLowerCase(), "secondary male head");
        }

        if(def.getSecondaryFemaleHeadModelId() > 0) {
            dumpModel(def.getSecondaryFemaleHeadModelId(), def.getId() + " " + def.getName().toLowerCase(), "secondary female head");
        }

        System.out.println();
        System.out.println();
    }

    public static void dumpModel(int id, String dir, String appendName) {
        if(cache == null) {
            System.err.println("Cache hasn't been opened yet!");
            return;
        }
        val archive = cache.getArchive(ArchiveType.MODELS);
        val group = archive.findGroupByID(id);
        if (group == null) {
            System.err.println("Model doesn't exist for id: " + id);
            return;
        }

        String nameWithUnderScores = appendName.replaceAll(" ", "_");
        String modelDir = dir.endsWith("/") ? dir.replaceAll(" ", "_") : dir.replaceAll(" ", "_") + "/";
        String fullDir = directory + "/" + modelDir;
        File full = new File(fullDir);
        if(!full.exists()) {
            full.mkdir();
        }
        File datFile = new File(fullDir, id + "_" + nameWithUnderScores + ".dat");
        try {
            val dos = new DataOutputStream(new FileOutputStream(datFile));
            dos.write(group.findFileByID(0).getData().getBuffer());
        } catch (Exception e) {
            System.err.println("Error while writing file: " + nameWithUnderScores);
            e.printStackTrace();
        }
        System.out.println("packModel(" + id + ", java.nio.file.Files.readAllBytes(Paths.get(\"assets/models/" + modelDir + id + "_" + nameWithUnderScores + ".dat\")));");
    }
}