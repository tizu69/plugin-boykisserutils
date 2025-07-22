package dev.tizu.boykisserutils.util;

import java.util.List;
import java.util.Map;

public class TimeUnits {
	// how many ticks is 1 [x]?
	private static final Map<String, Integer> MAP = Map.ofEntries(
			Map.entry("t", 1),
			Map.entry("s", 20),
			Map.entry("m", 1200),
			Map.entry("h", 72000));

	/**
	 * Parses a string into a tick count.
	 * 
	 * @param s The string to parse, of format [num][unit][num][unit]...
	 * @returns The parsed duration in ticks.
	 * @exception IllegalStateException If the string is invalid.
	 */
	public static int parse(String s) {
		int ticks = 0;
		int charpos = 0;
		while (charpos < s.length()) {
			int start = charpos;
			while (charpos < s.length() && Character.isDigit(s.charAt(charpos)))
				charpos++;
			if (start == charpos)
				throw new IllegalStateException("Expected number at position " + (start + 1));
			int num = Integer.parseInt(s.substring(start, charpos));

			int unitStart = charpos;
			while (charpos < s.length() && Character.isLetter(s.charAt(charpos)))
				charpos++;
			if (unitStart == charpos)
				throw new IllegalStateException("Expected unit at position " + (unitStart + 1));
			String unit = s.substring(unitStart, charpos);

			Integer multiplier = MAP.get(unit);
			if (multiplier == null)
				throw new IllegalStateException("Invalid unit: '" + unit +
						"'. Expected one of: " + String.join(", ", MAP.keySet()));

			ticks += num * multiplier;
		}
		return ticks;
	}

	/**
	 * Unparses a tick count into a string, using the largest full units possible,
	 * in the form [num][unit][num][unit]...
	 */
	public static String unparse(int ticks) {
		if (ticks <= 0)
			return ticks + "t";

		StringBuilder result = new StringBuilder();
		int remaining = ticks;
		List<Map.Entry<String, Integer>> units = MAP.entrySet().stream()
				.sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
				.toList();
		for (Map.Entry<String, Integer> entry : units) {
			String unit = entry.getKey();
			int unitTicks = entry.getValue();
			int count = remaining / unitTicks;
			if (count > 0) {
				result.append(count).append(unit);
				remaining %= unitTicks;
			}
		}
		return result.toString();
	}

}
