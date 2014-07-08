package pb.osrandoms;

import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import pb.osrandoms.core.GraphScript;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;
import pb.osrandoms.randoms.*;

import java.awt.*;
import java.util.Arrays;

@Script.Manifest(
		name = "Randoms",
		description = "Solves randoms to bot 24/7.",
		properties = "topic=-1;client=4"
)
public class MasterRandoms extends GraphScript<RandomContext> implements PaintListener {
	@Override
	public void start() {
		chain.addAll(Arrays.asList(new Molly(ctx), new Maze(ctx), new QuizMaster(ctx), new StrangePlant(ctx), 
				new StrangeBox(ctx), new ExpRewardClaimer(ctx), new TalkToRandoms(ctx), new Pinball(ctx), 
				new SandwichLady(ctx), new Mime(ctx), new DrillDemon(ctx), new Certer(ctx), new Frogs(ctx),
				new FrogCave(ctx)));
	}

	@Override
	public void repaint(Graphics g) {
		g.setFont(new Font("Tahoma", 0, 14));
		final OSRandom action = (OSRandom) this.current.get();
		if (action != null) {
			action.repaint(g);
			g.setColor(Color.WHITE);
			g.drawString(action.status(), 10, 50);
		}
	}

}
