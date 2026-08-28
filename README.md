# لعبكة | La3bka 🎮

### Multiplayer Network Game

لعبكة (La3bka) is a multiplayer network game developed in Java as a networking project. The game uses a client-server architecture to allow multiple players to connect to a central server and participate in the same game session.

The project focuses on network communication, client-server interaction, concurrent connections, game-room management, and real-time synchronization between players.

---

## Overview

لعبكة is a multiplayer game where players connect to a central server through separate client applications.

The server manages the connected players, creates and controls the game room, coordinates game rounds, and handles communication between the clients.

Each player interacts with the game through a Java Swing graphical interface, while the server manages the shared game state and coordinates the gameplay between connected players.

---

## Project Type

**Networking Project**

### Main Concepts

- Client-Server Architecture
- Socket Programming
- Network Communication
- Multithreading
- Concurrent Client Connections
- Real-Time Multiplayer Interaction

---

## How It Works

The application consists of two main sides:

### Server

The server acts as the central coordinator of the game.

It is responsible for:

- Accepting incoming client connections.
- Managing connected players.
- Creating and managing the game room.
- Coordinating game rounds.
- Processing player answers.
- Managing the shared game state.
- Sending game updates to connected clients.

### Client

Each player runs a client application that connects to the server.

The client is responsible for:

- Connecting to the server.
- Joining the game room.
- Displaying the game interface.
- Sending player answers.
- Receiving game updates from the server.
- Displaying the game results.

---

## Network Architecture

```text
                 ┌──────────────────────┐
                 │        SERVER        │
                 │                      │
                 │    ServerSocket      │
                 │    ClientHandler     │
                 │      PlayRoom        │
                 └──────────┬───────────┘
                            │
                 ┌──────────┴──────────┐
                 │                     │
          ┌──────▼──────┐       ┌──────▼──────┐
          │   CLIENT 1  │       │   CLIENT 2  │
          │              │       │              │
          │ Java Swing   │       │ Java Swing   │
          └──────────────┘       └──────────────┘
```

The server maintains the central game state and communicates with each connected client.

---

## Game Flow

```text
Launch Client
      ↓
Connect to Server
      ↓
Join Game Room
      ↓
Wait for Players
      ↓
Start Game
      ↓
Round Begins
      ↓
Players Submit Answers
      ↓
Server Validates & Updates Game State
      ↓
Round Ends
      ↓
Next Round
      ↓
Final Results
```

---

## Game Features

- Multiplayer gameplay
- Server-based game sessions
- Multiple client connections
- Game room management
- Multiple rounds
- Countdown timer
- Answer validation
- Real-time game updates
- Automatic round progression
- Final results display

---

## Networking Implementation

The project uses Java socket programming to establish communication between the server and clients.

The server uses `ServerSocket` to listen for incoming connections. Each connected player is handled independently through a `ClientHandler`, allowing multiple clients to communicate with the server concurrently.

The `PlayRoom` component coordinates the players and manages the shared state of the multiplayer game.

---

## Multithreading

Multithreading is an important part of the networking implementation.

Each connected client can be handled independently, allowing the server to communicate with multiple players concurrently without blocking other connections.

This enables multiple players to participate in the same game session at the same time.

---

## Game Mechanics

The game consists of **4 rounds**.

Each round has a **30-second countdown timer**. Players compete by submitting correct words based on the current game prompt.

A round can end when the required number of correct answers is reached or when the timer expires.

After completing the required rounds, the game displays the final results.

---

## Technologies

- **Java**
- **Java Swing**
- **Java Sockets**
- **ServerSocket**
- **TCP/IP Networking**
- **Multithreading**
- **Object-Oriented Programming**

---

## Main Components

| Component | Description |
|---|---|
| `ServerSocket` | Listens for incoming client connections |
| `ClientHandler` | Handles communication with an individual client |
| `PlayRoom` | Manages the multiplayer game session |
| `GamePanel` | Provides the main game interface |
| `GameResultPanel` | Displays the final game results |
| `GameAnswers` | Manages the game's answer data |
| `ClientConnection` | Manages the client's connection with the server |

---

## User Interface

The client-side interface was developed using **Java Swing** and provides players with the screens required to connect, participate in game rounds, submit answers, and view the final results.

---

## Project Structure

```text
La3bka/
│
├── src/
│   ├── client/
│   ├── server/
│   └── game/
│
├── images/
│
└── README.md
```

---

## Learning Outcomes

This project provided practical experience in:

- Client-server network architecture
- Java socket programming
- TCP/IP communication
- Multithreading
- Concurrent client handling
- Network-based application development
- Shared game-state management
- Java Swing GUI development
- Debugging network communication

---

## Project Status

**Completed — Networking Academic Project**

---

## Team Project

لعبكة was developed as a collaborative academic project focused on applying computer networking concepts to a real-time multiplayer application.
