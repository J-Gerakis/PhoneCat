package org.gerakis.phonecat.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gerakis.phonecat.controller.dto.BookingRequestDTO;
import org.gerakis.phonecat.exception.PhoneCatException;
import org.gerakis.phonecat.service.BookingService;
import org.gerakis.phonecat.service.dto.BookingInformationDTO;
import org.gerakis.phonecat.util.DateAdapter;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController("Booking Controller")
@RequestMapping(path = "${api.name}/${api.version}/bookings")
public class BookingController {

    public final static Logger logger = LogManager.getLogger(BookingController.class);

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
    public String booking(@RequestBody String body) throws PhoneCatException {
        logger.debug(body);
        try {
            BookingRequestDTO booking = gson.fromJson(body, BookingRequestDTO.class);
            if (booking.username().isEmpty()) {
                throw new PhoneCatException("Username must not be null");
            }
            if (booking.phoneId() == null) {
                throw new PhoneCatException("Phone id is missing");
            }
            BookingInformationDTO confirmationDTO = bookingService.bookPhone(booking.username(), booking.phoneId());
            return gson.toJson(confirmationDTO);
        } catch (JsonSyntaxException jse) {
            logger.error(jse);
            throw new PhoneCatException("Malformed payload", jse);
        }
    }

    @PostMapping("return/{phoneId}")
    public String returning(@PathVariable("phoneId") Long phoneId) throws PhoneCatException {
        if(phoneId < 1) {
            throw new PhoneCatException("Phone id must be positive");
        }
        try {
            return gson.toJson(bookingService.returnPhone(phoneId));
        } catch (JsonSyntaxException jse) {
            logger.error(jse);
            throw new PhoneCatException("Malformed payload", jse);
        }
    }
}
