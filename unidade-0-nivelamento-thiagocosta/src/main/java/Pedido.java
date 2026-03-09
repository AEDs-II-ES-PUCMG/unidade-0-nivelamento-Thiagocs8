import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa um pedido de compra.
 * Armazena itens de pedido (produto + quantidade + preço congelado),
 * data do pedido e forma de pagamento.
 */
public class Pedido {

    // Constantes
    private static final int MAX_PRODUTOS = 10;
    private static final double DESCONTO_PG_A_VISTA = 0.15;

    // Formas de pagamento
    public static final int PAGAMENTO_A_VISTA  = 1;
    public static final int PAGAMENTO_A_PRAZO  = 2;

    // Atributos
    private ItemDePedido[] itens;
    private LocalDate dataPedido;
    private int quantProdutos;
    private int formaDePagamento;

    /**
     * Cria um novo pedido.
     *
     * @param dataPedido      Data do pedido
     * @param formaDePagamento 1 = à vista, 2 = a prazo
     */
    public Pedido(LocalDate dataPedido, int formaDePagamento) {
        this.dataPedido = dataPedido;
        this.formaDePagamento = formaDePagamento;
        this.itens = new ItemDePedido[MAX_PRODUTOS];
        this.quantProdutos = 0;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public int getFormaDePagamento() {
        return formaDePagamento;
    }

    public int getQuantProdutos() {
        return quantProdutos;
    }

    public ItemDePedido[] getItens() {
        return itens;
    }

    /**
     * Inclui um produto no pedido criando um ItemDePedido.
     * O preço de venda é capturado (congelado) no momento da chamada.
     *
     * @param novo Produto a ser incluído (quantidade = 1)
     * @return true se incluído com sucesso, false se o vetor estiver cheio
     */
    public boolean incluirProduto(Produto novo) {
        if (quantProdutos >= MAX_PRODUTOS) return false;

        // Verificar se o produto já existe no pedido; se sim, incrementa quantidade
        for (int i = 0; i < quantProdutos; i++) {
            if (itens[i] != null && itens[i].getProduto().equals(novo)) {
                itens[i].setQuantidade(itens[i].getQuantidade() + 1);
                return true;
            }
        }

        // Novo produto: congela o preço de venda atual
        itens[quantProdutos] = new ItemDePedido(novo, 1, novo.valorDeVenda());
        quantProdutos++;
        return true;
    }

    /**
     * Calcula o valor final do pedido, aplicando desconto à vista quando aplicável.
     *
     * @return Valor total do pedido
     */
    public double valorFinal() {
        double total = 0;
        for (int i = 0; i < quantProdutos; i++) {
            if (itens[i] != null) {
                total += itens[i].getPrecoVenda() * itens[i].getQuantidade();
            }
        }
        if (formaDePagamento == PAGAMENTO_A_VISTA) {
            total *= (1.0 - DESCONTO_PG_A_VISTA);
        }
        return total;
    }

    /**
     * Mescla outro pedido neste pedido (operação atômica).
     * - Produtos já existentes têm sua quantidade somada (menor preço prevalece).
     * - Produtos novos são inseridos nas posições livres do vetor.
     * - Se não houver espaço para todos os produtos novos, lança IllegalStateException
     *   e nenhuma alteração é realizada.
     * - Ao final, o pedido secundário é esvaziado.
     *
     * @param outroPedido Pedido a ser incorporado neste
     * @throws IllegalStateException se a capacidade máxima for excedida
     */
    public void mesclarPedido(Pedido outroPedido) {
        // Contar quantos itens do outroPedido são produtos novos (não existem neste)
        int novosItens = 0;
        for (int i = 0; i < outroPedido.quantProdutos; i++) {
            ItemDePedido itemSecundario = outroPedido.itens[i];
            if (itemSecundario == null) continue;

            boolean jaExiste = false;
            for (int j = 0; j < this.quantProdutos; j++) {
                if (this.itens[j] != null && this.itens[j].equals(itemSecundario)) {
                    jaExiste = true;
                    break;
                }
            }
            if (!jaExiste) novosItens++;
        }

        // Verificar capacidade antes de qualquer alteração (operação atômica)
        if (this.quantProdutos + novosItens > MAX_PRODUTOS) {
            throw new IllegalStateException(
                "Capacidade máxima excedida: não é possível mesclar os pedidos. " +
                "Limite de " + MAX_PRODUTOS + " itens por pedido.");
        }

        // Realizar a mesclagem
        for (int i = 0; i < outroPedido.quantProdutos; i++) {
            ItemDePedido itemSecundario = outroPedido.itens[i];
            if (itemSecundario == null) continue;

            boolean encontrado = false;
            for (int j = 0; j < this.quantProdutos; j++) {
                if (this.itens[j] != null && this.itens[j].equals(itemSecundario)) {
                    // Produto já existe: somar quantidade e manter o menor preço
                    this.itens[j].setQuantidade(
                        this.itens[j].getQuantidade() + itemSecundario.getQuantidade());
                    if (itemSecundario.getPrecoVenda() < this.itens[j].getPrecoVenda()) {
                        this.itens[j].setPrecoVenda(itemSecundario.getPrecoVenda());
                    }
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                // Produto novo: inserir na próxima posição livre
                this.itens[this.quantProdutos] = itemSecundario;
                this.quantProdutos++;
            }
        }

        // Esvaziar o pedido secundário
        for (int i = 0; i < outroPedido.quantProdutos; i++) {
            outroPedido.itens[i] = null;
        }
        outroPedido.quantProdutos = 0;
    }

    /**
     * Imprime o recibo/cupom fiscal do pedido no terminal.
     * Aplica desconto de 5% no subtotal de itens com quantidade > 10.
     */
    public void imprimirRecibo() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formaPgto = (formaDePagamento == PAGAMENTO_A_VISTA) ? "À Vista" : "A Prazo";

        System.out.println("============================================");
        System.out.println("           CUPOM FISCAL - RECIBO           ");
        System.out.println("============================================");
        System.out.printf("Data: %-20s Pagamento: %s%n",
                dataPedido.format(fmt), formaPgto);
        System.out.println("--------------------------------------------");
        System.out.printf("%-20s %5s %10s %12s%n",
                "Produto", "Qtd", "Unit (R$)", "Subtotal(R$)");
        System.out.println("--------------------------------------------");

        double totalGeral = 0;
        for (int i = 0; i < quantProdutos; i++) {
            if (itens[i] == null) continue;

            String nome = itens[i].getProduto().getDescricao();
            int qtd = itens[i].getQuantidade();
            double unit = itens[i].getPrecoVenda();
            double subtotal = unit * qtd;

            // Desconto de 5% para itens com quantidade > 10
            boolean temDesconto = qtd > 10;
            if (temDesconto) subtotal *= 0.95;

            totalGeral += subtotal;

            System.out.printf("%-20s %5d %10.2f %12.2f%s%n",
                    nome, qtd, unit, subtotal,
                    temDesconto ? " (-5%)" : "");
        }

        System.out.println("--------------------------------------------");
        System.out.printf("%-38s %12.2f%n", "TOTAL GERAL (sem desconto pgto):", totalGeral);

        if (formaDePagamento == PAGAMENTO_A_VISTA) {
            double totalComDesconto = totalGeral * (1.0 - DESCONTO_PG_A_VISTA);
            System.out.printf("%-38s %12.2f%n",
                    "DESCONTO À VISTA (15%):", totalGeral * DESCONTO_PG_A_VISTA);
            System.out.printf("%-38s %12.2f%n", "TOTAL A PAGAR:", totalComDesconto);
        } else {
            System.out.printf("%-38s %12.2f%n", "TOTAL A PAGAR:", totalGeral);
        }

        System.out.println("============================================");
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("Pedido[data=%s, itens=%d, total=R$ %.2f]",
                dataPedido.format(fmt), quantProdutos, valorFinal());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pedido)) return false;
        Pedido outro = (Pedido) obj;
        return this.dataPedido.equals(outro.dataPedido)
                && this.formaDePagamento == outro.formaDePagamento;
    }
}