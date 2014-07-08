package pb.osrandoms.core;

import com.logicail.accessors.DefinitionManager;
import org.powerbot.script.rt4.ClientContext;

import java.io.FileNotFoundException;

public class RandomContext extends ClientContext {

	public Methods randomMethods;
	public DefinitionManager definitions;

	public RandomContext(ClientContext ctx) {
		super(ctx);
		this.randomMethods = new Methods(this);
		try {
			this.definitions = new DefinitionManager(ctx);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
