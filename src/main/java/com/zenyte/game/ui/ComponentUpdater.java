package com.zenyte.game.ui;

import com.zenyte.GameEngine;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import mgi.Indice;
import mgi.types.component.ComponentDefinitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Kris | 19/10/2018 13:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class ComponentUpdater {

    private static final Int2ObjectOpenHashMap<InterfaceInformation> MAP = new Int2ObjectOpenHashMap<>();

    public static final void main(final String[] args) {

        //Game.load();
        //Definitions.loadDefinitions(Definitions.LOW_PRIORITY_DEFINITIONS);
        //new Scanner().scan();
        /*val scanner = new FastClasspathScanner(Scanner.class.getPackage().getName());
        scanner.matchSubclassesOf(Interface.class, (SubclassMatchProcessor<Interface>) NewInterfaceHandler::add);
        scanner.scan();*/


        GameEngine.main(new String[0]);


        load();
        NewInterfaceHandler.INTERFACES.forEach((k, v) -> parseClass(v.getClass()));



        //
        //save();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error(Strings.EMPTY, e);
        }
        System.exit(-1);
    }

    public static final void parseClass(final Class<? extends Interface> clazz) {
        try {
            val reader = new BufferedReader(new FileReader(new File("src/main/java/" + clazz.getName().replaceAll("[.]", "/") + ".java")));
            String line;
            val file = new ArrayList<String>();
            val instance = clazz.newInstance();
            instance.attach();
            val k = instance.getInterface().getId();
            val info = MAP.get(k);
            if (info == null) {
                log.info("Interface " + k + " does not exist in previous components dump.");
                return;
            }
            while ((line = reader.readLine()) != null) {
                val originalLine = line;
                line = line.trim();

                val spaceBuilder = new StringBuilder();
                for (val c : originalLine.chars().toArray()) {
                    if (c != ' ' && c != 9)
                        break;
                    spaceBuilder.append((char) c);
                }

                if (line.startsWith("put(")) {
                    line = line.substring("put(".length(), line.length() - ");".length());
                    val split = line.split(",");
                    val name = split[split.length - 1].replaceAll("\"", "").trim();

                    val bitpacked = findInfo(instance.getComponentInfoCopy(), name);

                    val componentId = (bitpacked >> 16) & 0xFFFF;
                    val slotId = bitpacked & 0xFFFF;
                    val component = find(info.information, comp -> comp.componentId == componentId && comp.slotId == slotId, " Info: " +
                            "" + k + ", " + componentId + ", " + slotId);
                    if (!component.name.equals(name)) {
                        //log.warning("Component name mismatch: " + k + ", " + componentId + ", " + slotId + ", " + name + " | " + component.name);
                    }
                    try {
                        val count = getDifferentFieldsCount(component.definitions, ComponentDefinitions.get(k, componentId), "componentId");
                        if (count.size() > 0) {
                            throw new RuntimeException("Component: " + componentId + " is no longer valid.");
                        }
                        file.add(originalLine);
                    } catch (Exception e) {
                        log.error(Strings.EMPTY, e);
                        try {
                            val array = new int[Utils.getIndiceSize(Indice.INTERFACE_DEFINITIONS, k)];
                            for (int i = 0; i < array.length; i++) {
                                val defs = ComponentDefinitions.get(k, i);
                                val differenceCount = getDifferentFieldsCount(defs, component.definitions, "componentId");
                                array[i] = differenceCount.size();
                            }

                            int smallestDifferenceCount = Integer.MAX_VALUE;
                            for (int i = 0; i < array.length; i++) {
                                val value = array[i];
                                if (value < smallestDifferenceCount) {
                                    smallestDifferenceCount = value;
                                }
                            }
                            //up to three fields allowed to be different before the change is considered too much.
                            if (smallestDifferenceCount > 3) {
                                file.add(originalLine + "//TODO Find correct component id; ambiguous options.");
                                continue;
                            }

                            if (smallestDifferenceCount > 0) {
                                int ambiguousComponentsCount = 0;
                                for (val key : array) {
                                    if (key == smallestDifferenceCount)
                                        ambiguousComponentsCount++;
                                }

                                if (ambiguousComponentsCount > 1) {
                                    file.add(originalLine + "//TODO Find correct component id; ambiguous options.");
                                    continue;
                                }
                            }
                            val newComponentId = ArrayUtils.indexOf(array, smallestDifferenceCount);
                            file.add(spaceBuilder.toString() + "put(" + newComponentId + ", " + (split.length >= 3 ? (slotId + ", ") : "") + "\"" + name + "\");//Component updated.");
                        } catch (Exception a) {
                            //a.printStackTrace();
                            file.add(originalLine + "//TODO: Find correct component id");
                        }
                    }
                } else
                    file.add(originalLine);
            }
            for (val l : file) {
                System.err.println(l);
            }

            System.err.println();
            System.err.println();
            System.err.println();

        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    private static final int findInfo(final Int2ObjectOpenHashMap<String> map, final String name) {
        for (val entry : map.int2ObjectEntrySet()) {
            if (entry.getValue().equals(name))
                return entry.getIntKey();
        }

        return -1;
    }

    public static final List<String> getDifferentFieldsCount(final ComponentDefinitions a, final ComponentDefinitions b, final String... ignoreFields) {
        val list = new ArrayList<String>();
        loop:
        for (final Field field : a.getClass().getDeclaredFields()) {
            if ((field.getModifiers() & 8) != 0) {
                continue;
            }
            field.setAccessible(true);
            val fieldName = field.getName();
            for (val ignored : ignoreFields) {
                if (fieldName.equals(ignored))
                    continue loop;
            }
            try {
                val valueA = field.get(a);
                val valueB = b == null ? null : field.get(b);
                if (valueA == null || valueB == null) {
                    if (valueA != valueB) {
                        list.add(fieldName);
                    }
                    continue;
                }

                final Class<?> type = field.getType();
                if (type == int[][].class) {

                    val arrayA = (int[][]) valueA;
                    val arrayB = (int[][]) valueB;
                    if (arrayA.length != arrayB.length) {
                        list.add(fieldName);
                        continue;
                    }
                    for (int i = 0; i < arrayA.length; i++) {
                        if (!Arrays.equals(arrayA[i], arrayB[i])) {
                            list.add(fieldName);
                            continue loop;
                        }
                    }
                } else if (type == int[].class) {
                    if (!Arrays.equals((int[]) valueA, (int[]) valueB)) {
                        list.add(fieldName);
                    }
                    continue;
                } else if (type == byte[].class) {
                    if (!Arrays.equals((byte[]) valueA, (byte[]) valueB)) {
                        list.add(fieldName);
                    }
                    continue;
                } else if (type == short[].class) {
                    if (!Arrays.equals((short[]) valueA, (short[]) valueB)) {
                        list.add(fieldName);
                    }
                    continue;
                } else if (type == double[].class) {
                    if (!Arrays.equals((double[]) valueA, (double[]) valueB)) {
                        list.add(fieldName);
                    }
                    continue;
                } else if (type == float[].class) {
                    if (!Arrays.equals((float[]) valueA, (float[]) valueB)) {
                        list.add(fieldName);
                    }
                    continue;
                } else if (type == String[].class) {
                    val arrayA = (String[]) valueA;
                    val arrayB = (String[]) valueB;
                    if (arrayA.length != arrayB.length) {
                        list.add(fieldName);
                        continue;
                    }
                    for (int i = 0; i < arrayA.length; i++) {
                        if (!arrayA[i].equals(arrayB[i])) {
                            list.add(fieldName);
                            continue loop;
                        }
                    }
                    continue;
                } else if (type == Object[].class) {
                    val arrayA = (Object[]) valueA;
                    val arrayB = (Object[]) valueB;
                    if (arrayA.length != arrayB.length) {
                        list.add(fieldName);
                        continue;
                    }
                    for (int i = 0; i < arrayA.length; i++) {
                        Object va = arrayA[i];
                        Object vb = arrayB[i];
                        if (va instanceof Double) {
                            va = ((Double) va).intValue();
                        }
                        if (vb instanceof Double) {
                            vb = ((Double) vb).intValue();
                        }
                        if (va == null || vb == null) {
                            if (va != vb) {
                                list.add(fieldName);
                                continue loop;
                            }
                        }
                        if (!va.equals(vb)) {
                            list.add(fieldName);
                            continue loop;
                        }
                    }
                    continue;
                }

                if (!field.get(a).equals(field.get(b))) {
                    list.add(fieldName);
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        }
        return list;
    }

    private static final <T> T find(final List<T> list, final Predicate<T> predicate, final String additionalInfo) {
        for (val value : list) {
            if (predicate.test(value))
                return value;
        }
        throw new RuntimeException("Unable to locate an entry that suits the predicate." + additionalInfo);
    }

    private static final void load() {
        try {
            val reader = new BufferedReader(new FileReader("data/components.json"));
            val definitions = World.getGson().fromJson(reader, InterfaceInformation[].class);
            for (val info : definitions) {
                MAP.put(info.id, info);
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    private static final void save() {
        val list = new ArrayList<InterfaceInformation>(NewInterfaceHandler.INTERFACES.size());
        NewInterfaceHandler.INTERFACES.forEach((k, v) -> {
            List<ComponentInformation> componentList;
            val info = new InterfaceInformation(k, componentList = new ArrayList<>());
            list.add(info);
            v.getComponentInfoCopy().forEach((bitpacked, name) -> {
                val componentId = (bitpacked >> 16) & 0xFFFF;
                val slotId = bitpacked & 0xFFFF;
                val componentInfo = new ComponentInformation(componentId, slotId, name, ComponentDefinitions.get(k, componentId));
                info.information.add(componentInfo);
            });
        });

        val json = World.getGson().toJson(list);
        try {
            //val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            //val now = LocalDateTime.now();//TODO archive old file.
            val pw = new PrintWriter("data/components.json", "UTF-8");
            pw.println(json);
            pw.close();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @AllArgsConstructor
    private static final class InterfaceInformation {
        private final int id;
        private final List<ComponentInformation> information;
    }

    @AllArgsConstructor
    private static final class ComponentInformation {
        private final int componentId;
        private final int slotId;
        private final String name;
        private final ComponentDefinitions definitions;
    }

}
