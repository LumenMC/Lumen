package com.lumenmc.server;

import com.lumenmc.plugin.PluginManager;
import net.minestom.server.MinecraftServer;

import java.io.File;
import java.io.IOException;

public class LumenServer {

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
        Runtime.getRuntime().addShutdownHook(new Thread(getPluginManager()::disablePlugins));
        minecraftServer.start("0.0.0.0", 25565);
    }

    private static File getPluginsFolder() {
        File pluginsFolder = new File("plugins");
        if (!pluginsFolder.exists()) {
            pluginsFolder.mkdir();
        }
        return pluginsFolder;
    }


}
