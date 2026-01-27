const db = require("../db");
const crypto = require("crypto");

exports.getUsers = (req, res) => {
    db.query("SELECT user_id,name,email,role,created_at FROM users", (err, result) => {
        if (err) return res.status(500).json(err);
        res.json(result);
    });
};

exports.createUser = (req, res) => {
    const name = (req.body?.name || "").trim();
    const email = (req.body?.email || "").trim();
    const role = (req.body?.role || "CUSTOMER").trim() || "CUSTOMER";
    const password = (req.body?.password || "").toString();

    if (!name || !email) {
        return res.status(400).json({ message: "Name and email are required." });
    }

    // DB requires a password hash (even if you don't have auth UI yet).
    const effectivePassword = password.trim() || "ChangeMe123!";
    const password_hash = crypto.createHash("sha256").update(effectivePassword).digest("hex");

    // Best-effort duplicate prevention (also rely on DB unique constraint if present)
    db.query("SELECT user_id FROM users WHERE email = ? LIMIT 1", [email], (err, rows) => {
        if (err) return res.status(500).json(err);
        if (rows.length > 0) return res.status(409).json({ message: "Email already exists." });

        db.query(
            "INSERT INTO users(name,email,role,password_hash) VALUES(?,?,?,?)",
            [name, email, role, password_hash],
            (err2, userResult) => {
                if (err2) return res.status(500).json(err2);

                const userId = userResult.insertId;

                // Generate next account number (simple sequential strategy)
                db.query("SELECT MAX(account_number) AS maxAcc FROM accounts", (errMax, rowsMax) => {
                    if (errMax) return res.status(500).json(errMax);

                    const currentMax = Number(rowsMax?.[0]?.maxAcc) || 1000000000;
                    const newAccountNumber = currentMax + 1;

                    // Create default account with 0 balance and ACTIVE status
                    db.query(
                        "INSERT INTO accounts(user_id,account_number,balance,status) VALUES(?,?,0,'ACTIVE')",
                        [userId, newAccountNumber],
                        (errAcc, accResult) => {
                            if (errAcc) return res.status(500).json(errAcc);

                            // Audit log (falls back silently if audit table differs)
                            try {
                                const ip = (req.headers["x-forwarded-for"] || req.socket?.remoteAddress || req.ip || "").toString();
                                db.query("INSERT INTO audit_log(user_id,action,ip_address) VALUES(1,?,?)", [
                                    `Created user ${email} (${role}) with account ${newAccountNumber}`,
                                    ip || "127.0.0.1",
                                ]);
                            } catch (_) {}

                            db.query(
                                "SELECT user_id,name,email,role,created_at FROM users WHERE user_id = ?",
                                [userId],
                                (err3, createdRows) => {
                                    const basePayload = {
                                        message: "✅ Customer and account created",
                                        user_id: userId,
                                        account: {
                                            account_id: accResult.insertId,
                                            account_number: newAccountNumber,
                                            balance: 0,
                                            status: "ACTIVE",
                                        },
                                    };

                                    if (err3 || !createdRows?.length) {
                                        return res.status(201).json(basePayload);
                                    }

                                    return res.status(201).json({
                                        ...basePayload,
                                        user: createdRows[0],
                                    });
                                }
                            );
                        }
                    );
                });
            }
        );
    });
};