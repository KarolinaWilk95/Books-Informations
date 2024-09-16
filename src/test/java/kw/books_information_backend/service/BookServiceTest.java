package kw.books_information_backend.service;

import kw.books_information_backend.NotFoundException;
import kw.books_information_backend.model.Book;
import kw.books_information_backend.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void addOneBook() {
        // given
        var book = new Book();
        book.setId(null);

        var savedBook = new Book();
        savedBook.setId(2L);

        when(bookRepository.save(book)).thenReturn(savedBook);

        // when
        var result = bookService.addOneBook(book);

        // then
        verify(bookRepository).save(book);

        assertThat(result).isEqualTo(savedBook);
    }

    @Test
    void getAllBooks() {
        // given
        List<Book> savedBooks = new ArrayList<>();

        when(bookRepository.findAll()).thenReturn(savedBooks);
        // when

        var result = bookRepository.findAll();

        // then

        verify(bookRepository).findAll();
        assertThat(result).isEqualTo(savedBooks);
    }

    @Test
    void findBookByIdIfExist() {
        // given
        var idOfBook = 1L;
        var book = new Book();
        book.setId(1L);

        when(bookRepository.findById(idOfBook)).thenReturn(Optional.of(book));
        // when

        var result = bookService.findBookById(idOfBook);

        // then
        verify(bookRepository).findById(idOfBook);
        assertThat(result).hasValue(book);

    }


    @Test
    void updateBookIfExist() {
        // given
        var bookID = 1L;
        var existingBook = new Book();
        existingBook.setId(bookID);
        existingBook.setBookName("Tngo");
        existingBook.setAuthorName("Mrożek");
        existingBook.setBookCategory("drame");
        existingBook.setYearOfPublication((short) 124);
        existingBook.setRate((byte) 1);

        var updatedBook = new Book();
        updatedBook.setId(bookID);
        updatedBook.setBookName("Tango");
        updatedBook.setAuthorName("Sławomir Mrożek");
        updatedBook.setBookCategory("drama");
        updatedBook.setYearOfPublication((short) 1964);
        updatedBook.setRate((byte) 5);

        when(bookRepository.findById(bookID)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(eq(updatedBook))).thenReturn(updatedBook);

        // when
        var result = bookService.updateBook(bookID, updatedBook);

        // then
        verify(bookRepository).findById(bookID);
        verify(bookRepository).save(eq(updatedBook));
        assertThat(result).isEqualTo(updatedBook);

    }

    @Test
    void updateBookIfNotExist() {
        // given
        var bookID = 1L;
        var updatedBook = new Book();
        updatedBook.setId(bookID);
        updatedBook.setBookName("Tango");
        updatedBook.setAuthorName("Sławomir Mrożek");
        updatedBook.setBookCategory("drama");
        updatedBook.setYearOfPublication((short) 1964);
        updatedBook.setRate((byte) 5);

        when(bookRepository.findById(bookID)).thenReturn(Optional.empty());

        // when
        var exception = assertThrows(NotFoundException.class, () -> bookService.updateBook(bookID, updatedBook));

        // then
        verify(bookRepository).findById(bookID);
        assertThat(exception).hasMessage("Book not found");
    }

    @Test
    void updateBookIfDifferentID() {
        // given
        var existingBookID = 1L;
        var existingBook = new Book();
        existingBook.setId(existingBookID);
        var updatedBookID = 2L;
        var updatedBook = new Book();
        updatedBook.setId(updatedBookID);

        when(bookRepository.findById(existingBookID)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        // when

        var result = bookService.updateBook(existingBookID, updatedBook);

        // then
        verify(bookRepository).findById(existingBookID);
        verify(bookRepository, never()).findById(updatedBookID);
        assertThat(result.getId()).isEqualTo(existingBookID);
    }


    @Test
    void deleteBookIfExist() {
        // given
        var bookID = 1L;
        var existingBook = new Book();
        existingBook.setId(bookID);
        when(bookRepository.findById(bookID)).thenReturn(Optional.of(existingBook));
        doNothing().when(bookRepository).deleteById(bookID);

        // when
        bookService.deleteBook(bookID);

        // then
        verify(bookRepository).findById(bookID);
        verify(bookRepository).deleteById(bookID);

    }

    @Test
    void deleteBookIfNotExist() {
        // given
        var bookID = 1L;
        var existingBook = new Book();
        existingBook.setId(bookID);
        when(bookRepository.findById(bookID)).thenReturn(Optional.empty());
        // when
        var exception = assertThrows(NotFoundException.class, () -> bookService.deleteBook(bookID));
        // then
        verify(bookRepository).findById(bookID);
        assertThat(exception).hasMessage("Book not found");

    }
}