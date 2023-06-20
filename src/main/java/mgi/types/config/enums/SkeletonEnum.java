package mgi.types.config.enums;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

import java.util.Map;

/**
 * @author Kris | 26. juuli 2018 : 22:57:40
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@ToString
public class SkeletonEnum {

	@Getter @Setter private int id;
	@Setter private String keyType;
	@Setter private String valType;
	@Getter @Setter private String defaultString;
	@Getter @Setter private int defaultInt;
	@Getter @Setter private Map<Integer, Object> values;
	
	public static final ImmutableMap<String, Character> REVERSE_TYPE_MAP = ImmutableMap.<String, Character>builder().put("seq", 'A').put("int", 'i')
			.put("boolean", '1').put("string", 's').put("inv", 'v').put("char", 'z').put("namedobj", 'O').put("midi", 'M').put("idkit", 'K')
			.put("obj", 'o').put("npc", 'n').put("coordgrid", 'c').put("stat", 'S').put("model", 'm').put("graphic", 'd').put("struct", 'J')
			.put("fontmetrics", 'f').put("component", 'I').put("chatchar", 'k').put("enum", 'g').put("location", 'l').build();
	
	public final char getKeyType() {
		val c = REVERSE_TYPE_MAP.get(keyType);
		if (c == null) {
			throw new RuntimeException("Unable to find a matching type for " + keyType + ".");
		}
		return c;
	}
	
	public final char getValueType() {
		val c = REVERSE_TYPE_MAP.get(valType);
		if (c == null) {
			throw new RuntimeException("Unable to find a matching type for " + valType + ".");
		}
		return c;
	}
	
}
