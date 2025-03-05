package net.lumen;


import net.lumen.plugin.PluginLoader;
import net.minestom.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LumenServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LumenServer.class);


    public static void main(String[] args) {

        LOGGER.info("[LumenServer] Starting server...");

        //Initialize Minestom Server
        MinecraftServer server = new MinecraftServer();


        String address = "localhost";
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