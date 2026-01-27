const express = require("express");
const cors = require("cors");
require("dotenv").config();

const statsRoutes = require("./routes/statsRoutes");
const transactionRoutes = require("./routes/transactionRoutes");
const accountRoutes = require("./routes/accountRoutes");
const userRoutes = require("./routes/userRoutes");
const auditRoutes = require("./routes/auditRoutes");

const app = express();

app.use(cors());
app.use(express.json());

// Routes
app.use("/stats", statsRoutes);
app.use("/transactions", transactionRoutes);
app.use("/accounts", accountRoutes);
app.use("/users", userRoutes);
app.use("/audit", auditRoutes);

app.get("/", (req, res) => {
    res.send("✅ Banking Backend Running...");
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`✅ Server running on http://localhost:${PORT}`));
