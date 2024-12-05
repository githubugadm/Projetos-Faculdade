package br.com.valmir;

import javax.swing.*;
import java.util.Scanner;

public class SistemaBiblioteca {

    public static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1. Gerenciar Biblioteca (Cadastro de Livros)");
            System.out.println("2. Verificar Livros");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            while (!scanner.hasNextInt()) { 
                System.out.println("Entrada inválida! Digite um número.");
                scanner.next(); 
            }
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    BibliotecaComBD.main(null);
                    break;
                case 2:
                    JFrame frame = new JFrame("Visualizar Livros");
                    TabelaSwingMySQL.criarInterface(frame);
                    break;
                case 3:
                    System.out.println("Encerrando o sistema...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 3);

        scanner.close();
    }

    public static void main(String[] args) {
        mostrarMenu();
    }
}
