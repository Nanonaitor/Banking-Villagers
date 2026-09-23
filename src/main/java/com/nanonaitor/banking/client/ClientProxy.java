package com.nanonaitor.banking.client;
import com.nanonaitor.banking.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
public final class ClientProxy extends CommonProxy {public void pre(){RenderingRegistry.registerEntityRenderingHandler(BankerEntity.class,manager->new ClientSetup.Renderer(manager));}}
