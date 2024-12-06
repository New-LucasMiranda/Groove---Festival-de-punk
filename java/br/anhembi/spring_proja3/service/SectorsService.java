package br.anhembi.spring_proja3.service;

import br.anhembi.spring_proja3.entities.Sectors;
import br.anhembi.spring_proja3.entities.Ticket;
import br.anhembi.spring_proja3.Structures.TicketStack;
import br.anhembi.spring_proja3.repository.SectorsRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SectorsService {

     @Autowired
     private SectorsRepo repo;

     // Instanciando pilhas diretamente para cada setor
     private TicketStack primCamaroteStack;
     private TicketStack primPistaStack;
     private TicketStack primPistaPremiumStack;
     private TicketStack VIPStack;
     private TicketStack segCamaroteStack;
     private TicketStack segPistaStack;
     private TicketStack segPistaPremiumStack;

     // Método de inicialização com @PostConstruct
     @PostConstruct
     public void init() {
          // Buscar todos os setores do banco de dados
          List<Sectors> sectors = repo.findAll();

          // Para cada setor, inicialize a pilha com a quantidade de ingressos
          for (Sectors sector : sectors) {
               int qtdDisp = sector.getQtdDisp(); // Quantidade de ingressos disponíveis

               // Instancia as pilhas diretamente para cada setor
               switch (sector.getNome()) {
                    case "PrimCamarote":
                         primCamaroteStack = new TicketStack(qtdDisp);
                         break;
                    case "PrimPista":
                         primPistaStack = new TicketStack(qtdDisp);
                         break;
                    case "PrimPistaPremium":
                         primPistaPremiumStack = new TicketStack(qtdDisp);
                         break;

                    case "SegCamarote":
                         segCamaroteStack = new TicketStack(qtdDisp);
                         break;
                    case "SegPista":
                         segPistaStack = new TicketStack(qtdDisp);
                         break;
                    case "SegPistaPremium":
                         segPistaPremiumStack = new TicketStack(qtdDisp);
                         break;
                    case "VIP":
                         VIPStack = new TicketStack(qtdDisp);
                         break;
                    default:
                         // Caso algum setor desconhecido, pode lançar uma exceção ou fazer outra ação
                         break;
               }
               System.out.println("Setor: " + sector.getNome() + " - Pilha de ingressos inicializada com " + qtdDisp
                         + " ingressos.");
          }
     }

     // Métodos para acessar as pilhas de ingressos de cada setor
     public TicketStack getPrimCamaroteStack() {
          return primCamaroteStack;
     }

     public TicketStack getPrimPistaStack() {
          return primPistaStack;
     }

     public TicketStack getPrimPistaPremiumStack() {
          return primPistaPremiumStack;
     }

     public TicketStack getVIPStack() {
          return VIPStack;
     }

     public TicketStack getSegCamaroteStack() {
          return segCamaroteStack;
     }

     public TicketStack getSegPistaStack() {
          return segPistaStack;
     }

     public TicketStack getSegPistaPremiumStack() {
          return segPistaPremiumStack;
     }

     // Método para obter a quantidade disponível de um setor
     @Transactional(readOnly = true)
     public int getQtdDisp(String nome) {
          Optional<Sectors> obj = repo.findById(nome);
          return obj.map(Sectors::getQtdDisp).orElse(0); // Retorna 0 se não encontrar
     }

     // Método para empilhar um ingresso
     public boolean stackTicket(String nomeSetor, Ticket ticket) {
          TicketStack stack = getStackByNome(nomeSetor);
          if (stack != null) {
               return stack.stack(ticket); // Empilha o ingresso
          }
          return false; // Se a pilha não existir
     }

     // Método para desempilhar um ingresso
     public Ticket unstack(String nomeSetor) {
          TicketStack stack = getStackByNome(nomeSetor);
          return stack != null ? stack.unstack() : null;
     }

     // Método para obter a taxa de ocupação da pilha
     public double getOccupationRate(String nomeSetor) {
          TicketStack stack = getStackByNome(nomeSetor);
          return stack != null ? stack.getOccupationRate() : 0.0;
     }

     public List<Sectors> getAllSectors() {
          return repo.findAll(); // This fetches all sectors from the database
     }

     // Método para obter a pilha de um setor com base no nome
     private TicketStack getStackByNome(String nome) {
          switch (nome) {

               case "PrimCamarote":
                    return primCamaroteStack;
               case "PrimPista":
                    return primPistaStack;
               case "PrimPistaPremium":
                    return primPistaPremiumStack;
               case "SegCamarote":
                    return segCamaroteStack;
               case "SegPista":
                    return segPistaStack;
               case "SegPistaPremium":
                    return segPistaPremiumStack;
               case "VIP":
                    return VIPStack;
               default:
                    return null;
          }
     }

     // Métodos para manipular setores

     // Método para inserir um novo setor
     public Sectors insert(Sectors obj) {
          return repo.save(obj);
     }

     // Método para excluir um setor
     public boolean delete(String nome) {
          Optional<Sectors> optionalSectors = repo.findById(nome);
          if (optionalSectors.isPresent()) {
               repo.deleteById(nome);
               return true;
          } else {
               return false;
          }
     }

     // Método para atualizar a quantidade disponível de um setor
     public Sectors decrementQtdDisp(String nome) {
          Optional<Sectors> optionalSectors = repo.findById(nome);

          if (optionalSectors.isPresent()) {
               Sectors obj = optionalSectors.get();

               // Decrementa qtdDisp em 1, garantindo que não fique negativo
               if (obj.getQtdDisp() > 0) {
                    obj.setQtdDisp(obj.getQtdDisp() - 1);
                    return repo.save(obj); // Salva a alteração no banco de dados
               } else {
                    System.out.println("Quantidade disponível já é 0. Não é possível decrementar.");
                    return null; // Retorna null caso a quantidade já seja 0
               }
          } else {
               System.out.println("Setor com nome '" + nome + "' não encontrado.");
               return null;
          }
     }
}
