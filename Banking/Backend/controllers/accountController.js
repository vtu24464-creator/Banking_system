const db = require("../db");

// GET accounts (ordered by account_number for clean sequence)
exports.getAccounts = (req, res) => {
    db.query("SELECT * FROM accounts ORDER BY account_number ASC", (err, result) => {
        if (err) return res.status(500).json(err);
        res.json(result);
    });
};

// POST resequence account numbers for all customers
exports.resequenceAccounts = (req, res) => {
    db.getConnection((err, conn) => {
        if (err) return res.status(500).json(err);

        conn.beginTransaction((txErr) => {
            if (txErr) {
                conn.release();
                return res.status(500).json(txErr);
            }

            conn.query("SELECT account_id, account_number FROM accounts ORDER BY account_id ASC", (selErr, rows) => {
                if (selErr) {
                    return conn.rollback(() => {
                        conn.release();
                        res.status(500).json(selErr);
                    });
                }

                if (!rows || rows.length === 0) {
                    return conn.commit(() => {
                        conn.release();
                        res.json({ message: "No accounts to resequence.", updated: 0 });
                    });
                }

                const base = 1000000001;
                const updates = [];

                rows.forEach((row, idx) => {
                    const desired = base + idx;
                    const current = Number(row.account_number);
                    if (!current || current !== desired) {
                        updates.push({ account_id: row.account_id, account_number: desired });
                    }
                });

                if (updates.length === 0) {
                    return conn.commit(() => {
                        conn.release();
                        res.json({ message: "Account numbers already in sequence.", updated: 0 });
                    });
                }

                // Apply updates one by one
                let processed = 0;
                updates.forEach((u) => {
                    conn.query(
                        "UPDATE accounts SET account_number = ? WHERE account_id = ?",
                        [u.account_number, u.account_id],
                        (upErr) => {
                            if (upErr) {
                                return conn.rollback(() => {
                                    conn.release();
                                    res.status(500).json(upErr);
                                });
                            }
                            processed += 1;

                            if (processed === updates.length) {
                                conn.commit((commitErr) => {
                                    if (commitErr) {
                                        return conn.rollback(() => {
                                            conn.release();
                                            res.status(500).json(commitErr);
                                        });
                                    }

                                    conn.release();
                                    res.json({
                                        message: "✅ Account numbers resequenced.",
                                        updated: updates.length,
                                        start_from: base,
                                    });
                                });
                            }
                        }
                    );
                });
            });
        });
    });
};

// POST deposit
exports.deposit = (req, res) => {
    const { account_number, amount } = req.body;

    if (!account_number || !amount) {
        return res.status(400).json({ message: "Missing fields" });
    }

    db.query(
        "UPDATE accounts SET balance = balance + ? WHERE account_number = ?",
        [amount, account_number],
        (err, result) => {
            if (err) return res.status(500).json(err);
            if (result.affectedRows === 0) return res.status(404).json({ message: "Account not found" });

            db.query(
                `INSERT INTO transactions(from_account,to_account,amount,transaction_type,status)
         VALUES(NULL,(SELECT account_id FROM accounts WHERE account_number=?),?,'DEPOSIT','SUCCESS')`,
                [account_number, amount],
                (err2) => {
                    if (err2) return res.status(500).json(err2);

                    db.query(
                        "INSERT INTO audit_log(user_id,action,ip_address) VALUES(1,?,?)",
                        [`Deposit ${amount} to ${account_number}`, "127.0.0.1"]
                    );

                    res.json({ message: "✅ Deposit successful" });
                }
            );
        }
    );
};

// POST withdraw
exports.withdraw = (req, res) => {
    const { account_number, amount } = req.body;

    if (!account_number || !amount) {
        return res.status(400).json({ message: "Missing fields" });
    }

    db.query(
        "SELECT balance FROM accounts WHERE account_number=?",
        [account_number],
        (err, rows) => {
            if (err) return res.status(500).json(err);
            if (rows.length === 0) return res.status(404).json({ message: "Account not found" });

            if (rows[0].balance < amount) return res.status(400).json({ message: "❌ Insufficient balance" });

            db.query(
                "UPDATE accounts SET balance = balance - ? WHERE account_number=?",
                [amount, account_number],
                (err2) => {
                    if (err2) return res.status(500).json(err2);

                    db.query(
                        `INSERT INTO transactions(from_account,to_account,amount,transaction_type,status)
             VALUES((SELECT account_id FROM accounts WHERE account_number=?),NULL,?,'WITHDRAWAL','SUCCESS')`,
                        [account_number, amount]
                    );

                    db.query(
                        "INSERT INTO audit_log(user_id,action,ip_address) VALUES(1,?,?)",
                        [`Withdraw ${amount} from ${account_number}`, "127.0.0.1"]
                    );

                    res.json({ message: "✅ Withdrawal successful" });
                }
            );
        }
    );
};

// POST transfer (ACID transaction)
exports.transfer = (req, res) => {
    const { from_account, to_account, amount } = req.body;

    if (!from_account || !to_account || !amount) {
        return res.status(400).json({ message: "Missing fields" });
    }

    db.getConnection((err, conn) => {
        if (err) return res.status(500).json(err);

        conn.beginTransaction((err) => {
            if (err) return res.status(500).json(err);

            conn.query(
                "SELECT balance FROM accounts WHERE account_number=?",
                [from_account],
                (err, rows) => {
                    if (err) return conn.rollback(() => res.status(500).json(err));
                    if (rows.length === 0) return conn.rollback(() => res.status(404).json({ message: "From account not found" }));

                    if (rows[0].balance < amount) {
                        return conn.rollback(() => res.status(400).json({ message: "❌ Insufficient balance" }));
                    }

                    // Deduct from sender
                    conn.query(
                        "UPDATE accounts SET balance = balance - ? WHERE account_number=?",
                        [amount, from_account],
                        (err2) => {
                            if (err2) return conn.rollback(() => res.status(500).json(err2));

                            // Add to receiver
                            conn.query(
                                "UPDATE accounts SET balance = balance + ? WHERE account_number=?",
                                [amount, to_account],
                                (err3, result) => {
                                    if (err3) return conn.rollback(() => res.status(500).json(err3));
                                    if (result.affectedRows === 0) return conn.rollback(() => res.status(404).json({ message: "To account not found" }));

                                    // Insert transaction
                                    conn.query(
                                        `INSERT INTO transactions(from_account,to_account,amount,transaction_type,status)
                     VALUES(
                      (SELECT account_id FROM accounts WHERE account_number=?),
                      (SELECT account_id FROM accounts WHERE account_number=?),
                      ?,'TRANSFER','SUCCESS')`,
                                        [from_account, to_account, amount],
                                        (err4) => {
                                            if (err4) return conn.rollback(() => res.status(500).json(err4));

                                            conn.commit((err5) => {
                                                if (err5) return conn.rollback(() => res.status(500).json(err5));

                                                db.query(
                                                    "INSERT INTO audit_log(user_id,action,ip_address) VALUES(1,?,?)",
                                                    [`Transfer ${amount} from ${from_account} to ${to_account}`, "127.0.0.1"]
                                                );

                                                res.json({ message: "✅ Transfer successful (ACID committed)" });
                                                conn.release();
                                            });
                                        }
                                    );
                                }
                            );
                        }
                    );
                }
            );
        });
    });
};
