package com.nanonaitor.banking;
import java.util.*;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
public final class VillageBank extends StructureVillagePieces.Village {
 private boolean spawned;
 public VillageBank(){}
 private VillageBank(StructureVillagePieces.Start start,int type,StructureBoundingBox b,EnumFacing f){super(start,type);boundingBox=b;setCoordBaseMode(f);}
 public static void register(){MapGenStructureIO.registerStructureComponent(VillageBank.class,"BankingVillage");if(BankConfig.villageWeight<=0)return;VillagerRegistry.instance().registerVillageCreationHandler(new VillagerRegistry.IVillageCreationHandler(){
  public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random r,int size){return new StructureVillagePieces.PieceWeight(VillageBank.class,BankConfig.villageWeight,1);}
  public Class<?> getComponentClass(){return VillageBank.class;}
  public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight w,StructureVillagePieces.Start start,List<StructureComponent> list,Random r,int x,int y,int z,EnumFacing f,int type){StructureBoundingBox b=StructureBoundingBox.getComponentToAddBoundingBox(x,y,z,0,0,0,7,6,7,f);return canVillageGoDeeper(b)&&StructureComponent.findIntersecting(list,b)==null?new VillageBank(start,type,b,f):null;}
 });}
 @Override protected void writeStructureToNBT(NBTTagCompound t){super.writeStructureToNBT(t);t.setBoolean("BankerSpawned",spawned);}
 @Override protected void readStructureFromNBT(NBTTagCompound t,TemplateManager m){super.readStructureFromNBT(t,m);spawned=t.getBoolean("BankerSpawned");}
 @Override public boolean addComponentParts(World w,Random r,StructureBoundingBox bounds){if(averageGroundLvl<0){averageGroundLvl=getAverageGroundLevel(w,bounds);if(averageGroundLvl<0)return true;boundingBox.offset(0,averageGroundLvl-boundingBox.minY,0);}
  fillWithBlocks(w,bounds,0,0,0,6,0,6,Blocks.STONEBRICK.getDefaultState(),Blocks.STONEBRICK.getDefaultState(),false);
  fillWithBlocks(w,bounds,0,1,0,6,4,6,Blocks.PLANKS.getDefaultState(),Blocks.AIR.getDefaultState(),false);
  fillWithAir(w,bounds,1,1,1,5,4,5);
  fillWithBlocks(w,bounds,0,5,0,6,5,6,Blocks.PLANKS.getDefaultState(),Blocks.PLANKS.getDefaultState(),false);
  setBlockState(w,Blocks.AIR.getDefaultState(),3,1,0,bounds);setBlockState(w,Blocks.AIR.getDefaultState(),3,2,0,bounds);
  for(int x=1;x<=5;x+=4)setBlockState(w,Blocks.GLASS.getDefaultState(),x,2,0,bounds);
  setBlockState(w,Blocks.TORCH.getDefaultState(),1,1,1,bounds);
  setBlockState(w,BankingVillagers.BOOTH.getDefaultState(),3,1,3,bounds);setBlockState(w,BankingVillagers.BOOTH.getDefaultState().withProperty(BoothBlock.UPPER,true),3,2,3,bounds);
  BlockPos at=new BlockPos(getXWithOffset(3,4),getYWithOffset(1),getZWithOffset(3,4));
  if(!spawned&&bounds.isVecInside(at)){spawned=true;BankerEntity b=new BankerEntity(w);b.setPosition(at.getX()+.5,at.getY(),at.getZ()+.5);b.booth=new BlockPos(getXWithOffset(3,3),getYWithOffset(1),getZWithOffset(3,3));w.spawnEntity(b);}return true;
 }
}
