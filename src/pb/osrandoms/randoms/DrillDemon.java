package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Npc;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.concurrent.Callable;

/**
 * Need to test if it picks the correct mat, Exercise.id might be in the reverse order
 * Check it doesn't matter which mat you click on (tile1 or tile2)
 */
public class DrillDemon extends OSRandom {
	private static final int SETTING_ID = 531;
	private Exercise currentExercise = null;

	public DrillDemon(RandomContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		ctx.camera.angle('N');
		ctx.camera.pitch(true);

		if (ctx.players.local().animation() == -1) {
			final Npc sergeant = ctx.npcs.poll();
			if (currentExercise != null) {
				final GameObject mat = matForExercise(currentExercise);
				target.set(mat);
				if (mat.inViewport()) {
					if (mat.click("Use", "Exercise mat")) {
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.players.local().animation() != -1;
							}
						}, 100, 50);
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.players.local().animation() == -1;
							}
						}, 100, 80);
						Condition.wait(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return ctx.randomMethods.queryContinue();
							}
						}, 100, 20);
						currentExercise = null;
					}
				} else {
					ctx.movement.step(mat);
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return mat.inViewport();
						}
					}, 200, 15);
				}
			} else if (ctx.randomMethods.queryContinue()) {
				final Component component = ctx.widgets.widget(519).component(1);
				if (component.visible()) {
					currentExercise = Exercise.getExercise(component.text());
					status("Exercise: " + (currentExercise != null ? currentExercise.getName() : "null"));
				}
				ctx.randomMethods.clickContinue();
			} else if (sergeant.inViewport()) {
				if (sergeant.click("Talk-to")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.randomMethods.queryContinue();
						}
					}, 200, 15);
				}
			} else {
				ctx.camera.turnTo(sergeant);
				if (!sergeant.inViewport()) {
					ctx.movement.step(sergeant);
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return sergeant.inViewport();
						}
					}, 200, 15);
				}
			}
		}
	}

	private GameObject matForExercise(Exercise exercise) {
		final int varpbit = ctx.varpbits.varpbit(SETTING_ID);
		for (Mat mat : Mat.values()) {
			if ((varpbit >> mat.shift & 0x7) == exercise.id) {
				return mat.object(ctx);
			}
		}
		return ctx.objects.nil();
	}

	@Override
	public boolean valid() {
		return ctx.npcs.select().name("Sergeant Damien").within(10).poll().valid();
	}

	private enum Exercise {
		JUMPING_JACKS(1, "Star jumps"),
		PUSH_UPS(2, "Push ups"),
		SIT_UPS(3, "Sit ups"),
		JOGGING(4, "Jog");

		final int id;
		final String name;

		Exercise(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Exercise getExercise(String text) {
			for (Exercise exercise : Exercise.values()) {
				if (text.toLowerCase().contains(exercise.getName().toLowerCase())) {
					return exercise;
				}
			}
			return null;
		}
	}

	private enum Mat {
		ONE(0, new Tile(3160, 4819), new Tile(3160, 4820)),
		TWO(3, new Tile(3162, 4819), new Tile(3162, 4820)),
		THREE(6, new Tile(3164, 4819), new Tile(3164, 4820)),
		FOUR(9, new Tile(3166, 4819), new Tile(3166, 4820));
		private final int shift;
		private final Tile tile1;
		private final Tile tile2;

		Mat(int shift, Tile tile1, Tile tile2) {
			this.shift = shift;
			this.tile1 = tile1;
			this.tile2 = tile2;
		}

		public GameObject object(ClientContext ctx) {
			return ctx.objects.select().select(new Filter<GameObject>() {
				@Override
				public boolean accept(GameObject gameObject) {
					if (gameObject.type() != GameObject.Type.FLOOR_DECORATION) {
						return false;
					}
					final Tile tile = gameObject.tile();
					return tile.equals(tile1) || tile.equals(tile2);
				}
			}).shuffle().poll();
		}
	}
}
