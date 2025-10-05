# VisionForge

**Team Members:** Suveer K Upasani, Parth Paygude, Ansh Bhingardive, Ashitosh Lavhate  

**Project Name:** Customizable and scalable API enabling institutions to conduct proctored online exams with ease and reliability  

**Project Abstract:**  
A robust, scalable, and fully customizable API engineered to power proctor-based online examination systems. It delivers secure, flexible, and efficient exam management while ensuring seamless integration, adaptability to diverse exam formats, and a smooth experience for both administrators and candidates.

**Tech Stack:** Python, Mediapipe, Flask, Java (Spring Boot), HTML, CSS, JS  

---

## Demo Video

[![Watch the Demo](https://github.com/Suveer-Upasani/VisionForge/blob/main/Flow.jpeg)](https://github.com/Suveer-Upasani/VisionForge/blob/main/Demo.mp4)  

*Click the image to play the demo video.*

---

## Java Spring Boot Project

This is a Java Spring Boot project built for demonstration purposes. It explains how to clone the repository and run the main application.

---

### Table of Contents

1. [Prerequisites](#prerequisites)  
2. [Clone Repository](#clone-repository)  
3. [Project Structure](#project-structure)  
4. [Run the Application](#run-the-application)  
5. [Access the Application](#access-the-application)  
6. [Author](#author)  

---

### Prerequisites

Before running the project, ensure you have:  

- Java JDK (version 8 or above)  
- Maven (for building and running the project)  
- IDE such as IntelliJ IDEA, Eclipse, or VS Code (optional)  

---

### Clone Repository

```bash
git clone https://github.com/Suveer-Upasani/Java_Project.git
cd Java_Project
```

---

### Project Structure

```
Java_Project/
│
├── bin/                        # Compilation & wrapper files
├── src/
│   ├── main/
│   │   ├── java/com/suveer/demo/
│   │   │   ├── DemoApplication.java
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── Controller.java
│   │   │   └── Service.java
│   │   └── resources/templates/
│   └── test/java/com/suveer/demo/
├── target/                      # Compiled classes & build artifacts
├── .mvn/                        # Maven wrapper
├── .git/                        # Git repository
├── .settings/                   # IDE settings
├── pom.xml                      # Maven project file
└── README.md                    # Project documentation
```

---

### Run the Application

#### 1. Using Maven (Recommended)

From the root of the project:

```bash
mvn spring-boot:run
```

> The application runs on **port 9090** by default.

#### 2. Running Directly

```bash
cd src/main/java/com/suveer/demo
javac DemoApplication.java
java DemoApplication
```

> **Mac Users:** Ensure `JAVA_HOME` is set:
>
> ```bash
> export JAVA_HOME=$(/usr/libexec/java_home)
> ```

---

### Access the Application

Open your browser and go to:  

```
http://localhost:9090
```

You should see the application responding to requests.

---

### Author

**Suveer Upasani**  
Contact: 9423043271  
GitHub: [Suveer-Upasani](https://github.com/Suveer-Upasani)
