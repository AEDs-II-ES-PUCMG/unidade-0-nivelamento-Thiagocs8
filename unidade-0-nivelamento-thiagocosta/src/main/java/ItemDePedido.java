/**
 * Representa um item dentro de um pedido.
 * Armazena a referência ao produto, a quantidade e o preço
 * unitário "congelado" no momento da venda.
 */
public class ItemDePedido {

    private Produto produto;
    private int quantidade;
    private double precoVenda;

    /**
     * Cria um item de pedido.
     * O preço de venda é capturado (congelado) no momento da criação.
     *
     * @param produto    Produto sendo vendido
     * @param quantidade Quantidade do produto
     * @param precoVenda Preço unitário no momento da transação
     */
    public ItemDePedido(Produto produto, int quantidade, double precoVenda) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        if (precoVenda < 0) {
            throw new IllegalArgumentException("Preço de venda inválido.");
        }
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoVenda = precoVenda;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    /**
     * Dois itens são iguais se referenciarem o mesmo Produto
     * (independentemente de quantidade ou preço congelado).
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemDePedido)) return false;
        ItemDePedido outro = (ItemDePedido) obj;
        return this.produto.equals(outro.produto);
    }

    @Override
    public String toString() {
        return String.format("%s | Qtd: %d | Unit: R$ %.2f | Subtotal: R$ %.2f",
                produto.getDescricao(), quantidade, precoVenda, precoVenda * quantidade);
    }
}