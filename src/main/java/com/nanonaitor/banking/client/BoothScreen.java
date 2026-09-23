package com.nanonaitor.banking.client;
import com.nanonaitor.banking.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
public final class BoothScreen extends AbstractContainerScreen<BoothMenu> {
    private final Button[] buy=new Button[2];private Button bell;
    public BoothScreen(BoothMenu menu,Inventory inv,Component title){super(menu,inv,title);imageWidth=176;imageHeight=248;inventoryLabelY=154;}
    @Override protected void init(){super.init();for(int i=0;i<2;i++){final int kind=i;buy[i]=addRenderableWidget(Button.builder(Component.literal("Buy"),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,kind)).bounds(leftPos+117,topPos+39+i*57,51,20).build());}
        bell=addRenderableWidget(Button.builder(Component.empty(),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,2)).bounds(leftPos+117,topPos+122,25,22).build());updateButtons();}
    private void updateButtons(){for(int i=0;i<2;i++){buy[i].setMessage(Component.literal(menu.owned(i)?"Owned":"Buy"));buy[i].active=!menu.owned(i)&&menu.affordable(i);}bell.active=menu.bellReady();bell.setTooltip(Tooltip.create(Component.literal(!menu.assigned()?"No Banker Works at this Booth":menu.bellReady()?"Call Booth Banker":"Calling Banker...")));}
    @Override protected void containerTick(){super.containerTick();updateButtons();}
    @Override protected void renderBg(GuiGraphics g,float tick,int mx,int my){
        VanillaPanel.frame(g,leftPos,topPos,imageHeight);
        for(int i=0;i<2;i++){int y=topPos+41+i*57;VanillaPanel.slot(g,leftPos+10,y);VanillaPanel.slot(g,leftPos+64,y);g.renderItem(new ItemStack(menu.displayCurrency()),leftPos+10,y);g.renderItem(new ItemStack(BankingVillagers.certificate(i)),leftPos+64,y);}
        for(var slot:menu.slots)VanillaPanel.slot(g,leftPos+slot.x,topPos+slot.y);
    }
    @Override protected void renderLabels(GuiGraphics g,int mx,int my){
        g.drawString(font,"Bank Booth",8,7,0x404040,false);
        String[] names={"Bank Space","Double Stacking"};
        for(int i=0;i<2;i++){int y=25+i*57;g.drawString(font,names[i],10,y,0x404040,false);g.drawString(font,Integer.toString(menu.cost(i)),30,y+21,0x303030,false);if(!menu.owned(i))g.drawString(font,"Applies instantly",10,y+36,0x555555,false);}
        g.drawString(font,"Call Banker",10,129,0x404040,false);g.drawString(font,playerInventoryTitle,8,154,0x404040,false);
    }
    @Override public void render(GuiGraphics g,int mx,int my,float tick){renderBackground(g);super.render(g,mx,my,tick);g.renderItem(new ItemStack(Items.BELL),leftPos+121,topPos+125);if(!menu.assigned())g.drawString(font,"X",leftPos+127,topPos+127,0xff3333,true);renderTooltip(g,mx,my);}
}
