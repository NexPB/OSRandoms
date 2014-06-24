package pb.osrandoms;

import java.util.Arrays;

import org.powerbot.script.Script;

import pb.osrandoms.core.GraphScript;
import pb.osrandoms.core.RandomContext;
import pb.osrandoms.randoms.ExpRewardClaimer;
import pb.osrandoms.randoms.StrangePlant;
import pb.osrandoms.randoms.TalkToRandoms;

@Script.Manifest(
        name = "Randoms",
        description = "Sloves randoms to bot 24/7.",
        properties = "topic=-1;client=4"
)
public class MasterRandoms extends GraphScript<RandomContext> {

    public MasterRandoms() {
        chain.addAll(Arrays.asList(new StrangePlant(ctx), new ExpRewardClaimer(ctx), new TalkToRandoms(ctx)));
    }

}
