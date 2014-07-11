package pb.osrandoms.core;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

import java.util.concurrent.Callable;

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
		return getComponentByText("Click here to continue", "Click to continue");
	}

	public boolean clickContinue() {
		final Component component = getContinue();
		if (component.valid() && component.click()) {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return getComponentByText("Please wait...").valid();
				}
			}, 50, 5)) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !getComponentByText("Please wait...").valid();
					}
				}, 50, 5);
			} else {
				Condition.sleep(250);
			}
			return true;
		}
		return false;
	}

	public boolean queryContinue() {
		final Component component = getContinue();
		return component.valid() && component.visible();
	}

	private Component getComponentByText(Component component, String... needle) {
		final String text = component.text().toLowerCase();
		for (String s : needle) {
			if (text.contains(s.toLowerCase())) {
				return component;
			}
		}

		for (Component child : component.components()) {
			if (child.valid()) {
				final Component search = getComponentByText(child, needle);
				if (search.valid() && search.visible()) {
					return search;
				}
			}
		}

		return null;
	}

	public Component getComponentByText(String... needle) {
		for (Widget widget : ctx.widgets.array()) {
			for (Component component : widget.components()) {
				if (component.valid()) {
					final Component search = getComponentByText(component, needle);
					if (search != null && search.valid() && search.visible()) {
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
				final Tile halfWayTile = new Tile((tile.x() + loc.x()) / 2, (tile.y() + loc.y()) / 2);
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
