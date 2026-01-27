const db = require("../db");

exports.getTransactions = (req, res) => {
    const limitRaw = req.query.limit;
    const limit = Math.min(Math.max(parseInt(limitRaw || "10", 10) || 10, 1), 500);

    db.query("SELECT * FROM transactions ORDER BY created_at DESC LIMIT ?", [limit], (err, result) => {
        if (err) return res.status(500).json(err);
        res.json(result);
    });
};
