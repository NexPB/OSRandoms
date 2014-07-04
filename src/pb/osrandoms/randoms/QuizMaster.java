package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class QuizMaster extends OSRandom {

    private final int[][] ANSWERS = {
            {6189, 6190},
            {6197, 6198},
            {6191, 6192},
            {6193, 6194},
            {6195, 6196}
    };

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
            status("Select our prize: 1000 Coins.");
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
                status("Clicking answer.");
                target.set(answer);
                if (answer.click())
                    Condition.sleep(1500);
            }
        }
    }

}
