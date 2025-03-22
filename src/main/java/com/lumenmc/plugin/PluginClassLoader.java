package com.lumenmc.plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {
    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public void close() throws IOException{
        super.close();
    }
}
