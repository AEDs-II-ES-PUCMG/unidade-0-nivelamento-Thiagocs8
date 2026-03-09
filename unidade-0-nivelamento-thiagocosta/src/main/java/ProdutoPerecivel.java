import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ProdutoPerecivel extends Produto {

    private final LocalDate validade;
    // indica que o produto foi criado a partir de arquivo (ignorar regras que afetam comportamento em tempo de execução)
    private final boolean carregadoArquivo;

    // Construtor público: mantém a regra (não pode criar produto vencido)
    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
        this(desc, precoCusto, margemLucro, validade, false);
    }

    // Construtor "interno" para carga do arquivo: pode ignorar validação
    ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade, boolean ignorarValidacao) {
        super(desc, precoCusto, margemLucro);

        if (validade == null) {
            throw new IllegalArgumentException("Produto fora da validade.");
        }

        if (!ignorarValidacao && validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Produto fora da validade.");
        }

        this.validade = validade;
        this.carregadoArquivo = ignorarValidacao;
    }

    public LocalDate getValidade() {
        return validade;
    }

    @Override
    public double valorDeVenda() {
        double base = super.valorDeVenda();
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), validade);

        // Se produto foi carregado do arquivo, não aplicar o desconto por validade
        if (!carregadoArquivo && dias <= 7) return base * 0.75;

        return base;
    }

    @Override
    public String gerarDadosTexto() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "2;" + descricao + ";" + f2(precoCusto) + ";" + f2(margemLucro) + ";" + fmt.format(validade);
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return super.toString() + " (Validade: " + fmt.format(validade) + ")";
    }
}