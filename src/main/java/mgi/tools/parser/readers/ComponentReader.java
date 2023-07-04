package mgi.tools.parser.readers;

import com.google.common.collect.ImmutableMap;
import com.moandjiezana.toml.Toml;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.val;
import mgi.tools.parser.TypeProperty;
import mgi.tools.parser.TypeReader;
import mgi.types.Definitions;
import mgi.types.component.ComponentDefinitions;
import mgi.types.component.ComponentType;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tommeh | 01/02/2020 | 16:59
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ComponentReader implements TypeReader {

    private static final Int2ObjectOpenHashMap<Object2ObjectOpenHashMap<String, ComponentDefinitions>> namedComponents = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<List<ComponentDefinitions>> unnamedComponents = new Int2ObjectOpenHashMap<>();

    private static final Object2ObjectOpenHashMap<String, Object2ObjectOpenHashMap<String, ComponentDefinitions>> namedInterfaces = new Object2ObjectOpenHashMap<>();

    private static final Map<String, String> HOOKS = ImmutableMap.<String, String>builder()
            .put("onLoadListener", "onload")
            .put("onMouseOverListener", "onmouseover")
            .put("onMouseLeaveListener", "onmouseleave")
            .put("onTargetLeaveListener", "onttargetleave")
            .put("onTargetEnterListener", "onttargetenter")
            .put("onVarTransmitListener", "onvartransmit")
            .put("onInvTransmitListener", "oninvtransmit")
            .put("onStatTransmitListener", "onstattransmit")
            .put("onTimerListener", "ontimer")
            .put("onOpListener", "onop")
            .put("onMouseRepeatListener", "onmouserepeat")
            .put("onClickListener", "onclick")
            .put("onClickRepeatListener", "onclickrepeat")
            .put("onReleaseListener", "onrelease")
            .put("onHoldListener", "onhold")
            .put("onDragListener", "ondrag")
            .put("onDragCompleteListener", "ondragcomplete")
            .put("onScrollWheelListener", "onscrollwheel")
            .build();

    @Override
    public ArrayList<Definitions> read(final Toml toml) throws NoSuchFieldException, IllegalAccessException, CloneNotSupportedException {
        val defs = new ArrayList<Definitions>();
        val id = toml.getLong("id", -1L).intValue();
        val name = toml.getString("name", "");
        if (toml.contains("id")) {
            val interfaceComponents = new ArrayList<ComponentDefinitions>();
            val subTomls = getComponents(toml);

            val groupComponent = getComponent(subTomls.get(0), id);
            groupComponent.setInterfaceId(id);
            interfaceComponents.add(groupComponent);

            var componentId = 1;
            for (val t : subTomls.subList(1, subTomls.size())) {
                val component = getComponent(t, id);
                component.setComponentId(componentId++);
                interfaceComponents.add(component);
            }

            for (val component : interfaceComponents) {
                applyHooks(component);
                if (!name.isEmpty()) {
                    namedInterfaces.computeIfAbsent(name, map -> new Object2ObjectOpenHashMap<>()).put(component.getName(), component);
                }
            }

            groupComponent.getChildren().addAll(interfaceComponents.subList(1, interfaceComponents.size()));
            defs.add(groupComponent);
        } else {
            val subTomls = getComponents(toml);
            for (val subToml : subTomls) {
                val component = getComponent(subToml, id);
                if (component == null) {
                    continue;
                }
                applyHooks(component);
                defs.add(component);
                ComponentDefinitions.add(component);
            }
        }
        return defs;
    }

    private int getHighestComponentId(final int interfaceId) {
        val components = new ArrayList<ComponentDefinitions>();
        val unnamedComponents = ComponentReader.unnamedComponents.get(interfaceId);
        if (unnamedComponents != null) {
            components.addAll(unnamedComponents);
        }
        val namedComponents = ComponentReader.namedComponents.get(interfaceId);
        if (namedComponents != null) {
            components.addAll(namedComponents.values());
        }
        components.addAll(ComponentDefinitions.getComponents(interfaceId));
        if (components == null) {
            return -1;
        }
        var componentId = -1;
        for (val component : components) {
            if (component.getInterfaceId() != interfaceId) {
                continue;
            }
            if (componentId < component.getComponentId()) {
                componentId = component.getComponentId();
            }
        }
        return componentId;
    }
    
    private ComponentDefinitions getComponent(final Toml toml, int id) throws CloneNotSupportedException, NoSuchFieldException, IllegalAccessException {
        ComponentDefinitions component;
        val name = toml.getString("name", "");
        if (toml.contains("inherit")) {
            val inherit = toml.getString("inherit", "");
            val split = inherit.split(":");
            val inheritedInterface = split[0];
            val inheritedComponent = split[1];
            val interfaceId = (int) Double.parseDouble(inheritedInterface);
            val componentId = Integer.parseInt(inheritedComponent);
            component = ComponentDefinitions.get(interfaceId, componentId).clone();
        } else {
            component = new ComponentDefinitions();
            component.setIf3(true);
        }
        TypeReader.setFields(component, toml.toMap());

        if (toml.contains("type")) {
            val typeIdentifier = toml.getString("type", "");
            val type = ComponentType.get(typeIdentifier);
            if (type == null) {
                throw new IllegalStateException("Unknown component type: " + typeIdentifier);
            }
            if (type.equals(ComponentType.TEXT)) { //default properties for text components
                component.setColor("ff981f");
                component.setTextShadowed(true);
            }
            component.setType(type.getId());
        }
        
        if (toml.contains("id")) {
            id = toml.getLong("id").intValue();
        }
        if (toml.contains("parentid")) {
            val parentId = toml.getLong("parentid", 0L).intValue();
            component.setParentId(component.getInterfaceId() << 16 | parentId);
        }
        
        if (toml.contains("layer") && id > -1) {
            val layer = toml.getString("layer", "");
            val components = namedComponents.get(id);
            val parentComponent = components.get(layer);
            if (parentComponent == null) {
                throw new RuntimeException("Parent component: " + layer + " doesn't exist for interface " + id);
            }
            component.setInterfaceId(parentComponent.getInterfaceId());
            component.setComponentId(getHighestComponentId(parentComponent.getInterfaceId()) + 1);
            component.setParentId(parentComponent.getInterfaceId() << 16 | parentComponent.getComponentId());
        } else if (toml.contains("generatecomponentid") && toml.getBoolean("generatecomponentid")) {
            component.setInterfaceId(id);
            component.setComponentId(getHighestComponentId(id) + 1);
            component.setParentId(component.getParentId());
        }

        if (toml.contains("color")) {
            val color = toml.getString("color", "");
            component.setColor(color);
        }

        if (toml.contains("shadowcolor")) {
            val color = toml.getString("shadowcolor", "");
            component.setShadowColor(color);
        }

        for (val property : TypeProperty.values) {
            val identifier = property.getIdentifier();
            if (!toml.contains(identifier)) {
                continue;
            }
            if (property.toString().startsWith("OP_")) {
                val index = Integer.parseInt(identifier.substring(2)) - 1;
                component.setOption(index, toml.getString(identifier, ""));
            }
        }
        if (component.getHooks() == null) {
            component.setHooks(new HashMap<>());
        }
        for (val hook : HOOKS.values()) {
            if (toml.contains(hook)) {
                val list = (ArrayList<Object>) toml.getList(hook);
                val arguments = list.toArray();
                component.getHooks().put(hook, arguments);
            }
        }
        if (id > -1) {
            if (name.isEmpty()) {
                unnamedComponents.computeIfAbsent(id, list -> new ArrayList<>()).add(component);
            } else {
                namedComponents.computeIfAbsent(id, map -> new Object2ObjectOpenHashMap<>()).put(name, component);
            }
        }
        return component;
    }

    private ArrayList<Toml> getComponents(final Toml toml) {
        val components = new ArrayList<Toml>();
        for (val entry : toml.entrySet()) {
            if (entry.getKey().equals("id") || entry.getKey().equals("name")) {
                continue;
            }
            val value = entry.getValue();
            if (value instanceof Toml) {
                components.add((Toml) value);
            } else {
                components.addAll((ArrayList<Toml>) value);
            }
        }
        return components;
    }

    private void applyHooks(final ComponentDefinitions component) throws IllegalAccessException { //TODO support all hooks
        val hooks = component.getHooks();
        if (hooks == null || hooks.isEmpty()) {
            return;
        }
        val components = namedComponents.get(component.getInterfaceId());
        val clazz = component.getClass();
        for (val field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            if (HOOKS.containsKey(field.getName())) {
                val identifier = HOOKS.get(field.getName());
                if (!hooks.containsKey(identifier)) {
                    continue;
                }
                val transformed = new ArrayList<Object>();
                val arguments = hooks.get(identifier);
                for (var arg : arguments) {
                    if (arg instanceof String) {
                        val value = (String) arg;
                        if (value.startsWith("component:")) {
                            val componentName = value.split(":")[1];
                            if (componentName.equals("self")) {
                                arg = -2147483645;
                            } else {
                                val referredComponent = components.get(componentName);
                                if (referredComponent != null) {
                                    arg = referredComponent.getInterfaceId() << 16 | referredComponent.getComponentId();
                                }
                            }
                        } else if (value.startsWith("color:")) {
                            val color = value.split(":")[1];
                            arg = Integer.parseInt(color, 16);
                        }
                    } else if (arg instanceof Long) {
                        arg = ((Long) arg).intValue();
                    }
                    transformed.add(arg);
                }
                field.set(component, transformed.toArray());
            }
        }
    }

    @Override
    public String getType() {
        return "component";
    }
}
