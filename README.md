# Java Spring Boot Project

A Java Spring Boot project built for demonstration purposes. This guide explains how to clone the repository and run the main application.

---

## Table of Contents

1. [Prerequisites](#prerequisites)  
2. [Clone Repository](#clone-repository)  
3. [Project Structure](#project-structure)  
4. [Run the Application](#run-the-application)  
5. [Access the Application](#access-the-application)  
6. [Author](#author)  

---

## Prerequisites

Before running the project, make sure you have the following installed:

- **Java JDK** (version 8 or above)  
- **Maven** (for building and running the project)  
- **IDE** such as IntelliJ IDEA, Eclipse, or VS Code (optional)  

---

## Clone Repository

To get a copy of the project, run the following command in your terminal:

```bash
git clone https://github.com/Suveer-Upasani/Java_Project.git
```

Navigate into the project directory:

```bash
cd Java_Project
```

---

## Project Structure

The main files and directories of this project are organized as follows:

```
Java_Project/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── suveer/
│                   └── demo/
│                       ├── DemoApplication.java   # Main application entry point
│                       ├── Controller.java        # Example controller
│                       ├── Service.java           # Example service
│                       └── Repository.java        # Example repository
├── pom.xml                                      # Maven project file
└── README.md                                    # Project documentation
```

---

## Run the Application

To run the project, you **must execute the main application file**:

```bash
src/main/java/com/suveer/demo/DemoApplication.java
```

You can do this in two ways:

### 1. Using Maven (Recommended)

From the root of the project directory:

```bash
mvn spring-boot:run
```

Maven will automatically compile the project and run `DemoApplication.java`.

### 2. Running Directly

Navigate to the main package:

```bash
cd src/main/java/com/suveer/demo
```

Compile the Java file:

```bash
javac DemoApplication.java
```

Run the application:

```bash
java DemoApplication
```

> **Note:** Using Maven is recommended since it handles dependencies automatically.  

> **Mac Users:** If running directly on a Mac, ensure your terminal has Java JDK correctly configured. You may need to set `JAVA_HOME`:
>
> ```bash
> export JAVA_HOME=$(/usr/libexec/java_home)
> ```

---

## Access the Application

Once the application is running, open your web browser and visit:

```
http://localhost:8080
```

You should see the application responding to requests.

---

## Author

**Suveer Upasani**  
Contact: 9423043271  
GitHub: [https://github.com/Suveer-Upasani](https://github.com/Suveer-Upasani)
