package org.core.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Main.coreConfig;

public class db_connect {

    private final coreConfig config;
    private final JavaPlugin plugin;

    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public static HashMap<UUID, user> user_list = new HashMap<>();

    public db_connect(coreConfig config, JavaPlugin plugin) {
        this.config = config;
        this.plugin = plugin;

        this.host = plugin.getConfig().getString("mysql.host");
        this.port = plugin.getConfig().getInt("mysql.port");
        this.database = plugin.getConfig().getString("mysql.database");
        this.username = plugin.getConfig().getString("mysql.username");
        this.password = plugin.getConfig().getString("mysql.password");

        this.open_Connection();
    }

    public Connection open_Connection() {
        try {
            if (connection != null && !connection.isClosed()) {
                plugin.getLogger().info("Existing Database connection reused");
                return connection;
            }

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    plugin.getLogger().info("Existing Database connection reused");
                    return connection;
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                plugin.getLogger().info("Database driver loaded");

                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true",
                        username,
                        password
                );

                if (connection != null && !connection.isClosed()) {
                    plugin.getLogger().info("New database connection created");
                } else {
                    plugin.getLogger().severe("Failed to create database connection");
                }
            }
            return connection;

        } catch (Exception e) {
            plugin.getLogger().severe("Database connection error");
            e.printStackTrace();
            return null;
        }
    }

    public int insertMember(Player player) {
        Connection conn = null;

        try {
            conn = this.open_Connection();

            String uuid = player.getUniqueId().toString();
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

            String sql = "INSERT INTO user (u_uuid, u_name, u_core, u_level, u_exp, u_R, u_Q, u_F) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE u_name = VALUES(u_name), u_core = VALUES(u_core), " +
                    "u_level = VALUES(u_level), u_exp = VALUES(u_exp), u_R = VALUES(u_R), " +
                    "u_Q = VALUES(u_Q), u_F = VALUES(u_F)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, uuid);
                pstmt.setString(2, name);
                pstmt.setString(3, core);
                pstmt.setInt(4, Math.toIntExact(level));
                pstmt.setInt(5, Math.toIntExact(exp));
                pstmt.setInt(6, (int) R);
                pstmt.setInt(7, (int) Q);
                pstmt.setInt(8, (int) F);
                pstmt.executeUpdate();
            }

            user u = new user();
            u.setU_uuid(uuid);
            u.setU_name(name);
            u.setU_core(core);
            u.setU_level((int) level);
            u.setU_exp((int) exp);
            u.setU_r((int) R);
            u.setU_q((int) Q);
            u.setU_f((int) F);

            user_list.put(player.getUniqueId(), u);

            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 1;

        } finally {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return 2;
            }
        }
    }

    public user db_PlayerInfo(Player player) {
        UUID playerUUID = player.getUniqueId();
        user u = null;
        Connection conn = null;

        try {
            conn = this.open_Connection();

            String sql = "SELECT u_uuid, u_name, u_core, u_level, u_exp, u_R, u_Q, u_F FROM user WHERE u_uuid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return u;
    }

}