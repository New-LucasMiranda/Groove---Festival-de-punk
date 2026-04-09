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
        validateUser(obj);

        Optional<User> existingUser = repo.findById(obj.getCpf());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.getSituacao() && user.getPrimReserva() == null && user.getSegReserva() == null) {
                user.setNome(obj.getNome());
                user.setDia(obj.getDia());
                user.setEmail(obj.getEmail());
                user.setSituacao(obj.getSituacao());

                checkQueueCapacity(user.getDia());
                User updatedUser = repo.save(user);

                if (updatedUser.getSituacao()) {
                    queueService.enqueueUser(updatedUser);
                    logger.info("User updated and added to queue: {}", updatedUser.getCpf());
                }
                return updatedUser;
            } else {
                throw new InvalidUserException("User already has pending reservations or is inactive");
            }
        }

        checkQueueCapacity(obj.getDia());
        User savedUser = repo.save(obj);

        if (savedUser.getSituacao()) {
            queueService.enqueueUser(savedUser);
            logger.info("New user created and added to queue: {}", savedUser.getCpf());
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
        boolean queueFull = switch (dia) {
            case "1" -> queueService.getQueueByDia("1").isFull();
            case "2" -> queueService.getQueueByDia("2").isFull();
            default -> queueService.getQueueByDia("both").isFull();
        };

        if (queueFull) {
            throw new QueueFullException(dia);
        }
    }
}
