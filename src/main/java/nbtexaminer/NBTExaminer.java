package nbtexaminer;

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

@Mod(modid = NBTExaminer.MODID, name = NBTExaminer.NAME, version = NBTExaminer.VERSION)
public class NBTExaminer
{
    public static final String MODID = "nbtexaminer";
    public static final String NAME = "NBT Examiner";
    public static final String VERSION = "1.0";
    
    public static final String mainCommand = "/nbtex";
    
    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onChatSend(ClientChatEvent event) {
    	String message = event.getOriginalMessage().toString();
    	String[] msgArgs = message.split(" ");
    	if(message.contains(mainCommand)) {
    		event.setCanceled(true);
    		InventoryPlayer inventory = Minecraft.getMinecraft().player.inventory;
    		ItemStack itemStack = inventory.getCurrentItem();
    		Item item = itemStack.getItem();
    		
    		if(itemStack.hasTagCompound()) {
    			if(msgArgs.length == 1) {
    				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Tags found for currently held item: " 
    					+ itemStack.getTagCompound().getKeySet().toString()));
    			}
    			else if(msgArgs.length == 2) {
	    			NBTTagCompound nbt = itemStack.getTagCompound();
	    			if(nbt.hasKey(msgArgs[1])) {
		    			Minecraft.getMinecraft().player.sendMessage(new TextComponentString(msgArgs[1] + ": " 
		    				+ nbt.getTag(msgArgs[1])));
	    			}
	    			else {
	    				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Tag not found on currently held item."));
	    			}
    			}
    			else {
    				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Too many arguments given."));
    			}
    			
    		}
    		else {
    			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("No tags found for currently held item."));
    		}
    		
    	}
    }
}
