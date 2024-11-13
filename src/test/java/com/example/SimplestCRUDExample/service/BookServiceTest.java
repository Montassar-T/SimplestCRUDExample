package com.example.SimplestCRUDExample.service;

import com.example.SimplestCRUDExample.model.Book;
import com.example.SimplestCRUDExample.repo.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooks() {
        List<Book> books = List.of(
                new Book(1L, "Book One", "Author One"),
                new Book(2L, "Book Two", "Author Two")
        );
        when(bookRepository.findAll()).thenReturn(books);
        ResponseEntity<List<Book>> response = bookService.getAllBooks();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());


        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookByID() {
        // Arrange: Mock a book with an ID and simulate finding it in the repository
        Long bookId = 1L;
        Book book = new Book(bookId, "New Book", "Author");

        // Mock the repository call to return the book when findById is called
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        ResponseEntity<Book> response = bookService.getBookByID(bookId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(bookId, response.getBody().getId());
        assertEquals("New Book", response.getBody().getTitle());
        assertEquals("Author", response.getBody().getAuthor());

        // Verify that findById was called exactly once
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void addBook() {
        Book book = new Book(null, "New Book", "Author");
        Book savedBook = new Book(1L, "New Book", "Author");

        when(bookRepository.save(book)).thenReturn(savedBook);

        // Act: Call the addBook method
        ResponseEntity<Book> response = bookService.addBook(book);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedBook.getId(), response.getBody().getId());
        assertEquals(savedBook.getTitle(), response.getBody().getTitle());

        // Verify that save was called once
        verify(bookRepository, times(1)).save(book);
    }




    @Test
    void updateBook() {
    }

    @Test
    void deleteAllBooks() {
        // Arrange: No need to arrange any specific data for deleting all
        doNothing().when(bookRepository).deleteAll();

        // Act: Call the deleteAllBooks method
        ResponseEntity<HttpStatus> response = bookService.deleteAllBooks();

        // Assert: Verify the response status is OK
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify: Ensure deleteAll was called once
        verify(bookRepository, times(1)).deleteAll();
    }


    @Test
    void deleteBook() {
        // Arrange: Define a book ID to delete
        Long bookId = 1L;
        doNothing().when(bookRepository).deleteById(bookId);

        // Act: Call the deleteBook method
        ResponseEntity<HttpStatus> response = bookService.deleteBook(bookId);

        // Assert: Verify that the response status is OK
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify: Check that existsById and deleteById were called
        verify(bookRepository, times(1)).deleteById(bookId);
    }

}