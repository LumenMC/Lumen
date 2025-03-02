package net.lumen.plugin;

import net.lumen.LumenServer;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginLoader {

    private static final Map<String, Plugin> plugins = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);
    private static final File pluginsDir = new File("plugins");

    public static void loadPlugins() {

        if(!pluginsDir.exists()) {
            pluginsDir.mkdir();
        }

        File[] files = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));

        if (files == null || files.length == 0) {
            LOGGER.info("[PluginLoader] No plugins found.");
            return;
        }

            for(File file : files) {

            try {

                String mainClassName = getMainClass(file); //get main class from plugin.yml
                if(mainClassName == null) {
                    LOGGER.warn("[PluginLoader] No 'main' class found in plugin.yml for " + file.getName());
                    continue;//skips invalid plugin

                }

                LOGGER.info("[PluginLoader] Loading plugin: " + file.getName());

                //Load plugin jar
                URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, LumenServer.class.getClassLoader());

                //Load main class of plugin
                Class<?> mainClass = Class.forName(mainClassName, true, classLoader);
                if(!Plugin.class.isAssignableFrom(mainClass)) {
                    LOGGER.warn("[PluginLoader] Main class does not extend Plugin: " + mainClass);
                    continue; //skips invalid plugin
                }

                //Initialize and enable the plugin
                Plugin plugin = (Plugin) mainClass.getDeclaredConstructor().newInstance();
                plugins.put(file.getName(), plugin);
                plugin.onEnable();
                LOGGER.info("[PluginLoader] Enabled plugin: " + file.getName());


            }catch (Exception e) {
                LOGGER.warn("[PluginLoader] Failed to load plugin: " + file.getName());
                e.printStackTrace();
            }

        }

    }




    private static String getMainClass(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("plugin.yml");
            if (entry == null) return null;

            try (InputStream input = jar.getInputStream(entry)) {
                Yaml yaml = new Yaml();
                Map<String, Object> pluginData = yaml.load(input);
                return (String) pluginData.get("main");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void unloadPlugins() {
        for(Map.Entry<String, Plugin> entry : plugins.entrySet()) {
            try{
                entry.getValue().onDisable();
                LOGGER.info("[PluginLoader] Disabled plugin: " + entry.getKey());
            }catch (Exception e) {
                LOGGER.warn("[PluginLoader] Failed to disable plugin: " + entry.getKey());
                e.printStackTrace();
            }
        }
        plugins.clear();
    }

    public static Plugin getPlugin(String name) {
        return plugins.get(name);
    }

    public static Map<String, Plugin> getLoadedPlugins() {
        return plugins;
    }


    
}
