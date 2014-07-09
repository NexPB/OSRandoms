package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.Widget;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.concurrent.Callable;

/**
 * TODO:
 * - Make sure modelids are right!
 */
@OSRandom.RandomManifest(name = "Sandwich Lady")
public class SandwichLady extends OSRandom {
	public SandwichLady(RandomContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		final Component component = getComponent();
		if (component.valid()) {
			status("Interface open sandwich lady.");
			for (final Answer answer : Answer.values()) {
				final Component food;
				if ((food = answer.getAnswer(ctx, component.widget())).valid()) {
					status("Found answer food = " + answer);
					target.set(food);
					food.click();
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !getComponent().valid();
						}
					}, 250, 10);
				}
			}
		} else {
			final Component cont = ctx.randomMethods.getContinue();
			if (cont.valid()) {
				status("Talk to sandwich lady.");
				target.set(cont);
				cont.click();
				Condition.sleep(1600);
			} else {
				final Npc lady = ctx.randomMethods.getNpc("Sandwich Lady");
				status("Interact with sandwich lady.");
				target.set(lady);
				if (lady.inViewport() && lady.interact("Talk-to", lady.name())) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return getComponent().valid();
						}
					}, 250, 10);
				} else {
					status("Walk to sandwich lady.");
					ctx.movement.step(lady);
				}
			}
		}
	}

	private Component getComponent() {
		for (Answer answer : Answer.values()) {
			return ctx.randomMethods.getComponentByText(answer.getName());
		}
		return ctx.widgets.widget(0).component(0);
	}

	@Override
	public boolean valid() {
		return ctx.randomMethods.getNpc("Sandwich Lady").valid() || getComponent().valid();
	}

	public enum Answer {
		SQUARE(10731, "square"),
		ROLL(10727, "roll"),
		CHOCOLATE(10728, "chocolate"),
		BAQUETTE(10726, "baquette"),
		TRIANGLE(10732, "triangle"),
		KEBAB(10729, "kebab"),
		PIE(10730, "pie");

		private int modelId;
		private String name;

		Answer(final int modelId, final String name) {
			this.modelId = modelId;
			this.name = name;
		}

		public int getModelId() {
			return modelId;
		}

		public String getName() {
			return name;
		}

		public Component getAnswer(final RandomContext ctx, final Widget widget) {
			for (int i = 1; i < 8; i++) {
				Component component = widget.component(i);
				if (component.valid() && component.modelId() == getModelId()) {
					return component;
				}
			}
			return ctx.widgets.widget(0).component(0);
		}
	}
}
