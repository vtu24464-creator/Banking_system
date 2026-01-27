const db = require("../db");

exports.getAuditLogs = (req, res) => {
    const limitRaw = req.query.limit;
    const limit = Math.min(Math.max(parseInt(limitRaw || "20", 10) || 20, 1), 500);

    db.query("SELECT * FROM audit_log ORDER BY created_at DESC LIMIT ?", [limit], (err, result) => {
        if (err) return res.status(500).json(err);
        res.json(result);
    });
};
