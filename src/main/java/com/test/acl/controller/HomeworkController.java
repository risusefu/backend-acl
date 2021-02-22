/**
 * Spring Boot + JPA/Hibernate + PostgreSQL RESTful CRUD API Example (https://www.dariawan.com)
 * Copyright (C) 2020 Dariawan <hello@dariawan.com>
 *
 * Creative Commons Attribution-ShareAlike 4.0 International License
 *
 * Under this license, you are free to: # Share - copy and redistribute the
 * material in any medium or format # Adapt - remix, transform, and build upon
 * the material for any purpose, even commercially.
 *
 * The licensor cannot revoke these freedoms as long as you follow the license
 * terms.
 *
 * License terms: # Attribution - You must give appropriate credit, provide a
 * link to the license, and indicate if changes were made. You may do so in any
 * reasonable manner, but not in any way that suggests the licensor endorses you
 * or your use. # ShareAlike - If you remix, transform, or build upon the
 * material, you must distribute your contributions under the same license as
 * the original. # No additional restrictions - You may not apply legal terms or
 * technological measures that legally restrict others from doing anything the
 * license permits.
 *
 * Notices: # You do not have to comply with the license for elements of the
 * material in the public domain or where your use is permitted by an applicable
 * exception or limitation. # No warranties are given. The license may not give
 * you all of the permissions necessary for your intended use. For example,
 * other rights such as publicity, privacy, or moral rights may limit how you
 * use the material.
 *
 * You may obtain a copy of the License at
 * https://creativecommons.org/licenses/by-sa/4.0/
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package com.test.acl.controller;

import com.test.acl.domain.Homework;
import com.test.acl.exception.BadResourceException;
import com.test.acl.exception.ResourceAlreadyExistsException;
import com.test.acl.exception.ResourceNotFoundException;
import com.test.acl.request.HomeworkRequest;
import com.test.acl.service.HomeworkService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Api(description = "Endpoints for Creating, Retrieving, Updating and Deleting of Homework.",
        tags = {"homework"})
@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class HomeworkController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int ROW_PER_PAGE = 100;

    @Autowired
    private HomeworkService homeworkService;

    @ApiOperation(value = "Find Homeworks by homework", notes = "Name search by %name% format", tags = {"homework"})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful operation", response = List.class)})
    @GetMapping(value = "/homework")
    public ResponseEntity<List<Homework>> findAll(
            @ApiParam(name = "homeworkId",
                    value = "Page number, default is 1",
                    example = "1",
                    required = false)
            @RequestParam(value = "page", defaultValue = "1") int pageNumber,
            @ApiParam("Name of the homework for search.") @RequestParam(required = false) String description) {
        if (StringUtils.isEmpty(description)) {
            return ResponseEntity.ok(homeworkService.findAll(pageNumber, ROW_PER_PAGE));
        } else {
            return ResponseEntity.ok(homeworkService.findAllByName(description, pageNumber, ROW_PER_PAGE));
        }
    }

    @ApiOperation(value = "Find homework by ID", notes = "Returns a single homework", tags = {"homework"})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful operation", response = Homework.class),
        @ApiResponse(code = 404, message = "Homework not found")})
    @GetMapping(value = "/homeworks/{homeworkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Homework> findHomeworkById(
            @ApiParam(name = "homeworkId",
                    value = "Id of the homework to be obtained. Cannot be empty.",
                    example = "1",
                    required = true)
            @PathVariable long homeworkId) {
        try {
            Homework homework = homeworkService.findById(homeworkId);
            return ResponseEntity.ok(homework);  // return 200, with json body
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // return 404, with null body
        }
    }

    @ApiOperation(value = "Add a new homework", tags = {"homework"})
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Homework created"),
        @ApiResponse(code = 400, message = "Invalid input"),
        @ApiResponse(code = 409, message = "Homework already exists")})
    @PostMapping(value = "/homeworks")
    public ResponseEntity<Homework> addHomework(
            @ApiParam("Homework to add. Cannot null or empty.")
            @Valid @RequestBody HomeworkRequest homeworkRequest)
            throws URISyntaxException {
        try {
            Homework homework = new Homework();
            homework.setDescription(homeworkRequest.getDescription());
            homework.setActive(homeworkRequest.isActive());
            Homework newHomework = homeworkService.save(homework);
            return ResponseEntity.created(new URI("/api/homeworks/" + newHomework.getId()))
                    .body(homework);
        } catch (ResourceAlreadyExistsException ex) {
            // log exception first, then return Conflict (409)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (BadResourceException ex) {
            // log exception first, then return Bad Request (400)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ApiOperation(value = "Update an existing homework", tags = {"homework"})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful operation"),
        @ApiResponse(code = 400, message = "Invalid ID supplied"),
        @ApiResponse(code = 404, message = "Homework not found"),
        @ApiResponse(code = 405, message = "Validation exception")})
    @PutMapping(value = "/homeworks/{homeworkId}")
    public ResponseEntity<Homework> updateHomework(
            @ApiParam(name = "homeworkId",
                    value = "Id of the homework to be update. Cannot be empty.",
                    example = "1",
                    required = true)
            @PathVariable long homeworkId,
            @ApiParam("Homework to update. Cannot null or empty.")
            @Valid @RequestBody HomeworkRequest homeworkRequest) {
        try {
            Homework homework = new Homework();
            homework.setDescription(homeworkRequest.getDescription());
            homework.setActive(homeworkRequest.isActive());
            homework.setId(homeworkId);
            homeworkService.updateHomework(homeworkId, homework);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            // log exception first, then return Not Found (404)
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "Update an existing homework's address", tags = {"homework"})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful operation"),
        @ApiResponse(code = 404, message = "Homework not found")})
    @PatchMapping("/homeworks/{homeworkId}")
    public ResponseEntity<Void> updateAddress(
            @ApiParam(name = "homeworkId",
                    value = "Id of the homework to be update. Cannot be empty.",
                    example = "1",
                    required = true)
            @PathVariable long homeworkId,
            @ApiParam("Homework's address to update.")
            @RequestBody Homework homework) {
        try {
            homeworkService.updateHomework(homeworkId, homework);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            // log exception first, then return Not Found (404)
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "Deletes a homework", tags = {"homework"})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "successful operation"),
        @ApiResponse(code = 404, message = "Homework not found")})
    @DeleteMapping(path = "/homeworks/{homeworkId}")
    public ResponseEntity<Void> deleteHomeworkById(
            @ApiParam(name = "homeworkId",
                    value = "Id of the homework to be delete. Cannot be empty.",
                    example = "1",
                    required = true)
            @PathVariable long homeworkId) {
        try {
            homeworkService.deleteById(homeworkId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
