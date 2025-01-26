package com.example.demo.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Book;
import com.example.demo.repositories.BookRepository;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {

	@Mock
	private BookRepository bookRepository;
	@InjectMocks
	private BookService bookService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// JUnit test for addNewBook method
	@DisplayName("JUnit test for addNewBook method")
	@Test
	public void givenBookObject_whenAddNewBook_thenReturnBookObject() {
		Book book = new Book();
		book.setId(1L);
		book.setAuthor("Yogesh");
		book.setTitle("Learn Java");
		book.setPublishedDate(LocalDate.now());
		book.setIsbn("12345");

		when(bookRepository.save(any(Book.class))).thenReturn(book);

		BookDto bookDto = new BookDto(book);
		BookDto savedBook = bookService.addNewBook(bookDto);

		assertThat(savedBook).isNotNull();
		assertThat(savedBook.getAuthor()).isEqualTo(bookDto.getAuthor());
	}

	// JUnit test for getAllBook method
	@DisplayName("JUnit test for getAllBook method")
	@Test
	public void givenNothing_whenGetAllBook_thenReturnBookList() {
		Book book1 = new Book();
		book1.setId(1L);
		book1.setAuthor("Yogesh");
		book1.setTitle("Learn Java");
		book1.setPublishedDate(LocalDate.now());
		book1.setIsbn("12345");

		Book book2 = new Book();
		book2.setId(2L);
		book2.setAuthor("John");
		book2.setTitle("Learn Spring");
		book2.setPublishedDate(LocalDate.now());
		book2.setIsbn("67890");

		Pageable pageable = PageRequest.of(0, 5);
		
		Page<Book> bookPage = new PageImpl<>(List.of(book1, book2), pageable, 2);
		
		when(bookRepository.findAll(pageable)).thenReturn(bookPage);

		Page<BookDto> result = bookService.getAllBook(pageable);

		assertThat(result).isNotNull();
		assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Yogesh", result.getContent().get(0).getAuthor());
        assertEquals("Learn Spring", result.getContent().get(1).getTitle());
	}

	// JUnit test for getBookById method
	@DisplayName("JUnit test for getBookById method")
	@Test
	public void givenBookId_whenGetBookById_thenReturnBookOfGivenId() {

		long bookId = 1L;
		Book book = new Book();
		book.setId(1L);
		book.setAuthor("Yogesh");
		book.setTitle("Learn Java");
		book.setPublishedDate(LocalDate.now());
		book.setIsbn("12345");

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		ResponseEntity<?> response = bookService.getBookById(bookId);

		assertThat(response.getBody()).isInstanceOf(BookDto.class);

		BookDto bookDto = (BookDto) response.getBody();
		assertThat(bookDto.getAuthor()).isEqualTo(book.getAuthor());
	}

	@Test
	public void givenBookId_whenBookDoesNotExist_thenReturnBookNotFound() {
		// given
		long bookId = 1L;
		BookDto bookDto = new BookDto();
		bookDto.setAuthor("Updated Author");
		bookDto.setTitle("Updated Title");
		bookDto.setPublishedDate(LocalDate.now());
		bookDto.setIsbn("12345");

		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		ResponseEntity<?> response = bookService.updateBookById(bookId, bookDto);

		assertThat(response.getBody()).isEqualTo("Book not found");
	}

	// JUnit test for updateBookById method
	@DisplayName("JUnit test for updateBookById method")
	@Test
	public void givenBookIdAndBookObject_whenUpdateBookById_thenReturnUpdatedBook() {

		long bookId = 1L;
		Book book = new Book();
		book.setId(1L);
		book.setAuthor("Yogesh");
		book.setTitle("Learn Java");
		book.setPublishedDate(LocalDate.now());
		book.setIsbn("12345");

		BookDto updatedBookDto = new BookDto();
		updatedBookDto.setAuthor("Yogesh Updated");
		updatedBookDto.setTitle("Learn Java");
		updatedBookDto.setPublishedDate(LocalDate.now());
		updatedBookDto.setIsbn("12345");

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		ResponseEntity<?> response = bookService.updateBookById(bookId, updatedBookDto);

		assertThat(response.getBody()).isInstanceOf(BookDto.class);

		BookDto savedDto = (BookDto) response.getBody();
		assertThat(savedDto.getAuthor()).isEqualTo(updatedBookDto.getAuthor());
	}

	// JUnit test for deleteBookById method
	@DisplayName("JUnit test for deleteBookById method")
	@Test
	public void givenBookId_whenDeleteBookById_thenDeleteBookAndReturnBookDeleted() {
		long bookId = 1L;
		Book book = new Book();
		book.setId(bookId);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		ResponseEntity<?> response = bookService.deleteBookById(bookId);

		assertThat(response.getBody()).isEqualTo("Book Deleted");

	}
	
	// JUnit test for getBooksByAuthorOrTile method
	@DisplayName("JUnit test for getBooksByAuthorOrTile method")
	@Test
	public void givenSearchText_whenGetBooksByAuthorOrTile_thenReturnFilteredBooks() {
		String searchTxt = "Yogesh";
		
		Book book1 = new Book();
		book1.setId(1L);
		book1.setAuthor("Yogesh");
		book1.setTitle("Learn Java");
		book1.setPublishedDate(LocalDate.now());
		book1.setIsbn("12345");

		Book book2 = new Book();
		book2.setId(2L);
		book2.setAuthor("Yogesh Updated");
		book2.setTitle("Learn Java");
		book2.setPublishedDate(LocalDate.now());
		book2.setIsbn("12345");

 		when(bookRepository.findByAuthorOrTitle(searchTxt)).thenReturn(List.of(book1, book2));

		List<BookDto> booksDto = bookService.getBooksByAuthorOrTile(searchTxt);

		assertThat(booksDto).isNotNull();
		assertThat(booksDto.get(0).getAuthor()).isEqualTo(book1.getAuthor()); 

	}

}
