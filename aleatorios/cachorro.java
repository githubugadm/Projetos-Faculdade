class Cachorro {
    private String nome;
    private String raca;
    private int idade;

    public Cachorro(String nome, String raca, int idade) {
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
    }

    public void latir() {
        System.out.println(nome + " está latindo!");
    }

    public void mostrarInfo() {
        System.out.println("Cachorro: " + nome + ", Raça: " + raca + ", Idade: " + idade + " anos");
    }
}