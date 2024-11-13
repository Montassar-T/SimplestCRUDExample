package com.example.SimplestCRUDExample.integration;

import com.example.SimplestCRUDExample.model.Book;
import com.example.SimplestCRUDExample.repo.BookRepository;
import com.example.SimplestCRUDExample.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookControllerIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        book = new Book(null, "New Book", "Author");
    }

    @Test
    void addBook_ShouldReturnSavedBook() {
        // Act: Call the service method to add the book
        ResponseEntity<Book> response = bookService.addBook(book);

        // Assert: Verify that the returned book has an ID and correct data
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Book", response.getBody().getTitle());
        assertEquals("Author", response.getBody().getAuthor());
    }

    @Test
    void getBookById_ShouldReturnBook_WhenFound() {
        // Arrange: Save the book in the repository
        Book savedBook = bookService.addBook(book).getBody();

        // Act: Call the service to retrieve the book by ID
        ResponseEntity<Book> response = bookService.getBookByID(savedBook.getId());

        // Assert: Verify that the book is returned correctly
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedBook.getTitle(), response.getBody().getTitle());
    }

    @Test
    void getAllBooks_ShouldReturnBooks() {
        // Arrange: Save a few books in the repository
        bookService.addBook(new Book(null, "Book One", "Author One"));
        bookService.addBook(new Book(null, "Book Two", "Author Two"));

        // Act: Call the service to retrieve all books
        ResponseEntity<List<Book>> response = bookService.getAllBooks();

        // Assert: Verify that the response contains the books
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() > 0); // Check that at least one book is present
    }

    @Test
    void deleteBook_ShouldDeleteBook_WhenExists() {
        // Arrange: Save a book in the repository
        Book savedBook = bookService.addBook(book).getBody();

        // Act: Call the service to delete the book
        ResponseEntity<HttpStatus> response = bookService.deleteBook(savedBook.getId());

        // Assert: Verify that the book is deleted (status NO_CONTENT)
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify that the book is no longer in the repository
        assertFalse(bookRepository.findById(savedBook.getId()).isPresent());
    }

    @Test
    void deleteAllBooks_ShouldDeleteAllBooks() {
        // Arrange: Add a couple of books
        bookService.addBook(new Book(null, "Book One", "Author One"));
        bookService.addBook(new Book(null, "Book Two", "Author Two"));

        // Act: Call the service to delete all books
        ResponseEntity<HttpStatus> response = bookService.deleteAllBooks();

        // Assert: Verify that the status is NO_CONTENT
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify that the repository is empty
        assertTrue(bookRepository.findAll().isEmpty());
    }
}
