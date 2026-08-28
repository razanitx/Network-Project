# لعبكة 🎮

### Multiplayer Word Game — Java Client-Server Application

La3bka is a multiplayer word game developed in Java using a client-server architecture. Players connect to a central server and compete in real time by providing correct words based on the given game prompts.

The project focuses on implementing multiplayer communication, game-room management, real-time player interaction, round control, and a graphical user interface using Java Swing.

---

## Overview

La3bka is designed as a multiplayer desktop game where players connect to a shared server before entering a game session.

The server is responsible for managing player connections, creating and controlling game rooms, coordinating game rounds, and synchronizing the game state between connected clients.

Each client provides an interactive Java Swing interface through which players can participate in the game, submit answers, and view the current game progress.

---

## Game Concept

The game consists of multiple rounds in which players compete by submitting correct words.

Each round has a limited time and players must submit answers before the timer expires. The game validates submitted answers and updates the game state accordingly.

The round can end when the required number of correct answers is reached or when the timer expires.

At the end of the game, the players are presented with the final results.

---

## Key Features

- Multiplayer gameplay
- Client-server communication
- Real-time player interaction
- Game room management
- Multiple game rounds
- Countdown timer
- Answer validation
- Score and result tracking
- Automatic round transitions
- Final results screen
- Java Swing graphical user interface

---

## System Architecture

La3bka follows a **Client-Server Architecture**.

```text
                    ┌─────────────────┐
                    │      Server     │
                    │                 │
                    │  ServerSocket   │
                    │  ClientHandler  │
                    │    PlayRoom      │
                    └────────┬────────┘
                             │
                 ┌───────────┴───────────┐
                 │                       │
          ┌──────▼──────┐         ┌──────▼──────┐
          │   Client 1  │         │   Client 2  │
          │              │         │              │
          │ Java Swing   │         │ Java Swing   │
          └──────────────┘         └──────────────┘
```

The server acts as the central coordinator, while each player connects through a client application.

---

## Server-Side Components

### ServerSocket

The `ServerSocket` is responsible for listening for incoming client connections and accepting players who want to join the game.

### ClientHandler

Each connected player is handled through a dedicated `ClientHandler`.

The handler manages communication between the server and an individual client and is responsible for receiving and sending game-related messages.

### PlayRoom

The `PlayRoom` manages the game session and coordinates the connected players.

It is responsible for:

- Managing players in the room
- Starting game rounds
- Controlling the game state
- Processing player answers
- Managing round completion
- Coordinating transitions between rounds

---

## Client-Side Components

The client application provides the graphical interface through Java Swing.

Players can:

- Connect to the server.
- Join a game.
- Participate in rounds.
- Submit answers.
- View game progress.
- Receive game updates.
- View the final game results.

---

## Game Flow

```text
Start Application
       ↓
Connect to Server
       ↓
Join Game Room
       ↓
Wait for Players
       ↓
Start Round
       ↓
Display Game Prompt
       ↓
Players Submit Answers
       ↓
Validate Answers
       ↓
Round Ends
       ↓
Next Round
       ↓
Repeat Until Final Round
       ↓
Display Game Results
```

---

## Game Rules

- Players must connect to the server before joining a game.
- The game is divided into multiple rounds.
- Each round has a countdown timer.
- Players submit answers through the client interface.
- Submitted answers are validated by the game logic.
- A round ends when the required number of correct answers is reached or the timer expires.
- After the required rounds are completed, the final results are displayed.

---

## Technologies

- **Java**
- **Java Swing**
- **Socket Programming**
- **TCP/IP Networking**
- **Multithreading**
- **Client-Server Architecture**
- **Object-Oriented Programming**

---

## Networking

The project uses Java socket programming to establish communication between the clients and the server.

The server listens for incoming connections, while clients connect to the server using sockets.

Multiple clients can communicate with the server simultaneously, allowing the game to operate as a multiplayer application.

---

## Multithreading

Multithreading is used to handle multiple connected players concurrently.

Each client connection can be managed independently, allowing the server to communicate with multiple players without blocking the entire application.

This approach enables real-time multiplayer interaction between connected clients.

---

## User Interface

The graphical user interface was developed using **Java Swing**.

The application includes interfaces for:

- Connecting to the game
- Waiting for players
- Playing each round
- Submitting answers
- Displaying game progress
- Displaying final results

---

## Project Structure

```text
La3bka/
│
├── src/
│   ├── Client/
│   ├── Server/
│   ├── Game/
│   └── UI/
│
├── images/
│
└── README.md
```

> The exact folder and class structure may vary depending on the final project organization.

---

## Main Components

| Component | Responsibility |
|---|---|
| `ServerSocket` | Accepts incoming client connections |
| `ClientHandler` | Handles communication with an individual client |
| `PlayRoom` | Manages the multiplayer game session |
| `GamePanel` | Provides the main game interface |
| `GameResultPanel` | Displays the final game results |
| `GameAnswers` | Stores and manages game answers |

---

## Learning Outcomes

Through this project, we gained practical experience in:

- Java network programming
- Client-server communication
- Socket programming
- Multithreading
- Concurrent client handling
- Game state management
- Object-oriented software development
- Java Swing GUI development
- Real-time application design

---

## Project Highlights

- Built a multiplayer game using Java.
- Implemented client-server communication using sockets.
- Developed a server capable of handling multiple client connections.
- Implemented game-room and round management.
- Designed a graphical interface using Java Swing.
- Implemented timed rounds and answer validation.
- Developed a final results interface.

---

## Project Status

**Completed — Academic Project**

---

## Team Project

La3bka was developed as a collaborative academic software project.

Each team member contributed to different aspects of the application, including development, networking, game logic, interface design, and testing.
