package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.GameObject;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Logicail
 *         <p/>
 *         May need bounds on appendage
 */
@OSRandom.RandomManifest(name = "Lost and Found")
public class LostAndFound extends OSRandom {
	private static final String APPENDAGE = "Appendage";

	public LostAndFound(RandomContext ctx) {
		super(ctx);
	}

	@Override
	public boolean valid() {
		final GameObject appendage = ctx.objects.select().name(APPENDAGE).within(1).poll();
		return appendage.valid() || ctx.randomMethods.getComponentByText("Abyssal Services apologise").valid();
	}

	private GameObject oddOneOut() {
		HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();
		for (GameObject appendage : ctx.objects.select().name(APPENDAGE).within(1)) {
			final int id = appendage.id();
			if (counter.containsKey(id)) {
				counter.put(id, counter.get(id) + 1);
			} else {
				counter.put(id, 1);
			}
		}

		for (Map.Entry<Integer, Integer> entry : counter.entrySet()) {
			if (entry.getValue() == 1) {
				return ctx.objects.select().id(entry.getValue()).nearest().poll();
			}
		}

		return ctx.objects.nil();
	}

	@Override
	public void run() {
		if (ctx.randomMethods.queryContinue()) {
			status("Click continue.");
			target.set(ctx.randomMethods.getContinue());
			ctx.randomMethods.clickContinue();
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return !ctx.randomMethods.queryContinue();
				}
			}, 200, 10);
			return;
		}

		if (ctx.camera.pitch() < 75) {
			ctx.camera.pitch(Random.nextInt(80, 100));
			return;
		}

		final GameObject appendage = oddOneOut();
		if (appendage.valid()) {
			target.set(appendage);
			status("Interact with appendage.");
			if (appendage.interact("Operate")) { // interact since they are so close together
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !appendage.valid();
					}
				}, 200, 50);
			}
		} else {
			target.set(null);
		}
	}
}
