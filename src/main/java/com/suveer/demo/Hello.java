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
                         @RequestParam String email) {

        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, username, password, email);

        return "redirect:/welcome";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {

        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, password);

        if (count != null && count > 0) {
            return "redirect:/welcome";
        } else {
            return "redirect:/?error";
        }
    }

    @GetMapping("/welcome")
    public String welcomePage() {
        return "welcome";
    }

    // Start Test Page (Randomized Questions)
    @GetMapping("/start-test")
    public String startTest(Model model, HttpSession session) {
        // Reset violation tracking for new test
        currentTestViolations = 0;
        testTerminated = false;
        violations.clear();

        List<Question> questions = new ArrayList<>();

        // Add questions without numbering (numbers will be added dynamically in template)
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

        // Shuffle questions
        Collections.shuffle(questions, new Random());

        // Shuffle options for each question
        for (Question q : questions) {
            Collections.shuffle(q.getOptions(), new Random());
        }

        // ✅ Save correct answers in session (hidden inputs removed)
        Map<Integer, String> correctAnswers = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            correctAnswers.put(i, questions.get(i).getAnswer());
        }
        session.setAttribute("correctAnswers", correctAnswers);

        model.addAttribute("questions", questions);
        return "test";
    }

    // Handle test submission
    @PostMapping("/submit-test")
    public String submitTest(@RequestParam Map<String, String> allParams,
                             HttpSession session, Model model) {

        @SuppressWarnings("unchecked")
        Map<Integer, String> correctAnswers = (Map<Integer, String>) session.getAttribute("correctAnswers");

        int score = 0;
        int total = correctAnswers.size();

        for (int i = 0; i < total; i++) {
            String chosen = allParams.get("q" + i);
            String correct = correctAnswers.get(i);
            if (chosen != null && chosen.equals(correct)) {
                score++;
            }
        }

        // Store test result in database
        String sql = "INSERT INTO test_results (score, total, violations, submitted_at, terminated) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, score, total, currentTestViolations, LocalDateTime.now().toString(), testTerminated);

        model.addAttribute("score", score);
        model.addAttribute("total", total);
        model.addAttribute("violations", currentTestViolations);
        model.addAttribute("terminated", testTerminated);
        return "result";
    }

    // --- New API endpoints for proctoring integration ---
    
    @PostMapping("/api/violation")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> recordViolation(@RequestBody ViolationRequest request) {
        currentTestViolations = request.getTotalViolations();
        
        ViolationRecord violation = new ViolationRecord();
        violation.setType(request.getType());
        violation.setTimestamp(request.getTimestamp());
        violation.setReadableTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        violations.add(violation);

        // Store violation in database
        String sql = "INSERT INTO violations (type, timestamp, readable_time) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, request.getType(), request.getTimestamp(), violation.getReadableTime());

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

// ✅ Question class (clean and immutable)
class Question {
    private final String question;
    private final List<String> options;
    private final String answer;

    public Question(String question, List<String> options, String answer) {
        this.question = question;
        this.options = new ArrayList<>(options); // copy for safe shuffle
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getAnswer() {
        return answer;
    }
}

// Violation tracking classes
class ViolationRecord {
    private String type;
    private double timestamp;
    private String readableTime;

    // Getters and setters
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

    // Getters and setters
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