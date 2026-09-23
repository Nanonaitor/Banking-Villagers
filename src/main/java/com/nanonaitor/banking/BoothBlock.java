package com.nanonaitor.banking;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.*;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
public final class BoothBlock extends Block {
 public static final PropertyBool UPPER=PropertyBool.create("upper");
 public BoothBlock(){super(Material.WOOD);setRegistryName(BankingVillagers.ID,"bank_booth");setUnlocalizedName("banking_villagers.bank_booth");setHardness(2.5F);setCreativeTab(net.minecraft.creativetab.CreativeTabs.DECORATIONS);setDefaultState(blockState.getBaseState().withProperty(UPPER,false));}
 @Override protected BlockStateContainer createBlockState(){return new BlockStateContainer(this,UPPER);}
 @Override public IBlockState getStateFromMeta(int m){return getDefaultState().withProperty(UPPER,m==1);}
 @Override public int getMetaFromState(IBlockState s){return s.getValue(UPPER)?1:0;}
 @Override public boolean isOpaqueCube(IBlockState s){return false;} @Override public boolean isFullCube(IBlockState s){return false;}
 @Override public BlockRenderLayer getBlockLayer(){return BlockRenderLayer.CUTOUT;}
 @Override public boolean canPlaceBlockAt(World w,BlockPos p){return super.canPlaceBlockAt(w,p)&&w.isAirBlock(p.up());}
 @Override public void onBlockPlacedBy(World w,BlockPos p,IBlockState s,EntityLivingBase who,ItemStack stack){w.setBlockState(p.up(),getDefaultState().withProperty(UPPER,true),3);}
 @Override public void breakBlock(World w,BlockPos p,IBlockState s){BlockPos other=s.getValue(UPPER)?p.down():p.up();if(w.getBlockState(other).getBlock()==this)w.setBlockToAir(other);super.breakBlock(w,p,s);}
 @Override public net.minecraft.item.Item getItemDropped(IBlockState s,java.util.Random r,int f){return net.minecraft.item.Item.getItemFromBlock(this);}
 @Override public boolean onBlockActivated(World w,BlockPos p,IBlockState s,EntityPlayer player,EnumHand hand,EnumFacing face,float x,float y,float z){BlockPos base=s.getValue(UPPER)?p.down():p;if(!w.isRemote&&hand==EnumHand.MAIN_HAND)player.openGui(BankingVillagers.instance,1,w,base.getX(),base.getY(),base.getZ());return true;}
}
