package com.lumenmc.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    private PluginLoader() {}

    public static class Holder{
        private static final PluginLoader INSTANCE = new PluginLoader();
    }

    public static PluginLoader getInstance() {
        return Holder.INSTANCE;
    }

    public PluginDescriptionFile getPluginDescriptionFile(File jarFile) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("plugin.yml");
            if (entry == null) {
                throw new IOException("Could not find plugin.yml in " + jarFile);
            }

            try (InputStream in = jar.getInputStream(entry)) {
                return new PluginDescriptionFile(in);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Map<String, PluginDescriptionFile> getDescriptions(File pluginsFolder) throws IOException {
        Map<String, PluginDescriptionFile> descriptions = new HashMap<>();
        Map<String, File> pluginFiles = new HashMap<>();
        for(File jarFile : pluginsFolder.listFiles()){
            if(!jarFile.getName().endsWith(".jar")){continue;}

            try {
                PluginDescriptionFile desc = PluginLoader.getInstance().getPluginDescriptionFile(jarFile);
                descriptions.put(desc.getName(), desc);
                pluginFiles.put(desc.getName(), jarFile);
            }catch(Exception e){
                System.out.println("Error loading plugin file " + jarFile.getName());
                e.printStackTrace();
            }
        }
        return descriptions;
    }

    public Map<String, File> getPluginFiles(File pluginsFolder) throws IOException {
        Map<String, File> pluginFiles = new HashMap<>();
        for(File jarFile : pluginsFolder.listFiles()){
            if(!jarFile.getName().endsWith(".jar")){continue;}
            try {
                PluginDescriptionFile desc = PluginLoader.getInstance().getPluginDescriptionFile(jarFile);
                pluginFiles.put(desc.getName(), jarFile);
            }catch(Exception e){
                System.out.println("Error loading plugin file " + jarFile.getName());
                e.printStackTrace();
            }
        }
        return pluginFiles;
    }




    public Plugin loadPlugin(File jarFile) throws IOException, ClassNotFoundException {
        PluginDescriptionFile desc = getPluginDescriptionFile(jarFile);
        try{
            PluginClassLoader classLoader  = new PluginClassLoader(new URL[]{jarFile.toURI().toURL()}, getClass().getClassLoader());
            Class<?> clazz = classLoader.loadClass(desc.getMainClass());
            return (Plugin) clazz.getDeclaredConstructor().newInstance();
        }catch (RuntimeException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
