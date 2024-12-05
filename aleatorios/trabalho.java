import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class Endereco {
    String cidade;
    String estado;
    String cep;
    public Endereco(String cidade, String estado, String cep) {
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }
}
class Produto {
    int id;
    String nome;
    double preco;
    int estoque;
    public Produto(int id, String nome, double preco, int estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public boolean disponivel() {
        return estoque > 0;
    }

    public void reduzirEstoque() {
        estoque--;
    }
}
class Cliente {
    int id;
    String nome;
    String classificacao;
    Endereco endereco;

    public Cliente(int id, String nome, String classificacao, Endereco endereco) {
        this.id = id;
        this.nome = nome;
        this.classificacao = classificacao;
        this.endereco = endereco;
    }
}

class Transacao {
    Cliente cliente;
    List<Produto> produtos;
    LocalDateTime dataHora;

    public Transacao(Cliente cliente, List<Produto> produtos) {
        this.cliente = cliente;
        this.produtos = produtos;
        this.dataHora = LocalDateTime.now();
    }

    public boolean efetivar() {
        if ("mal_classificado".equals(cliente.classificacao)) {
            System.out.println("Transação não efetivada: Cliente mal classificado.");
            return false;
        }

        for (Produto p : produtos) {
            if (!p.disponivel()) {
                System.out.println(p.nome + " não está disponível.");
                return false;
            }
            p.reduzirEstoque();
        }

        System.out.println("Transação realizada com sucesso!");
        return true;
    }
}


public class trabalho {
    public static void main(String[] args) {
        Endereco endereco = new Endereco("Mogi", "SP", "01234-567");
        Cliente cliente = new Cliente(1, "Valmir", "classificado", endereco);
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto(1, "Produto A", 100.0, 0));
        produtos.add(new Produto(2, "Produto B", 200.0, 5));

        Transacao transacao = new Transacao(cliente, produtos);
        transacao.efetivar();
    }
}