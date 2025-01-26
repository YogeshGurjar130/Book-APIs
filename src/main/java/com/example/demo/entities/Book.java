package com.example.demo.entities;

import java.time.LocalDate;

import com.example.demo.dto.BookDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false)
	private String title;
	@Column(nullable = false)
	private String author;
	@Column(nullable = false)
	private LocalDate publishedDate;
	@Column(nullable = false, unique = true)
	private String isbn;

	public Book() {
	}

	public Book(BookDto bookDto) {
		this.id = bookDto.getId();
		this.title = bookDto.getTitle();
		this.author = bookDto.getAuthor();
		this.publishedDate = bookDto.getPublishedDate();
		this.isbn = bookDto.getIsbn();
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
