package com.medirisk.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.medirisk.model.Assessment;
import com.medirisk.model.HealthRecord;
import com.medirisk.model.Patient;
import com.medirisk.model.User;
import com.medirisk.model.VitalSigns;
import com.medirisk.service.AssessmentService;
import com.medirisk.service.PatientService;
import com.medirisk.service.ReportService;
import com.medirisk.service.RiskCalculator;
import com.medirisk.service.UserService;
import com.medirisk.threads.AssessmentThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Built-in Core Java HTTP Server listening on port 8080.
 * Serves both API endpoints (/api/*) and static Web Frontend files (frontend/*).
 */
public class MediRiskServer {

    private final int port;
    private final UserService userService;
    private final PatientService patientService;
    private final AssessmentService assessmentService;
    private final ReportService reportService;
    private final RiskCalculator riskCalculator;
    private HttpServer server;

    public MediRiskServer(int port, UserService userService, PatientService patientService,
                          AssessmentService assessmentService, ReportService reportService) {
        this.port = port;
        this.userService = userService;
        this.patientService = patientService;
        this.assessmentService = assessmentService;
        this.reportService = reportService;
        this.riskCalculator = new RiskCalculator();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/status", new StatusHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/patients", new PatientHandler());
        server.createContext("/api/assessments", new AssessmentHandler());
        server.createContext("/api/health-records", new ReportHandler());
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("[MediRiskServer] HTTP Server started successfully on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("[MediRiskServer] HTTP Server stopped.");
        }
    }

    // --- STATIC FILE HANDLER ---
    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.trim().isEmpty()) {
                path = "/index.html";
            }
            File file = new File("frontend" + path);

            if (!file.getCanonicalPath().startsWith(new File("frontend").getCanonicalPath())) {
                sendJsonResponse(exchange, 403, "{\"error\":\"Access Denied\"}");
                return;
            }

            if (!file.exists() || file.isDirectory()) {
                File fallback = new File("frontend/index.html");
                if (fallback.exists()) {
                    serveFile(exchange, fallback, "text/html");
                } else {
                    sendJsonResponse(exchange, 404, "{\"error\":\"File not found\"}");
                }
                return;
            }

            serveFile(exchange, file, getMimeType(file.getName()));
        }

        private void serveFile(HttpExchange exchange, File file, String contentType) throws IOException {
            addCorsHeaders(exchange);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());
            OutputStream os = exchange.getResponseBody();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            fis.close();
            os.flush();
            os.close();
        }

        private String getMimeType(String filename) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
            return "text/plain; charset=UTF-8";
        }
    }

    // --- API HTTP HANDLERS ---
    private class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            sendJsonResponse(exchange, 200, "{\"status\":\"ONLINE\",\"server\":\"MediRisk Core Java HTTP Server\"}");
        }
    }

    private class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            Map<String, String> json = parseSimpleJson(readRequestBody(exchange));
            User user = userService.authenticate(json.get("username"), json.get("password"));
            if (user != null) {
                sendJsonResponse(exchange, 200, "{\"success\":true,\"user\":{\"id\":\"" + user.getId() 
                        + "\",\"username\":\"" + user.getUsername() + "\",\"name\":\"" + user.getName() + "\"}}");
            } else {
                sendJsonResponse(exchange, 401, "{\"success\":false,\"error\":\"Invalid credentials\"}");
            }
        }
    }

    private class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            Map<String, String> json = parseSimpleJson(readRequestBody(exchange));
            User user = userService.register(json.get("username"), json.get("password"), json.get("name"));
            if (user != null) {
                sendJsonResponse(exchange, 201, "{\"success\":true,\"user\":{\"id\":\"" + user.getId() 
                        + "\",\"username\":\"" + user.getUsername() + "\",\"name\":\"" + user.getName() + "\"}}");
            } else {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Registration failed\"}");
            }
        }
    }

    private class PatientHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String method = exchange.getRequestMethod();

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(method)) {
                String id = getQueryParam(exchange.getRequestURI().getQuery(), "id");
                if (id != null) {
                    Patient p = patientService.getPatientById(id);
                    if (p != null) {
                        sendJsonResponse(exchange, 200, patientToJson(p));
                    } else {
                        sendJsonResponse(exchange, 404, "{\"error\":\"Patient not found\"}");
                    }
                } else {
                    List<Patient> patients = patientService.getAllPatients();
                    sendJsonResponse(exchange, 200, patientListToJson(patients));
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                Map<String, String> json = parseSimpleJson(readRequestBody(exchange));
                String name = json.get("name");
                int age = parseInt(json.get("age"), -1);
                String gender = json.get("gender");
                String contact = json.get("contact");

                if (name == null || age < 0) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Invalid name or age\"}");
                    return;
                }

                Patient created = patientService.createPatient(name, age, gender, contact);
                if (created != null) {
                    sendJsonResponse(exchange, 201, "{\"success\":true,\"patient\":" + patientToJson(created) + "}");
                } else {
                    sendJsonResponse(exchange, 500, "{\"success\":false,\"error\":\"Could not save\"}");
                }
            } else if ("PUT".equalsIgnoreCase(method)) {
                Map<String, String> json = parseSimpleJson(readRequestBody(exchange));
                Patient p = new Patient(json.get("id"), json.get("name"), parseInt(json.get("age"), 0), json.get("gender"), json.get("contact"));
                if (patientService.updatePatient(p)) {
                    sendJsonResponse(exchange, 200, "{\"success\":true}");
                } else {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Update failed\"}");
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                String id = getQueryParam(exchange.getRequestURI().getQuery(), "id");
                if (patientService.deletePatient(id)) {
                    sendJsonResponse(exchange, 200, "{\"success\":true}");
                } else {
                    sendJsonResponse(exchange, 404, "{\"success\":false,\"error\":\"Not found\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    private class AssessmentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String method = exchange.getRequestMethod();

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(method)) {
                List<Assessment> list = assessmentService.getAllAssessments();
                StringBuilder jsonArray = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    jsonArray.append(assessmentToJson(list.get(i)));
                    if (i < list.size() - 1) jsonArray.append(",");
                }
                jsonArray.append("]");
                sendJsonResponse(exchange, 200, "{\"success\":true,\"data\":" + jsonArray.toString() + "}");
            } else if ("POST".equalsIgnoreCase(method)) {
                Map<String, String> json = parseSimpleJson(readRequestBody(exchange));
                
                String patientId = json.getOrDefault("patientId", "P001");
                Patient patient = patientService.getPatientById(patientId);
                if (patient == null) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Patient not found\"}");
                    return;
                }

                VitalSigns vitals = new VitalSigns(
                    parseInt(json.get("heartRate"), 70),
                    parseInt(json.get("systolicBP"), 120),
                    parseInt(json.get("diastolicBP"), 80),
                    parseInt(json.get("oxygenLevel"), 98),
                    parseDouble(json.get("temperature"), 36.6),
                    parseInt(json.get("respiratoryRate"), 16)
                );

                // Multi-threading demonstration
                AssessmentThread worker = new AssessmentThread(patient, vitals, riskCalculator, assessmentService);
                worker.start();
                try {
                    worker.join(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Assessment result = worker.getResultAssessment();
                if (result != null) {
                    sendJsonResponse(exchange, 201, "{\"success\":true,\"data\":" + assessmentToJson(result) + "}");
                } else {
                    sendJsonResponse(exchange, 500, "{\"success\":false,\"error\":\"Assessment failed\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    private class ReportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            sendJsonResponse(exchange, 200, "{\"success\":true,\"data\":[]}");
        }
    }

    // --- HELPER METHODS ---
    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.flush();
        os.close();
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    private String getQueryParam(String query, String paramName) {
        if (query == null || query.isEmpty()) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2 && kv[0].equalsIgnoreCase(paramName)) return kv[1];
        }
        return null;
    }

    private Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<String, String>();
        if (json == null || json.trim().isEmpty()) return map;
        String clean = json.trim().replaceAll("^\\{|\\}$", "");
        for (String pair : clean.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim().replaceAll("^\"|\"$", ""), kv[1].trim().replaceAll("^\"|\"$", ""));
            }
        }
        return map;
    }

    private String patientToJson(Patient p) {
        return "{\"id\":\"" + p.getId() + "\",\"name\":\"" + p.getName() + "\",\"age\":" + p.getAge() 
                + ",\"gender\":\"" + p.getGender() + "\",\"contact\":\"" + p.getContact() + "\"}";
    }

    private String patientListToJson(List<Patient> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(patientToJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String assessmentToJson(Assessment a) {
        return "{\"id\":\"" + a.getId() + "\",\"patientId\":\"" + a.getPatientId() 
                + "\",\"patientName\":\"" + a.getPatientName() + "\",\"age\":" + a.getAge()
                + ",\"heartRate\":" + a.getHeartRate() + ",\"systolicBP\":" + a.getSystolicBP()
                + ",\"diastolicBP\":" + a.getDiastolicBP() + ",\"oxygenLevel\":" + a.getOxygenLevel()
                + ",\"temperature\":" + a.getTemperature() + ",\"respiratoryRate\":" + a.getRespiratoryRate()
                + ",\"riskScore\":" + a.getRiskScore() + ",\"riskLevel\":\"" + a.getRiskLevel()
                + "\",\"recommendation\":\"" + a.getRecommendation() + "\",\"date\":\"" + a.getDate() 
                + "\",\"time\":\"" + a.getTime() + "\"}";
    }

    private int parseInt(String str, int defaultVal) {
        try { return Integer.parseInt(str); } catch (Exception e) { return defaultVal; }
    }

    private double parseDouble(String str, double defaultVal) {
        try { return Double.parseDouble(str); } catch (Exception e) { return defaultVal; }
    }
}
