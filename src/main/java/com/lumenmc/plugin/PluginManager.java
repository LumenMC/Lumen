package com.lumenmc.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {
    private PluginManager(){}

    private static class Holder{
        private static final PluginManager INSTANCE = new PluginManager();
    }

    public static PluginManager getInstance(){
        return Holder.INSTANCE;
    }

    private final Map<String,Plugin> plugins = new HashMap<>();
    private final PluginLoader loader = PluginLoader.getInstance();
    private List<String> loadOrder = new ArrayList<>();

    public void loadPlugins(File pluginsFolder) throws IOException {
        if(!pluginsFolder.exists() || !pluginsFolder.isDirectory()){
            System.out.println("Plugins folder does not exist or is not a directory");
            return;
        }

        Map<String, PluginDescriptionFile> descriptions = loader.getDescriptions(pluginsFolder);
        Map<String, File> pluginFiles = loader.getPluginFiles(pluginsFolder);

        loadOrder = DependencyResolver.getLoadOrder(descriptions);

        for(String pluginName : loadOrder){
            File jarFile = pluginFiles.get(pluginName);
            if(jarFile == null){continue;}

            try {
                Plugin plugin = loader.loadPlugin(jarFile);
                if(plugin != null){
                    plugins.put(pluginName, plugin);
                    System.out.println("Loaded plugin " + pluginName);
                    plugin.onLoad();
                }
            }catch(Exception e){
                System.out.println("Error loading plugin " + pluginName);
                e.printStackTrace();
            }
        }
    }

    public void enablePlugins(){
        for(String pluginName : loadOrder){
            Plugin plugin = plugins.get(pluginName);
            if(plugin != null){
                System.out.println("Enabling plugin " + pluginName);
                plugin.onEnable();
            }
        }
    }

    public void disablePlugins(){
        for(int i = loadOrder.size() - 1; i >= 0; i--){
            Plugin plugin = plugins.get(loadOrder.get(i));
            if(plugin != null){
                System.out.println("Disabling plugin " + loadOrder.get(i));
                plugin.onDisable();
            }
        }
    }

    public Plugin getPlugin(String pluginName){
        return plugins.get(pluginName);
    }
}
