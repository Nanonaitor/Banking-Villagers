package com.nanonaitor.banking.client;
import com.nanonaitor.banking.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import java.io.IOException;
public final class BankScreen extends GuiContainer {
 private final BankMenu menu;private final String title;private GuiTextField search;private String term="";
 public BankScreen(BankMenu m,EntityPlayer p){super(m);menu=m;title=p.getName()+"'s Bank";xSize=176;ySize=248;}
 @Override public void initGui(){super.initGui();buttonList.add(new GuiButton(0,guiLeft+8,guiTop+17,20,16,"<"));buttonList.add(new GuiButton(1,guiLeft+148,guiTop+17,20,16,">"));search=new GuiTextField(2,fontRenderer,guiLeft+34,guiTop+20,108,12);search.setText(term);}
 @Override protected void actionPerformed(GuiButton b){mc.playerController.sendEnchantPacket(menu.windowId,b.id);}
 @Override protected void drawGuiContainerBackgroundLayer(float p,int x,int y){Panel.frame(this,guiLeft,guiTop,ySize);for(net.minecraft.inventory.Slot s:menu.inventorySlots)Panel.slot(this,guiLeft+s.xPos,guiTop+s.yPos);}
 @Override protected void drawGuiContainerForegroundLayer(int x,int y){fontRenderer.drawString(title,8,5,0x404040);fontRenderer.drawString("Capacity: "+menu.capacity,8,145,0x354839);fontRenderer.drawString((menu.page+1)+" / "+menu.pages,140,145,0x404040);fontRenderer.drawString("Inventory",8,154,0x404040);GlStateManager.pushMatrix();GlStateManager.translate(0,0,350);for(int i=0;i<54;i++)if(menu.counts[i]>1){net.minecraft.inventory.Slot s=menu.inventorySlots.get(i);fontRenderer.drawStringWithShadow(""+menu.counts[i],s.xPos+17-fontRenderer.getStringWidth(""+menu.counts[i]),s.yPos+9,0xffffff);}GlStateManager.popMatrix();}
 @Override public void drawScreen(int x,int y,float p){drawDefaultBackground();super.drawScreen(x,y,p);String q=term.trim().toLowerCase(java.util.Locale.ROOT);GlStateManager.disableDepth();if(!q.isEmpty())for(int i=0;i<54;i++){net.minecraft.inventory.Slot s=menu.inventorySlots.get(i);if(!s.getStack().isEmpty()&&!s.getStack().getDisplayName().toLowerCase(java.util.Locale.ROOT).contains(q))drawRect(guiLeft+s.xPos,guiTop+s.yPos,guiLeft+s.xPos+16,guiTop+s.yPos+16,0xb0888888);}search.drawTextBox();GlStateManager.enableDepth();buttonList.get(0).enabled=menu.page>0;buttonList.get(1).enabled=menu.page+1<menu.pages;renderHoveredToolTip(x,y);}
 @Override protected void keyTyped(char c,int k)throws IOException{if(search.isFocused()&&k!=1){search.textboxKeyTyped(c,k);term=search.getText();return;}super.keyTyped(c,k);}
 @Override protected void mouseClicked(int x,int y,int b)throws IOException{search.mouseClicked(x,y,b);super.mouseClicked(x,y,b);}
}
