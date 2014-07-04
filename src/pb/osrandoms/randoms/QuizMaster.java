package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * User: k0na
 * Date: 04-07-2014
 * Time: 04:33
 */
public class QuizMaster extends OSRandom {

    private static final int[] FISH = { 6189, 6190 };
    private static final int[] WEAPONS = { 6191, 6192 };
    private static final int[] ARMOUR = { 6193, 6194 };
    private static final int[] FARMING = { 6195, 6196 };
    private static final int[] JEWELRY = { 6197, 6198 };
    
    private static final int[][] ANSWERS = { FISH, JEWELRY, WEAPONS, ARMOUR, FARMING };

    public QuizMaster(RandomContext ctx) {
        super(ctx);
    }

    @Override
    public boolean valid() {
        return ctx.npcs.select().name("Quiz Master").nearest().poll().valid();
    }

    @Override
    public void run() {
        final Component prize = randomMethods.getComponentByText("1000 Coins");
        if (prize.valid()) {
            status("Selecting price, 1000 coins");
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
            if (answer != null && answer.valid()) {
                status("Clicking answer, " + answer.toString());
                target.set(answer);
                if (answer.click())
                    Condition.sleep(1500);
            }
        }
    }

    private Component getAnswer() {
        Widget p = randomMethods.getComponentByText("Pick the odd one out.").widget();
        for (int[] i : ANSWERS) {
            if (getCount(i, p) == 1) {
                for (Component c : p.components()) {
                    for (int j : i) {
                        if (j == c.modelId()) {
                            return c;
                        }
                    }
                }
            }
        }
        return null;
    }

    private int getCount(int[] t, Widget p) {
        int c = 0;
        if (p.componentCount() > 0) {
            for (Component component : p.components()) {
                for (int i : t) {
                    if (i == component.modelId()) {
                        c++;
                    }
                }
            }
        }
        return c;
    }

}
