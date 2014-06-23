package master.nex;

import master.nex.core.GraphScript;
import master.nex.randoms.StrangePlant;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;

import java.util.Arrays;

@Script.Manifest(
        name = "MasterRandoms",
        description = "Sloves randoms to bot 24/7.",
        properties = "topic=-1;client=4"
)
public class MasterRandoms extends GraphScript<ClientContext> {

    public MasterRandoms() {
        chain.addAll(Arrays.asList(new StrangePlant(ctx)));
    }

}
