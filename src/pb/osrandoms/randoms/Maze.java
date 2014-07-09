package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.*;

/**
 * 
 * @author Timer
 * 
 *         Updated by Robert G 6/7/2014
 *         Needs testing and possibly more work.
 *
 */
@OSRandom.RandomManifest(name = "Maze")
public class Maze extends OSRandom {

	private final class Door {
		private final char direction;
		private final Tile main;
		private final Tile after;

		public Door(int x, int y, final char direction) {
			x = (x + ctx.client().getOffsetX());
			y = (x + ctx.client().getOffsetY());
			this.main = new Tile(x, y, 0);
			this.direction = direction;
			switch (direction) {
				case 'n':
					y += 1;
					break;
				case 's':
					y -= 1;
					break;
				case 'w':
					x -= 1;
					break;
				case 'e':
					x += 1;
					break;
			}
			this.after = new Tile(x, y, 0);
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Door) {
				Door d = (Door) o;
				return this.main.equals(d.main) && this.after.equals(d.after) && this.direction == d.direction;
			}
			return false;
		}

	}

	public final class Vertex {
		public final int x, y, z;
		public Vertex prev;
		public double g, f;
		public boolean special;

		public Vertex(final int x, final int y, final int z) {
			this(x, y, z, false);
		}

		public Vertex(final int x, final int y, final int z, final boolean special) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.special = special;
			g = f = 0;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Vertex) {
				final Vertex n = (Vertex) o;
				return x == n.x && y == n.y && z == n.z;
			}
			return false;
		}

		public Tile get(final int baseX, final int baseY) {
			return new Tile(x + baseX, y + baseY, z);
		}

		@Override
		public int hashCode() {
			return x << 4 | y;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}

	private final Component reward_component = ctx.widgets.widget(209).component(3);

	private static final int WIDGET_CHAT = 1184;
	private static final int WIDGET_CHAT_TEXT = 13;

	private LinkedList<Tile> path = null;
	private LinkedList<Door> allowedDoors = null;

	public Maze(RandomContext ctx) {
		super(ctx);
	}

	private void clean() {
		allowedDoors = null;
		path = null;
	}

	private double dist(final Vertex start, final Vertex end) {
		if (start.x != end.x && start.y != end.y) {
			return 1.41421356;
		} else {
			return 1.0;
		}
	}

	private LinkedList<Tile> findCentre() {
		final Tile start = ctx.players.local().tile();
		final Tile end = getCentre();
		if (start.floor() != end.floor()) {
			return null;
		}
		final Tile base = ctx.game.mapOffset();

		final int curr_plane = start.floor();
		final int base_x = base.x(), base_y = base.y();
		final int curr_x = start.x() - base_x, curr_y = start.y() - base_y;
		int dest_x = end.x() - base_x, dest_y = end.y() - base_y;

		final int plane = ctx.game.floor();
		if (curr_plane != plane) {
			return null;
		}

		final int[][] flags = ctx.client().getCollisionMaps()[plane].getFlags();
		final int offX = ctx.client().getCollisionMaps()[plane].getOffsetX();
		final int offY = ctx.client().getCollisionMaps()[plane].getOffsetY();

		if (flags == null || curr_x < 0 || curr_y < 0 || curr_x >= flags.length || curr_y >= flags.length) {
			return null;
		} else if (dest_x < 0 || dest_y < 0 || dest_x >= flags.length || dest_y >= flags.length) {
			if (dest_x < 0) {
				dest_x = 0;
			} else if (dest_x >= flags.length) {
				dest_x = flags.length - 1;
			}
			if (dest_y < 0) {
				dest_y = 0;
			} else if (dest_y >= flags.length) {
				dest_y = flags.length - 1;
			}
		}

		final HashSet<Vertex> open = new HashSet<Vertex>();
		final HashSet<Vertex> closed = new HashSet<Vertex>();
		Vertex curr = new Vertex(curr_x, curr_y, curr_plane);
		final Vertex dest = new Vertex(dest_x, dest_y, curr_plane);

		curr.f = heuristic(curr, dest);
		open.add(curr);

		while (!open.isEmpty()) {
			curr = lowest_f(open);
			if (curr.equals(dest)) {
				return path(curr, base_x, base_y);
			}
			open.remove(curr);
			closed.add(curr);
			for (final Vertex next : successors(curr, base, offX, offY, flags)) {
				if (!closed.contains(next)) {
					final double t = curr.g + dist(curr, next);
					boolean use_t = false;
					if (!open.contains(next)) {
						open.add(next);
						use_t = true;
					} else if (t < next.g) {
						use_t = true;
					}
					if (use_t) {
						next.prev = curr;
						next.g = t;
						next.f = t + heuristic(next, dest);
					}
				}
			}
		}

		return null;
	}

	private Tile getCentre() {
		return new Tile(71 + ctx.client().getOffsetX(), 31 + ctx.client().getOffsetY(), ctx.game.floor());
	}

	private GameObject getDoor(final Door door) {
		return ctx.objects.select().select(new Filter<GameObject>() {
			public boolean accept(final GameObject location) {
				return location.id() >= 3628 && location.id() <= 3632 && (location.tile().equals(door.main));
			}
		}).poll();
	}

	private Door[] getDoors() {
		return new Door[]{
				new Door(90, 45, 'w'), new Door(61, 53, 's'),
				new Door(63, 43, 's'), new Door(62, 30, 'e'),
				new Door(92, 53, 's'), new Door(69, 55, 's'),
				new Door(68, 47, 's'), new Door(66, 41, 's'),
				new Door(76, 23, 'n'), new Door(64, 28, 'e'),
				new Door(52, 33, 'e'), new Door(90, 16, 'w'),
				new Door(75, 15, 'n'), new Door(69, 17, 'n'),
				new Door(58, 25, 'e'), new Door(73, 53, 's'),
				new Door(96, 11, 'w'), new Door(56, 17, 'e'),
				new Door(86, 30, 'w'), new Door(50, 21, 'e'),
				new Door(76, 41, 's'), new Door(90, 9, 'n'),
				new Door(70, 27, 'n'), new Door(68, 51, 's'),
				new Door(53, 55, 's'), new Door(56, 37, 'e'),
				new Door(71, 21, 'n'), new Door(54, 42, 'e'),
				new Door(81, 55, 's'), new Door(92, 30, 'w'),
				new Door(60, 22, 'e'), new Door(79, 49, 's'),
				new Door(70, 37, 's'), new Door(54, 22, 'e'),
				new Door(58, 40, 'e'), new Door(57, 51, 's'),
				new Door(84, 38, 'w'), new Door(84, 47, 's'),
				new Door(80, 37, 'w'), new Door(65, 15, 'n'),
				new Door(90, 36, 'w'), new Door(50, 43, 'e'),
				new Door(82, 30, 'w'), new Door(63, 9, 'n'),
				new Door(48, 51, 'e'), new Door(72, 7, 'n'),
				new Door(84, 21, 'w'), new Door(70, 31, 'e'),
				new Door(94, 23, 'w')
		};
	}

	private boolean hasDoor(final int x, final int y, final Tile base, final char direction) {
		for (final Door door : allowedDoors) {
			if (door.main.x() == x + base.x() && door.main.y() == y + base.y() && door.direction == direction) {
				return true;
			}
		}
		return false;
	}

	private boolean hasDoor(final int x, final int y, final Tile base, final Vertex source) {
		char dir = 'a';
		if (source.y < y) {
			dir = 's';
		}
		if (source.x < x) {
			dir = 'w';
		} else if (source.y > y) {
			dir = 'n';
		} else if (source.x > x) {
			dir = 'e';
		}
		return hasDoor(x, y, base, dir);
	}

	private double heuristic(final Vertex start, final Vertex end) {
		final double dx = Math.abs(start.x - end.x);
		final double dy = Math.abs(start.y - end.y);
		final double diag = Math.min(dx, dy);
		final double straight = dx + dy;
		return Math.sqrt(2.0) * diag + straight - 2 * diag;
	}

	private Vertex lowest_f(final Set<Vertex> open) {
		Vertex best = null;
		for (final Vertex t : open) {
			if (best == null || t.f < best.f) {
				best = t;
			}
		}
		return best;
	}

	private LinkedList<Tile> path(final Vertex end, final int base_x, final int base_y) {
		final LinkedList<Tile> path = new LinkedList<Tile>();
		Vertex p = end;
		while (p != null) {
			if (p.special || p.equals(end)) {
				path.addFirst(p.get(base_x, base_y));
			}
			p = p.prev;
		}
		return path;
	}

	private List<Vertex> successors(final Vertex t, final Tile base, final int offX, final int offY, final int[][] flags) {
		final LinkedList<Vertex> tiles = new LinkedList<Vertex>();
		final int x = t.x, y = t.y, z = t.z;
		final int f_x = x - offX, f_y = y - offY;
		final int upper = flags.length - 1;
		if (f_y > 0 && ((flags[f_x][f_y - 1] & 0x1280102) == 0 || (flags[f_x][f_y - 1] & 0x1280108) == 0x1000000 ||
				hasDoor(t.x, t.y, base, 'n') || hasDoor(t.x, t.y + 1, base, t))) {
			tiles.add(new Vertex(x, y - 1, z, hasDoor(t.x, t.y, base, 'n') || hasDoor(t.x, t.y + 1, base, t)));
		}
		if (f_x > 0 && ((flags[f_x - 1][f_y] & 0x1280108) == 0 || (flags[f_x - 1][f_y] & 0x1280108) == 0x1000000 ||
				hasDoor(t.x, t.y, base, 'w') || hasDoor(t.x - 1, t.y, base, t))) {
			tiles.add(new Vertex(x - 1, y, z, hasDoor(t.x, t.y, base, 'w') || hasDoor(t.x - 1, t.y, base, t)));
		}
		if (f_y < upper && ((flags[f_x][f_y + 1] & 0x1280120) == 0 || (flags[f_x][f_y + 1] & 0x1280108) == 0x1000000 ||
				hasDoor(t.x, t.y, base, 's') || hasDoor(t.x, t.y - 1, base, t))) {
			tiles.add(new Vertex(x, y + 1, z, hasDoor(t.x, t.y, base, 's') || hasDoor(t.x, t.y - 1, base, t)));
		}
		if (f_x < upper && ((flags[f_x + 1][f_y] & 0x1280180) == 0 || (flags[f_x + 1][f_y] & 0x1280108) == 0x1000000 ||
				hasDoor(t.x, t.y, base, 'e') || hasDoor(t.x + 1, t.y, base, t))) {
			tiles.add(new Vertex(x + 1, y, z, hasDoor(t.x, t.y, base, 'e') || hasDoor(t.x + 1, t.y, base, t)));
		}
		return tiles;
	}

	@Override
	public void run() {
		if (allowedDoors == null) {
			allowedDoors = new LinkedList<Door>(Arrays.asList(getDoors()));
		}

		if (ctx.players.local().inMotion() || ctx.players.local().animation() != -1) {
			Condition.sleep(Random.nextInt(80, 150));
			return;
		}

		if (ctx.players.local().tile().equals(getCentre())) {
			final GameObject shrine = ctx.objects.select().name("strange shrine").poll();
			if (shrine.valid()) {
				status("Touching Strange shrine.");
				target.set(shrine);
				if (ctx.players.local().animation() == -1 && shrine.interact("Touch")) {
					for (int i = 0; i < 3000; i += 20) {
						if (ctx.players.local().animation() != -1) {
							break;
						}
						Condition.sleep(20);
					}
					Condition.sleep(Random.nextInt(500, 700));
				}
			}
			return;
		}

		path = findCentre();
		Door nearestDoor = null;
		System.out.println(path);

		if (path != null) {
			main:
			for (final Tile doorStep : path) {
				for (final Door door : allowedDoors) {
					if ((door.main.equals(doorStep) || door.after.equals(doorStep)) && (door.main.matrix(ctx).reachable() || door.after.matrix(ctx).reachable())) {
						nearestDoor = door;
						break main;
					}
				}
			}

			if (nearestDoor != null) {
				if (!nearestDoor.main.matrix(ctx).inViewport()) {
					if (ctx.movement.step(nearestDoor.main)) {
						Condition.sleep(Random.nextInt(800, 1400));
					}
					return;
				}

				final Component notification = ctx.widgets.widget(WIDGET_CHAT).component(WIDGET_CHAT_TEXT);
				if (notification != null && notification.valid()) {
					final String text = notification.text().toLowerCase().trim();
					if (text.contains("right way")) {
						status("Found unopenable door, removing it from the list.");
						allowedDoors.remove(nearestDoor);
						path = null;
						Condition.sleep(Random.nextInt(1000, 1800));
						return;
					}
				}

				final GameObject door = getDoor(nearestDoor);
				if (door != null && door.inViewport()) {
					target.set(door);
					status("Opening door @ " + door.tile());
					if (door.interact("Open", "Door")) {
						Condition.sleep(Random.nextInt(1800, 3500));
						return;
					}
					ctx.camera.angle(nearestDoor.direction);
					ctx.camera.pitch(true);
				}
				Condition.sleep(Random.nextInt(800, 1400));
				return;
			}
		}

		Condition.sleep(Random.nextInt(1000, 2500));
	}

	@Override
	public boolean valid() {
		if (reward_component.valid() && reward_component.text().toLowerCase().contains("of the reward")) {
			return true;
		} else {
			clean();
			return false;
		}
	}

}
