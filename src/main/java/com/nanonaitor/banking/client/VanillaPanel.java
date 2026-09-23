package com.nanonaitor.banking.client;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
final class VanillaPanel {
    static final ResourceLocation CHEST=new ResourceLocation("minecraft","textures/gui/container/generic_54.png");
    static void frame(GuiGraphics g,int x,int y,int height){
        g.blit(CHEST,x,y,0,0,176,17);
        for(int row=17;row<height-7;row++){
            g.blit(CHEST,x,y+row,0,17,7,1);
            g.fill(x+7,y+row,x+169,y+row+1,0xffc6c6c6);
            g.blit(CHEST,x+169,y+row,169,17,7,1);
        }
        g.blit(CHEST,x,y+height-7,0,215,176,7);
    }
    static void slot(GuiGraphics g,int x,int y){g.blit(CHEST,x-1,y-1,7,17,18,18);}
}
