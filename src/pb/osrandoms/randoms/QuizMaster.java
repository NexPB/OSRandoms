package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.Arrays;
import java.util.concurrent.Callable;

@OSRandom.RandomManifest(name = "Quiz Master")
public class QuizMaster extends OSRandom {

	private static final int[] FISH = {6189, 6190};
	private static final int[] WEAPONS = {6191, 6192};
	private static final int[] ARMOUR = {6193, 6194};
	private static final int[] FARMING = {6195, 6196};
	private static final int[] JEWELRY = {6197, 6198};
	private static final int[][] ANSWERS = {FISH, JEWELRY, WEAPONS, ARMOUR, FARMING};

	public QuizMaster(RandomContext ctx) {
		super(ctx);
	}

	private Widget getWidget() {
		return ctx.randomMethods.getComponentByText("Pick the odd one out.").widget();
	}

	private Component getAnswer() {
		final Component[] components = getWidget().components();
		if (components.length > 0) {
			for (int[] arr : ANSWERS) {
				for (Component component : components) {
					if (Arrays.binarySearch(arr, component.modelId()) > -1)
						return component;
				}
			}
		}
		return ctx.widgets.widget(0).component(0);
	}

	@Override
	public boolean valid() {
		return ctx.npcs.select().name("Quiz Master").nearest().poll().valid();
	}

	@Override
	public void run() {
		final Component prize = ctx.randomMethods.getComponentByText("1000 Coins");
		if (prize.valid()) {
			status("Select our prize, 1000 Coins.");
			target.set(prize);
			if (prize.click()) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !ctx.npcs.select().name("Quiz Master").nearest().poll().valid();
					}
				}, 200, 6);
			}
		} else {
			final Component answer = getAnswer();
			if (answer.valid()) {
				status("Clicking answer: " + answer.text().toLowerCase());
				target.set(answer);
				if (answer.click())
					Condition.sleep(1500);
			}
		}
	}

}
