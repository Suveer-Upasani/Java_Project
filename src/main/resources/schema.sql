-- Drop old tables
DROP TABLE IF EXISTS test_results;
DROP TABLE IF EXISTS violations;
DROP TABLE IF EXISTS users;  -- optional if you want to reset users too

-- Recreate tables with updated schema

-- Users table
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    prn TEXT UNIQUE NOT NULL,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Test results table
CREATE TABLE test_results (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    prn TEXT NOT NULL,
    score INTEGER NOT NULL,
    total INTEGER NOT NULL,
    violations INTEGER DEFAULT 0,
    submitted_at DATETIME NOT NULL,
    terminated BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Violations table
CREATE TABLE violations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    prn TEXT NOT NULL,
    type TEXT NOT NULL,
    timestamp REAL NOT NULL,
    readable_time TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
