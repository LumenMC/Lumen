package net.lumen;


import net.lumen.plugin.PluginLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;

import java.util.logging.Logger;

public class LumenServer {

    private static final Logger LOGGER = Logger.getLogger("LumenServer");


    public static void main(String[] args) {

        LOGGER.info("[LumenServer] Starting server...");
        //Initialize Minestom Server
        MinecraftServer server = new MinecraftServer();


        String address = "localhost"; //can be altered
        int port = 25565;

        MinecraftServer.init();

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

}