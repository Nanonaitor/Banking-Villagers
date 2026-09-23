package com.nanonaitor.banking.client;
import com.nanonaitor.banking.BankConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.ForgeConfigSpec;
public final class BankConfigScreen extends Screen {
    private final Screen parent;
    private final ForgeConfigSpec.IntValue[] values={BankConfig.START,BankConfig.SIZE_MULTIPLIER,BankConfig.STACK_MULTIPLIER,BankConfig.COST[0],BankConfig.COST[1],BankConfig.VILLAGE_WEIGHT};
    private final String[] names={"Starting slots (1–16384)","Space multiplier (2–8)","Stack multiplier (2–8)","Space price (1–4096)","Stack price (1–4096)","Village weight (0–30)","Payment item registry ID"};
    private final String[] notes={"Slots before upgrades. Shrinking never deletes stored items.","One purchase multiplies capacity; maximum 16384 slots.","Bank-only stack increase. Withdrawals use normal item limits.","Payment items consumed to unlock extra space.","Payment items consumed to unlock larger stacks.","0 disables new booths. Existing villages do not change.","Example: minecraft:diamond. Used for both upgrade prices."};
    private final int[] min={1,2,2,1,1,0},max={16384,8,8,4096,4096,30};
    private final String[] draft=new String[7];
    private final String[] saved=new String[7];
    private Button saveButton;
    private boolean saving;
    private int page; private String message="";
    public BankConfigScreen(Screen parent){super(Component.literal("Banking Villagers Config"));this.parent=parent;}
    @Override protected void init(){
        boolean editable=minecraft.hasSingleplayerServer();
        if(editable){
            if(draft[0]==null){for(int i=0;i<values.length;i++)draft[i]=""+values[i].get();draft[6]=BankConfig.CURRENCY.get();System.arraycopy(draft,0,saved,0,7);}
            for(int i=page*3;i<Math.min(7,page*3+3);i++){
                final int index=i;int y=43+(i%3)*47;
                var box=new EditBox(font,width/2+65,y,115,18,Component.literal(names[i]));box.setMaxLength(128);box.setValue(draft[i]);box.setResponder(v->{draft[index]=v;updateSaveButton();});addRenderableWidget(box);
            }
        }
        var prev=addRenderableWidget(Button.builder(Component.literal("<"),b->{page--;rebuildWidgets();}).bounds(width/2-180,height-26,25,20).build());prev.active=editable&&page>0;
        var next=addRenderableWidget(Button.builder(Component.literal(">"),b->{page++;rebuildWidgets();}).bounds(width/2+155,height-26,25,20).build());next.active=editable&&page<2;
        saveButton=addRenderableWidget(Button.builder(Component.literal("Save Changes"),b->save()).bounds(width/2-105,height-26,100,20).build());updateSaveButton();
        addRenderableWidget(Button.builder(Component.literal("Done"),b->onClose()).bounds(width/2+5,height-26,100,20).build());
    }
    private void updateSaveButton(){if(saveButton!=null)saveButton.active=minecraft.hasSingleplayerServer()&&!saving&&!java.util.Arrays.equals(draft,saved);}
    private void save(){
        if(saving||java.util.Arrays.equals(draft,saved))return;
        int[] input=new int[values.length];
        try{for(int i=0;i<input.length;i++){input[i]=Integer.parseInt(draft[i]);if(input[i]<min[i]||input[i]>max[i])throw new IllegalArgumentException();}}
        catch(IllegalArgumentException ex){message="Invalid number: check the ranges on each page.";return;}
        var id=ResourceLocation.tryParse(draft[6].trim());
        if(id==null||!ForgeRegistries.ITEMS.containsKey(id)||ForgeRegistries.ITEMS.getValue(id)==Items.AIR){message="Unknown item. Use an existing modid:item ID.";return;}
        var server=minecraft.getSingleplayerServer();if(server==null)return;
        String[] submitted=draft.clone();saving=true;updateSaveButton();message="Saving...";
        server.execute(()->{
            try{for(int i=0;i<input.length;i++)values[i].set(input[i]);BankConfig.CURRENCY.set(id.toString());BankConfig.SPEC.save();
                minecraft.execute(()->{System.arraycopy(submitted,0,saved,0,7);saving=false;message="Saved. Reopen bank menus to refresh their layout.";updateSaveButton();});
            }catch(RuntimeException ex){minecraft.execute(()->{saving=false;message="Could not save changes. Check the game log.";updateSaveButton();});com.mojang.logging.LogUtils.getLogger().error("Unable to save bank configuration",ex);}
        });
    }
    @Override public void render(GuiGraphics g,int mx,int my,float dt){
        renderBackground(g);g.drawCenteredString(font,title,width/2,10,0xffffff);
        if(!minecraft.hasSingleplayerServer()){g.drawCenteredString(font,"Open a singleplayer world to edit its settings.",width/2,60,0xffffff);g.drawCenteredString(font,"Multiplayer settings are controlled by the server owner.",width/2,78,0xaaaaaa);}
        else {
            g.drawCenteredString(font,"One purchase per upgrade per bank. Page "+(page+1)+"/3",width/2,25,0xaaaaaa);
            for(int i=page*3;i<Math.min(7,page*3+3);i++){int y=43+(i%3)*47;g.drawString(font,names[i],width/2-180,y+4,0xffffff);g.drawWordWrap(font,Component.literal(notes[i]),width/2-180,y+22,360,0xaaaaaa);}
        }
        g.drawCenteredString(font,message,width/2,height-43,0xffdd77);super.render(g,mx,my,dt);
    }
    @Override public void onClose(){minecraft.setScreen(parent);}
}
