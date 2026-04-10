package br.anhembi.groove_aplication.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.anhembi.groove_aplication.entities.User;
import br.anhembi.groove_aplication.exception.InvalidUserException;
import br.anhembi.groove_aplication.exception.QueueFullException;
import br.anhembi.groove_aplication.exception.UserNotFoundException;
import br.anhembi.groove_aplication.repository.UserRepo;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepo repo;

    @Autowired
    private QueueService queueService;

    public Optional<User> getUser(String cpf) {
        return repo.findById(cpf);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User insert(User obj) {
        logger.info("Starting user insertion: cpf={}, nome={}, dia={}, situacao={}", obj.getCpf(), obj.getNome(),
                obj.getDia(), obj.getSituacao());
        validateUser(obj);
        logger.debug("User validation passed for: {}", obj.getCpf());

        Optional<User> existingUser = repo.findById(obj.getCpf());

        if (existingUser.isPresent()) {
            logger.info("Existing user found: {}", obj.getCpf());
            User user = existingUser.get();
            if (!user.getSituacao() && user.getPrimReserva() == null && user.getSegReserva() == null) {
                logger.info("Updating existing inactive user: {}", obj.getCpf());
                user.setNome(obj.getNome());
                user.setDia(obj.getDia());
                user.setEmail(obj.getEmail());
                user.setSituacao(obj.getSituacao());

                checkQueueCapacity(user.getDia());
                User updatedUser = repo.save(user);
                logger.info("User updated in DB: {}", updatedUser);

                if (updatedUser.getSituacao()) {
                    queueService.enqueueUser(updatedUser);
                    logger.info("User updated and added to queue: {}", updatedUser.getCpf());
                }
                return updatedUser;
            } else {
                logger.warn("User already has pending reservations or is active: {}", obj.getCpf());
                throw new InvalidUserException("User already has pending reservations or is inactive");
            }
        }

        logger.info("Creating new user: {}", obj.getCpf());
        checkQueueCapacity(obj.getDia());
        User savedUser = repo.save(obj);
        logger.info("User saved to DB: {}", savedUser);

        if (savedUser.getSituacao()) {
            queueService.enqueueUser(savedUser);
            logger.info("New user created and added to queue: {}", savedUser.getCpf());
        } else {
            logger.info("New user created but not added to queue (situacao=false): {}", savedUser.getCpf());
        }
        return savedUser;
    }

    public User insertVIP(User obj) {
        validateUser(obj);
        Optional<User> existingUser = repo.findById(obj.getCpf());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.getSituacao() && user.getPrimReserva() == null && user.getSegReserva() == null
                    && !user.getDia().equals("VIP")) {
                user.setNome(obj.getNome());
                user.setDia(obj.getDia());
                user.setEmail(obj.getEmail());

                User updatedUser = repo.save(user);
                logger.info("VIP user updated: {}", updatedUser.getCpf());
                return updatedUser;
            } else {
                throw new InvalidUserException("User does not meet VIP requirements");
            }
        }

        User savedUser = repo.save(obj);
        logger.info("New VIP user created: {}", savedUser.getCpf());
        return savedUser;
    }

    public boolean delete(String cpf) {
        if (repo.existsById(cpf)) {
            repo.deleteById(cpf);
            logger.info("User deleted: {}", cpf);
            return true;
        }
        throw new UserNotFoundException(cpf);
    }

    public User updatePrimReserva(String cpf, String primReserva) {
        User user = repo.findById(cpf)
                .orElseThrow(() -> new UserNotFoundException(cpf));
        user.setPrimReserva(primReserva);
        logger.info("Primary reservation updated for user: {}", cpf);
        return repo.save(user);
    }

    public User updateSegReserva(String cpf, String segReserva) {
        User user = repo.findById(cpf)
                .orElseThrow(() -> new UserNotFoundException(cpf));
        user.setSegReserva(segReserva);
        logger.info("Secondary reservation updated for user: {}", cpf);
        return repo.save(user);
    }

    public User updateSituacao(String cpf, boolean situacao) {
        User user = repo.findById(cpf)
                .orElseThrow(() -> new UserNotFoundException(cpf));
        user.setSituacao(situacao);
        logger.info("Situation updated for user {}: {}", cpf, situacao);
        return repo.save(user);
    }

    private void validateUser(User user) {
        if (user == null || user.getCpf() == null || user.getCpf().isEmpty()) {
            throw new InvalidUserException("User CPF is required");
        }
        if (user.getNome() == null || user.getNome().isEmpty()) {
            throw new InvalidUserException("User name is required");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new InvalidUserException("User email is required");
        }
    }

    private void checkQueueCapacity(String dia) {
        logger.debug("Checking queue capacity for dia: {}", dia);
        boolean queueFull = switch (dia) {
            case "1" -> {
                boolean full = queueService.getQueueByDia("1").isFull();
                logger.debug("Day1 queue full: {}", full);
                yield full;
            }
            case "2" -> {
                boolean full = queueService.getQueueByDia("2").isFull();
                logger.debug("Day2 queue full: {}", full);
                yield full;
            }
            default -> {
                boolean full = queueService.getQueueByDia("both").isFull();
                logger.debug("Both queue full: {}", full);
                yield full;
            }
        };

        if (queueFull) {
            logger.warn("Queue is full for dia: {}", dia);
            throw new QueueFullException(dia);
        } else {
            logger.debug("Queue has capacity for dia: {}", dia);
        }
    }
}
