package pb.osrandoms.randoms;

import pb.osrandoms.core.RandomContext;
import pb.osrandoms.core.OSRandom;

import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Widget;

import java.util.concurrent.Callable;

/**
 * User: k0na
 * Date: 09-07-2014
 * Time: 20:49
 */
//TODO set bounds for cage door, ensure all model ids are constant
@OSRandom.RandomManifest(name = "Pillory")
public class Pillory extends OSRandom {
	
	private int pitch = -1;
	private final Widget PARENT = ctx.widgets.widget(189);
	private final Component LOCK = PARENT.component(2);

	private static final Tile[] CAGES = {
			new Tile(2608, 3105), new Tile(2606, 3105), new Tile(2604, 3105),
			new Tile(3226, 3407), new Tile(3228, 3407), new Tile(3230, 3407),
			new Tile(2685, 3489), new Tile(2683, 3489), new Tile(2681, 3489)
	};
  
	private enum Key {
		TRIANGLE(13393, 11032),
		DIAMOND(13394, 13395),
		SQUARE(13390, 4141),
		CIRCLE(13382, 13396);

		private final int modelLockId, keyId;

		Key(int modelLockId, int keyId) {
			this.modelLockId = modelLockId;
			this.keyId = keyId;
		}

		public int getModelLock() {
			return modelLockId;
		}

		public int getKey() {
			return keyId;
		}

		public static Key getKey(int lock) {
			for (Key key : Key.values()) {
				if (key.getModelLock() == lock) {
					return key;
				}
			}
			return null;
		}

	}

	public Pillory(RandomContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		if (LOCK.visible()) {
			status("Attempting to unlock locks");
			Key key = Key.getKey(LOCK.modelId());
			if (key != null) {
				for (int i = 3; i < 6; i++) {
					Component component = PARENT.component(i);
					final int previous = getState();
					if (previous == 2 && pitch != -1) {
						ctx.camera.pitch(pitch);
					}
					if (component.modelId() == key.getKey() && component.click("Ok")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return getState() != previous;
							}
						}, 300, 5);
					}
				}
			}
		} else {
			GameObject cage = ctx.objects.select().name("Cage").nearest().poll();
			if (cage.valid() && cage.inViewport()) {
				status("Opening lock interface");
				if (pitch == -1)
					pitch = ctx.camera.pitch();

				ctx.camera.pitch(false);
				target.set(cage);
				if (cage.interact("Unlock")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return LOCK.visible();
						}
					}, 200, 20);
				}
			}
		}
	}

	public int getState() {
		return ctx.varpbits.varpbit(531);
	}

	@Override
	public boolean valid() {
		for (Tile tile : CAGES) {
			if (ctx.players.local().tile().equals(tile)) {
				return true;
			}
		}
		return false;
	}

}
