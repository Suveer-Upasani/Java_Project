package com.suveer.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
public class Hello {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

        model.addAttribute("score", score);
        model.addAttribute("total", total);
        return "result";
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
