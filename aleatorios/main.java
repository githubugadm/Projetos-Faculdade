import java.util.Scanner;
class Passageiro {
    private String nome;
    private String cpf;
    private String dataNascimento;
    private String dataViagem;
    private String modalidade;
    private String tipoAcomodacao;

    public Passageiro(String nome, String cpf, String dataNascimento, String dataViagem, String modalidade, String tipoAcomodacao) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.dataViagem = dataViagem;
        this.modalidade = modalidade;
        this.tipoAcomodacao = tipoAcomodacao;
    }

    public int calcularPontos() {
        int pontos = 0;
        if (modalidade.equalsIgnoreCase("Aéreo")) {
            pontos = tipoAcomodacao.equalsIgnoreCase("Luxo") ? 2500 : 1000;
        } else if (modalidade.equalsIgnoreCase("Rodoviário")) {
            pontos = tipoAcomodacao.equalsIgnoreCase("Luxo") ? 1000 : 500;
        } else if (modalidade.equalsIgnoreCase("Ferroviário")) {
            pontos = tipoAcomodacao.equalsIgnoreCase("Luxo") ? 5000 : 2500;
        }

        return pontos * 12;
    }

    public void exibirInformacoes() {
        System.out.println("Passageiro: " + nome);
        System.out.println("Pontos acumulados em um ano: " + calcularPontos());
    }
}

public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Data de Nascimento: ");
        String dataNascimento = scanner.nextLine();
        System.out.print("Data da Viagem: ");
        String dataViagem = scanner.nextLine();
        System.out.print("Modalidade de Transporte (Aéreo, Rodoviário, Ferroviário): ");
        String modalidade = scanner.nextLine();
        System.out.print("Tipo de Acomodação (Standart ou Luxo): ");
        String tipoAcomodacao = scanner.nextLine();

        Passageiro passageiro = new Passageiro(nome, cpf, dataNascimento, dataViagem, modalidade, tipoAcomodacao);
        passageiro.exibirInformacoes();

        scanner.close();
    }
}