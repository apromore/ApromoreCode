package org.apromore.service.impl;

import org.apromore.dao.UserDao;
import org.apromore.dao.jpa.UserDaoJpa;
import org.apromore.dao.model.EditSession;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
@PrepareForTest({ UserDaoJpa.class })
public class UserServiceImplUnitTest {

    @Rule
	public ExpectedException exception = ExpectedException.none();

    @Autowired
    private UserDao usrDAOJpa;

    private UserServiceImpl usrServiceImpl;

    @Before
    public final void setUp() throws Exception {
        usrServiceImpl = new UserServiceImpl();
        usrDAOJpa = createMock(UserDao.class);
        usrServiceImpl.setUserDao(usrDAOJpa);
    }

    @Test
    public void getAllUsers() {
        List<User> users = new ArrayList<User>();

        expect(usrDAOJpa.findAllUsers()).andReturn(users);
        replay(usrDAOJpa);

        List<User> serviceUsers = usrServiceImpl.findAllUsers();
        verify(usrDAOJpa);
        assertThat(serviceUsers, equalTo(users));
    }

    @Test
    public void getUser() throws Exception {
        String username = "jaybob";
        User usr = new User();

        expect(usrDAOJpa.findUserByLogin(username)).andReturn(usr);
        replay(usrDAOJpa);

        User serviceUsr = usrServiceImpl.findUserByLogin(username);
        verify(usrDAOJpa);
        assertThat(serviceUsr, equalTo(usr));
    }

    @Test
    public void getUserNotFound() throws Exception {
        String username = "jaybob";

        expect(usrDAOJpa.findUserByLogin(username)).andReturn(null);
        replay(usrDAOJpa);

        exception.expect(UserNotFoundException.class);
        usrServiceImpl.findUserByLogin(username);

        verify(usrDAOJpa);
    }


    @Test
    public void writeUser() {
        String username = "username";
        User usr = createUser();

        expect(usrDAOJpa.findUserByLogin(username)).andReturn(usr);
        expect(usrDAOJpa.update((User) anyObject())).andReturn(usr);
        replay(usrDAOJpa);

        usrServiceImpl.writeUser(usr);
        verify(usrDAOJpa);
    }

    private User createUser() {
        User user = new User();

        user.setUsername("username");
        user.setFirstname("firstname");
        user.setLastname("lastname");
        user.setEmail("email");
        user.setEditSessions(new HashSet<EditSession>(1));

        return user;
    }
}
