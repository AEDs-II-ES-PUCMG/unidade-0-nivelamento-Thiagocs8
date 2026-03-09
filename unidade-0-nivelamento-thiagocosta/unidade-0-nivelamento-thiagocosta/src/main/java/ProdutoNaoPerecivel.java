

public class ProdutoNaoPerecivel extends Produto {

    public ProdutoNaoPerecivel(String desc, double precoCusto, double margemLucro) {
        super(desc, precoCusto, margemLucro);
    }

    public ProdutoNaoPerecivel(String desc, double precoCusto) {
        super(desc, precoCusto);
    }

    @Override
    public String gerarDadosTexto() {
        // "1; descrição;preçoDeCusto;margemDeLucro" (com 2 casas e ponto)
        return "1;" + descricao + ";" + f2(precoCusto) + ";" + f2(margemLucro);
    }
}