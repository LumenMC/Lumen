package com.lumenmc.server;

import com.lumenmc.configuration.LumenYaml;
import com.lumenmc.plugin.PluginManager;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.server.ServerListPingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class LumenServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LumenServer.class);
    private static final String prefix = "[LumenServer] ";

    private LumenServer() {}

    private static class Holder {
        private static final LumenServer INSTANCE = new LumenServer();
    }

    public static LumenServer getInstance() {
        return Holder.INSTANCE;
    }

    public static PluginManager getPluginManager() {
        return PluginManager.getInstance();
    }

    public static void main(String[] args) throws IOException {
        MinecraftServer minecraftServer = MinecraftServer.init();
        getPluginManager().loadPlugins(getPluginsFolder());
        getPluginManager().enablePlugins();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            LOGGER.info(prefix+"Shutting down...");
            getPluginManager().disablePlugins();
            MinecraftServer.stopCleanly();
        }));
        LumenYaml lumenYaml = new LumenYaml();
        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, event -> {
            event.getResponseData().setDescription(Component.text(lumenYaml.getMotd()));
        });
        String address = lumenYaml.getServerIp();
        int port = lumenYaml.getServerPort();
        minecraftServer.start(address, port);
        LOGGER.info(prefix+"Server started on {}:{}", address, port);
    }

    private static File getPluginsFolder() {
        File pluginsFolder = new File("plugins");
        if (!pluginsFolder.exists()) {
            pluginsFolder.mkdir();
        }
        return pluginsFolder;
    }


}
