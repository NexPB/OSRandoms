package com.logicail.wrappers;

import com.logicail.accessors.DefinitionManager;
import com.sk.datastream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/07/2014
 * Time: 18:00
 */
public class NpcDefinition extends Definition {
	public String name = "null";
	public int combatLevel = -1;
	public String[] actions = new String[5];
	public int idleAnimation = -1;
	public int[] modelIds;
	public int[] headModels;
	public int scriptId = -1;
	public int configId = -1;
	public int[] childrenIds;
	private short[] recolorTarget;
	private boolean visible = false;
	private int walkAnimation = -1;
	private int scaleY = 128;
	private int turn90CCWAnimation = -1;
	private int turn180Animation = -1;
	private boolean clickable = true;
	private int degreesToTurn = 32;
	private short[] recolorOriginal;
	private boolean drawMiniMapDot = true;
	private short[] unknown41a;
	private int lightModifier = 0;
	private int scaleXZ = 128;
	private int turn90CWAnimation = -1;
	private int boundDim = -1;
	private short[] unknown41b;
	private int headIcon = -1;
	private int shadowModifier = 0;

	public NpcDefinition(int id, Stream stream) {
		super(id);
		decode(stream);
	}

	@Override
	protected void decode(Stream s, int opcode) {
		if (1 == opcode) {
			int count = s.getUByte();
			this.modelIds = new int[count];
			for (int k = 0; k < count; k++) {
				modelIds[k] = s.getUShort();
			}
		} else if (opcode == 2) {
			this.name = s.getString();
		} else if (opcode == 12) {
			this.boundDim = s.getUByte();
		} else if (13 == opcode) {
			this.idleAnimation = s.getUShort();
		} else if (opcode == 14) {
			this.walkAnimation = s.getUShort();
		} else if (15 == opcode) {
			s.getUShort();
		} else if (16 == opcode) {
			s.getUShort();
		} else if (opcode == 17) {
			this.walkAnimation = s.getUShort();
			this.turn180Animation = s.getUShort();
			this.turn90CWAnimation = s.getUShort();
			this.turn90CCWAnimation = s.getUShort();
		} else if (opcode >= 30 && opcode < 35) {
			this.actions[opcode - 30] = s.getString();
			if (this.actions[opcode - 30].equalsIgnoreCase("Hidden")) {
				this.actions[opcode - 30] = null;
			}
		} else if (40 == opcode) {
			int length = s.getUByte();
			this.recolorOriginal = new short[length];
			this.recolorTarget = new short[length];
			for (int i = 0; i < length; i++) {
				recolorOriginal[i] = (short) s.getUShort();
				recolorTarget[i] = (short) s.getUShort();
			}
		} else if (opcode == 41) {
			int length = s.getUByte();
			this.unknown41a = new short[length];
			this.unknown41b = new short[length];
			for (int i = 0; i < length; i++) {
				unknown41a[i] = (short) s.getUShort();
				unknown41b[i] = (short) s.getUShort();
			}
		} else if (60 == opcode) {
			int count = s.getUByte();
			this.headModels = new int[count];
			for (int i = 0; i < count; i++) {
				headModels[i] = s.getUShort();
			}
		} else if (93 == opcode) {
			this.drawMiniMapDot = false;
		} else if (95 == opcode) {
			this.combatLevel = s.getUShort();
		} else if (97 == opcode) {
			this.scaleXZ = s.getUShort();
		} else if (opcode == 98) {
			this.scaleY = s.getUShort();
		} else if (opcode == 99) {
			this.visible = true;
		} else if (opcode == 100) {
			this.lightModifier = (int) s.getByte();
		} else if (opcode == 101) {
			this.shadowModifier = (int) s.getByte();
		} else if (102 == opcode) {
			this.headIcon = s.getUShort();
		} else if (103 == opcode) {
			this.degreesToTurn = s.getUShort();
		} else if (106 == opcode) {
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
		} else if (opcode == 107) {
			this.clickable = false;
		}
	}

	public NpcDefinition child(DefinitionManager manager) {
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
			return manager.getLoader(NpcDefinition.class).get(childrenIds[index]);
		}
		return null;
	}
}
