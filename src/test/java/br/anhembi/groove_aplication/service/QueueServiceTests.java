package br.anhembi.groove_aplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import br.anhembi.groove_aplication.Structures.UserQueue;
import br.anhembi.groove_aplication.entities.User;
import br.anhembi.groove_aplication.repository.UserRepo;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("QueueService Tests")
class QueueServiceTests {

    @Autowired
    private QueueService queueService;

    @Autowired
    private UserRepo userRepo;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        testUser = new User("12345678901", "John Doe", 25, "john@example.com", "1", null, null, true);
    }

    @Test
    @DisplayName("Should enqueue user successfully")
    void testEnqueueUser() {
        UserQueue queue = queueService.getDay1Queue();
        int initialSize = queue.getCount();

        queueService.enqueueUser(testUser);

        assertEquals(initialSize + 1, queue.getCount());
    }

    @Test
    @DisplayName("Should not enqueue null user")
    void testEnqueueNullUser() {
        UserQueue queue = queueService.getDay1Queue();
        int initialSize = queue.getCount();

        queueService.enqueueUser(null);

        assertEquals(initialSize, queue.getCount());
    }

    @Test
    @DisplayName("Should not enqueue VIP user")
    void testEnqueueVIPUser() {
        testUser.setDia("VIP");
        UserQueue queue = queueService.getQueueByDia("VIP"); // falls to bothQueue
        int initialSize = queue.getCount();

        queueService.enqueueUser(testUser);

        assertEquals(initialSize, queue.getCount());
    }

    @Test
    @DisplayName("Should not enqueue inactive user")
    void testEnqueueInactiveUser() {
        testUser.setSituacao(false);
        UserQueue queue = queueService.getDay1Queue();
        int initialSize = queue.getCount();

        queueService.enqueueUser(testUser);

        assertEquals(initialSize, queue.getCount());
    }

    @Test
    @DisplayName("Should dequeue user successfully")
    void testDequeueUser() {
        userRepo.save(testUser);
        queueService.enqueueUser(testUser);

        User dequeued = queueService.dequeueUser("1");

        assertNotNull(dequeued);
        assertEquals(testUser.getCpf(), dequeued.getCpf());
    }

    @Test
    @DisplayName("Should return null when dequeueing from empty queue")
    void testDequeueFromEmptyQueue() {
        User dequeued = queueService.dequeueUser("2");
        assertNull(dequeued);
    }

    @Test
    @DisplayName("Should get user position by CPF")
    void testGetUserPositionByCpf() {
        userRepo.save(testUser);
        queueService.enqueueUser(testUser);

        int position = queueService.getUserPositionByCpf("12345678901", "1");

        assertTrue(position > 0);
    }

    @Test
    @DisplayName("Should get occupation rate")
    void testGetOccupationRate() {
        userRepo.save(testUser);
        queueService.enqueueUser(testUser);

        double rate = queueService.getOccupationRate("1");

        assertTrue(rate > 0);
        assertTrue(rate <= 100);
    }

    @Test
    @DisplayName("Should remove user by CPF")
    void testRemoveUserByCpf() {
        userRepo.save(testUser);
        queueService.enqueueUser(testUser);

        boolean removed = queueService.removeUserByCpf("12345678901", "1");

        assertTrue(removed);
    }

    @Test
    @DisplayName("Should get queue by day 1")
    void testGetQueueByDay1() {
        UserQueue queue = queueService.getQueueByDia("1");
        assertNotNull(queue);
        assertSame(queue, queueService.getDay1Queue());
    }

    @Test
    @DisplayName("Should get queue by day 2")
    void testGetQueueByDay2() {
        UserQueue queue = queueService.getQueueByDia("2");
        assertNotNull(queue);
        assertSame(queue, queueService.getDay2Queue());
    }

    @Test
    @DisplayName("Should get pass queue for other days")
    void testGetQueueByPass() {
        UserQueue queue = queueService.getQueueByDia("Pass");
        assertNotNull(queue);
        assertSame(queue, queueService.getBothQueue());
    }

    @Test
    @DisplayName("Should peek at first user in queue")
    void testPeekUser() {
        userRepo.save(testUser);
        queueService.enqueueUser(testUser);

        User peeked = queueService.peek("1");

        assertNotNull(peeked);
        assertEquals(testUser.getCpf(), peeked.getCpf());
    }

    @Test
    @DisplayName("Should return null when peeking empty queue")
    void testPeekEmptyQueue() {
        User peeked = queueService.peek("1");
        assertNull(peeked);
    }

    @Test
    @DisplayName("Should get all queues")
    void testGetAllQueues() {
        var queues = queueService.getAllQueues();
        assertEquals(3, queues.size());
    }
}
