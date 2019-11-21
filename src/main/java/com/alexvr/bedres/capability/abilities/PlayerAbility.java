package com.alexvr.bedres.capability.abilities;

import com.alexvr.bedres.Config;
import com.alexvr.bedres.blocks.tiles.EnderianRitualPedestalTile;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class PlayerAbility implements  IPlayerAbility{

    private String axe = Config.DEF_AXE.get(),pick= Config.DEF_PICK.get(),shovel= Config.DEF_SHOVEL.get(),sword= Config.DEF_SWORD.get(),hoe= Config.DEF_HOE.get(),name = "no",result="";
    private int ritualTimer =1,totalRitual=1;
    private double gravilty = 0,jump=Config.DEF_JUMP.get(),speed =Config.DEF_SPEED.get();
    private boolean ritual = false,checking = false;
    private ArrayList<EnderianRitualPedestalTile> pedestals;
    private double FOV;
    private Vec3d lookingAt;

    @Override
    public String getNAme() {
        return name;
    }

    @Override
    public String getAxe() {
        return axe;
    }

    @Override
    public String getPick() {
        return pick;
    }

    @Override
    public String getShovel() {
        return shovel;
    }

    @Override
    public String getSword() {
        return sword;
    }

    @Override
    public String getHoe() {
        return hoe;
    }

    @Override
    public double getMiningSpeedBoost() {
        return speed;
    }

    @Override
    public int getRitualTimer() {
        return ritualTimer;
    }

    @Override
    public int getRitualTotalTimer() {
        return totalRitual;
    }

    @Override
    public ArrayList<EnderianRitualPedestalTile> getListOfPedestals() {
        return pedestals;
    }

    @Override
    public String getRitualCraftingResult() {
        return result;
    }

    @Override
    public double getJumpBoost() {
        return jump;
    }

    @Override
    public double getGravityMultiplier() {
        return gravilty;
    }

    @Override
    public boolean getInRitual() {
        return ritual;
    }

    @Override
    public boolean getChecking() {
        return checking;
    }

    @Override
    public double getFOV() {
        return FOV;
    }

    @Override
    public Vec3d getlookPos() {
        return lookingAt;
    }

    @Override
    public void setAxe(String name) {
        axe=name;
    }

    @Override
    public void setPick(String name) {
        pick=name;

    }

    @Override
    public void setShovel(String name) {
        shovel=name;

    }

    @Override
    public void setSword(String name) {
        sword=name;

    }

    @Override
    public void setHoe(String name) {
        hoe=name;

    }

    @Override
    public void setname(String name) {
        this.name=name;
    }

    @Override
    public void setMiningSpeedBoost(double amount) {
        speed=amount;

    }

    @Override
    public void setJumpBoost(double amount) {
        jump=amount;

    }

    @Override
    public void setGRavityMultiplier(double amount) {
        gravilty=amount;
    }

    @Override
    public void addMiningSpeed(double amount) {
        speed+=amount;
    }

    @Override
    public void addJump(double amount) {
        jump+=amount;
    }

    @Override
    public void addGrav(double amount) {
        gravilty+=amount;
    }

    @Override
    public void flipRitual() {
        ritual=!ritual;
    }

    @Override
    public void setRitualTimer(int amount) {
        ritualTimer = amount;
    }

    @Override
    public void setRitualTotalTimer(int amount) {
        totalRitual = amount;
    }

    @Override
    public void incrementRitualTimer() {
        ritualTimer++;
    }

    @Override
    public void flipChecking() {
        checking=!checking;
    }

    @Override
    public void setRitualPedestals(ArrayList<EnderianRitualPedestalTile> pedestals) {
        this.pedestals = pedestals;
    }

    @Override
    public void setRitualCraftingResult(String result) {
        this.result = result;
    }

    @Override
    public void setFOV(double FOV) {
        this.FOV = FOV;
    }

    @Override
    public void setLookPos(Vec3d vec3d) {
        lookingAt = vec3d;
    }

    @Override
    public void addLookPos(double x, double y, double z) {
        lookingAt.add(x,y,z);
    }

    public void clear(){
        axe = "no";
        pick= "no";
        shovel= "no";
        sword= "no";
        hoe= "no";
        name = "no";
        result="";
        speed =0;
        ritualTimer =1;
        totalRitual=1;
        gravilty = 0;
        jump=0;
        ritual = false;
        checking = false;
    }
}
