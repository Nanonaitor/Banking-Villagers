package com.nanonaitor.banking.client;
import com.nanonaitor.banking.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.*;
public final class BoothScreen extends GuiContainer {
 private final BoothMenu menu;
 public BoothScreen(BoothMenu m){super(m);menu=m;xSize=176;ySize=248;}
 @Override public void initGui(){super.initGui();for(int i=0;i<2;i++)buttonList.add(new GuiButton(i,guiLeft+117,guiTop+39+i*57,51,20,"Buy"));buttonList.add(new GuiButton(2,guiLeft+117,guiTop+127,45,20,"Call"));}
 @Override protected void actionPerformed(GuiButton b){mc.playerController.sendEnchantPacket(menu.windowId,b.id);}
 @Override protected void drawGuiContainerBackgroundLayer(float p,int x,int y){Panel.frame(this,guiLeft,guiTop,ySize);for(net.minecraft.inventory.Slot s:menu.inventorySlots)Panel.slot(this,guiLeft+s.xPos,guiTop+s.yPos);for(int i=0;i<2;i++){int yy=guiTop+41+i*57;Panel.slot(this,guiLeft+10,yy);Panel.slot(this,guiLeft+64,yy);itemRender.renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(menu.data[4])),guiLeft+10,yy);itemRender.renderItemAndEffectIntoGUI(new ItemStack(i==0?BankingVillagers.SPACE:BankingVillagers.STACK),guiLeft+64,yy);}}
 @Override protected void drawGuiContainerForegroundLayer(int x,int y){fontRenderer.drawString("Bank Booth",8,7,0x404040);for(int i=0;i<2;i++){int yy=25+i*57;fontRenderer.drawString(i==0?"Bank Space":"Double Stacking",10,yy,0x404040);fontRenderer.drawString(""+menu.data[i+2],30,yy+21,0x404040);}fontRenderer.drawString("Call Banker",10,133,0x404040);fontRenderer.drawString("Inventory",8,154,0x404040);}
 @Override public void drawScreen(int x,int y,float p){drawDefaultBackground();for(int i=0;i<2;i++){buttonList.get(i).displayString=menu.data[i]>0?"Owned":"Buy";buttonList.get(i).enabled=menu.data[i]==0&&menu.data[5]>=menu.data[i+2];}buttonList.get(2).enabled=menu.data[6]>0&&menu.data[7]==0;super.drawScreen(x,y,p);renderHoveredToolTip(x,y);}
}
