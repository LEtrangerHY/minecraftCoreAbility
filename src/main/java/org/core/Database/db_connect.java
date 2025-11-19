package org.core.Database;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Main.coreConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Level;

public class db_connect {

    private final coreConfig config;
    private final JavaPlugin plugin;

    public static Map<UUID, user> user_list = new ConcurrentHashMap<>();

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public db_connect(coreConfig config, JavaPlugin plugin) {
        this.config = config;
        this.plugin = plugin;

        this.jdbcUrl = "jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + ":"
                + plugin.getConfig().getInt("mysql.port") + "/"
                + plugin.getConfig().getString("mysql.database") + "?autoReconnect=true";
        this.username = plugin.getConfig().getString("mysql.username");
        this.password = plugin.getConfig().getString("mysql.password");

        plugin.getLogger().info("Ready to connect to MySQL");
        try (Connection conn = openConnection()) {
            if (conn != null && !conn.isClosed()) {
                plugin.getLogger().info("Database connection test successful");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database connection test failed", e);
        }
    }

    private Connection openConnection() {
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to open database connection", e);
            return null;
        }
    }

    public int insertMember(Player player) {
        String sql = "INSERT INTO user (u_uuid, u_name, u_core, u_level, u_exp, u_R, u_Q, u_F) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE u_name = VALUES(u_name), u_core = VALUES(u_core), " +
                "u_level = VALUES(u_level), u_exp = VALUES(u_exp), u_R = VALUES(u_R), " +
                "u_Q = VALUES(u_Q), u_F = VALUES(u_F)";

        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            UUID uuid = player.getUniqueId();
            String name = player.getName();
            String core = config.getPlayerCore(player);

            long level = player.getPersistentDataContainer()
                    .getOrDefault(new NamespacedKey(plugin, "level"), PersistentDataType.LONG, 0L);
            long exp = player.getPersistentDataContainer()
                    .getOrDefault(new NamespacedKey(plugin, "exp"), PersistentDataType.LONG, 0L);
            long R = player.getPersistentDataContainer()
                    .getOrDefault(new NamespacedKey(plugin, "R"), PersistentDataType.LONG, 0L);
            long Q = player.getPersistentDataContainer()
                    .getOrDefault(new NamespacedKey(plugin, "Q"), PersistentDataType.LONG, 0L);
            long F = player.getPersistentDataContainer()
                    .getOrDefault(new NamespacedKey(plugin, "F"), PersistentDataType.LONG, 0L);

            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, name);
            pstmt.setString(3, core);
            pstmt.setLong(4, level);
            pstmt.setLong(5, exp);
            pstmt.setLong(6, R);
            pstmt.setLong(7, Q);
            pstmt.setLong(8, F);

            pstmt.executeUpdate();

            user u = new user();
            u.setU_uuid(uuid.toString());
            u.setU_name(name);
            u.setU_core(core);
            u.setU_level((int) level);
            u.setU_exp((int) exp);
            u.setU_r((int) R);
            u.setU_q((int) Q);
            u.setU_f((int) F);

            user_list.put(uuid, u);

            plugin.getLogger().info("Saved player " + name + " to database");
            return 0;

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player " + player.getName(), e);
            return 1;
        }
    }

    public user db_PlayerInfo(Player player) {
        UUID playerUUID = player.getUniqueId();
        user u = null;
        String sql = "SELECT u_uuid, u_name, u_core, u_level, u_exp, u_R, u_Q, u_F FROM user WHERE u_uuid = ?";

        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerUUID.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                u = new user();
                u.setU_uuid(rs.getString("u_uuid"));
                u.setU_name(rs.getString("u_name"));
                u.setU_core(rs.getString("u_core"));
                u.setU_level(rs.getInt("u_level"));
                u.setU_exp(rs.getInt("u_exp"));
                u.setU_r(rs.getInt("u_R"));
                u.setU_q(rs.getInt("u_Q"));
                u.setU_f(rs.getInt("u_F"));

                user_list.put(playerUUID, u);
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to fetch player info " + player.getName(), e);
        }

        return u;
    }

    public user db_PastePlayerInfo(Player player) {
        UUID playerUUID = player.getUniqueId();
        user u = null;
        String sql = "SELECT u_uuid, u_name, u_core, u_level, u_exp, u_R, u_Q, u_F FROM user WHERE u_uuid = ?";

        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerUUID.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                config.clearPlayerCore(player);
                config.setSetting(player, rs.getString("u_core"), true);

                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "level"), PersistentDataType.LONG, (long) rs.getInt("u_level"));
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "exp"), PersistentDataType.LONG, (long) rs.getInt("u_exp"));
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "R"), PersistentDataType.LONG, (long) rs.getInt("u_R"));
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "Q"), PersistentDataType.LONG, (long) rs.getInt("u_Q"));
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "F"), PersistentDataType.LONG, (long) rs.getInt("u_F"));

                u = new user();
                u.setU_uuid(rs.getString("u_uuid"));
                u.setU_name(rs.getString("u_name"));
                u.setU_core(rs.getString("u_core"));
                u.setU_level(rs.getInt("u_level"));
                u.setU_exp(rs.getInt("u_exp"));
                u.setU_r(rs.getInt("u_R"));
                u.setU_q(rs.getInt("u_Q"));
                u.setU_f(rs.getInt("u_F"));
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update player info " + player.getName(), e);
        }

        return u;
    }
}
