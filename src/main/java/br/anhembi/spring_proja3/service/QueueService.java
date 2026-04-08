package br.anhembi.spring_proja3.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.anhembi.spring_proja3.Structures.UserQueue;
import br.anhembi.spring_proja3.entities.User;
import br.anhembi.spring_proja3.repository.UserRepo;
import jakarta.annotation.PostConstruct;

@Service
public class QueueService {

    private UserQueue day1Queue;
    private UserQueue day2Queue;
    private UserQueue bothQueue;

    private boolean day1TimerActive;
    private boolean day2TimerActive;
    private boolean bothTimerActive;

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

        // 🔒 PROTEÇÃO COMPLETA
        if (user == null) return;
        if (!user.getSituação()) return;
        if ("VIP".equals(user.getDia())) return;
        if (user.getPrimReserva() != null || user.getSegReserva() != null) return;

        switch (user.getDia()) {
            case "1" -> day1Queue.enqueue(user);
            case "2" -> day2Queue.enqueue(user);
            default -> bothQueue.enqueue(user);
        }

        startTimerForFirstUserInQueue();
    }

    public User dequeueUser(String dia) {
        UserQueue queue = getQueueByDia(dia);

        if (queue == null || queue.isEmpty()) {
            System.out.println("Fila vazia ou dia inválido.");
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
               user.getSituação() &&
               user.getSituação() &&
               user.getPrimReserva() == null &&
               user.getSegReserva() == null &&
               !"VIP".equals(user.getDia());
    }

    private void scheduleDequeueForUser(User user, UserQueue queue, String queueKey) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        long delay = 2 * 60 * 60; // 2 horas

        setTimerActive(queueKey, true);

        scheduler.schedule(() -> {
            synchronized (queue) {
                if (!queue.isEmpty() && queue.peek().equals(user)) {
                    queue.dequeue();
                    setTimerActive(queueKey, false);
                    startTimerForFirstUserInQueue();
                }
            }
        }, delay, TimeUnit.SECONDS);
    }

    private void sendEmailToFirstUser(User user) {

        if (!isUserEligible(user)) return;

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

        if (!isUserEligible(user)) return;

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
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (isUserEligible(user)) {
                enqueueUser(user);
            }
        }
    }
}