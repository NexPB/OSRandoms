package pb.osrandoms;

import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import pb.osrandoms.core.GraphScript;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;
import pb.osrandoms.randoms.ExpRewardClaimer;
import pb.osrandoms.randoms.StrangePlant;
import pb.osrandoms.randoms.TalkToRandoms;

import java.awt.*;
import java.util.Arrays;

@Script.Manifest(
        name = "Randoms",
        description = "Sloves randoms to bot 24/7.",
        properties = "topic=-1;client=4"
)
public class MasterRandoms extends GraphScript<RandomContext> implements PaintListener {

    public MasterRandoms() {
        chain.addAll(Arrays.asList(new StrangePlant(ctx), new ExpRewardClaimer(ctx), new TalkToRandoms(ctx)));
    }

    @Override
    public void repaint(Graphics g) {
        g.setFont(new Font("Tahoma", 0, 14));
        final OSRandom action = (OSRandom) this.current.get();
        if (action != null) {
            if (action instanceof PaintListener) {
                ((PaintListener) action).repaint(g);
            }
            g.setColor(Color.WHITE);
            g.drawString(action.status(), 10, 50);
        }
    }

}
