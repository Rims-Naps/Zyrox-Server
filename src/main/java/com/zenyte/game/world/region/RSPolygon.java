package com.zenyte.game.world.region;

import com.zenyte.game.world.entity.Location;
import com.zenyte.utils.efficientarea.Polygon;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

/**
 * @author Kris | 16. mai 2018 : 01:33:57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class RSPolygon {

	public RSPolygon(final int[][] points) {
		this(points, 0, 1, 2, 3);
	}
	
	public RSPolygon(final int[][] points, final int... planes) {
		final int[] xPoints = new int[points.length];
		final int[] yPoints = new int[points.length];
		
		int index = 0;
		for (final int[] area : points) {
			xPoints[index] = area[0];
			yPoints[index] = area[1];
			index++;
		}
		this.points = points;
		polygon = new EfficientPolygon(xPoints, yPoints, points.length);
		this.planes.addElements(0, planes);
	}
	
	@Getter private final Polygon polygon;
	@Getter private final IntArrayList planes = new IntArrayList(4);
	@Getter private final int[][] points;
	
	public final boolean contains(final int x, final int y) {
		return polygon.contains(x, y);
	}
	
	public final boolean contains(final int x, final int y, final int plane) {
		return planes.contains(plane) && polygon.contains(x, y);
	}
	
	public final boolean contains(final Location location) {
		//System.err.println(points.length);
		return planes.contains(location.getPlane()) && polygon.contains(location.getX(), location.getY());
	}
	
	public void addPoint(final int x, final int y) {
		polygon.addPoint(x, y);
	}
	
}
