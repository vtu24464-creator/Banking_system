// ============================================================
// Task 7.1 - Library Management System
// Microservices based on Single Responsibility Principle
// Each service has ONE clear responsibility
// ============================================================

// ── SERVICE 1: Book Service ──────────────────────────────────
// Responsibility: Manage book inventory (add, update, delete, search books)

package com.example.library.book;

import org.springframework.web.bind.annotation.*;
import java.util.*;

// Model
class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;

    public Book() {}
    public Book(int id, String title, String author, String isbn) {
        this.id = id; this.title = title;
        this.author = author; this.isbn = isbn;
        this.available = true;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

@RestController
@RequestMapping("/api/books")
class BookController {

    private List<Book> books = new ArrayList<>();
    private int idCounter = 1;

    @GetMapping
    public List<Book> getAllBooks() {
        return books;
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable int id) {
        return books.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        book.setId(idCounter++);
        books.add(book);
        return book;
    }

    @PutMapping("/{id}/availability")
    public String updateAvailability(@PathVariable int id,
                                     @RequestParam boolean available) {
        books.stream().filter(b -> b.getId() == id)
             .findFirst().ifPresent(b -> b.setAvailable(available));
        return "Book availability updated";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable int id) {
        books.removeIf(b -> b.getId() == id);
        return "Book deleted";
    }
}


// ── SERVICE 2: Member Service ────────────────────────────────
// Responsibility: Manage library members (register, update, view members)

package com.example.library.member;

import org.springframework.web.bind.annotation.*;
import java.util.*;

class Member {
    private int id;
    private String name;
    private String email;
    private String phone;

    public Member() {}
    public Member(int id, String name, String email, String phone) {
        this.id = id; this.name = name;
        this.email = email; this.phone = phone;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}

@RestController
@RequestMapping("/api/members")
class MemberController {

    private List<Member> members = new ArrayList<>();
    private int idCounter = 1;

    @PostMapping
    public Member registerMember(@RequestBody Member member) {
        member.setId(idCounter++);
        members.add(member);
        return member;
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return members;
    }

    @GetMapping("/{id}")
    public Member getMember(@PathVariable int id) {
        return members.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable int id, @RequestBody Member updated) {
        for (Member m : members) {
            if (m.getId() == id) {
                m.setName(updated.getName());
                m.setEmail(updated.getEmail());
                m.setPhone(updated.getPhone());
                return m;
            }
        }
        return null;
    }
}


// ── SERVICE 3: Borrow Service ────────────────────────────────
// Responsibility: Handle borrowing and returning of books

package com.example.library.borrow;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

class BorrowRecord {
    private int id;
    private int memberId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned;

    public BorrowRecord() {}
    public BorrowRecord(int id, int memberId, int bookId) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.borrowDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(14);
        this.returned = false;
    }
    public int getId() { return id; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }
}

@RestController
@RequestMapping("/api/borrows")
class BorrowController {

    private List<BorrowRecord> records = new ArrayList<>();
    private int idCounter = 1;

    // Borrow a book → POST /api/borrows?memberId=1&bookId=2
    @PostMapping
    public BorrowRecord borrowBook(@RequestParam int memberId,
                                   @RequestParam int bookId) {
        BorrowRecord record = new BorrowRecord(idCounter++, memberId, bookId);
        records.add(record);
        // In real system: call Book Service to mark book unavailable
        return record;
    }

    // Return a book → PUT /api/borrows/1/return
    @PutMapping("/{id}/return")
    public String returnBook(@PathVariable int id) {
        records.stream().filter(r -> r.getId() == id)
               .findFirst().ifPresent(r -> r.setReturned(true));
        // In real system: call Book Service to mark book available
        return "Book returned successfully";
    }

    @GetMapping
    public List<BorrowRecord> getAllRecords() {
        return records;
    }

    @GetMapping("/member/{memberId}")
    public List<BorrowRecord> getByMember(@PathVariable int memberId) {
        return records.stream()
                .filter(r -> r.getMemberId() == memberId)
                .toList();
    }
}


// ── SERVICE 4: Fine Service ──────────────────────────────────
// Responsibility: Calculate and manage overdue fines

package com.example.library.fine;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

class Fine {
    private int id;
    private int memberId;
    private int borrowId;
    private long overdueDays;
    private double amount;
    private boolean paid;

    public Fine() {}
    public Fine(int id, int memberId, int borrowId, long overdueDays) {
        this.id = id;
        this.memberId = memberId;
        this.borrowId = borrowId;
        this.overdueDays = overdueDays;
        this.amount = overdueDays * 5.0; // Rs. 5 per day
        this.paid = false;
    }
    public int getId() { return id; }
    public int getMemberId() { return memberId; }
    public int getBorrowId() { return borrowId; }
    public long getOverdueDays() { return overdueDays; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
}

@RestController
@RequestMapping("/api/fines")
class FineController {

    private List<Fine> fines = new ArrayList<>();
    private int idCounter = 1;

    // Calculate fine → POST /api/fines?memberId=1&borrowId=2&dueDate=2025-03-01
    @PostMapping
    public Fine calculateFine(@RequestParam int memberId,
                               @RequestParam int borrowId,
                               @RequestParam String dueDate) {
        LocalDate due = LocalDate.parse(dueDate);
        long overdueDays = ChronoUnit.DAYS.between(due, LocalDate.now());
        if (overdueDays <= 0) {
            return null; // No fine
        }
        Fine fine = new Fine(idCounter++, memberId, borrowId, overdueDays);
        fines.add(fine);
        return fine;
    }

    // Pay fine → PUT /api/fines/1/pay
    @PutMapping("/{id}/pay")
    public String payFine(@PathVariable int id) {
        fines.stream().filter(f -> f.getId() == id)
             .findFirst().ifPresent(f -> f.setPaid(true));
        return "Fine paid successfully";
    }

    @GetMapping("/member/{memberId}")
    public List<Fine> getFinesByMember(@PathVariable int memberId) {
        return fines.stream()
                .filter(f -> f.getMemberId() == memberId)
                .toList();
    }
}
