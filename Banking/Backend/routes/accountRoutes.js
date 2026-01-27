const express = require("express");
const router = express.Router();
const { getAccounts, deposit, withdraw, transfer, resequenceAccounts } = require("../controllers/accountController");

router.get("/", getAccounts);
router.post("/deposit", deposit);
router.post("/withdraw", withdraw);
router.post("/transfer", transfer);
router.post("/resequence", resequenceAccounts);

module.exports = router;
