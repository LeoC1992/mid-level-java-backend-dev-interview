const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

// Intentional Bug: CORS not configured correctly for all origins or specific Android emulator IPs
// Sometimes localhost from Android emulator is 10.0.2.2
app.use(cors({
    origin: 'http://localhost:8080' // Restrictive CORS
}));

app.use(bodyParser.json());

// In-memory data store
let tasks = [
    { id: 1, title: "Fix the bugs", is_completed: false }, // Intentional Bug: Field name mismatch (is_completed vs completed)
    { id: 2, title: "Refactor the code", is_completed: true },
    { id: 3, title: "Write unit tests", is_completed: false }
];

app.post('/api/login', (req, res) => {
    const { username, password } = req.body;
    if (username === 'admin' && password === 'password') {
        // Intentional Bug: Returning 200 OK but with error field structure that client might not expect, or just simple success
        res.status(200).json({ token: "fake-jwt-token-12345" });
    } else {
        res.status(401).json({ error: "Invalid credentials" });
    }
});

app.get('/api/tasks', (req, res) => {
    // Intentional Bug: Random server failure to test error handling
    if (Math.random() < 0.3) {
        // Simulating a crash or timeout
        // res.status(500).json({ error: "Internal Server Error" }); 
        // Or even worse, just hang? No, let's just return 500.
         return res.status(500).send("Server exploded");
    }

    // Intentional Bug: Returning correct HTTP status but data format mismatch
    // The client expects "taskId", "title", "completed"
    // We are sending "id", "title", "is_completed"
    
    // Intentional Bug: Another subtle one, sometimes return 204 No Content for list
    if (tasks.length === 0) {
        return res.status(204).send();
    }

    res.json(tasks);
});

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
