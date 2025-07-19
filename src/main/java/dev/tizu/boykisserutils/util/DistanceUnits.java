package dev.tizu.boykisserutils.util;

import java.util.Map;
import java.util.regex.Pattern;

public class DistanceUnits {
	private static final Map<String, Float> MAP = Map.ofEntries(
			// in-game
			Map.entry("px", 1f / 16.0f),
			Map.entry("steve", 1.8f),
			Map.entry("b", 1.0f),
			// real-world
			Map.entry("cm", 0.01f),
			Map.entry("m", 1.0f),
			Map.entry("km", 1000.0f),
			Map.entry("in", 0.0254f),
			Map.entry("inch", 0.0254f),
			Map.entry("ft", 0.3048f),
			Map.entry("foot", 0.3048f),
			Map.entry("yd", 0.9144f),
			Map.entry("yard", 0.9144f),
			Map.entry("mi", 1609.34f),
			// people
			Map.entry("nico", 1.65f),
			Map.entry("tizu", 1.87f),
			// joke
			Map.entry("lns", 0.3f),
			Map.entry("catgirl", 1.6f));
	private static final Pattern REGEX = Pattern.compile(
			"^(-?\\d*\\.?\\d+)?\s*([%a-zA-Z]+)?$");

	/**
	 * Parses a string into a float.
	 *
	 * @param s                The string to parse.
	 * @param fallbackUnit     The unit to use if the unit is not specified.
	 * @param metersPerPercent How many meters are in 1%?
	 * @returns The parsed size in meters.
	 * @exception IllegalStateException If the string is invalid.
	 * @credit ElNico56 for original function.
	 */
	public static float parse(String s, String fallbackUnit, float metersPerPercent) {
		var m = REGEX.matcher(s);
		if (!m.matches())
			throw new IllegalStateException("Invalid input, expected [<num>][<unit>]");

		var num = 1.0f;
		if (m.group(1) != null)
			num = Float.parseFloat(m.group(1));

		var unit = m.group(2);
		if (unit == null)
			unit = fallbackUnit;

		if (unit.equals("%"))
			return roundTo2(num * metersPerPercent);
		if (MAP.get(unit) == null)
			throw new IllegalStateException("Invalid unit, expected one of: " +
					String.join(", ", MAP.keySet()));

		return roundTo2(num * MAP.get(unit));
	}

	private static float roundTo2(float f) {
		return (float) Math.round(f * 100) / 100;
	}
}
