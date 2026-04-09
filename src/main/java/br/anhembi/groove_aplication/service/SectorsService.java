package br.anhembi.groove_aplication.service;

import br.anhembi.groove_aplication.entities.Sectors;
import br.anhembi.groove_aplication.entities.SectorsId;
import br.anhembi.groove_aplication.entities.Ticket;
import br.anhembi.groove_aplication.Structures.TicketStack;
import br.anhembi.groove_aplication.repository.SectorsRepo;
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

    private TicketStack primCamaroteStack;
    private TicketStack primPistaStack;
    private TicketStack primPistaPremiumStack;
    private TicketStack VIPStack;
    private TicketStack segCamaroteStack;
    private TicketStack segPistaStack;
    private TicketStack segPistaPremiumStack;

    @PostConstruct
    public void init() {
        List<Sectors> sectors = repo.findAll();

        for (Sectors sector : sectors) {
            int qtdDisp = sector.getQtdDisp();

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
                    break;
            }
            System.out.println("Setor: " + sector.getNome() + " - Pilha inicializada com " + qtdDisp + " ingressos.");
        }
    }

    public TicketStack getPrimCamaroteStack() { return primCamaroteStack; }
    public TicketStack getPrimPistaStack() { return primPistaStack; }
    public TicketStack getPrimPistaPremiumStack() { return primPistaPremiumStack; }
    public TicketStack getVIPStack() { return VIPStack; }
    public TicketStack getSegCamaroteStack() { return segCamaroteStack; }
    public TicketStack getSegPistaStack() { return segPistaStack; }
    public TicketStack getSegPistaPremiumStack() { return segPistaPremiumStack; }

    @Transactional(readOnly = true)
    public int getQtdDisp(String nome) {
        // nome alone is not enough for a composite key lookup; returns max across all days for that sector name
        return repo.findAll().stream()
                .filter(s -> s.getNome().equals(nome))
                .mapToInt(Sectors::getQtdDisp)
                .sum();
    }

    public boolean stackTicket(String nomeSetor, Ticket ticket) {
        TicketStack stack = getStackByNome(nomeSetor);
        if (stack != null) {
            return stack.stack(ticket);
        }
        return false;
    }

    public Ticket unstack(String nomeSetor) {
        TicketStack stack = getStackByNome(nomeSetor);
        return stack != null ? stack.unstack() : null;
    }

    public double getOccupationRate(String nomeSetor) {
        TicketStack stack = getStackByNome(nomeSetor);
        return stack != null ? stack.getOccupationRate() : 0.0;
    }

    public List<Sectors> getAllSectors() {
        return repo.findAll();
    }

    private TicketStack getStackByNome(String nome) {
        return switch (nome) {
            case "PrimCamarote" -> primCamaroteStack;
            case "PrimPista" -> primPistaStack;
            case "PrimPistaPremium" -> primPistaPremiumStack;
            case "SegCamarote" -> segCamaroteStack;
            case "SegPista" -> segPistaStack;
            case "SegPistaPremium" -> segPistaPremiumStack;
            case "VIP" -> VIPStack;
            default -> null;
        };
    }

    public Sectors insert(Sectors obj) {
        return repo.save(obj);
    }

    public boolean delete(String nome, String dia) {
        SectorsId id = new SectorsId(nome, dia);
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public Sectors decrementQtdDisp(String nome, String dia) {
        SectorsId id = new SectorsId(nome, dia);
        Optional<Sectors> optionalSectors = repo.findById(id);

        if (optionalSectors.isPresent()) {
            Sectors obj = optionalSectors.get();
            if (obj.getQtdDisp() > 0) {
                obj.setQtdDisp(obj.getQtdDisp() - 1);
                return repo.save(obj);
            } else {
                System.out.println("Quantidade disponível já é 0. Não é possível decrementar.");
                return null;
            }
        } else {
            System.out.println("Setor '" + nome + "' dia '" + dia + "' não encontrado.");
            return null;
        }
    }
}
