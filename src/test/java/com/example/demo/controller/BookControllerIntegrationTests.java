package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.BookDto;
import com.example.demo.services.BookService;

@WebMvcTest(BookController.class)
public class BookControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private BookService bookService;

	// Integration test for createBook method
	@DisplayName("Integration test for createBook method")
	@Test
	public void givenBookDto_whenCreateBook_thenReturnBookDto() throws Exception {

		BookDto bookDto = new BookDto();
		bookDto.setAuthor("Yogesh");
		bookDto.setTitle("Learn Java");
		bookDto.setPublishedDate(LocalDate.now());
		bookDto.setIsbn("12345");

		when(bookService.addNewBook(any(BookDto.class))).thenReturn(bookDto);

		mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content("""
				    {
				        "author": "Yogesh",
				        "title": "Learn Java",
				        "publishedDate": "2025-01-25",
				        "isbn": "12345"
				    }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$.author").value("Yogesh"))
				.andExpect(jsonPath("$.title").value("Learn Java"));
	}
	
	@Test
	public void givenInvalidBookDto_whenCreateBook_thenReturnMessage() throws Exception {

		BookDto bookDto = new BookDto();
		bookDto.setAuthor("");

		when(bookService.addNewBook(any(BookDto.class))).thenThrow(new IllegalArgumentException("Author cannot be null or empty"));

		mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content("""
				    {
				        "author": "",
				        "title": "Learn Java",
				        "publishedDate": "2025-01-25",
				        "isbn": "12345"
				    }
				""")).andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string("Author cannot be null or empty"));
	}

	// Integration test for getAllBook method
	@DisplayName("Integration test for getAllBook method")
	@Test
	public void givenNothing_whenGetAllBooks_thenReturnListOfBookDto() throws Exception {

		BookDto bookDto1 = new BookDto();
		bookDto1.setId(1L);
		bookDto1.setAuthor("Yogesh");
		bookDto1.setTitle("Learn Java");
		bookDto1.setPublishedDate(LocalDate.now());
		bookDto1.setIsbn("12345");

		BookDto bookDto2 = new BookDto();
		bookDto2.setId(2L);
		bookDto2.setAuthor("Mohit");
		bookDto2.setTitle("Core Java");
		bookDto2.setPublishedDate(LocalDate.now());
		bookDto2.setIsbn("123456");

        Pageable pageable = PageRequest.of(0, 5);
		
		Page<BookDto> bookPage = new PageImpl<>(List.of(bookDto1, bookDto2), pageable, 2);

		when(bookService.getAllBook(pageable)).thenReturn(bookPage);

		mockMvc.perform(get("/api/books?page=0&size=5").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].author").value("Yogesh")).andExpect(jsonPath("$.content[1].author").value("Mohit"));
	}

	// Integration test for getBookById method
	@DisplayName("Integration test for getBookById method")
	@Test
	public void givenBookId_whenGetBookById_thenReturnBookDtoOfId() throws Exception {

		long bookId = 1L;
		BookDto bookDto = new BookDto();
		bookDto.setId(bookId);
		bookDto.setAuthor("Yogesh");
		bookDto.setTitle("Learn Java");
		bookDto.setPublishedDate(LocalDate.now());
		bookDto.setIsbn("12345");

		Mockito.<ResponseEntity<?>>when(bookService.getBookById(bookId))
				.thenReturn(ResponseEntity.status(200).body(bookDto));

		mockMvc.perform(get("/api/books/{id}", 1L).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.title").value("Learn Java"));
	}

	@Test
	public void givenInvalidBookId_whenGetBookById_thenReturnNotFound() throws Exception {

		long bookId = 1L;
		BookDto bookDto = new BookDto();
		bookDto.setId(bookId);
		bookDto.setAuthor("Yogesh");
		bookDto.setTitle("Learn Java");
		bookDto.setPublishedDate(LocalDate.now());
		bookDto.setIsbn("12345");

		Mockito.<ResponseEntity<?>>when(bookService.getBookById(2L))
				.thenReturn(ResponseEntity.status(404).body("Book not found"));

		mockMvc.perform(get("/api/books/{id}", 2L).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(content().string("Book not found"));
	}

	// Integration test for updateBookById method
	@DisplayName("Integration test for updateBookById method")
	@Test
	public void givenBookIdAndBookDto_whenUpdateBookById_thenReturnUpdatedDto() throws Exception {

		long bookId = 1L;
		BookDto updatedBookDto = new BookDto();
		updatedBookDto.setId(bookId);
		updatedBookDto.setAuthor("Yogesh Updated");
		updatedBookDto.setTitle("Learn Java");
		updatedBookDto.setPublishedDate(LocalDate.now());
		updatedBookDto.setIsbn("12345");

		Mockito.<ResponseEntity<?>>when(bookService.updateBookById(Mockito.eq(bookId), Mockito.any(BookDto.class)))
				.thenReturn(ResponseEntity.status(200).body(updatedBookDto));

		mockMvc.perform(put("/api/books/{id}", bookId).contentType(MediaType.APPLICATION_JSON).content("""
					    {
				        "author": "Yogesh Updated",
				        "title": "Learn Java",
				        "publishedDate": "2025-01-25",
				        "isbn": "12345"
				    }
				""")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.author").value("Yogesh Updated"));
	}

	// Integration test for deleteBookById method
	@DisplayName("Integration test for deleteBookById method")
	@Test
	void givenBookId_whenDeleteBook_thenReturnBookDeleted() throws Exception {
		long bookId = 1L;
		Mockito.<ResponseEntity<?>>when(bookService.deleteBookById(bookId))
				.thenReturn(ResponseEntity.status(200).body("Book Deleted"));

		mockMvc.perform(delete("/api/books/{id}", bookId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().string("Book Deleted"));
	}
	
	@Test
	void givenInvalidBookId_whenDeleteBook_thenReturnNotFound() throws Exception {
		long bookId = 1L;
		Mockito.<ResponseEntity<?>>when(bookService.deleteBookById(bookId))
				.thenReturn(ResponseEntity.status(404).body("Book not found"));

		mockMvc.perform(delete("/api/books/{id}", bookId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(content().string("Book not found"));
	}

}
