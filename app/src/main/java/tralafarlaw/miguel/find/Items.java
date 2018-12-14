package tralafarlaw.miguel.find;

public class Items {
    String Cliente, Conductor, Estado;
    public  Items (String cliente, String conductor, String estado){
        this.Cliente = cliente;
        this.Conductor = conductor;
        this.Estado = estado;
    }

    @Override
    public String toString() {
        return Cliente+" "+Conductor+" "+Estado;
    }
}
