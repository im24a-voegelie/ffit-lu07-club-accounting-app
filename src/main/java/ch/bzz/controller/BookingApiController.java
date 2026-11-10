package ch.bzz.controller;

import ch.bzz.generated.api.BookingApi;
import ch.bzz.generated.model.Booking;
import ch.bzz.generated.model.UpdateBookingsRequest;
import ch.bzz.model.Project;
import ch.bzz.repository.BookingRepository;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class BookingApiController implements BookingApi {

    private final BookingRepository bookingRepository;
    private final ProjectRepository projectRepository;
    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    public BookingApiController(BookingRepository bookingRepository, ProjectRepository projectRepository,
                                JwtUtil jwtUtil, HttpServletRequest request) {
        this.bookingRepository = bookingRepository;
        this.projectRepository = projectRepository;
        this.jwtUtil = jwtUtil;
        this.request = request;
    }

    @Override
    public ResponseEntity<List<Booking>> getBookings() {
        Optional<Project> project = jwtUtil.extractProjectName(request.getHeader(HttpHeaders.AUTHORIZATION))
                .flatMap(projectRepository::findById);
        if (project.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Booking> bookings = bookingRepository.findByProject(project.get()).stream()
                .map(this::toApiBooking)
                .toList();
        return ResponseEntity.ok(bookings);
    }

    @Override
    public ResponseEntity<Void> updateBookings(UpdateBookingsRequest updateBookingsRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    private Booking toApiBooking(ch.bzz.model.Booking booking) {
        return new Booking()
                .number(booking.getBookingNumber())
                .date(booking.getDate())
                .text(booking.getText())
                .debit(booking.getDebitAccount().getAccountNumber())
                .credit(booking.getCreditAccount().getAccountNumber())
                .amount(booking.getAmount().floatValue());
    }
}
