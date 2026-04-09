package br.anhembi.groove_aplication.Structures;

import br.anhembi.groove_aplication.entities.User;

public class UserQueue {

    private User[] queue;
    private int last, size, count;

    public UserQueue(int size) {
        queue = new User[size];
        last = -1;
        this.size = size;
        count = 0;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == size;
    }

    public boolean enqueue(User user) {
        if (isFull()) {
            System.out.println("Queue is full");
            return false;
        }
        last++;
        queue[last] = user;
        count++;
        return true;
    }

    public User dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return null;
        }

        User user = queue[0];
        for (int i = 0; i < last; i++) {
            queue[i] = queue[i + 1];
        }
        queue[last] = null;
        last--;
        count--;

        return user;
    }

    public double getOccupationRate() {
        return Math.round(((double) count / size * 100) * 100.0) / 100.0;
    }

    public int getUserPositionByCpf(String cpf) {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return -1;
        }
        for (int i = 0; i <= last; i++) {
            if (queue[i] != null && queue[i].getCpf().equals(cpf)) {
                return i + 1; // 1-indexed
            }
        }
        System.out.println("User with CPF " + cpf + " not found in the queue");
        return -1;
    }

    public boolean removeUserByCpf(String cpf) {
        int position = getUserPositionByCpf(cpf);

        if (position == -1) {
            System.out.println("User with CPF " + cpf + " not found in the queue");
            return false;
        }

        int index = position - 1;

        for (int i = index; i < last; i++) {
            queue[i] = queue[i + 1];
        }
        queue[last] = null;
        last--;
        count--;

        System.out.println("User with CPF " + cpf + " removed successfully.");
        return true;
    }

    public User peek() {
        if (!isEmpty()) {
            return queue[0].copy();
        } else {
            System.out.println("Fila vazia.");
            return null;
        }
    }

    public int getCount() {
        return count;
    }
}
