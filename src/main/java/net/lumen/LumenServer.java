package net.lumen;


import net.lumen.plugin.PluginLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;

import java.util.logging.Logger;

public class LumenServer {

    private static final Logger LOGGER = Logger.getLogger("LumenServer");
    private static MinecraftServer server;
    private static GlobalEventHandler globalEventHandler;

    public static void main(String[] args) {

        LOGGER.info("[LumenServer] Starting server...");

        //Initialize Minestom Server
        server = new MinecraftServer();


        String address = "localhost"; //can be altered
        int port = 25565;

        MinecraftServer.init();
        globalEventHandler = server.getGlobalEventHandler();

        //Load Plugins
        PluginLoader.loadPlugins();

        server.start(address, port);
        LOGGER.info("[LumenServer] Server started on " + address + ":" + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("[LumenServer] Shutting down...");
            PluginLoader.unloadPlugins();
            MinecraftServer.stopCleanly();
            LOGGER.info("[LumenServer] Server shutdown complete.");
        }));


    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static GlobalEventHandler getGlobalEventHandler() {
        return globalEventHandler;
    }
}