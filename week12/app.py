# app.py — Simple Flask Web Application
# Used across Sessions 57–60 to demonstrate GitLab CI/CD

from flask import Flask, jsonify

app = Flask(__name__)

# ---------------------------------------------------------------------------
# Helper functions (unit-testable, no HTTP context needed)
# ---------------------------------------------------------------------------

def add(a: int | float, b: int | float) -> int | float:
    """Return the sum of two numbers."""
    return a + b


def greet(name: str) -> str:
    """Return a personalised greeting string."""
    if not name or not name.strip():
        raise ValueError("Name must not be empty")
    return f"Hello, {name.strip()}!"


def is_palindrome(word: str) -> bool:
    """Return True when *word* reads the same forwards and backwards."""
    cleaned = word.lower().replace(" ", "")
    return cleaned == cleaned[::-1]


# ---------------------------------------------------------------------------
# Routes
# ---------------------------------------------------------------------------

@app.route("/")
def index():
    return jsonify({
        "app": "GitLab CI/CD Demo",
        "status": "running",
        "version": "1.0.0",
        "sessions": ["57 - Intro", "58 - Git Integration",
                     "59 - Pipeline Basics", "60 - Deployment"]
    })


@app.route("/health")
def health():
    """Health-check endpoint used by deployment environments."""
    return jsonify({"status": "healthy"}), 200


@app.route("/greet/<name>")
def greet_endpoint(name: str):
    try:
        return jsonify({"message": greet(name)}), 200
    except ValueError as exc:
        return jsonify({"error": str(exc)}), 400


@app.route("/add/<int:a>/<int:b>")
def add_endpoint(a: int, b: int):
    return jsonify({"result": add(a, b)}), 200


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
