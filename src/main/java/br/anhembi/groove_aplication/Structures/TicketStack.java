package br.anhembi.groove_aplication.Structures;

import br.anhembi.groove_aplication.entities.Ticket;

public class TicketStack {

    private Ticket[] stack;
    private int top;

    public TicketStack(int qtdDisp) {
        if (qtdDisp <= 0) {
            throw new IllegalArgumentException("QtdDisp must be greater than 0.");
        }

        stack = new Ticket[qtdDisp];
        top = qtdDisp; // stack starts full; top points past last ticket

        for (int i = 0; i < qtdDisp; i++) {
            stack[i] = new Ticket(i + 1);
        }
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public boolean isFull() {
        return top == stack.length;
    }

    public boolean stack(Ticket ticket) {
        if (isFull()) {
            return false;
        }
        stack[top++] = ticket;
        return true;
    }

    public Ticket unstack() {
        if (isEmpty()) {
            return null;
        }
        Ticket ticket = stack[--top];
        stack[top] = null; // release reference
        return ticket;
    }

    public double getOccupationRate() {
        if (stack.length == 0) {
            return 0.0;
        }
        return Math.round((double) top / stack.length * 100 * 100.0) / 100.0;
    }
}
