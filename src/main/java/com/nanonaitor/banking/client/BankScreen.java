package com.nanonaitor.banking.client;
import com.nanonaitor.banking.BankMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class BankScreen extends AbstractContainerScreen<BankMenu> {
    private Button prev,next;
    private EditBox search;
    private String query="";
    public BankScreen(BankMenu menu,Inventory inv,Component title){super(menu,inv,Component.literal(inv.player.getGameProfile().getName()+"'s Bank"));imageWidth=176;imageHeight=248;inventoryLabelY=154;}
    @Override protected void init(){
        super.init();
        prev=addRenderableWidget(Button.builder(Component.literal("<"),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,0)).bounds(leftPos+8,topPos+17,20,16).build());
        next=addRenderableWidget(Button.builder(Component.literal(">"),b->minecraft.gameMode.handleInventoryButtonClick(menu.containerId,1)).bounds(leftPos+148,topPos+17,20,16).build());
        search=new EditBox(font,leftPos+34,topPos+17,108,16,Component.literal("Search Bank"));
        search.setHint(Component.literal("Search..."));search.setMaxLength(100);search.setValue(query);
        search.setResponder(value->query=value);addRenderableWidget(search);
    }
    @Override protected void containerTick(){super.containerTick();prev.active=menu.page()>0;next.active=menu.page()+1<menu.pages();}
    @Override protected void renderBg(GuiGraphics g,float tick,int mx,int my){
        VanillaPanel.frame(g,leftPos,topPos,imageHeight);
        for(var slot:menu.slots)VanillaPanel.slot(g,leftPos+slot.x,topPos+slot.y);
        for(int i=0;i<54;i++)if(!menu.usable(i)){var slot=menu.slots.get(i);g.fill(leftPos+slot.x,topPos+slot.y,leftPos+slot.x+16,topPos+slot.y+16,0x66773322);}
    }
    @Override protected void renderLabels(GuiGraphics g,int mx,int my){
        g.drawString(font,title,8,5,0x404040,false);
        String page=(menu.page()+1)+" / "+menu.pages();g.drawString(font,page,168-font.width(page),145,0x303030,false);
        g.drawString(font,"Capacity: "+menu.capacity()+ "",8,145,0x354839,false);
        g.drawString(font,playerInventoryTitle,8,154,0x404040,false);
        // Use Minecraft's decoration renderer: transparent background, normal shadow,
        // right-aligned count, and no count label for a single item.
        g.pose().pushPose();g.pose().translate(0,0,100);
        for(int i=0;i<54;i++)if(menu.bankCount(i)>1){var s=menu.slots.get(i);g.renderItemDecorations(font,s.getItem(),s.x,s.y,Integer.toString(menu.bankCount(i)));}
        g.pose().popPose();
    }
    @Override public boolean keyPressed(int key,int scan,int modifiers){
        if(search!=null&&search.isFocused()&&key!=256){search.keyPressed(key,scan,modifiers);return true;}
        return super.keyPressed(key,scan,modifiers);
    }
    @Override public void render(GuiGraphics g,int mx,int my,float tick){renderBackground(g);super.render(g,mx,my,tick);
        String term=query.trim().toLowerCase(java.util.Locale.ROOT);
        if(!term.isEmpty()){
            g.pose().pushPose();g.pose().translate(0,0,350);
            for(int i=0;i<54;i++){var s=menu.slots.get(i);var item=s.getItem();
                if(!item.isEmpty()&&!item.getHoverName().getString().toLowerCase(java.util.Locale.ROOT).contains(term)&&!net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(item.getItem()).toString().contains(term))
                    g.fill(leftPos+s.x,topPos+s.y,leftPos+s.x+16,topPos+s.y+16,0xb0888888);
            }g.pose().popPose();
        }
        renderTooltip(g,mx,my);
        if(hoveredSlot!=null&&hoveredSlot.index<54&&!menu.usable(hoveredSlot.index)&&hoveredSlot.getItem().isEmpty())g.renderTooltip(font,Component.literal("Locked / empty overflow slot"),mx,my);
    }
}
