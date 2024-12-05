package br.com.valmir;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class BibliotecaComBD {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = "root";
    private static final String PASSWORD = "Valmir@123";


    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            int opcao;

            do {
                System.out.println("\n=== Gerenciar Biblioteca ===");
                System.out.println("1. Registrar Empréstimo");
                System.out.println("2. Verificar Empréstimos");
                System.out.println("3. Registrar Devolução Antecipada");
                System.out.println("4. Voltar ao Menu Principal");
                System.out.print("Escolha uma opção: ");
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        registrarEmprestimo(conn, scanner);
                        break;
                    case 2:
                        verificarEmprestimos(conn);
                        break;
                    case 3:
                        registrarDevolucaoAntecipada(conn, scanner);
                        break;
                    case 4:
                        System.out.println("Retornando ao menu principal...");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } while (opcao != 4);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void registrarEmprestimo(Connection conn, Scanner scanner) {
        try {
            System.out.print("Nome do livro: ");
            String nomeLivro = scanner.nextLine();

            int livroId = verificarDisponibilidadeLivro(conn, nomeLivro);
            if (livroId == -1) {
                System.out.println("Livro não encontrado ou não está disponível para empréstimo.");
                return;
            }

            System.out.print("Nome do usuário: ");
            String nomeUsuario = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Telefone: ");
            String telefone = scanner.nextLine();

            int usuarioId = inserirUsuario(conn, nomeUsuario, email, telefone);

            LocalDate dataRetirada = LocalDate.now();
            LocalDate dataDevolucao = dataRetirada.plusDays(7);
            inserirEmprestimo(conn, livroId, usuarioId, dataRetirada, dataDevolucao);

            atualizarDisponibilidadeLivro(conn, livroId, false);

            System.out.println("Empréstimo registrado com sucesso. Data de devolução: " + dataDevolucao);
        } catch (SQLException e) {
            System.out.println("Erro ao registrar o empréstimo: " + e.getMessage());
        }
    }

    private static void registrarDevolucaoAntecipada(Connection conn, Scanner scanner) {
        try {
            System.out.print("Nome do livro: ");
            String nomeLivro = scanner.nextLine();
    
            int livroId = verificarDisponibilidadeLivro(conn, nomeLivro);
            if (livroId == -1) {
                System.out.println("Livro não encontrado ou já foi devolvido.");
                return;
            }
    
            System.out.print("Nome do usuário: ");
            String nomeUsuario = scanner.nextLine();
    
            int usuarioId = verificarUsuario(conn, nomeUsuario);
            if (usuarioId == -1) {
                System.out.println("Usuário não encontrado.");
                return;
            }
    
            if (!verificarEmprestimo(conn, livroId, usuarioId)) {
                System.out.println("Este livro não foi emprestado para este usuário.");
                return;
            }
    
            LocalDate dataDevolucaoReal = LocalDate.now();
            atualizarDataDevolucao(conn, livroId, usuarioId, dataDevolucaoReal);
    

            atualizarDisponibilidadeLivro(conn, livroId, true);
    
            System.out.println("Devolução antecipada registrada com sucesso! O livro '" + nomeLivro + "' foi devolvido em " + dataDevolucaoReal + ".");
        } catch (SQLException e) {
            System.out.println("Erro ao registrar a devolução antecipada: " + e.getMessage());
        }
    }
    
    private static boolean verificarEmprestimo(Connection conn, int livroId, int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM emprestimos WHERE livro_id = ? AND usuario_id = ? AND data_devolucao IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            stmt.setInt(2, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }
    

    private static int verificarDisponibilidadeLivro(Connection conn, String nomeLivro) throws SQLException {
        String sql = "SELECT id, disponivel FROM livros WHERE nome = ?";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeLivro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean disponivel = rs.getBoolean("disponivel");
                    if (disponivel) {
                        return rs.getInt("id");
                    } else {
                        System.out.println("O livro '" + nomeLivro + "' não está disponível para empréstimo.");
                    }
                } else {
                    System.out.println("Livro não encontrado.");
                }
            }
        }
        return -1;
    }
    
    

    private static int verificarUsuario(Connection conn, String nomeUsuario) throws SQLException {
        String sql = "SELECT id FROM usuarios WHERE nome = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }

    private static void atualizarDisponibilidadeLivro(Connection conn, int livroId, boolean disponivel) throws SQLException {
        String sql = "UPDATE livros SET disponivel = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, disponivel);
            stmt.setInt(2, livroId);
            stmt.executeUpdate();
        }
    }

    private static void atualizarDataDevolucao(Connection conn, int livroId, int usuarioId, LocalDate dataDevolucao) throws SQLException {
        String sql = "UPDATE emprestimos SET data_devolucao = ? WHERE livro_id = ? AND usuario_id = ? AND data_devolucao IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dataDevolucao));
            stmt.setInt(2, livroId);
            stmt.setInt(3, usuarioId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Data de devolução atualizada com sucesso.");
            } else {
                System.out.println("Erro ao atualizar a data de devolução.");
            }
        }
    }
    

    private static int inserirUsuario(Connection conn, String nome, String email, String telefone) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, telefone) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, telefone);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Erro ao inserir usuário.");
    }

    private static void inserirEmprestimo(Connection conn, int livroId, int usuarioId, LocalDate dataRetirada, LocalDate dataDevolucao) throws SQLException {
        String sql = "INSERT INTO emprestimos (livro_id, usuario_id, data_retirada, data_devolucao) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, livroId);
            stmt.setInt(2, usuarioId);
            stmt.setDate(3, Date.valueOf(dataRetirada));
            stmt.setDate(4, Date.valueOf(dataDevolucao));
            stmt.executeUpdate();
        }
    }

    private static void verificarEmprestimos(Connection conn) {
        String sql = "SELECT usuarios.nome, livros.nome AS livro, emprestimos.data_devolucao " +
                     "FROM emprestimos " +
                     "JOIN usuarios ON emprestimos.usuario_id = usuarios.id " +
                     "JOIN livros ON emprestimos.livro_id = livros.id " +
                     "WHERE emprestimos.data_devolucao <= CURDATE() OR CURDATE() = emprestimos.data_devolucao - INTERVAL 1 DAY";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String nomeUsuario = rs.getString("nome");
                String nomeLivro = rs.getString("livro");
                Date dataDevolucao = rs.getDate("data_devolucao");
                LocalDate hoje = LocalDate.now();

                if (hoje.equals(dataDevolucao.toLocalDate().minusDays(1))) {
                    System.out.println("Alerta: " + nomeUsuario + ", você deve devolver o livro '" + nomeLivro + "' amanhã.");
                } else if (hoje.isAfter(dataDevolucao.toLocalDate())) {
                    long diasAtraso = ChronoUnit.DAYS.between(dataDevolucao.toLocalDate(), hoje);
                    double multa = diasAtraso * 20.0;
                    System.out.printf("Alerta: %s, você está com %d dias de atraso. A multa é de R$ %.2f.%n", nomeUsuario, diasAtraso, multa);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar empréstimos: " + e.getMessage());
        }
    }
}
