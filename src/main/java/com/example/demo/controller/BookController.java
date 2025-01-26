package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BookDto;
import com.example.demo.services.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api")
public class BookController {

	@Autowired
	private BookService bookService;
	Logger logger = LoggerFactory.getLogger(BookController.class);

	@Operation(summary = "Create a new book", description = "Create a new book with the provided details")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully created the book"),
			@ApiResponse(responseCode = "400", description = "Invalid input provided"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PostMapping("/books")
	public ResponseEntity<?> createBook(@RequestBody BookDto bookDto) {
		try {
			return ResponseEntity.status(200).body(bookService.addNewBook(bookDto));
		} catch (DataIntegrityViolationException e) {
			logger.error(e.getLocalizedMessage());
			if (e.getMessage().contains("ConstraintViolationException") || e.getMessage().contains("isbn")) {
				return ResponseEntity.status(400).body("Book with the same ISBN already exists");
			} else {
				return ResponseEntity.status(500).body("Failed to save book");
			}
		} catch (IllegalArgumentException e) {
			logger.info(e.getLocalizedMessage());
			return ResponseEntity.status(400).body(e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			return ResponseEntity.status(500).body("Failed to save book");
		}

	}

	@Operation(summary = "Get all books from database with Pagination", description = "Get All Books based on user input like page number and size")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully find all the books from database and return to end user"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/books")
	public ResponseEntity<?> getAllBook(Pageable pageable) {
		try {
			Page<BookDto> booksPage = bookService.getAllBook(pageable);
			return ResponseEntity.status(200).body(booksPage);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			return ResponseEntity.status(500).body("Failed to fetch books");
		}
	}

	@Operation(summary = "Get particular book from database", description = "Get Book from database for the given bookId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully find the book from database and return to end user"),
			@ApiResponse(responseCode = "404", description = "Book not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/books/{id}")
	public ResponseEntity<?> getBookByBookId(@PathVariable("id") long bookId) {
		try {
			return bookService.getBookById(bookId);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			return ResponseEntity.status(500).body("Failed to fetch book");
		}

	}

	@Operation(summary = "Update book details in the database", description = "Update Book into database for the given bookId with given details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully find the book from database and updated the book and return to end user"),
			@ApiResponse(responseCode = "404", description = "Book not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PutMapping("/books/{id}")
	public ResponseEntity<?> updateBookByBookId(@PathVariable("id") long bookId, @RequestBody BookDto bookDto) {
		try {
			return bookService.updateBookById(bookId, bookDto);
		} catch (DataIntegrityViolationException e) {
			logger.error(e.getLocalizedMessage());
			if (e.getMessage().contains("ConstraintViolationException") || e.getMessage().contains("isbn")) {
				return ResponseEntity.status(400).body("Book with the same ISBN already exists");
			} else {
				logger.error(e.getLocalizedMessage());
				return ResponseEntity.status(500).body("Failed to update book");
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getLocalizedMessage());
			return ResponseEntity.status(400).body(e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			return ResponseEntity.status(500).body("Failed to update book");
		}
	}

	@Operation(summary = "Delete particular book from database", description = "Delete Book from database for the given bookId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully deleted the book from database and return success message to end user"),
			@ApiResponse(responseCode = "404", description = "Book not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@DeleteMapping("/books/{id}")
	public ResponseEntity<?> deleteBookByBookId(@PathVariable("id") long bookId) {
		try {
			return bookService.deleteBookById(bookId);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			return ResponseEntity.status(500).body("Failed to delete book");
		}
	}

	@Operation(summary = "Get all filtered books from database", description = "Get all books from database filtered by author or title for given text")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully find the filtered books from the database or empty list and return end user"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/search")
	public ResponseEntity<?> getBooksByAuthorOrTitle(@RequestParam String searchText) {
		try {
			List<BookDto> booksDto = bookService.getBooksByAuthorOrTile(searchText);
			return ResponseEntity.status(200).body(booksDto);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			return ResponseEntity.status(500).body("Failed to fetch book");
		}

	}
}
