package com.logicail.wrappers;

import com.logicail.accessors.DefinitionManager;
import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 13:49
 */
public class ObjectDefinition extends Definition {
	public String name = "null";
	public int type = 0;
	public int width = 1;
	public int height = 1;
	public String[] actions = new String[5];
	public int[] modelTypes;
	public int[] modelIds;
	public int scriptId = -1;
	public int configId = -1;
	public int[] childrenIds;
	private boolean unwalkable = true;
	private int offsetY = 0;
	private int minimapIcon = -1;
	private int mapSceneId = -1;
	private int blockType = 0;
	private boolean nonFlatShading = false;
	private int constrast = 0;
	private int modelSizeX = 128;
	private int offsetH = 0;
	private int modelSizeY = 128;
	private int brightness = 0;
	private int modelSizeH = 128;
	private boolean isSolid = false;
	private int adjustToTerrain = 0;
	private short[] recolorTarget;
	private int animationId = -1;
	private short[] recolorOriginal;
	private int offsetx = 0;
	private short[] unknown41a;
	private short[] unknown41b;

	public ObjectDefinition(int id, Stream stream) {
		super(id);
		decode(stream);
	}

	@Override
	protected void decode(Stream s, int opcode) {
		if (opcode == 1) {
			int count = s.getUByte();
			if (count > 0) {
				if (this.modelIds != null) {
					s.skip(count * 3);
					return;
				}
				this.modelTypes = new int[count];
				this.modelIds = new int[count];
				for (int i = 0; i < count; i++) {
					this.modelIds[i] = s.getUShort();
					this.modelTypes[i] = s.getUByte();
				}
			}
		} else if (2 == opcode) {
			this.name = s.getString();
		} else if (5 == opcode) {
			int count = s.getUByte();
			if (count > 0) {
				if (this.modelIds == null) {
					s.skip(count * 2);
					return;
				}
				this.modelTypes = null;
				this.modelIds = new int[count];
				for (int i = 0; i < count; i++) {
					this.modelIds[i] = s.getUShort();
				}
			}
		} else if (opcode == 14) {
			this.width = s.getUByte();
		} else if (15 == opcode) {
			this.height = s.getUByte();
		} else if (17 == opcode) {
			this.blockType = 0;
			this.unwalkable = false;
		} else if (opcode == 18) {
			this.unwalkable = false;
		} else if (opcode == 19) {
			this.type = s.getUByte();
		} else if (21 == opcode) {
			this.adjustToTerrain = 0;
		} else if (opcode == 22) {
			this.nonFlatShading = true;
		} else if (23 == opcode) {
		} else if (opcode == 24) {
			this.animationId = s.getBigSmart();
		} else if (27 == opcode) {
			this.blockType = 1;
		} else if (28 == opcode) {
			s.getUByte();
		} else if (opcode == 29) {
			this.brightness = (int) s.getByte();
		} else if (opcode == 39) {
			this.constrast = s.getByte();
		} else if (opcode >= 30 && opcode < 35) {
			this.actions[opcode - 30] = s.getString();
			if (this.actions[opcode - 30].equalsIgnoreCase("Hidden")) {
				this.actions[opcode - 30] = null;
			}
		} else if (opcode == 40) {
			int length = s.getUByte();
			this.recolorOriginal = new short[length];
			this.recolorTarget = new short[length];
			for (int i = 0; i < length; i++) {
				recolorOriginal[i] = (short) s.getUShort();
				recolorTarget[i] = (short) s.getUShort();
			}
		} else if (41 == opcode) {
			int length = s.getUByte();
			this.unknown41a = new short[length];
			this.unknown41b = new short[length];
			for (int i = 0; i < length; i++) {
				unknown41a[i] = (short) s.getUShort();
				unknown41b[i] = (short) s.getUShort();
			}
		} else if (opcode == 60) {
			this.minimapIcon = s.getUShort();
		} else if (62 == opcode) {
		} else if (64 == opcode) {
		} else if (opcode == 65) {
			this.modelSizeX = s.getUShort();
		} else if (opcode == 66) {
			this.modelSizeH = s.getUShort();
		} else if (67 == opcode) {
			this.modelSizeY = s.getUShort();
		} else if (opcode == 68) {
			this.mapSceneId = s.getUShort();
		} else if (69 == opcode) {
			s.getUByte();
		} else if (70 == opcode) {
			this.offsetx = s.getShort();
		} else if (opcode == 71) {
			this.offsetH = s.getShort();
		} else if (72 == opcode) {
			this.offsetY = s.getShort();
		} else if (73 == opcode) {
		} else if (opcode == 74) {
			this.isSolid = true;
		} else if (75 == opcode) {
			s.getUByte();
		} else if (77 == opcode) {
			this.scriptId = s.getUShort();
			if (this.scriptId == 65535) {
				this.scriptId = -1;
			}
			this.configId = s.getUShort();
			if (this.configId == 65535) {
				this.configId = -1;
			}
			int count = s.getUByte();
			this.childrenIds = new int[1 + count];
			for (int i = 0; i <= count; i++) {
				this.childrenIds[i] = s.getUShort();
				if (this.childrenIds[i] == 65535) {
					this.childrenIds[i] = -1;
				}
			}
		} else if (78 == opcode) {
			s.getUShort();
			s.getUByte();
		} else {
			if (opcode == 79) {
				s.skip(5);
				int ci = s.getUByte();
				s.skip(2 * ci);
			} else if (81 == opcode) {
				this.adjustToTerrain = s.getUByte();
			}
		}
	}

	public boolean hasChildren() {
		return childrenIds != null;
	}

	public ObjectDefinition child(DefinitionManager manager) {
		int index = 0;
		if (scriptId == -1) {
			index = configId != -1 ? manager.ctx.varpbits.varpbit(configId) : -1;
		} else {
			final VarpDefinition varpDefinition = manager.getLoader(VarpDefinition.class).get(scriptId);
			if (varpDefinition != null) {
				index = manager.ctx.varpbits.varpbit(varpDefinition.configId) >> varpDefinition.lowerBitIndex & VarpDefinition.MASKS[varpDefinition.upperBitIndex - varpDefinition.lowerBitIndex];
			}
		}
		if (index >= 0 && index < childrenIds.length && childrenIds[index] != -1) {
			return manager.getLoader(ObjectDefinition.class).get(childrenIds[index]);
		}
		return null;
	}
}
