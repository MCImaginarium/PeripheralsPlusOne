package com.austinv11.peripheralsplusplus.tiles;

import com.austinv11.peripheralsplusplus.PeripheralsPlusPlus;
import com.austinv11.peripheralsplusplus.mount.DynamicMount;
import com.austinv11.peripheralsplusplus.network.AudioPacket;
import com.austinv11.peripheralsplusplus.reference.Config;
import com.austinv11.peripheralsplusplus.utils.ReflectionHelper;
import com.austinv11.peripheralsplusplus.utils.TranslateUtils;
import com.gtranslate.Language;
import cpw.mods.fml.common.network.NetworkRegistry;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySpeaker extends TileEntity implements IPeripheral {

	public static String publicName = "speaker";
	private String name = "tileEntitySpeaker";
	private ITurtleAccess turtle;
	private int id;
	private static final int TICKER_INTERVAL = 20;
	private int ticker = 0;
	private int subticker = 0;

	public TileEntitySpeaker() {
		super();
	}

	public TileEntitySpeaker(ITurtleAccess turtle) {
		this.turtle = turtle;
	}

	public String getName() {
		return name;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
	}

	@Override
	public void updateEntity() {
		if (turtle != null) {
			this.setWorldObj(turtle.getWorld());
			this.xCoord = turtle.getPosition().posX;
			this.yCoord = turtle.getPosition().posY;
			this.zCoord = turtle.getPosition().posZ;
		}
		if (worldObj != null)
			id = worldObj.provider.dimensionId;
		if (subticker > 0)
			subticker--;
		if (subticker == 0 && ticker != 0)
			ticker = 0;
	}

	@Override
	public String getType() {
		return publicName;
	}

	@Override
	public String[] getMethodNames() {
		return new String[]{"speak"/*text, [range, [lang]]*/};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if (!Config.enableSpeaker)
			throw new LuaException("Speakers have been disabled");
		if (method == 0) {
//			try {
			if (!(arguments.length > 0) || !(arguments[0] instanceof String))
				throw new LuaException("Bad argument #1 (expected string)");
			if (arguments.length > 1 && !(arguments[1] instanceof Double))
				throw new LuaException("Bad argument #2 (expected string)");
			if (arguments.length > 2 && !(arguments[2] instanceof String))
				throw new LuaException("Bad argument #3 (expected boolean)");
			String lang = null;
			if (arguments.length > 2)
				if (TranslateUtils.isPrefix((String) arguments[2]))
					lang = (String) arguments[2];
				else {
					try {
						lang = ReflectionHelper.getLangFromWord((String) arguments[2]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (lang == null || lang.length() < 2)
						throw new LuaException("Language "+(String)arguments[2]+" is unknown");
				}
			else
				lang = Language.ENGLISH;//TranslateUtils.detectLangPrefix((String) arguments[0]);
			if (ticker == Config.speakerSayRate)
				throw new LuaException("Please try again later, you are sending messages too often");
			double range;
			if (Config.sayRange < 0)
				range = Double.MAX_VALUE;
			else
				range = Config.sayRange;
			if (arguments.length > 1)
				range = (Double) arguments[1];
			PeripheralsPlusPlus.NETWORK.sendToAllAround(new AudioPacket(lang, (String) arguments[0]), new NetworkRegistry.TargetPoint(id, xCoord, yCoord, zCoord, range));
			subticker = TICKER_INTERVAL;
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		return new Object[0];
	}

	@Override
	public void attach(IComputerAccess computer) {
		computer.mount(DynamicMount.DIRECTORY, new DynamicMount(this));
	}

	@Override
	public void detach(IComputerAccess computer) {
		computer.unmount(DynamicMount.DIRECTORY);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (this == other);
	}
}