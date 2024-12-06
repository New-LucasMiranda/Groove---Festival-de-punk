package br.anhembi.spring_proja3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.anhembi.spring_proja3.entities.User;
import br.anhembi.spring_proja3.repository.UserRepo;
@Service
@Transactional
public class UserService {


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
        Optional<User> existingUser = repo.findById(obj.getCpf());
    
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Verifica se a situação é false e ambas as reservas são nulas
            if (!user.getSituação() && user.getPrimReserva() == null && user.getSegReserva() == null) {
                // Sobrescreve informações do usuário existente
                user.setNome(obj.getNome());
                user.setDia(obj.getDia());
                user.setEmail(obj.getEmail());
                user.setSituação(obj.getSituação());
                // Atualiza outras informações necessárias aqui, se houver
                boolean canAddToQueue = switch (user.getDia()) {
                    case "1" -> !queueService.getQueueByDia("1").isFull();
                    case "2" -> !queueService.getQueueByDia("2").isFull();
                    default -> !queueService.getQueueByDia("both").isFull();
                };
    
                if (!canAddToQueue) {
                    System.out.println("Fila cheia. Não é possível adicionar o usuário.");
                    return null;
                }   
                // Salva as alterações do usuário e o adiciona à fila
                User updatedUser = repo.save(user);
                queueService.enqueueUser(updatedUser);
                return updatedUser;
            } else {
                System.out.println("Usuário já existe no banco e não atende aos critérios para sobrescrição.");
                return null;
            }
        }
    
        // Caso o usuário não exista, procede com a lógica original de inserção
        boolean canAddToQueue = switch (obj.getDia()) {
            case "1" -> !queueService.getQueueByDia("1").isFull();
            case "2" -> !queueService.getQueueByDia("2").isFull();
            default -> !queueService.getQueueByDia("both").isFull();
        };
    
        if (!canAddToQueue) {
            System.out.println("Fila cheia. Não é possível adicionar o usuário.");
            return null;
        }
    
        User savedUser = repo.save(obj);
        queueService.enqueueUser(savedUser);
        return savedUser;
    }
    

    public User insertVIP(User obj) {
        Optional<User> existingUser = repo.findById(obj.getCpf());
    
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Verifica se a situação é false e ambas as reservas são nulas
            if (!user.getSituação() && user.getPrimReserva() == null && user.getSegReserva() == null && !user.getDia().equals("VIP") ) {
                // Sobrescreve informações do usuário existente
                user.setNome(obj.getNome());
                user.setDia(obj.getDia());
                user.setEmail(obj.getEmail());
                // Atualiza outras informações necessárias aqui, se houver 
                // Atualiza o usuário no banco de dados
                User updatedUser = repo.save(user);
                System.out.println("Usuário VIP atualizado no banco: " + updatedUser);
                return updatedUser;
            } else {
                System.out.println("Usuário já existe no banco e não atende aos critérios para sobrescrição.");
                return null;
            }
        }
    
        // Caso o usuário não exista, procede com a lógica original de inserção VIP
        User savedUser = repo.save(obj);
        System.out.println("Usuário VIP salvo no banco: " + savedUser);
      
        return savedUser;
    }
    
    

    public boolean delete(String cpf) {
        if (repo.existsById(cpf)) {
            repo.deleteById(cpf);
            return true;
        }
        return false;
    }

    public User updatePrimReserva(String cpf, String primReserva) {
        Optional<User> optionalUser = repo.findById(cpf);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPrimReserva(primReserva);
            return repo.save(user);
        }
        return null;
    }

    public User updateSegReserva(String cpf, String segReserva) {
        Optional<User> optionalUser = repo.findById(cpf);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setSegReserva(segReserva);
            return repo.save(user);
        }
        return null;
    }

    public User updateSituacao(String cpf, boolean situacao) {
        Optional<User> optionalUser = repo.findById(cpf);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setSituação(situacao);
            return repo.save(user);
        }
        return null;
    }
    
}
