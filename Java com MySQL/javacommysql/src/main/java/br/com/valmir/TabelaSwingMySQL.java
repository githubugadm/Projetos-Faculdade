package br.com.valmir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class TabelaSwingMySQL {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> SistemaBiblioteca.mostrarMenu());
    }

    public static void criarInterface(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTable tabela = new JTable();
        JScrollPane scrollPane = new JScrollPane(tabela);
        frame.add(scrollPane);

        preencherTabela(tabela);

        frame.setVisible(true);
    }

    public static void preencherTabela(JTable tabela) {

        String url = "jdbc:mysql://localhost:3306/biblioteca";
        String usuario = "root";
        String senha = "Valmir@123";

        String sql = "SELECT id, nome, autor, ano_publicacao, disponivel FROM livros";

        DefaultTableModel modelo = new DefaultTableModel();
        tabela.setModel(modelo);

        modelo.addColumn("ID");
        modelo.addColumn("Nome do Livro");
        modelo.addColumn("Autor");
        modelo.addColumn("Ano de Publicação");
        modelo.addColumn("Disponível");

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("autor"),
                        rs.getInt("ano_publicacao"),
                        rs.getBoolean("disponivel") ? "Sim" : "Não"
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
}
