package com.alexvr.bedres.capability.abilities;

import com.alexvr.bedres.tiles.EnderianRitualPedestalTile;

import java.util.ArrayList;

public interface IPlayerAbility {


    String getNAme();
    String getAxe();
    String getPick();
    String getShovel();
    String getSword();
    String getHoe();

    int getMiningSpeedBoost();
    int getRitualTimer();
    int getRitualTotalTimer();
    ArrayList<EnderianRitualPedestalTile> getListOfPedestals();
    String getRitualCraftingResult();

    float getJumpBoost();
    float getGravityMultiplier();

    boolean getInRitual();
    boolean getChecking();

    double getFOV();


    void setAxe(String name);
    void setPick(String name);
    void setShovel(String name);
    void setSword(String name);
    void setHoe(String name);
    void setname(String name);

    void setMiningSpeedBoost(int amount);
    void setJumpBoost(float amount);
    void setGRavityMultiplier(float amount);

    void addMiningSpeed(int amount);
    void addJump(float amount);
    void addGrav(float amount);

    void flipRitual();

    void setRitualTimer(int amount);
    void setRitualTotalTimer(int amount);
    void incrementRitualTimer();
    void flipChecking();
    void setRitualPedestals(ArrayList<EnderianRitualPedestalTile> pedestals);
    void setRitualCraftingResult(String result);
    void setFOV(double FOV);

}
