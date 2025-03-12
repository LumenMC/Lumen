package com.lumenmc.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class LumenYaml {
    private static final Logger LOGGER = LoggerFactory.getLogger(LumenYaml.class);
    private static final String prefix = "[LumenServer] ";
    private String server_ip, motd;
    private int server_port;

    public LumenYaml(){
        GenerateYaml();
        LoadYaml();
    }

    public void GenerateYaml(){
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String filePath = "lumen.yaml";
        File file = new File(filePath);
        if(file.exists()){
           return;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("server-ip", "127.0.0.1");
        data.put("server-port", 25565);
        data.put("motd", "A minecraft server");

        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(data, writer);
            LOGGER.info(prefix+"Lumen.yml file generated successfully!");
        } catch (IOException e) {
            LOGGER.error(prefix+"Lumen.yml file generation failed!");
        }
    }

    public void LoadYaml(){
        Yaml yaml = new Yaml();
        try (FileReader reader = new FileReader("lumen.yaml")) {
            Map<String, Object> data = yaml.load(reader);

            server_ip = (String) data.get("server-ip");
            server_port = (Integer) data.get("server-port");
            motd = (String) data.get("motd");
        } catch (FileNotFoundException e) {
            LOGGER.error(prefix+"Lumen.yml file was not found.");
        } catch (Exception e) {
            LOGGER.error(prefix+"An error occurred while reading the Lumen.yml file.");
        }
    }

    public String getServerIp() {
        return server_ip;
    }

    public int getServerPort() {
        return server_port;
    }

    public String getMotd() {
        return motd;
    }

}
