# test_app.py — Unit Tests for app.py
# Session 59 & 60: Automated testing with JUnit report generation
#
# Run locally:   pytest test_app.py -v
# Run with JUnit report (CI):
#   pytest test_app.py --junitxml=reports/junit.xml -v

import pytest
from app import app, add, greet, is_palindrome


# ---------------------------------------------------------------------------
# Flask test client fixture
# ---------------------------------------------------------------------------

@pytest.fixture
def client():
    """Create a Flask test client for HTTP-level tests."""
    app.config["TESTING"] = True
    with app.test_client() as c:
        yield c


# ===========================================================================
# Tests for add()
# ===========================================================================

class TestAdd:
    def test_add_positive_integers(self):
        assert add(2, 3) == 5

    def test_add_negative_numbers(self):
        assert add(-4, -6) == -10

    def test_add_mixed_sign(self):
        assert add(-1, 5) == 4

    def test_add_floats(self):
        assert add(1.5, 2.5) == pytest.approx(4.0)

    def test_add_zero(self):
        assert add(0, 0) == 0


# ===========================================================================
# Tests for greet()
# ===========================================================================

class TestGreet:
    def test_greet_normal_name(self):
        assert greet("Alice") == "Hello, Alice!"

    def test_greet_strips_whitespace(self):
        assert greet("  Bob  ") == "Hello, Bob!"

    def test_greet_empty_string_raises(self):
        with pytest.raises(ValueError):
            greet("")

    def test_greet_whitespace_only_raises(self):
        with pytest.raises(ValueError):
            greet("   ")

    def test_greet_multiword_name(self):
        assert greet("John Doe") == "Hello, John Doe!"


# ===========================================================================
# Tests for is_palindrome()
# ===========================================================================

class TestIsPalindrome:
    def test_simple_palindrome(self):
        assert is_palindrome("racecar") is True

    def test_not_palindrome(self):
        assert is_palindrome("hello") is False

    def test_case_insensitive(self):
        assert is_palindrome("Madam") is True

    def test_palindrome_with_spaces(self):
        assert is_palindrome("a man a plan a canal panama".replace(" ", "")) is True

    def test_single_character(self):
        assert is_palindrome("x") is True

    def test_empty_string(self):
        assert is_palindrome("") is True


# ===========================================================================
# HTTP endpoint tests
# ===========================================================================

class TestEndpoints:
    def test_index_returns_200(self, client):
        response = client.get("/")
        assert response.status_code == 200

    def test_index_json_keys(self, client):
        data = client.get("/").get_json()
        assert "app" in data
        assert "status" in data
        assert data["status"] == "running"

    def test_health_check(self, client):
        response = client.get("/health")
        assert response.status_code == 200
        assert response.get_json()["status"] == "healthy"

    def test_greet_endpoint_valid(self, client):
        response = client.get("/greet/Alice")
        assert response.status_code == 200
        assert "Hello, Alice!" in response.get_json()["message"]

    def test_add_endpoint(self, client):
        response = client.get("/add/3/7")
        assert response.status_code == 200
        assert response.get_json()["result"] == 10
