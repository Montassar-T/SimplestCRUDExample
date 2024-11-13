package com.example.SimplestCRUDExample.controller;

import com.example.SimplestCRUDExample.model.Book;
import com.example.SimplestCRUDExample.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
class BookControllerTest {
    @Autowired
    private MockMvc mvc;

    // Mock the BookService bean
    @MockBean
    private BookService bookService;

    @Test
    void testGetAllBooks() throws Exception {
        // Arrange: Setup the mock to return a ResponseEntity containing a list of books
        List<Book> books = List.of(
                new Book(1L, "Book One", "Author One"),
                new Book(2L, "Book Two", "Author Two")
        );
        when(bookService.getAllBooks()).thenReturn(new ResponseEntity<>(books, HttpStatus.OK));
        mvc.perform(get("/api/getAllBooks"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetBookById() throws Exception {
        Book book = new Book(1L, "Book One", "Author One");
        when(bookService.getBookByID(book.getId())).thenReturn(new ResponseEntity<>(book, HttpStatus.OK));
        mvc.perform(get("/api/getBookById/" + book.getId()))
                .andExpect(status().isOk());

        verify(bookService, times(1)).getBookByID(book.getId());
    }

    @Test
    void testAddBook() throws Exception {
        // Arrange: Prepare the expected Book and mock the service call
        Book book = new Book(1L, "New Book", "New Author");

        // Return ResponseEntity with the book
        when(bookService.addBook(any(Book.class))).thenReturn(new ResponseEntity<>(book, HttpStatus.CREATED));

        // Act & Assert: Perform the POST request and assert the response
        mvc.perform(post("/api/addBook")
                        .contentType("application/json")
                        .content("{\"title\": \"New Book\", \"author\": \"New Author\"}"))
                .andExpect(status().isCreated())  // Assert that the response status is 201 Created
                .andExpect(jsonPath("$.title").value("New Book"));  // Assert that the response contains the correct title

        // Verify: Ensure that the service method was called once
        verify(bookService, times(1)).addBook(any(Book.class));
    }
    @Test
    void testDeleteBook_Success() throws Exception {
        Long bookId = 2L;
        when(bookService.deleteBook(bookId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(delete("/api/deleteBookById/{id}", bookId))
                .andExpect(status().isOk());  // Assert 200 OK

        verify(bookService, times(1)).deleteBook(bookId);
    }
    @Test
    void testUpdateBook_Success() throws Exception {
        // Arrange: Prepare the book and mock the service to return the updated book
        Long bookId = 1L;
        Book existingBook = new Book(bookId, "Old Title", "Old Author");
        Book updatedBook = new Book(bookId, "Updated Title", "Updated Author");

        when(bookService.updateBook(eq(bookId), any(Book.class))).thenReturn(new ResponseEntity<>(updatedBook, HttpStatus.OK));

        // Act & Assert: Perform the PUT request and assert the response
        mvc.perform(put("/api/updateBook/{id}", bookId)
                        .contentType("application/json")
                        .content("{\"title\": \"Updated Title\", \"author\": \"Updated Author\"}"))
                .andExpect(status().isOk())  // Assert that the response status is 200 OK
                .andExpect(jsonPath("$.title").value("Updated Title"))  // Assert that the title was updated
                .andExpect(jsonPath("$.author").value("Updated Author"));  // Assert that the author was updated

        // Verify: Ensure the service method was called once with the correct parameters
        verify(bookService, times(1)).updateBook(eq(bookId), any(Book.class));
    }


    @Test
    void testDeleteAllBooks_Success() throws Exception{
        when(bookService.deleteAllBooks()).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(delete("/api/deleteAllBooks"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).deleteAllBooks();
    }


}
