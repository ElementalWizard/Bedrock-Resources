package com.alexvr.bedres.setup;

import net.minecraft.world.World;

public interface IProxy {

    void init();

    World getClientWorld();
}
