package Server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.concurrent.Executors;

/**
 * BankingServer - Lightweight HTTP API Server
 * Uses built-in JDK HttpServer (no extra dependencies needed)
 *
 * Endpoints:
 *   POST /api/login    - { "name": "Anubhaw", "customerId": "A8884" }
 *   POST /api/register - { "name": "Ravi",    "customerId": "R1234" }
 *
 * Run this class BEFORE opening the frontend HTML page.
 * Server starts on: http://localhost:8080
 */
public class BankingServer {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/bank_app";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Anubhaw@123";
    private static final int    PORT    = 8081;

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/login",    new LoginHandler());
        server.createContext("/api/register", new RegisterHandler());

        // Serve a simple health-check so you can test in browser
        server.createContext("/api/ping", exchange -> {
            addCorsHeaders(exchange);
            String resp = "{\"status\":\"ok\",\"message\":\"BankingServer running\"}";
            sendResponse(exchange, 200, resp);
        });

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("====================================");
        System.out.println("  BankingServer started on port " + PORT);
        System.out.println("  http://localhost:" + PORT + "/api/ping");
        System.out.println("====================================");
    }

    // ------------------------------------------------------------------
    // LOGIN HANDLER
    // ------------------------------------------------------------------
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            // Handle preflight OPTIONS request (browser sends this first)
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "");
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                String body = readBody(exchange);
                String name       = extractJson(body, "name");
                String customerId = extractJson(body, "customerId");

                if (name == null || customerId == null || name.isEmpty() || customerId.isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Name and Customer ID are required\"}");
                    return;
                }

                boolean valid = isCustomerRegistered(name, customerId);

                if (valid) {
                    System.out.println("[LOGIN] Success: " + name + " / " + customerId);
                    sendResponse(exchange, 200,
                        "{\"success\":true,\"message\":\"Login successful\",\"name\":\"" + escape(name) +
                        "\",\"customerId\":\"" + escape(customerId) + "\"}");
                } else {
                    System.out.println("[LOGIN] Failed: " + name + " / " + customerId);
                    sendResponse(exchange, 401,
                        "{\"success\":false,\"message\":\"Invalid credentials. Please register first.\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\":false,\"message\":\"Server error: " + escape(e.getMessage()) + "\"}");
            }
        }
    }

    // ------------------------------------------------------------------
    // REGISTER HANDLER
    // ------------------------------------------------------------------
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "");
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                String body = readBody(exchange);
                String name       = extractJson(body, "name");
                String customerId = extractJson(body, "customerId");

                if (name == null || customerId == null || name.isEmpty() || customerId.isEmpty()) {
                    sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Name and Customer ID are required\"}");
                    return;
                }

                if (isCustomerIdExists(customerId)) {
                    sendResponse(exchange, 409,
                        "{\"success\":false,\"message\":\"Customer ID already exists. Please choose another.\"}");
                    return;
                }

                boolean registered = registerCustomer(name, customerId);

                if (registered) {
                    System.out.println("[REGISTER] New user: " + name + " / " + customerId);
                    sendResponse(exchange, 201,
                        "{\"success\":true,\"message\":\"Registered successfully\",\"name\":\"" + escape(name) +
                        "\",\"customerId\":\"" + escape(customerId) + "\"}");
                } else {
                    sendResponse(exchange, 500, "{\"success\":false,\"message\":\"Registration failed. Try again.\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"success\":false,\"message\":\"Server error: " + escape(e.getMessage()) + "\"}");
            }
        }
    }

    // ------------------------------------------------------------------
    // DATABASE METHODS (same logic as Main.java)
    // ------------------------------------------------------------------

    static boolean isCustomerRegistered(String name, String customerId) {
        String sql = "SELECT * FROM login_details WHERE username=? AND customer_id=?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, customerId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    static boolean isCustomerIdExists(String customerId) {
        String sql = "SELECT * FROM login_details WHERE customer_id=?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    static boolean registerCustomer(String name, String customerId) {
        String sql = "INSERT INTO login_details (username, customer_id) VALUES (?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------------------------------------------------------
    // UTILITY HELPERS
    // ------------------------------------------------------------------

    static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin",  "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
    }

    static void sendResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    /**
     * Simple JSON value extractor — no external library needed.
     * Works for flat string values like: {"name":"Anubhaw","customerId":"A8884"}
     */
    static String extractJson(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return null;
        idx += search.length();
        // skip whitespace and colon
        while (idx < json.length() && (json.charAt(idx) == ' ' || json.charAt(idx) == ':')) idx++;
        if (idx >= json.length()) return null;
        if (json.charAt(idx) == '"') {
            // string value
            idx++;
            StringBuilder val = new StringBuilder();
            while (idx < json.length() && json.charAt(idx) != '"') {
                if (json.charAt(idx) == '\\') idx++; // skip escape char
                if (idx < json.length()) val.append(json.charAt(idx));
                idx++;
            }
            return val.toString();
        }
        return null;
    }

    static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
