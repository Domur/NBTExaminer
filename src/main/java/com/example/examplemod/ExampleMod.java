package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketCombatEvent.Event;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.Logger;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "1.0";

    private static Logger logger;
    
    private boolean debounce = false;
    //private NonNullList<ItemStack> lastInv = null;
    private int oldInvCount = -1;
    private String lastMsg = "";
    private String trackedItem;
    
    Random rand = new Random();
    
    @SideOnly(Side.CLIENT)
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }
    
    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onChatSend(ClientChatEvent event) {
    	String message = event.getOriginalMessage().toString();
    	if(message.contains("/track")) {
    		event.setCanceled(true);
    		String[] args = message.split(" ");
    		if(args.length <= 1) {
    			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Too few arguments. Enter the name of the item to track. Ex: /track Diamond"));
    			return;
    		}
    		else if(args.length >= 2) {
    			trackedItem = args[1];
    			for(int i = 2; i < args.length; i ++) {
    				trackedItem += " " + args[i];
    			}
    		}
    		else {
    			trackedItem = args[1];
    		}
    		Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Now tracking: " + trackedItem));
    		oldInvCount = getItemCount(trackedItem);
    		checkInv();
    	}
    	else if(message.contains("/checkv")){
    		event.setCanceled(true);
    		checkInv();
    	}
    	else if(message.contains("/pdis")) {
    		event.setCanceled(true);
    		InventoryPlayer inventory = Minecraft.getMinecraft().player.inventory;
    		ItemStack itemStack = inventory.getCurrentItem();
    		Item item = itemStack.getItem();
    		
    		if(itemStack.hasTagCompound()) {
    			//NBTTagCompound nbt = itemStack.getTagCompound().getCompoundTag("ench");
    			Set<String> s = itemStack.getTagCompound().getKeySet();
    			for(String str : s) {
    				Minecraft.getMinecraft().player.sendMessage(new TextComponentString(str));
    			}
    			NBTTagCompound nbt = itemStack.getTagCompound().getCompoundTag("ench");
    			Set<String> s2 = nbt.getKeySet();
    			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("size: " + s2.size()));
    			for(String str : s2) {
    				Minecraft.getMinecraft().player.sendMessage(new TextComponentString(str));
    			}
    			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("WORKED: " + itemStack.getTagCompound().getString("ench")));
    			
    			
    		}
    		else {
    			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("no tag cmpnd"));
    		}
    		
    	}
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onChatReceive(ClientChatReceivedEvent event) {
    	String message = event.getMessage().getUnformattedComponentText();
    	if(message.length() >= 3 && message.substring(2, 3).equals("+")) {
    		if(!message.equals(lastMsg)) {
    			lastMsg = message;
    			checkInv();
    		}
    	}
    }
    
    public void checkInv() {
    	if(!debounce) {
    		debounce = true;
    		int newInvCount = getItemCount(trackedItem);
    		if(newInvCount > oldInvCount) {
    			Minecraft.getMinecraft().player.sendMessage(new TextComponentString(trackedItem + ": " + Integer.toString(oldInvCount) + " --> " + Integer.toString(newInvCount) + " (+" + Integer.toString(newInvCount - oldInvCount) + ")"));
    		}
    		oldInvCount = newInvCount;
    		
    		debounce = false;
    	}
    }
    
    public int getItemCount(String itemName) {
    	int count = 0;
    	NonNullList<ItemStack> items = Minecraft.getMinecraft().player.inventory.mainInventory; 
    	for(ItemStack item : items) {
    		if(item.getDisplayName().substring(2, item.getDisplayName().length()).equalsIgnoreCase(trackedItem)) {
    			count += item.getCount();
    		}
    	}
    	return count;
    }
    


    
    /* @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
    	EntityItem item = event.getItem();
    	Minecraft.getMinecraft().player.sendMessage(new TextComponentString("You picked up: " + item.getItem().getCount() + " " + item.getItem().getDisplayName()));
    }*/
    
    /*@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
    	World world = event.getWorld();
    	TileEntity tile = world.getTileEntity(event.getPos());
    	ItemStack item = event.getState().getBlock().getItem(world, event.getPos(), event.getState());
    	if(item == null) {
    		System.out.println("item name was null");
    		return;
    	}
    	//Item item = tile.getBlockType().getItemDropped(event.getState(), rand, 3);
    	Minecraft.getMinecraft().player.sendMessage(new TextComponentString("You broke: " + item.getCount() + " " + item.getDisplayName()));
    } */
    
    /*@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void fishedItem(TickEvent.ClientTickEvent event) {
    	//NonNullList<ItemStack> inv = Minecraft.getMinecraft().player.inventory.mainInventory;
    	System.out.println("Ticked");
    } */
    
}
