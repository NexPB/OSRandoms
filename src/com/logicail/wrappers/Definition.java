package com.logicail.wrappers;

import com.logicail.wrappers.loaders.WrapperLoader;
import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 13:41
 */
public abstract class Definition extends StreamedWrapper {
	public final int id;

	public Definition(WrapperLoader<?> loader, int id) {
		super(loader, id);
		this.id = id;
	}

	public void decode(Stream stream) {
		while (true) {
			int opcode = stream.getUByte();
			if (opcode != 0) {
				this.decode(stream, opcode);
			} else {
				return;
			}
		}
	}

	protected abstract void decode(Stream stream, int opcode);
}
