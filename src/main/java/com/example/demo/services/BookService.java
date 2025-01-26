package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Book;
import com.example.demo.repositories.BookRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;

	public BookDto addNewBook(BookDto bookDto) throws DataIntegrityViolationException {
		validateBookDto(bookDto);
		Book book = new Book(bookDto);
		Book savedBook = bookRepository.save(book);
		BookDto savedDto = new BookDto(savedBook);
		return savedDto;
	}

	public Page<BookDto> getAllBook(Pageable pageable) {
		Page<Book> booksPage = bookRepository.findAll(pageable);
		return booksPage.map(book -> new BookDto(book));
	}

	public ResponseEntity<?> getBookById(long bookId) {
		Book book = bookRepository.findById(bookId).orElse(null);
		BookDto bookDto = null;
		if (book == null) {
			return ResponseEntity.status(404).body("Book not found");
		} else {
			bookDto = new BookDto(book);
			return ResponseEntity.status(200).body(bookDto);
		}
	}

	public ResponseEntity<?> updateBookById(long bookId, BookDto bookDto) throws DataIntegrityViolationException {
		validateBookDto(bookDto);
		Book dbBook = bookRepository.findById(bookId).orElse(null);
		BookDto bookDtoReturn = null;
		if (dbBook == null) {
			return ResponseEntity.status(404).body("Book not found");
		} else {
			Book book = new Book(bookDto);
			book.setId(bookId);
			bookRepository.save(book);
			bookDtoReturn = new BookDto(book);
			return ResponseEntity.status(200).body(bookDtoReturn);
		}

	}

	public ResponseEntity<?> deleteBookById(long bookId) {
		Book book = bookRepository.findById(bookId).orElse(null);
		if (book == null) {
			return ResponseEntity.status(404).body("Book not found");
		} else {
			bookRepository.delete(book);
			return ResponseEntity.status(200).body("Book Deleted");
		}
	}

	public void validateBookDto(BookDto bookDto) {
		if (bookDto.getTitle() == null || bookDto.getTitle().isEmpty()) {
			throw new IllegalArgumentException("Title cannot be null or empty");
		}
		if (bookDto.getAuthor() == null || bookDto.getAuthor().isEmpty()) {
			throw new IllegalArgumentException("Author cannot be null or empty");
		}
		if (bookDto.getPublishedDate() == null) {
			throw new IllegalArgumentException("Published Date cannot be null");
		}
		if (bookDto.getIsbn() == null || bookDto.getIsbn().isEmpty()) {
			throw new IllegalArgumentException("ISBN cannot be null or empty");
		}
	}

	public List<BookDto> getBooksByAuthorOrTile(String searchText) {
		List<BookDto> booksDto = new ArrayList<>();
		List<Book> books = bookRepository.findByAuthorOrTitle(searchText);
		for (Book book : books) {
			booksDto.add(new BookDto(book));
		}
		return booksDto;
	}

}
