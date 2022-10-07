package net.dom.companies.database;

import net.dom.companies.Companies;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cache.ehcache.internal.SingletonEhcacheRegionFactory;
import org.hibernate.cfg.Environment;

import java.util.HashMap;

public class hibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                ConfigurationSection section;
                if (Companies.getInstance().getConfig() == null) {
                    Companies.log.severe("config.yml not found");
                    Bukkit.getPluginManager().disablePlugin(Companies.getInstance());
                }
                if ((section = Companies.getInstance().getConfig().getConfigurationSection("database")) == null) {
                    throw new IllegalArgumentException("database section in configuration not found");
                }
                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
                HashMap<String, Object> settings = new HashMap<>();
                settings.put(Environment.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
                settings.put(Environment.DRIVER, org.mariadb.jdbc.Driver.class.getName());
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MariaDBDialect");
                settings.put(Environment.URL, "jdbc:mariadb://" + section.getString("host") + ":" + section.getString("port") + "/" + section.getString("database") + "?useSSL=" + section.getString("ssl"));
                settings.put(Environment.USER, section.getString("user"));
                settings.put(Environment.PASS, section.getString("password"));
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.CACHE_REGION_FACTORY, SingletonEhcacheRegionFactory.class.getName());
                settings.put(Environment.USE_SECOND_LEVEL_CACHE, true);
                settings.put(Environment.USE_QUERY_CACHE, true);
                //settings.put(Environment.GENERATE_STATISTICS, true);
                settings.put(Environment.CACHE_PROVIDER_CONFIG, "/ehcache.xml");
                section = section.getConfigurationSection("properties");
                if (section == null) {
                    throw new IllegalArgumentException("database.properties section in configuration not found");
                }
                for (String key : section.getKeys(false)) {
                    settings.put("hibernate.hikari." + key, section.getString(key));
                }
                settings.put("hibernate.cache.ehcache.missing_cache_strategy", "create");
                settings.put("hibernate.hikari.dataSource.cachePrepStmts", "true");
                settings.put("hibernate.hikari.dataSource.prepStmtCacheSize", "250");
                settings.put("hibernate.hikari.dataSource.prepStmtCacheSqlLimit", "2048");
                settings.put("hibernate.hikari.dataSource.useUnicode", "true");
                settings.put("hibernate.hikari.dataSource.characterEncoding", "utf8");
                registryBuilder.applySettings(settings);
                registry = registryBuilder.build();
                MetadataSources sources = new MetadataSources(registry)
                        .addAnnotatedClass(Company.class)
                        .addAnnotatedClass(Employee.class)
                        .addAnnotatedClass(CompaniesEmployeesKeys.class)
                        .addAnnotatedClass(CompaniesEmployees.class);
                Metadata metadata = sources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            }
            catch (Exception e) {
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}

