package com.suveer.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class Hello {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Violation tracking
    private int currentTestViolations = 0;
    private boolean testTerminated = false;
    private List<ViolationRecord> violations = new ArrayList<>();
    private final int MAX_VIOLATIONS = 3;

    @GetMapping("/api")
    @ResponseBody
    public String greet() {
        return "Hello World, Using Spring Boot with SQLite!";
    }

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String email,
                         @RequestParam String prn,
                         Model model) {

        if (!prn.matches("\\d{10}")) {
            model.addAttribute("error", "PRN must be exactly 10 digits.");
            return "signup";
        }

        if (!email.endsWith("@despu.edu.in")) {
            model.addAttribute("error", "Email must end with @despu.edu.in");
            return "signup";
        }

        try {
            String sql = "INSERT INTO users (prn, username, password, email) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, prn, username, password, email);
        } catch (Exception e) {
            model.addAttribute("error", "Username, PRN or Email already exists!");
            return "signup";
        }

        return "redirect:/welcome";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String prn,
                        Model model,
                        HttpSession session) {

        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ? AND prn = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, password, prn);

        if (count != null && count > 0) {
            session.setAttribute("username", username);
            session.setAttribute("prn", prn);
            return "redirect:/welcome";
        } else {
            model.addAttribute("error", "Invalid login credentials!");
            return "login";
        }
    }

    @GetMapping("/welcome")
    public String welcomePage() {
        return "welcome";
    }

    // --- Test Page ---
    @GetMapping("/start-test")
    public String startTest(Model model, HttpSession session) {
        currentTestViolations = 0;
        testTerminated = false;
        violations.clear();

        List<Question> questions = generateQuestions();
        Collections.shuffle(questions, new Random());
        for (Question q : questions) Collections.shuffle(q.getOptions(), new Random());

        Map<Integer, String> correctAnswers = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) correctAnswers.put(i, questions.get(i).getAnswer());
        session.setAttribute("correctAnswers", correctAnswers);

        model.addAttribute("questions", questions);
        return "test";
    }

    private List<Question> generateQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Which keyword is used to inherit a class in Java?",
                Arrays.asList("extends", "implements", "inherits", "super"), "extends"));
        questions.add(new Question("What is the default value of boolean variable in Java?",
                Arrays.asList("true", "false", "0", "null"), "false"));
        questions.add(new Question("Which method is the entry point of Java program?",
                Arrays.asList("main", "start", "run", "init"), "main"));
        questions.add(new Question("Which operator is used for logical AND in Java?",
                Arrays.asList("&", "&&", "|", "||"), "&&"));
        questions.add(new Question("Which of these is NOT a primitive data type?",
                Arrays.asList("int", "float", "String", "char"), "String"));
        questions.add(new Question("Which exception is thrown when dividing by zero?",
                Arrays.asList("IOException", "ArithmeticException", "NullPointerException", "ArrayIndexOutOfBoundsException"), "ArithmeticException"));
        questions.add(new Question("Which keyword is used to prevent inheritance?",
                Arrays.asList("static", "final", "const", "private"), "final"));
        questions.add(new Question("Which package contains the Scanner class?",
                Arrays.asList("java.util", "java.lang", "java.io", "java.sql"), "java.util"));
        questions.add(new Question("What is the size of int in Java?",
                Arrays.asList("2 bytes", "4 bytes", "8 bytes", "Depends on platform"), "4 bytes"));
        questions.add(new Question("Which loop executes at least once?",
                Arrays.asList("for", "while", "do-while", "foreach"), "do-while"));
        return questions;
    }

    @PostMapping("/submit-test")
    public String submitTest(@RequestParam Map<String, String> allParams,
                             HttpSession session, Model model) {

        @SuppressWarnings("unchecked")
        Map<Integer, String> correctAnswers = (Map<Integer, String>) session.getAttribute("correctAnswers");

        String username = (String) session.getAttribute("username");
        String prn = (String) session.getAttribute("prn");

        if (username == null || prn == null) {
            model.addAttribute("error", "Session expired or user not logged in!");
            return "login";
        }

        int score = 0;
        int total = correctAnswers != null ? correctAnswers.size() : 0;
        if (correctAnswers != null) {
            for (int i = 0; i < total; i++) {
                String chosen = allParams.get("q" + i);
                String correct = correctAnswers.get(i);
                if (chosen != null && chosen.equals(correct)) score++;
            }
        }

        String sql = "INSERT INTO test_results (username, prn, score, total, violations, submitted_at, terminated) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, username, prn, score, total, currentTestViolations,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), testTerminated);

        model.addAttribute("score", score);
        model.addAttribute("total", total);
        model.addAttribute("violations", currentTestViolations);
        model.addAttribute("terminated", testTerminated);
        return "result";
    }

    // --- Violation API ---
    @PostMapping("/api/violation")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> recordViolation(@RequestBody ViolationRequest request,
                                                               HttpSession session) {
        currentTestViolations = request.getTotalViolations();
        ViolationRecord violation = new ViolationRecord();
        violation.setType(request.getType());
        violation.setTimestamp(request.getTimestamp());
        violation.setReadableTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        violations.add(violation);

        String username = (String) session.getAttribute("username");
        String prn = (String) session.getAttribute("prn");

        if (username != null && prn != null) {
            String sql = "INSERT INTO violations (username, prn, type, timestamp, readable_time) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, username, prn, request.getType(), request.getTimestamp(), violation.getReadableTime());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "violation_recorded");
        response.put("total_violations", currentTestViolations);
        response.put("max_violations", MAX_VIOLATIONS);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/terminate-test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> terminateTest(@RequestBody TerminationRequest request) {
        testTerminated = true;
        Map<String, Object> response = new HashMap<>();
        response.put("status", "test_terminated");
        response.put("reason", request.getReason());
        response.put("total_violations", currentTestViolations);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/test-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTestStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("violations", currentTestViolations);
        response.put("max_violations", MAX_VIOLATIONS);
        response.put("terminated", testTerminated);
        response.put("violation_list", violations);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/reset-violations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetViolations() {
        currentTestViolations = 0;
        testTerminated = false;
        violations.clear();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "violations_reset");
        return ResponseEntity.ok(response);
    }
}

// --- Question & Violation Classes ---
class Question {
    private final String question;
    private final List<String> options;
    private final String answer;

    public Question(String question, List<String> options, String answer) {
        this.question = question;
        this.options = new ArrayList<>(options);
        this.answer = answer;
    }

    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public String getAnswer() { return answer; }
}

class ViolationRecord {
    private String type;
    private double timestamp;
    private String readableTime;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getTimestamp() { return timestamp; }
    public void setTimestamp(double timestamp) { this.timestamp = timestamp; }
    public String getReadableTime() { return readableTime; }
    public void setReadableTime(String readableTime) { this.readableTime = readableTime; }
}

class ViolationRequest {
    private String type;
    private double timestamp;
    private int totalViolations;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getTimestamp() { return timestamp; }
    public void setTimestamp(double timestamp) { this.timestamp = timestamp; }
    public int getTotalViolations() { return totalViolations; }
    public void setTotalViolations(int totalViolations) { this.totalViolations = totalViolations; }
}

class TerminationRequest {
    private String reason;
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
