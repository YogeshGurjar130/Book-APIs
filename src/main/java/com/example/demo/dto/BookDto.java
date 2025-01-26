package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.entities.Book;

public class BookDto {

	private long id;
	private String title;
	private String author;
	private LocalDate publishedDate;
	private String isbn;

	public BookDto() {
	}

	public BookDto(Book book) {
		this.id = book.getId();
		this.title = book.getTitle();
		this.author = book.getAuthor();
		this.publishedDate = book.getPublishedDate();
		this.isbn = book.getIsbn();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public LocalDate getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(LocalDate publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

}
