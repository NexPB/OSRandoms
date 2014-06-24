package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Menu;
import org.powerbot.script.rt4.Player;
import pb.osrandoms.core.GraphScript;
import pb.osrandoms.core.RandomContext;

import java.awt.*;
import java.util.concurrent.Callable;

/**
 * TODO:
 * - Ensure cave exit works
 */
public class PinBall extends GraphScript.Action<RandomContext> {
	private static final Tile[] TILES = new Tile[]{new Tile(47, 54, 0), new Tile(49, 57, 0), new Tile(52, 58, 0), new Tile(55, 57, 0), new Tile(57, 54, 0)};

	public PinBall(RandomContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		if (ctx.inventory.selectedItemIndex() != -1) {
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
			Condition.sleep(1200);
			return;
		}

		if (ctx.randomMethods.clickContinue()) {
			Condition.sleep(1600);
			return;
		}

		if (score() >= 10) {
			final GameObject exit = ctx.objects.select().name("Exit", "Cave Exit").nearest().poll();
			if (exit.valid()) {
				if (!exit.inViewport()) {
					ctx.camera.turnTo(exit);
				}
				if (!exit.inViewport()) {
					ctx.camera.pitch(20 + Random.nextInt(5, 20));
				}

				if (exit.interact("Exit")) { // Check this works, could be a "Exit cave, Exit" or something
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !exit.valid();
						}
					}, 200, 20);
					return;
				}

				ctx.movement.step(exit);
			}
		}

		final GameObject pillar = pillar();
		int tries = 1;
		while (tries < 10) {
			final Tile location = pillar.tile();
			final Point point = location.matrix(ctx).point(0.5, 0.5, 200);
			if (ctx.input.move(point.x + Random.nextInt(tries, tries * 2) - tries * 2, point.y + Random.nextInt(tries, tries * 2) - tries * 2)) {
				Condition.sleep(100);
				if (ctx.menu.indexOf(Menu.filter("Tag")) > -1 && ctx.menu.click(Menu.filter("Tag"))) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return pillar() != pillar;
						}
					}, 200, 15);
					return;
				}
			}
			tries++;
		}
	}

	private GameObject pillar() {
		return ctx.objects.select().at(tile()).name("Pinball post").poll();
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
