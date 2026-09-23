package com.nanonaitor.banking;

import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkHooks;

public final class BoothBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF=BlockStateProperties.DOUBLE_BLOCK_HALF;
    public BoothBlock(){super(Properties.copy(Blocks.OAK_PLANKS).strength(2.5F).noOcclusion());registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(HALF,DoubleBlockHalf.LOWER));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b){b.add(FACING,HALF);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext c){return c.getClickedPos().getY()<c.getLevel().getMaxBuildHeight()-1&&c.getLevel().getBlockState(c.getClickedPos().above()).canBeReplaced(c)?defaultBlockState().setValue(FACING,c.getHorizontalDirection().getOpposite()):null;}
    @Override public void setPlacedBy(Level l,BlockPos p,BlockState s,LivingEntity e,ItemStack stack){l.setBlock(p.above(),s.setValue(HALF,DoubleBlockHalf.UPPER),3);}
    @Override public BlockState updateShape(BlockState s,Direction d,BlockState other,LevelAccessor l,BlockPos p,BlockPos op){
        Direction required=s.getValue(HALF)==DoubleBlockHalf.LOWER?Direction.UP:Direction.DOWN;
        if(d==required&&(!other.is(this)||other.getValue(HALF)==s.getValue(HALF)))return Blocks.AIR.defaultBlockState();return super.updateShape(s,d,other,l,p,op);
    }
    @Override public void playerWillDestroy(Level l,BlockPos p,BlockState s,Player player){
        if(!l.isClientSide&&s.getValue(HALF)==DoubleBlockHalf.UPPER){BlockPos below=p.below();if(l.getBlockState(below).is(this)){if(!player.isCreative())dropResources(l.getBlockState(below),l,below);l.setBlock(below,Blocks.AIR.defaultBlockState(),35);}}
        super.playerWillDestroy(l,p,s,player);
    }
    @Override public VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){return s.getValue(HALF)==DoubleBlockHalf.LOWER?Block.box(0,0,0,16,16,16):Block.box(0,0,4,16,16,12);}
    @Override public InteractionResult use(BlockState s,Level l,BlockPos p,Player player,InteractionHand h,BlockHitResult hit){
        if(player instanceof ServerPlayer sp){BlockPos base=s.getValue(HALF)==DoubleBlockHalf.UPPER?p.below():p;NetworkHooks.openScreen(sp,new SimpleMenuProvider((id,inv,pl)->new BoothMenu(id,inv,base),Component.translatable("container.banking_villagers.shop")),buf->buf.writeBlockPos(base));}
        return InteractionResult.sidedSuccess(l.isClientSide);
    }
}
