package com.github.ustc_zzzz.fmltutor.common;

import com.github.ustc_zzzz.fmltutor.achievement.AchievementLoader;
import com.github.ustc_zzzz.fmltutor.block.BlockLoader;
import com.github.ustc_zzzz.fmltutor.client.KeyLoader;
import com.github.ustc_zzzz.fmltutor.enchantment.EnchantmentLoader;
import com.github.ustc_zzzz.fmltutor.entity.EntityGoldenChicken;
import com.github.ustc_zzzz.fmltutor.item.ItemLoader;
import com.github.ustc_zzzz.fmltutor.potion.PotionLoader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.Item;

public class EventLoader {
	 public static final EventBus EVENT_BUS = new EventBus();
	 public EventLoader()
	    {
	        MinecraftForge.EVENT_BUS.register(this);
	        EventLoader.EVENT_BUS.register(this);
	    }
	 @Cancelable
	    public static class PlayerRightClickGrassBlockEvent extends net.minecraftforge.event.entity.player.PlayerEvent
	    {
	        public final BlockPos pos;
	        public final World world;

	        public PlayerRightClickGrassBlockEvent(EntityPlayer player, BlockPos pos, World world)
	        {
	            super(player);
	            this.pos = pos;
	            this.world = world;
	        }
	    }
	   @SubscribeEvent
	    public void onPlayerClickGrassBlock(PlayerRightClickGrassBlockEvent event)
	    {
	        if (!event.world.isRemote)
	        {
	        	ItemStack heldItem = event.entityPlayer.getHeldItem();
	        	if (ItemLoader.redstoneApple.equals(heldItem.getItem())) {
	            BlockPos pos = event.pos;
	            EntityLiving entityLiving=new EntitySheep(event.world);
	            entityLiving.setPositionAndUpdate(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
	            event.world.spawnEntityInWorld(entityLiving);
	        	 }
	        	if (ItemLoader.goldenEgg.equals(heldItem.getItem()))
	            {
	                EntityLiving entityLiving = new EntityGoldenChicken(event.world);
	                BlockPos pos = event.pos;
	                entityLiving.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	                --heldItem.stackSize;
	                event.world.spawnEntityInWorld(entityLiving);
	                return;
	            }
	        	BlockPos pos=event.pos;
	        	Entity tnt=new EntityTNTPrimed(event.world,pos.getX(),pos.getY(),pos.getZ(),null);
	        	event.world.spawnEntityInWorld(tnt);
	        	 event.entityPlayer.triggerAchievement(AchievementLoader.explosionFromGrassBlock);
	        }
	    }
	   @SubscribeEvent
	    public void onEntityInteract(EntityInteractEvent event)
	    {
	        EntityPlayer player = event.entityPlayer;
	        if (player.isServerWorld() && event.target instanceof EntityPig)
	        {
	            EntityPig pig = (EntityPig) event.target;
	            ItemStack stack = player.getCurrentEquippedItem();
	            if (stack != null && (stack.getItem() == Items.wheat || stack.getItem() == Items.wheat_seeds))
	            {
	                player.attackEntityFrom((new DamageSource("byPig")).setDifficultyScaled().setExplosion(), 8.0F);
	                player.worldObj.createExplosion(pig, pig.posX, pig.posY, pig.posZ, 2.0F, false);
	                pig.setDead();
	            }
	        }
	    }
	   @SubscribeEvent
	    public void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event)
	    {
	        if (!event.world.isRemote && event.harvester != null)
	        {
	            ItemStack itemStack = event.harvester.getHeldItem();
	            if (EnchantmentHelper.getEnchantmentLevel(EnchantmentLoader.fireBurn.effectId, itemStack) > 0
	                    && itemStack.getItem() != Items.shears)
	            {
	                for (int i = 0; i < event.drops.size(); ++i)
	                {
	                    ItemStack stack = event.drops.get(i);
	                    ItemStack newStack = FurnaceRecipes.instance().getSmeltingResult(stack);
	                    if (newStack != null)
	                    {
	                        newStack = newStack.copy();
	                        newStack.stackSize = stack.stackSize;
	                        event.drops.set(i, newStack);
	                    }
	                    else if (stack != null)
	                    {
	                        Block block = Block.getBlockFromItem(stack.getItem());
	                        boolean b = (block == null);
	                        if (!b && (block.isFlammable(event.world, event.pos, EnumFacing.DOWN)
	                                || block.isFlammable(event.world, event.pos, EnumFacing.EAST)
	                                || block.isFlammable(event.world, event.pos, EnumFacing.NORTH)
	                                || block.isFlammable(event.world, event.pos, EnumFacing.SOUTH)
	                                || block.isFlammable(event.world, event.pos, EnumFacing.UP)
	                                || block.isFlammable(event.world, event.pos, EnumFacing.WEST)))
	                        {
	                            event.drops.remove(i);
	                        }
	                    }
	                }
	            }
	        }
	    }
	   @SideOnly(Side.CLIENT)
	    @SubscribeEvent
	    public void onKeyInput(InputEvent.KeyInputEvent event)
	    {
	        if (KeyLoader.showTime.isPressed())
	        {
	            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
	            World world = Minecraft.getMinecraft().theWorld;
	            player.addChatMessage(new ChatComponentTranslation("chat.fmltutor.time", world.getTotalWorldTime()));
	        }
	    }
	   @SubscribeEvent
	    public void onLivingHurt(LivingHurtEvent event)
	    {
	        if (event.source.getDamageType().equals("fall"))
	        {
	            PotionEffect effect = event.entityLiving.getActivePotionEffect(PotionLoader.potionFallProtection);
	            if (effect != null)
	            {
	                if (effect.getAmplifier() == 0)
	                {
	                    event.ammount /= 2;
	                }
	                else
	                {
	                    event.ammount = 0;
	                }
	            }
	        }
	    }
	   @SubscribeEvent
	    public void onLivingDeath(LivingDeathEvent event)
	    {
	        if (event.entityLiving instanceof EntityPlayer && event.source.getDamageType().equals("byPig"))
	        {
	            ((EntityPlayer) event.entityLiving).triggerAchievement(AchievementLoader.worseThanPig);
	        }
	    }

	    @SubscribeEvent
	    public void onPlayerItemCrafted(PlayerEvent.ItemCraftedEvent event)
	    {
	        if (event.crafting.getItem() == Item.getItemFromBlock(BlockLoader.grassBlock))
	        {
	            event.player.triggerAchievement(AchievementLoader.buildGrassBlock);
	        }
	    }
	    @SubscribeEvent
	    public void onFillBucket(FillBucketEvent event)
	    {
	        BlockPos blockpos = event.target.getBlockPos();
	        IBlockState blockState = event.world.getBlockState(blockpos);
	        Fluid fluid = FluidRegistry.lookupFluidForBlock(blockState.getBlock());
	        if (fluid != null && new Integer(0).equals(blockState.getValue(BlockFluidBase.LEVEL)))
	        {
	            FluidStack fluidStack = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
	            event.world.setBlockToAir(blockpos);
	            event.result = FluidContainerRegistry.fillFluidContainer(fluidStack, event.current);
	            event.setResult(Result.ALLOW);
	        }
	    }
}
