package br.anhembi.spring_proja3.Structures;

import br.anhembi.spring_proja3.entities.User;

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

          // Remove o primeiro usuário da fila (posição 0)
          User user = queue[0]; // Agora estamos pegando o primeiro usuário
          // Desloca os outros usuários para a esquerda
          for (int i = 0; i < last; i++) {
               queue[i] = queue[i + 1];
          }
          queue[last] = null; // Limpa o último usuário da fila (agora vago)
          last--; // Ajusta o índice de 'last'
          // Reduz o número de usuários na fila
          
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
                    return i + 1; // 1-indexed position
               }
          }
          System.out.println("User with CPF " + cpf + " not found in the queue");
          return -1;
     }

     public boolean removeUserByCpf(String cpf) {

          int position = getUserPositionByCpf(cpf);

          // Verifica se o usuário está na fila
          if (position == -1) {
               System.out.println("User with CPF " + cpf + " not found in the queue");
               return false;
          }

          int index = position - 1; // Ajusta para índice 0-based

          // Remove o usuário deslocando os elementos para a esquerda
          for (int i = index; i < last; i++) {
               queue[i] = queue[i + 1];
          }
          queue[last] = null; // Limpa a última posição
          last--;
          count--;

          System.out.println("User with CPF " + cpf + " removed successfully.");
          return true;
     }

     public User peek() {

          if (!isEmpty()) {
               return queue[0].copy(); // Retorna o primeiro usuário
          } else {
               System.out.println("Fila vazia.");
               return null;
          }
     }

     public int getCount() {

          return count;
     }

}
