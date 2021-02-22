/**
 * Spring Boot + JPA/Hibernate + PostgreSQL RESTful CRUD API Example (https://www.dariawan.com)
 * Copyright (C) 2020 Dariawan <hello@dariawan.com>
 * <p>
 * Creative Commons Attribution-ShareAlike 4.0 International License
 * <p>
 * Under this license, you are free to:
 * # Share - copy and redistribute the material in any medium or format
 * # Adapt - remix, transform, and build upon the material for any purpose,
 * even commercially.
 * <p>
 * The licensor cannot revoke these freedoms
 * as long as you follow the license terms.
 * <p>
 * License terms:
 * # Attribution - You must give appropriate credit, provide a link to the
 * license, and indicate if changes were made. You may do so in any
 * reasonable manner, but not in any way that suggests the licensor
 * endorses you or your use.
 * # ShareAlike - If you remix, transform, or build upon the material, you must
 * distribute your contributions under the same license as the original.
 * # No additional restrictions - You may not apply legal terms or
 * technological measures that legally restrict others from doing anything the
 * license permits.
 * <p>
 * Notices:
 * # You do not have to comply with the license for elements of the material in
 * the public domain or where your use is permitted by an applicable exception
 * or limitation.
 * # No warranties are given. The license may not give you all of
 * the permissions necessary for your intended use. For example, other rights
 * such as publicity, privacy, or moral rights may limit how you use
 * the material.
 * <p>
 * You may obtain a copy of the License at
 * https://creativecommons.org/licenses/by-sa/4.0/
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package com.test.acl.service;

import com.test.acl.domain.Homework;
import com.test.acl.exception.BadResourceException;
import com.test.acl.exception.ResourceAlreadyExistsException;
import com.test.acl.exception.ResourceNotFoundException;
import com.test.acl.repository.HomeworkRepository;
import com.test.acl.specification.HomeworkSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomeworkService {

    @Autowired
    private HomeworkRepository homeworkRepository;

    private boolean existsById(Long id) {
        return homeworkRepository.existsById(id);
    }

    public Homework findById(Long id) throws ResourceNotFoundException {
        Homework homework = homeworkRepository.findById(id).orElse(null);
        if (homework == null) {
            throw new ResourceNotFoundException("Cannot find homework with id: " + id);
        } else return homework;
    }

    public List<Homework> findAll(int pageNumber, int rowPerPage) {
        List<Homework> homeworks = new ArrayList<>();
        homeworkRepository.findAll(PageRequest.of(pageNumber - 1, rowPerPage)).forEach(homeworks::add);
        return homeworks;
    }

    public List<Homework> findAllByName(String description, int pageNumber, int rowPerPage) {
        Homework filter = new Homework();
        filter.setDescription(description);
        Specification<Homework> spec = new HomeworkSpecification(filter);

        List<Homework> homeworks = new ArrayList<>();
        homeworkRepository.findAll(spec, PageRequest.of(pageNumber - 1, rowPerPage)).forEach(homeworks::add);
        return homeworks;
    }

    public Homework save(Homework homework) throws BadResourceException, ResourceAlreadyExistsException {
        if (!StringUtils.isEmpty(homework.getDescription())) {
            if (homework.getId() != null && existsById(homework.getId())) {
                throw new ResourceAlreadyExistsException("Homework with id: " + homework.getId() +
                        " already exists");
            }
                return homeworkRepository.save(homework);
        } else {
            BadResourceException exc = new BadResourceException("Failed to save homework");
            exc.addErrorMessage("Homework is null or empty");
            throw exc;
        }
    }

    public void updateHomework(Long id, Homework homework)
            throws ResourceNotFoundException {
        Homework homeworkUpdate = findById(id);
        homeworkUpdate.setDescription(homework.getDescription());
        homeworkUpdate.setActive(homework.isActive());
        homeworkRepository.save(homework);
    }

    public void update(Homework homework)
            throws BadResourceException, ResourceNotFoundException {
        if (!StringUtils.isEmpty(homework.getDescription())) {
            if (!existsById(homework.getId())) {
                throw new ResourceNotFoundException("Cannot find Homework with id: " + homework.getId());
            }
            homeworkRepository.save(homework);
        } else {
            BadResourceException exc = new BadResourceException("Failed to save homework");
            exc.addErrorMessage("Homework is null or empty");
            throw exc;
        }
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!existsById(id)) {
            throw new ResourceNotFoundException("Cannot find homework with id: " + id);
        } else {
            homeworkRepository.deleteById(id);
        }
    }

    public Long count() {
        return homeworkRepository.count();
    }
}
