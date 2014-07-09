package pb.osrandoms.randoms;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.Widget;

import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

/**
 * 
 * @author Robert G
 * 
 *         Tested working as of 08/07/2014
 * 
 */
@OSRandom.RandomManifest(name = "Certer")
public class Certer extends OSRandom {

	private enum Answer {

		FISH(8829),
		RING(8834),
		SWORD(8836),
		SPADE(8837),
		SHIELD(8832),
		AXE(8828),
		SHEARS(8835),
		HELMET(8833);

		private int model;

		private Answer(int model) {
			this.model = model;
		}

		private String getName() {
			return name().substring(0, 1) + name().substring(1).toLowerCase();
		}

	}

	private final String[] npcs = {"Niles", "Giles", "Miles"};
	private final int parent_widget_id = 184;
	private final int item_component_id = 7;
	private final Filter<Npc> npcFilter = new Filter<Npc>() {
		@Override
		public boolean accept(Npc arg0) {
			return arg0.interacting().equals(ctx.players.local())
					|| arg0.overheadMessage().contains(ctx.players.local().name());
		}
	};

	private Npc npc = ctx.npcs.nil();

	public Certer(RandomContext ctx) {
		super(ctx);
	}

	private Answer getAnswer(Component c) {
		for (Answer answer : Answer.values()) {
			if (answer.model == c.modelId()) {
				return answer;
			}
		}
		return null;
	}

	private Component getAnswerComponent(Answer answer) {
		final Widget widg = ctx.widgets.widget(parent_widget_id);
		for (int i = 1; i < widg.components().length; i++) {
			final Component comp = widg.component(i);
			if (comp.text().toLowerCase().contains(answer.getName())) {
				return widg.component(comp.index() + 7);
			}
		}
		return null;
	}

	@Override
	public void run() {
		if (ctx.randomMethods.clickContinue()) {
			Condition.wait(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return !npc.valid();
				}

			});
			return;
		}
		final Component itemComponent = ctx.widgets.widget(parent_widget_id).component(item_component_id);
		if (itemComponent.valid()) {
			final Answer answer = getAnswer(itemComponent);
			if (answer != null) {
				status("[Certer] Selecting " + answer.getName() + ".");
				final Component answerComponent = getAnswerComponent(answer);
				if (answerComponent != null) {
					target.set(answerComponent);
					if (answerComponent.click()) {
						Condition.wait(new Callable<Boolean>() {

							@Override
							public Boolean call() throws Exception {
								return answerComponent.modelId() != answer.model;
							}

						});
					}
				}
			}
			return;
		}
		status("[Certer] Talking to " + npc.name() + ".");
		if (npc.interact("Talk-to")) {
			Condition.wait(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return itemComponent.valid();
				}

			});
		}
	}

	@Override
	public boolean valid() {
		return (npc = ctx.randomMethods.getNpc(npcFilter, npcs)).valid();
	}

}
