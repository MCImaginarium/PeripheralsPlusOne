package com.austinv11.peripheralsplusplus.turtles.peripherals;

import com.austinv11.peripheralsplusplus.reference.Config;
import com.austinv11.peripheralsplusplus.utils.IPlusPlusPeripheral;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class PeripheralCompass implements IPlusPlusPeripheral {

	ITurtleAccess turtle;

	public PeripheralCompass(ITurtleAccess turtle) {
		this.turtle = turtle;
	}

	@Override
	public String getType() {
		return "compass";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] {"getFacing"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		if (method == 0) {
			if (!Config.enableNavigationTurtle)
				throw new LuaException("The compass upgrade has been disabled");
			return new Object[]{turtle.getDirection().getName(), turtle.getDirection().ordinal()};
		}
		return new Object[0];
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (other == this);
	}
}
