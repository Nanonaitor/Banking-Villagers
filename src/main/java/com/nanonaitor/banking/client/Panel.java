package com.nanonaitor.banking.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
final class Panel {
 static void frame(Gui g,int x,int y,int h){GlStateManager.color(1,1,1,1);Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("minecraft","textures/gui/container/generic_54.png"));g.drawTexturedModalRect(x,y,0,0,176,17);for(int r=17;r<h-7;r++){g.drawTexturedModalRect(x,y+r,0,17,7,1);Gui.drawRect(x+7,y+r,x+169,y+r+1,0xffc6c6c6);g.drawTexturedModalRect(x+169,y+r,169,17,7,1);}g.drawTexturedModalRect(x,y+h-7,0,215,176,7);}
 static void slot(Gui g,int x,int y){Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("minecraft","textures/gui/container/generic_54.png"));GlStateManager.color(1,1,1,1);g.drawTexturedModalRect(x-1,y-1,7,17,18,18);}
}
