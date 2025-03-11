package com.lumenmc.plugin;

import com.lumenmc.server.LumenServer;

public class Plugin {
    public void onLoad() {}
    public void onEnable() {}
    public void onDisable() {}

    public LumenServer getLumenServer() {
        return LumenServer.getInstance();
    }

    public PluginManager getPluginManager() {
        return PluginManager.getInstance();
    }
}
