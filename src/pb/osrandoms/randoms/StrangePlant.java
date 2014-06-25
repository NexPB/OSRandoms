package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.Npc;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * TODO:
 * - Make sure false animations are right
 */
public class StrangePlant extends OSRandom {
	private final int[] FALSE_ANIMATIONS = {348, 350};
	private final String PLANT_NAME = "Strange plant";

	public StrangePlant(RandomContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		final Npc plant = getPlant();
		status("Pick strange plant.");
		target.set(plant);
		if (plant.inViewport() && plant.interact("Pick", plant.name())) {
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !getPlant().valid();
				}
			}, 250, 10);
		} else {
			status("Walk to strange plant.");
			ctx.movement.step(plant);
		}
	}

	public Npc getPlant() {
		return ctx.randomMethods.getNpc(new Filter<Npc>() {
			@Override
			public boolean accept(Npc npc) {
				return Arrays.binarySearch(FALSE_ANIMATIONS, npc.animation()) > -1 &&
						npc.interacting().equals(ctx.players.local());
			}
		}, PLANT_NAME);
	}

	@Override
	public boolean valid() {
		return getPlant().valid();
	}
}
