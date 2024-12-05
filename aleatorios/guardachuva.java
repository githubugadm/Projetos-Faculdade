class GuardaChuva {
    private String cor;
    private boolean aberto;

    public GuardaChuva(String cor) {
        this.cor = cor;
        this.aberto = false; 
    }

    public void abrir() {
        aberto = true;
        System.out.println("Guarda-chuva " + cor + " está aberto.");
    }

    public void fechar() {
        aberto = false;
        System.out.println("Guarda-chuva " + cor + " está fechado.");
    }

    public void mostrarStatus() {
        String status = aberto ? "aberto" : "fechado";
        System.out.println("Guarda-chuva " + cor + " está " + status + ".");
    }
}