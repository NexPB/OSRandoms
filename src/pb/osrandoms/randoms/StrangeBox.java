package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game.Tab;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Widget;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.concurrent.Callable;

/**
 * @author Robert G
 */
public class StrangeBox extends OSRandom {

	private static final int ANSWER_SETTING_ID = 312;
	private static final int STRANGE_BOX_ID = 3062;
	private final Widget PARENT = ctx.widgets.widget(190);

	public StrangeBox(RandomContext ctx) {
		super(ctx);
	}

	private int answerComponentId() {
		switch (ctx.varpbits.varpbit(ANSWER_SETTING_ID) >> 24) {
			case 0:
				return 10;
			case 1:
				return 11;
			case 2:
				return 12;
		}
		return -1;
	}

	@Override
	public void run() {
		final Component comp = PARENT.component(answerComponentId());
		if (comp.visible()) {
			status("Selecting " + comp.text() + ".");
			target.set(comp);
			if (comp.click()) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return answerComponentId() == -1;
					}
				});
			}
		} else {
			final Item box = ctx.inventory.poll();
			target.set(box);
			if (box.click("Open", "Strange box")) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return answerComponentId() > -1;
					}
				});
			} else {
				ctx.game.tab(Tab.INVENTORY);
			}
		}
	}

	@Override
	public boolean valid() {
		return !ctx.inventory.select().id(STRANGE_BOX_ID).isEmpty();
	}

}
