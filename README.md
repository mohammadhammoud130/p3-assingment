# Factory & Storage Management System

A desktop application for managing factory production lines, inventory, and worker-assigned tasks, built with **Java (OOP)** and a **Swing** GUI.

## Overview

This system simulates a small production facility: users log in, manage inventory items, define products and product lines, and assign tasks to production lines. A background engine continuously monitors active tasks, simulates production progress in real time, and auto-saves state to disk — so the app behaves less like a static CRUD tool and more like a live operations dashboard.

## Features

- **User authentication** — login system with role handling and duplicate-session protection (`UserAlreadyLoggedInException`)
- **Inventory management** — add, update, and track stock items with category, price, quantity, and minimum-threshold alerts
- **Product & product line management** — define products and assign them to production lines
- **Task management & live simulation** — create tasks, assign them to a product line, and watch produced quantity update automatically as a background thread simulates manufacturing time
- **Auto-save** — a dedicated background thread periodically persists task state to disk without blocking the UI
- **Error logging** — a centralized `ErrorLogger` records parsing errors and invalid data instead of failing silently
- **Modern UI** — styled with [FlatLaf](https://github.com/JFormDesigner/FlatLaf) for a clean, modern look and feel

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| GUI | Java Swing + FlatLaf |
| Architecture | MVC (Model / View / Controller) |
| Data storage | CSV files |
| Concurrency | Java Threads (task simulation, auto-save, monitoring) |

## Project Structure

```
src/
├── Main.java                # Application entry point
├── controller/               # Business logic and orchestration
│   ├── MainController.java   # App bootstrap, window setup, data loading
│   ├── UserController.java
│   ├── ItemController.java
│   ├── ProductController.java
│   ├── ProductLineController.java
│   ├── TaskController.java
│   ├── TaskRunner.java        # Background threads: task simulation + auto-save
│   └── ErrorLogger.java
├── model/                     # Data classes
│   ├── User.java
│   ├── Item.java
│   ├── Product.java
│   ├── ProductLine.java
│   ├── Task.java
│   ├── Category.java
│   ├── Status.java
│   └── Rule.java
├── view/                      # Swing UI screens
│   ├── Login.java
│   ├── DashBoard.java
│   ├── Storage.java
│   ├── ProductionLines.java
│   ├── TaskView.java
│   ├── AddUser.java
│   ├── AddItem.java
│   ├── AddProduct.java
│   ├── AddProductionLine.java
│   ├── AddTask.java
│   └── UserInfo.java
└── exception/
    └── UserAlreadyLoggedInException.java
```

## Getting Started

### Prerequisites
- JDK 17 or later
- [FlatLaf](https://mvnrepository.com/artifact/com.formdev/flatlaf) library on the classpath

### Data files
The app expects a `data/` folder (for CSV storage, e.g. `data/Items.csv`) and an `assets/` folder (for UI icons, e.g. `assets/32-logo.jpg`) in the working directory. Create these before running the app for the first time.

### Running
```bash
javac -cp .:flatlaf.jar -d out $(find src -name "*.java")
java -cp out:flatlaf.jar Main
```
*(On Windows, replace `:` with `;` in the classpath.)*

## Roadmap / Possible Improvements
- Migrate CSV storage to a proper database (SQLite/MySQL)
- Add unit tests for controller logic
- Externalize file paths and configuration instead of hardcoding them
- Package as a runnable `.jar` with bundled dependencies

## Author
**Mohammad Hammoud** — ITE Engineering student, Damascus University
