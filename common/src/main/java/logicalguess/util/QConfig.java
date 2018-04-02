package logicalguess.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class QConfig {
    public final static Config base = ConfigFactory.load().getConfig("quantum");
    public final static String environment =
            System.getenv("ENVIRONMENT") == null ? "qx" : System.getenv("ENVIRONMENT");

    /**
     * Returns a subtree of the base configuration with environment settings applied.
     *
     * @param setting The subtree to return config for.
     * @return A config with base in given setting, with environment modifications applied.
     */
    public static Config load(String setting) {

        Config config = base.getConfig(setting);

        if (config.hasPath(environment)) {
            return config.getConfig(environment).withFallback(config);
        }

        return config;
    }
}