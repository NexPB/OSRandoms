package pb.osrandoms.core;

import org.powerbot.script.rt4.ClientContext;
import pb.osrandoms.randoms.Methods;

public class RandomContext extends ClientContext {

    public Methods randomMethods;

    public RandomContext(ClientContext ctx) {
        super(ctx);
        this.randomMethods = new Methods(this);
    }

}
