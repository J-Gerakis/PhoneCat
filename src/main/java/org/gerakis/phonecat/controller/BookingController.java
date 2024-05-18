package org.gerakis.phonecat.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.gerakis.phonecat.controller.dto.BookingRequestDTO;
import org.gerakis.phonecat.service.BookingService;
import org.gerakis.phonecat.service.dto.BookingInformationDTO;
import org.gerakis.phonecat.util.DateAdapter;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController("Booking Controller")
@RequestMapping(path = "${api.name}/${api.version}/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final Gson gson;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
                .serializeNulls()
                .create();
    }


    @PostMapping("book")
    public String booking(@RequestBody String body) {
        BookingRequestDTO booking = gson.fromJson(body, BookingRequestDTO.class);
        //.... validation
        BookingInformationDTO confirmationDTO = bookingService.bookPhone(booking.username(), booking.phoneId());
        return gson.toJson(confirmationDTO);
    }

    @PostMapping("return/{phoneId}")
    public String returning(@PathVariable("phoneId") Long phoneId) {
        return gson.toJson(bookingService.returnPhone(phoneId));
    }
}
