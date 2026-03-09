import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class Produto {

    private static final double MARGEM_PADRAO = 0.2;

    protected String descricao;
    protected double precoCusto;
    protected double margemLucro;

    protected Produto(String desc, double precoCusto, double margemLucro) {
        if (desc == null || desc.length() < 3) {
            throw new IllegalArgumentException("Descrição inválida.");
        }
        if (precoCusto < 0) {
            throw new IllegalArgumentException("Preço de custo inválido.");
        }
        if (margemLucro < 0) {
            throw new IllegalArgumentException("Margem de lucro inválida.");
        }
        this.descricao = desc;
        this.precoCusto = precoCusto;
        this.margemLucro = margemLucro;
    }

    protected Produto(String desc, double precoCusto) {
        this(desc, precoCusto, MARGEM_PADRAO);
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }

    public double getMargemLucro() {
        return margemLucro;
    }

    public double valorDeVenda() {
        return (precoCusto * (1.0 + margemLucro));
    }

    @Override
    public String toString() {
        NumberFormat moeda = NumberFormat.getCurrencyInstance();
        return String.format("%s - %s", descricao, moeda.format(valorDeVenda()));
    }

    /**
     * Igualdade de produtos: caso possuam o mesmo nome/descrição (ignorando maiúsculas/minúsculas).
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Produto)) return false;
        Produto outro = (Produto) obj;
        return this.descricao.toLowerCase().equals(outro.descricao.toLowerCase());
    }

    /**
     * Gera uma linha de texto a partir dos dados do produto
     * @return Uma string no formato "tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
     */
    public abstract String gerarDadosTexto();

    /**
     * Cria um produto a partir de uma linha de dados em formato texto.
     * Formato: "tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
     * Tipo 1 = não perecível, Tipo 2 = perecível (com data dd/MM/yyyy).
     */
    public static Produto criarDoTexto(String linha) {
    if (linha == null || linha.isBlank()) {
        throw new IllegalArgumentException("Linha inválida.");
    }

    String[] partes = linha.split(";");
    if (partes.length < 4) {
        throw new IllegalArgumentException("Formato inválido: " + linha);
    }

    int tipo = Integer.parseInt(partes[0].trim());
    String desc = partes[1].trim();
    double preco = Double.parseDouble(partes[2].trim());
    double margem = Double.parseDouble(partes[3].trim());

    if (tipo == 1) {
        return new ProdutoNaoPerecivel(desc, preco, margem);
    }

    if (tipo == 2) {
        if (partes.length < 5) {
            throw new IllegalArgumentException("Produto perecível sem validade: " + linha);
        }

        String dataTxt = partes[4].trim();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate validade = LocalDate.parse(dataTxt, fmt);

        // Ao ler do arquivo, permitir criar mesmo que esteja vencido (carga de dados)
        return new ProdutoPerecivel(desc, preco, margem, validade, true);
    }

    throw new IllegalArgumentException("Tipo de produto inválido: " + tipo);
}

    /** Formata double com ponto e 2 casas, independente do Locale (exigido nos testes). */
    protected static String f2(double v) {
        return String.format(Locale.US, "%.2f", v);
    }
}