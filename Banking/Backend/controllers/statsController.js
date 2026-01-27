const db = require("../db");

exports.getStats = (req, res) => {
    const q1 = "SELECT COUNT(*) AS totalUsers FROM users";
    const q2 = "SELECT COUNT(*) AS totalAccounts FROM accounts";
    const q3 = "SELECT COUNT(*) AS totalTransactions FROM transactions";
    const q4 = "SELECT SUM(balance) AS totalBalance FROM accounts WHERE status='ACTIVE'";

    db.query(q1, (err, usersData) => {
        if (err) return res.status(500).json(err);

        db.query(q2, (err, accountsData) => {
            if (err) return res.status(500).json(err);

            db.query(q3, (err, txnData) => {
                if (err) return res.status(500).json(err);

                db.query(q4, (err, balanceData) => {
                    if (err) return res.status(500).json(err);

                    res.json({
                        totalUsers: usersData[0].totalUsers,
                        totalAccounts: accountsData[0].totalAccounts,
                        totalTransactions: txnData[0].totalTransactions,
                        totalBalance: balanceData[0].totalBalance || 0,
                    });
                });
            });
        });
    });
};
