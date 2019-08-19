package nbtexaminer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


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
    		
    		TextComponentString text;
    		if(itemStack.hasTagCompound()) {
    			if(msgArgs.length == 1) {
    				text = new TextComponentString("Tag(s) found for currently held item: " 
        					+ itemStack.getTagCompound().getKeySet().toString());
    				text.getStyle().setColor(TextFormatting.GREEN);
    			}
    			else if(msgArgs.length == 2) {
	    			NBTTagCompound nbt = itemStack.getTagCompound();
	    			if(nbt.hasKey(msgArgs[1])) {
	    				text = new TextComponentString(msgArgs[1] + ": " 
		    				+ nbt.getTag(msgArgs[1]));
	    				text.getStyle().setColor(TextFormatting.YELLOW);
	    			}
	    			else {
	    				text = new TextComponentString("Tag \"" + msgArgs[1] + "\" not found on currently held item.");
	    				text.getStyle().setColor(TextFormatting.RED);
	    			}
    			}
    			else {
    				text = new TextComponentString("Too many arguments given.");
    				text.getStyle().setColor(TextFormatting.RED);
    			}
    			
    		}
    		else {
    			text = new TextComponentString("No tags found for currently held item.");
    			text.getStyle().setColor(TextFormatting.RED);
    		}
    		Minecraft.getMinecraft().player.sendMessage(text);
    	}
    }
}
