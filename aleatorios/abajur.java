class Abajur {
    private String cor;
    private boolean ligado;

    public Abajur(String cor) {
        this.cor = cor;
        this.ligado = false; 
    }

    public void ligar() {
        ligado = true;
        System.out.println("Abajur " + cor + " está ligado.");
    }

    public void desligar() {
        ligado = false;
        System.out.println("Abajur " + cor + " está desligado.");
    }

    public void mostrarStatus() {
        String status = ligado ? "ligado" : "desligado";
        System.out.println("Abajur " + cor + " está " + status + ".");
    }
}