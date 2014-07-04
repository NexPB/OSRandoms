package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.concurrent.Callable;

public class Pinball extends OSRandom {
	private static final Tile[] TILES = new Tile[]{new Tile(47, 54, 0), new Tile(49, 57, 0), new Tile(52, 58, 0), new Tile(55, 57, 0), new Tile(57, 54, 0)};
	private static final int[] POST_BOUNDS = {-60, 60, -60, 60, 0, 800};

	public Pinball(RandomContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		if (ctx.inventory.selectedItemIndex() != -1) {
			status("Deselect item.");
			target.set(ctx.inventory.selectedItem());
			ctx.menu.click(Menu.filter("Cancel"));
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.inventory.selectedItemIndex() == -1;
				}
			}, 200, 10);
		}

		final Player local = ctx.players.local();
		if (local.inMotion() || local.animation() != -1) {
			status("Wait until player idle.");
			Condition.sleep(1200);
			return;
		}

		if (ctx.randomMethods.clickContinue()) {
			status("Click continue.");
			target.set(ctx.randomMethods.getContinue());
			Condition.sleep(1600);
			return;
		}

		final int scoreBefore = score();
		if (scoreBefore >= 10) {
			status("Exit cave.");
			final GameObject exit = ctx.objects.select().name("Cave Exit").nearest().poll();
			target.set(exit);
			if (exit.valid()) {
				if (!exit.inViewport()) {
					ctx.camera.turnTo(exit);
				}
				if (!exit.inViewport()) {
					ctx.camera.pitch(20 + Random.nextInt(5, 20));
				}

				status("Interact with exit.");
				if (exit.interact("Exit")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !exit.valid();
						}
					}, 200, 20);
					return;
				}

				ctx.randomMethods.walkTileOnScreen(exit);
			}
			return;
		}

		final GameObject post = post();
		target.set(post);
		if (post.interact("Tag")) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return scoreBefore != score();
				}
			}, 200, 15);
		}
	}

	private GameObject post() {
		return ctx.objects.select().at(tile()).name("Pinball post").each(Interactive.doSetBounds(POST_BOUNDS)).poll();
	}

	private int score() {
		final Component component = ctx.randomMethods.getComponentByText("Score: ");
		if (component.valid()) {
			return Integer.parseInt(component.text().substring(component.text().indexOf("Score: ") + 7));
		}
		return -1;
	}

	private Tile tile() {
		final int index = ctx.varpbits.varpbit(727) >> 1 & 0xf;
		final Tile mapOffset = ctx.game.mapOffset();
		return TILES[index].derive(mapOffset.x(), mapOffset.y());
	}

	@Override
	public boolean valid() {
		return !ctx.npcs.select().name("Flippa").isEmpty();
	}
}
