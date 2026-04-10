package br.anhembi.groove_aplication.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.anhembi.groove_aplication.Structures.UserQueue;
import br.anhembi.groove_aplication.entities.User;
import br.anhembi.groove_aplication.repository.UserRepo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class QueueService {

    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);
    private static final long QUEUE_TIMEOUT_SECONDS = 2 * 60 * 60; // 2 hours

    private UserQueue day1Queue;
    private UserQueue day2Queue;
    private UserQueue bothQueue;

    private boolean day1TimerActive;
    private boolean day2TimerActive;
    private boolean bothTimerActive;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepo userRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @PostConstruct
    public void initQueues() {
        day1Queue = new UserQueue(4000);
        day2Queue = new UserQueue(4000);
        bothQueue = new UserQueue(2000);

        day1TimerActive = false;
        day2TimerActive = false;
        bothTimerActive = false;

        loadUsersToQueues();
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }

    public UserQueue getDay1Queue() {
        return day1Queue;
    }

    public UserQueue getDay2Queue() {
        return day2Queue;
    }

    public UserQueue getBothQueue() {
        return bothQueue;
    }

    public List<UserQueue> getAllQueues() {
        List<UserQueue> queues = new ArrayList<>();
        queues.add(day1Queue);
        queues.add(day2Queue);
        queues.add(bothQueue);
        return queues;
    }

    public UserQueue getQueueByDia(String dia) {
        return switch (dia) {
            case "1" -> day1Queue;
            case "2" -> day2Queue;
            default -> bothQueue;
        };
    }

    public void enqueueUser(User user) {
        logger.info("Attempting to enqueue user: cpf={}, dia={}, situacao={}", user.getCpf(), user.getDia(),
                user.getSituacao());
        if (user == null) {
            logger.warn("Attempt to enqueue null user");
            return;
        }
        if (!user.getSituacao()) {
            logger.debug("User {} not added: inactive", user.getCpf());
            return;
        }
        if ("VIP".equals(user.getDia())) {
            logger.debug("VIP user {} skipped queue", user.getCpf());
            return;
        }
        if (user.getPrimReserva() != null && user.getSegReserva() != null) {
            logger.debug("User {} already has both reservations", user.getCpf());
            return;
        }

        switch (user.getDia()) {
            case "1" -> {
                day1Queue.enqueue(user);
                logger.info("User {} added to day1 queue", user.getCpf());
            }
            case "2" -> {
                day2Queue.enqueue(user);
                logger.info("User {} added to day2 queue", user.getCpf());
            }
            default -> {
                bothQueue.enqueue(user);
                logger.info("User {} added to both queue", user.getCpf());
            }
        }

        logger.info("User {} added to queue: {}", user.getCpf(), user.getDia());
        startTimerForFirstUserInQueue();
    }

    public User dequeueUser(String dia) {
        UserQueue queue = getQueueByDia(dia);

        if (queue == null || queue.isEmpty()) {
            logger.warn("Invalid queue or empty: {}", dia);
            return null;
        }

        startTimerForFirstUserInQueue();
        return queue.dequeue();
    }

    public void startTimerForFirstUserInQueue() {
        if (!day1Queue.isEmpty() && !day1TimerActive) {
            User user = day1Queue.peek();
            if (isUserEligible(user)) {
                scheduleDequeueForUser(user, day1Queue, "day1");
                sendEmailToFirstUser(user);
            }
        }

        if (!day2Queue.isEmpty() && !day2TimerActive) {
            User user = day2Queue.peek();
            if (isUserEligible(user)) {
                scheduleDequeueForUser(user, day2Queue, "day2");
                sendEmailToFirstUser(user);
            }
        }

        if (!bothQueue.isEmpty() && !bothTimerActive) {
            User user = bothQueue.peek();
            if (isUserEligible(user)) {
                scheduleDequeueForUser(user, bothQueue, "both");
                sendEmailToFirstPassUser(user);
            }
        }
    }

    private boolean isUserEligible(User user) {
        return user != null &&
                user.getSituacao() &&
                user.getPrimReserva() == null &&
                user.getSegReserva() == null &&
                !"VIP".equals(user.getDia());
    }

    private void scheduleDequeueForUser(User user, UserQueue queue, String queueKey) {
        setTimerActive(queueKey, true);

        scheduler.schedule(() -> {
            synchronized (queue) {
                if (!queue.isEmpty() && queue.peek().equals(user)) {
                    queue.dequeue();
                    setTimerActive(queueKey, false);
                    startTimerForFirstUserInQueue();
                }
            }
        }, QUEUE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private void sendEmailToFirstUser(User user) {
        if (!isUserEligible(user))
            return;

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            String token = tokenService.generateToken(user.getCpf());
            String link = baseUrl + "/setor-day.html?token=" + token;

            String body = "Olá, " + user.getNome() +
                    " Você chegou em primeiro na fila!" +
                    " Agora você tem um prazo de 2 horas para escolher seus setores e garantir seu lugar." +
                    " Acesse: " + link;

            emailService.sendEmail(user.getEmail(), "Aviso: Escolha suas Reservas", body);
        }
    }

    private void sendEmailToFirstPassUser(User user) {
        if (!isUserEligible(user))
            return;

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            String token = tokenService.generateToken(user.getCpf());
            String link = baseUrl + "/setor-pass.html?token=" + token;

            String body = "Olá, " + user.getNome() +
                    " Você chegou em primeiro na fila!" +
                    " Agora você tem um prazo de 2 horas para escolher seus setores e garantir seu lugar." +
                    " Acesse: " + link;

            emailService.sendEmail(user.getEmail(), "Aviso: Escolha suas reservas", body);
        }
    }

    private void setTimerActive(String queueKey, boolean isActive) {
        switch (queueKey) {
            case "day1" -> day1TimerActive = isActive;
            case "day2" -> day2TimerActive = isActive;
            case "both" -> bothTimerActive = isActive;
        }
    }

    public double getOccupationRate(String dia) {
        return getQueueByDia(dia).getOccupationRate();
    }

    public boolean removeUserByCpf(String cpf, String dia) {
        return getQueueByDia(dia).removeUserByCpf(cpf);
    }

    public int getUserPositionByCpf(String cpf, String dia) {
        return getQueueByDia(dia).getUserPositionByCpf(cpf);
    }

    public User peek(String dia) {
        UserQueue queue = getQueueByDia(dia);
        return (queue != null && !queue.isEmpty()) ? queue.peek() : null;
    }

    public void loadUsersToQueues() {
        logger.info("Loading users to queues on startup");
        List<User> users = userRepository.findAll();
        logger.info("Found {} users in database", users.size());
        int enqueuedCount = 0;
        for (User user : users) {
            if (isUserEligible(user)) {
                enqueueUser(user);
                enqueuedCount++;
            } else {
                logger.debug("User {} not eligible for queue", user.getCpf());
            }
        }
        logger.info("Loaded {} users into queues", enqueuedCount);
    }
}
