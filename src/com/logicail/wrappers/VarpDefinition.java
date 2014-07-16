package com.logicail.wrappers;

import com.logicail.wrappers.loaders.ScriptDefinitionLoader;
import com.sk.datastream.Stream;
import org.powerbot.script.rt4.ClientContext;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 13:31
 */
public class VarpDefinition extends Definition {
	public static final int[] MASKS;

	static {
		MASKS = new int[32];
		int x = 2;
		for (int i = 0; i < 32; i++) {
			MASKS[i] = x - 1;
			x += x;
		}
	}

	public int configId = -1;
	public int lowerBitIndex = -1;
	public int upperBitIndex = -1;
	public int mask = -1;

	public VarpDefinition(ScriptDefinitionLoader loader, int id) {
		super(loader, id);
	}

	@Override
	public String toString() {
		return "ScriptDef " + id + " => ctx.varpbits.varpbit(" + configId + ", " + lowerBitIndex + ", 0x" + Integer.toHexString(mask) + ")";
	}

	@Override
	protected void decode(Stream stream, int opcode) {
		if (opcode == 1) {
			configId = stream.getUShort();
			lowerBitIndex = stream.getUByte();
			upperBitIndex = stream.getUByte();
			mask = MASKS[upperBitIndex - lowerBitIndex];
		} else {
			throw new IllegalArgumentException("Unknown opcode " + opcode);
		}
	}

	public int execute(ClientContext ctx) {
		return ctx.varpbits.varpbit(configId) >> lowerBitIndex & mask;
	}
}
