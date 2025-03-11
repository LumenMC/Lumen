package com.lumenmc.plugin;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PluginDescriptionFile {
    private final String name, version, mainClass;
    public final List<String> depend, softDepend;

    public PluginDescriptionFile(InputStream yamlStream) throws Exception {
        if(yamlStream == null) {
            throw new Exception("YAML stream is null");
        }
        Yaml yaml = new Yaml();
        Map<String, Object> config = yaml.load(yamlStream);
        this.name = getString(config,"name","Example Plugin");
        this.version = getString(config,"version","1.0.0");
        this.mainClass = getString(config,"mainClass","ExampleClass");
        this.depend = getStringList(config, "depend");
        this.softDepend = getStringList(config, "softDepend");
    }

    private String getString(Map<String, Object> config, String key, String defaultValue) {
        Object value = config.get(key);
        return (value instanceof String str) ? str : defaultValue;
    }

    private List<String> getStringList(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getMainClass() {
        return mainClass;
    }

    public List<String> getDepend() {
        return depend;
    }

    public List<String> getSoftDepend() {
        return softDepend;
    }
}
