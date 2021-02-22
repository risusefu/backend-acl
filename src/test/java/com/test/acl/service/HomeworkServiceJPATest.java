/**
 * Spring Boot + JPA/Hibernate + PostgreSQL RESTful CRUD API Example (https://www.dariawan.com)
 * Copyright (C) 2020 Dariawan <hello@dariawan.com>
 *
 * Creative Commons Attribution-ShareAlike 4.0 International License
 *
 * Under this license, you are free to:
 * # Share - copy and redistribute the material in any medium or format
 * # Adapt - remix, transform, and build upon the material for any purpose,
 *   even commercially.
 *
 * The licensor cannot revoke these freedoms
 * as long as you follow the license terms.
 *
 * License terms:
 * # Attribution - You must give appropriate credit, provide a link to the
 *   license, and indicate if changes were made. You may do so in any
 *   reasonable manner, but not in any way that suggests the licensor
 *   endorses you or your use.
 * # ShareAlike - If you remix, transform, or build upon the material, you must
 *   distribute your contributions under the same license as the original.
 * # No additional restrictions - You may not apply legal terms or
 *   technological measures that legally restrict others from doing anything the
 *   license permits.
 *
 * Notices:
 * # You do not have to comply with the license for elements of the material in
 *   the public domain or where your use is permitted by an applicable exception
 *   or limitation.
 * # No warranties are given. The license may not give you all of
 *   the permissions necessary for your intended use. For example, other rights
 *   such as publicity, privacy, or moral rights may limit how you use
 *   the material.
 *
 * You may obtain a copy of the License at
 *   https://creativecommons.org/licenses/by-sa/4.0/
 *   https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package com.test.acl.service;

import com.test.acl.domain.Homework;
import com.test.acl.exception.BadResourceException;
import com.test.acl.exception.ResourceAlreadyExistsException;
import com.test.acl.exception.ResourceNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import javax.sql.DataSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HomeworkServiceJPATest {

    @Autowired 
    private DataSource dataSource;
    
    @Autowired 
    private HomeworkService homeworkService;
    
    @Before
    public void cleanTestData() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "delete from tareas where descripcion not like ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%Task 1");
            ps.executeUpdate();
        }
    }
    
    @Test
    public void testFindAllHomework() throws BadResourceException, ResourceAlreadyExistsException {
        Homework c = new Homework();
        c.setDescription("Task 1");
        homeworkService.save(c);

        List<Homework> homeworks = homeworkService.findAll(1, 20);
        assertNotNull(homeworks);
        assertTrue(homeworks.size() == 1);
        for (Homework homework : homeworks) {
            assertNotNull(homework.getId());
            assertNotNull(homework.getDescription());
        }
    }
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    
    @Test
    public void testSaveUpdateDeleteHomework() throws Exception{
        Homework c = new Homework();
        c.setDescription("Task 1");
        
        homeworkService.save(c);
        assertNotNull(c.getId());
        
        Homework findHomework = homeworkService.findById(c.getId());
        assertEquals("Task 1", findHomework.getDescription());
        
        // test delete
        homeworkService.deleteById(c.getId());
        
        // query after delete
        exceptionRule.expect(ResourceNotFoundException.class);
        homeworkService.findById(c.getId());
    }    
}
