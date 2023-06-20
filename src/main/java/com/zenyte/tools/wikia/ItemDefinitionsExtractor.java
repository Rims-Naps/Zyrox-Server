package com.zenyte.tools.wikia;

import com.google.common.io.Files;
import com.zenyte.Game;
import com.zenyte.game.parser.impl.JSONItemDefinitionsLoader;
import com.zenyte.game.util.Utils;
import mgi.Indice;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.items.JSONItemDefinitions;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ItemDefinitionsExtractor {

	private static final Map<Integer, String> REQUIRED_ITEMS = new HashMap<Integer, String>();
	
	public static final void main(final String...strings) {
    	try {
    		System.out.println("Archiving the existing item definitions.");
    		if (!new File("data/items/archive").exists()) {
				new File("data/items/archive").mkdirs();
			}
			Files.copy(new File("data/items/ItemDefinitions.json"), new File("data/items/archive/ItemDefinitions" + LocalDateTime.now().toString().replaceAll(":", ".") + ".json"));
			System.out.println("Successfully archived the existing item definitions.");
    	} catch (final IOException e1) {
    		System.err.println("Failure to archive existing item definitions! Aborting.");
			e1.printStackTrace();
			return;
		} 
    	Game.load();
    	try {
			new JSONItemDefinitionsLoader().parse();
		} catch (final Throwable e) {
			e.printStackTrace();
			return;
		}
    	
    	for (int i = 0; i < Utils.getIndiceSize(Indice.ITEM_DEFINITIONS); i++) {
    		final ItemDefinitions defs = ItemDefinitions.get(i);
    		if (JSONItemDefinitionsLoader.lookup(i) != null) {
				continue;
			}
    		if (defs.getName().toLowerCase().equals("null")) {
				continue;
			}
    		if (defs.isNoted()) {
				continue;
			}
    		REQUIRED_ITEMS.put(i, defs.getName());
    	}
    	System.err.println("Requesting: " + REQUIRED_ITEMS.size() + " items.");
    	REQUIRED_ITEMS.forEach((k, v) -> {
    		final JSONItemDefinitions defs = new JSONItemDefinitions();
    		defs.setId(k);
    		
    		extract(k, v, defs, ItemDefinitions.get(k).getNotedId());
    		final JSONItemDefinitions def = JSONItemDefinitionsLoader.lookup(k);
    		if (def != null) {
    			if (def.getEquipmentDefinition() != null) {
    				defs.setEquipmentDefinition(def.getEquipmentDefinition());
    			}
    			if (def.getEquipmentType() != null) {
					defs.setEquipmentType(def.getEquipmentType());
				}
    		}
    		JSONItemDefinitionsLoader.DEFINITIONS.put(k, defs);
    	});
    	
    	JSONItemDefinitionsLoader.save();
    	
	}
	public static final void extract(final int id, final String name, final JSONItemDefinitions defs, final int notedId) {
		try {
        	final long startTime = System.currentTimeMillis();
            final String formattedName = name.replaceAll(" ", "_");
            final URL url = new URL("https://oldschool.runescape.wiki/w/" + formattedName);
            Document doc = null;
            try {
                doc = Jsoup.connect(url.toString()).get();
            } catch (final Exception e) {
                return;
            }
            final Element table = doc.select("table").first();
            Elements rows = null;
            try {
                rows = table.select("tr");
            } catch (final Exception e) {
                return;
            }

            final List<Element> mainInfoRows = rows.stream().collect(Collectors.toList());
            defs.parseMainTableData(mainInfoRows);
            final Elements bonusesTable = doc.select("table");
            for (final Element tab : bonusesTable) {
                if (tab.attr("class").equalsIgnoreCase(/*"wikitable smallpadding"*/"rsw-infobox infobox-bonuses")) {
                    final Elements bonuses = tab.select("tr");
                    final List<Element> bonusRows = bonuses.stream().collect(Collectors.toList());
                    defs.parseBonusesTable(bonusRows);

                    final Optional<String> slot = tab.select("[src]").stream().filter(img -> img.attr("alt").contains("slot")).map(img -> img.attr("alt")).findFirst();

                    if (slot.isPresent()) {
                    	defs.parseItemSlot(slot.get());
                    }
                    break;
                }
            }
            
            if (notedId != -1) {
            	final JSONItemDefinitions def = new JSONItemDefinitions();
        		def.setId(notedId);
        		def.setTradable(defs.getTradable());
        		JSONItemDefinitionsLoader.DEFINITIONS.put(notedId, def);
            }
			System.out.println("Name: " + name + ", id: " + id + "\nURL: " + url);

			System.out.println("\nParsed in " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (final IOException e) {
            log.error(Strings.EMPTY, e);
        }
    }
	
}
