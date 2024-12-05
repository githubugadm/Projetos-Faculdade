import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;
import java.time.temporal.ChronoUnit;

public class BibliotecaComBD {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = "root"; // substitua pelo seu usuário do MySQL
    private static final String PASSWORD = "Valmir@123"; // substitua pela sua senha

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado ao banco de dados com sucesso!");

            // Registrar um novo empréstimo
            System.out.print("Nome do livro: ");
            String nomeLivro = scanner.nextLine();
            System.out.print("Autor: ");
            String autor = scanner.nextLine();
            System.out.print("Ano de publicação: ");
            int anoPublicacao = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer

            System.out.print("Nome do usuário: ");
            String nomeUsuario = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Telefone: ");
            String telefone = scanner.nextLine();

            // Adicionar livro ao banco de dados
            int livroId = inserirLivro(conn, nomeLivro, autor, anoPublicacao);

            // Adicionar usuário ao banco de dados
            int usuarioId = inserirUsuario(conn, nomeUsuario, email, telefone);

            // Criar empréstimo
            LocalDate dataRetirada = LocalDate.now();
            LocalDate dataDevolucao = dataRetirada.plusDays(7);
            inserirEmprestimo(conn, livroId, usuarioId, dataRetirada, dataDevolucao);

            System.out.println("Empréstimo registrado com sucesso. Data de devolução: " + dataDevolucao);

            // Verificar empréstimos vencidos ou com alertas
            verificarEmprestimos(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int inserirLivro(Connection conn, String nome, String autor, int anoPublicacao) throws SQLException {
        String sql = "INSERT INTO livros (nome, autor, ano_publicacao) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setString(2, autor);
            stmt.setInt(3, anoPublicacao);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Erro ao inserir livro.");
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

    private static void verificarEmprestimos(Connection conn) throws SQLException {
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
                    System.out.printf("Alerta: %s, você está com %d dias de atraso. A multa é de $%.2f.%n", nomeUsuario, diasAtraso, multa);
                }
            }
        }
    }
}
