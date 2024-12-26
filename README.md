# ChatLink Server 📢✨

Welcome to **ChatLinkServer**! This is a server built using Java's NIO package for the [ChatLink app](https://github.com/om1cael/ChatLink). It provides functionalities for both global and private messaging using UUIDs.

> [!NOTE]  
> This server is not meant to be used in real applications, but rather is a project to study Java's NIO networking.

## Features 🌟

- **Global Messaging**: Broadcast messages to all connected clients.
- **Private Chat Support**: Initiate one-on-one private conversations.
- **Client Registration**: Unique client identification using UUIDs.
- **Efficient Communication**: Powered by Java NIO for non-blocking IO operations.
- **Scalable Architecture**: Designed to handle multiple clients concurrently.

## Getting Started ⚙️

Follow these steps to get the ChatLinkServer up and running on your machine.

### Prerequisites 

Ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 11 or higher.
- **Apache Log4j**: Used for logging.
- **Gradle**: For build automation.

### Installation 

1. Clone the repository:
   ```bash
   git clone https://github.com/om1cael/ChatLinkServer.git
   ```

2. Navigate to the project directory:
   ```bash
   cd ChatLinkServer
   ```

3. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```

4. Run the server:
   ```bash
   java -jar build/libs/<build-name>.jar
   ```

## Usage ⚡

### Connecting Clients
- Clients can connect to the server on port `1024`.
- Each client must register with a unique UUID.

### Sending Messages
- **Global Message**: Type a message and send to broadcast globally.
- **Private Chat**: Use the `/chat <UUID>` command to initiate a private conversation with a specific client.

### Sample Commands
- `/chat 123e4567-e89b-12d3-a456-426614174000` - Start a private chat with the specified UUID.
- `<message>` - Send a message globally or privately depending on the chat state.

### Key Components

1. **ServerSocketChannel**:
   - Handles incoming client connections.

2. **Selector**:
   - Manages multiple channels using a single thread.

3. **ConcurrentHashMap**:
   - Stores connected clients and active private chats.

4. **ByteBuffer**:
   - Efficiently handles message reading and writing.

## Logging 🔍

ChatLinkServer uses Log4j for logging server activities, including:
- Connection events.
- Private chat creation.
- Message transmissions.

Logs are written to the console for easy debugging and monitoring.

## Contributing 🌱

Here’s how you can get involved:

1. Fork the repository.
2. Create a feature branch:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add feature X"
   ```
4. Push your branch:
   ```bash
   git push origin feature-name
   ```
5. Open a pull request.

## License ⚖️

This project is licensed under the MIT License. See the `LICENSE` file for details.


> Made with ❤️ and Java.

