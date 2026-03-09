import java.time.LocalDate;

public class App {

    public static void main(String[] args) {

        // --- Produtos ---
        Produto lapis       = new ProdutoNaoPerecivel("Lapis", 1.30, 0.50);
        Produto frigideira  = new ProdutoNaoPerecivel("Frigideira", 18.00, 0.30);
        Produto guardanapos = new ProdutoNaoPerecivel("Guardanapos", 2.50, 0.35);
        Produto iogurte     = new ProdutoPerecivel("Iogurte", 7.50, 0.15, LocalDate.now().plusDays(30));
        Produto chips       = new ProdutoPerecivel("Chips de Banana", 3.50, 0.25, LocalDate.now().plusDays(20));

        // --- Pedido 1: à vista ---
        Pedido pedido1 = new Pedido(LocalDate.now(), Pedido.PAGAMENTO_A_VISTA);
        pedido1.incluirProduto(lapis);
        pedido1.incluirProduto(lapis);        // mesmo produto → soma quantidade
        pedido1.incluirProduto(frigideira);
        pedido1.incluirProduto(iogurte);

        System.out.println("=== PEDIDO 1 ANTES DA MESCLAGEM ===");
        pedido1.imprimirRecibo();

        // --- Pedido 2 (será mesclado no pedido 1) ---
        Pedido pedido2 = new Pedido(LocalDate.now(), Pedido.PAGAMENTO_A_PRAZO);
        pedido2.incluirProduto(guardanapos);
        pedido2.incluirProduto(chips);
        pedido2.incluirProduto(lapis);        // produto repetido → soma quantidade no pedido1

        System.out.println("\n=== PEDIDO 2 ANTES DA MESCLAGEM ===");
        pedido2.imprimirRecibo();

        // --- Mesclagem ---
        pedido1.mesclarPedido(pedido2);

        System.out.println("\n=== PEDIDO 1 APÓS MESCLAGEM ===");
        pedido1.imprimirRecibo();

        System.out.println("\n=== PEDIDO 2 APÓS MESCLAGEM (deve estar vazio) ===");
        System.out.println("Itens no pedido 2: " + pedido2.getQuantProdutos());

        // --- Teste de exceção por capacidade ---
        System.out.println("\n=== TESTE DE CAPACIDADE MÁXIMA ===");
        Pedido pedidoGrande = new Pedido(LocalDate.now(), Pedido.PAGAMENTO_A_PRAZO);
        for (int i = 0; i < 10; i++) {
            pedidoGrande.incluirProduto(
                new ProdutoNaoPerecivel("Produto " + i, 10.0 + i, 0.1));
        }
        Pedido pedidoExtra = new Pedido(LocalDate.now(), Pedido.PAGAMENTO_A_PRAZO);
        pedidoExtra.incluirProduto(new ProdutoNaoPerecivel("Item Extra", 5.0, 0.1));
        try {
            pedidoGrande.mesclarPedido(pedidoExtra);
        } catch (IllegalStateException e) {
            System.out.println("Exceção capturada (esperado): " + e.getMessage());
        }
    }
}