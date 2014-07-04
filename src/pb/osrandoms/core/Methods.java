package pb.osrandoms.core;

import org.powerbot.script.Filter;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

public class Methods extends ClientAccessor {
	public Methods(ClientContext arg0) {
		super(arg0);
	}

	public Npc getNpc(final String... name) {
		return getNpc(new Filter<Npc>() {
			@Override
			public boolean accept(Npc npc) {
				return npc.interacting().equals(ctx.players.local());
			}
		}, name);
	}

	public Npc getNpc(final Filter<Npc> filter, final String... name) {
		return ctx.npcs.select().name(name).select(filter).poll();
	}

	public Component getContinue() {
		return getComponentByText("Click here to continue");
	}

	public boolean clickContinue() {
		final Component component = getContinue();
		return component.valid() && component.click();
	}

	public boolean queryContinue() {
		return getContinue().valid();
	}

	private Component getComponentByText(Component component, String needle) {
		if (component.text().contains(needle)) {
			return component;
		}

		for (Component child : component.components()) {
			if (child.valid()) {
				final Component search = getComponentByText(child, needle);
				if (search.valid()) {
					return search;
				}
			}
		}

		return ctx.widgets.widget(0).component(0);
	}

	public Component getComponentByText(String needle) {
		for (Widget widget : ctx.widgets.array()) {
			for (Component component : widget.components()) {
				if (component.valid()) {
					final Component search = getComponentByText(component, needle);
					if (search.valid()) {
						return search;
					}
				}
			}
		}
		return ctx.widgets.widget(0).component(0);
	}
	
	private Tile getTileOnScreen(final Tile tile) {
		try {
			if (tile.matrix(ctx).inViewport()) {
				return tile;
			} else {
				final Tile loc = ctx.players.local().tile();
				final Tile halfWayTile = new Tile((tile.x() + loc.y()) / 2, (tile.x() + loc.y()) / 2);
				if (halfWayTile.matrix(ctx).inViewport()) {
					return halfWayTile;
				} else {
					return getTileOnScreen(halfWayTile);
				}
			}
		} catch (final Exception e) {
			return null;
		}
	}
	
	public boolean walkTileOnScreen(Locatable loc) {
		final Tile closest = getTileOnScreen(loc.tile());
		return closest != null ? closest.matrix(ctx).interact("walk") : false;
	}

}
