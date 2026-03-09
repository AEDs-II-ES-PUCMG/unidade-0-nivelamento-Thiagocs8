
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Comercio {
    /** Para inclusão de novos produtos no vetor */
    static final int MAX_NOVOS_PRODUTOS = 10;

    /** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;

    /** Scanner para leitura do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados. Sempre terá espaço para 10 novos produtos a cada execução */
    static Produto[] produtosCadastrados;

    /** Quantidade produtos cadastrados atualmente no vetor */
    static int quantosProdutos;

    static void pausa(){
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    static void cabecalho(){
        System.out.println("AEDII COMÉRCIO DE COISINHAS");
        System.out.println("===========================");
    }

    static int menu(){
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar e listar um produto");
        System.out.println("3 - Cadastrar novo produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    static Produto[] lerProdutos(String nomeArquivoDados) {
        Produto[] vetorProdutos;

        try (Scanner arq = new Scanner(new File(nomeArquivoDados), Charset.forName("ISO-8859-2"))) {
            int n = Integer.parseInt(arq.nextLine().trim());
            vetorProdutos = new Produto[n + MAX_NOVOS_PRODUTOS];

            quantosProdutos = 0;
            while (arq.hasNextLine() && quantosProdutos < n) {
                String linha = arq.nextLine().trim();
                if (linha.isEmpty()) continue;
                vetorProdutos[quantosProdutos] = Produto.criarDoTexto(linha);
                quantosProdutos++;
            }

            return vetorProdutos;

        } catch (FileNotFoundException e) {
            // se não existir, inicia vazio com espaço de reserva
            quantosProdutos = 0;
            return new Produto[MAX_NOVOS_PRODUTOS];
        } catch (Exception e) {
            quantosProdutos = 0;
            return new Produto[MAX_NOVOS_PRODUTOS];
        }
    }

    static void listarTodosOsProdutos(){
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < produtosCadastrados.length; i++) {
            if(produtosCadastrados[i]!=null)
                System.out.println(String.format("%02d - %s", (i+1),produtosCadastrados[i].toString()));
        }
    }

    static void localizarProdutos(){
        cabecalho();
        System.out.print("Digite a descrição do produto: ");
        String busca = teclado.nextLine().trim();

        Produto encontrado = null;
        for (int i = 0; i < quantosProdutos; i++) {
            if (produtosCadastrados[i] != null &&
                produtosCadastrados[i].getDescricao().equalsIgnoreCase(busca)) {
                encontrado = produtosCadastrados[i];
                break;
            }
        }

        if (encontrado != null) {
            System.out.println("ENCONTRADO: " + encontrado.toString());
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    static void cadastrarProduto(){
        cabecalho();
        System.out.println("CADASTRO DE NOVO PRODUTO");
        System.out.println("1 - Não perecível");
        System.out.println("2 - Perecível");
        System.out.print("Tipo: ");
        int tipo = Integer.parseInt(teclado.nextLine().trim());

        System.out.print("Descrição: ");
        String desc = teclado.nextLine().trim();

        System.out.print("Preço de custo: ");
        double preco = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));

        System.out.print("Margem de lucro: ");
        double margem = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));

        Produto novo;
        if (tipo == 1) {
            novo = new ProdutoNaoPerecivel(desc, preco, margem);
        } else if (tipo == 2) {
            System.out.print("Data de validade (dd/MM/yyyy): ");
            String dataStr = teclado.nextLine().trim();
            LocalDate validade = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            novo = new ProdutoPerecivel(desc, preco, margem, validade);
        } else {
            System.out.println("Tipo inválido.");
            return;
        }

        if (quantosProdutos >= produtosCadastrados.length) {
            System.out.println("Vetor cheio. Não é possível cadastrar mais produtos.");
            return;
        }

        produtosCadastrados[quantosProdutos] = novo;
        quantosProdutos++;
        System.out.println("Produto cadastrado com sucesso!");
    }

    public static void salvarProdutos(String nomeArquivo){
        try (FileWriter fw = new FileWriter(nomeArquivo)) {
            fw.write(String.valueOf(quantosProdutos));
            fw.write("\n");
            for (int i = 0; i < quantosProdutos; i++) {
                if (produtosCadastrados[i] != null) {
                    fw.write(produtosCadastrados[i].gerarDadosTexto());
                    fw.write("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        teclado = new Scanner(System.in, Charset.forName("ISO-8859-2"));
        nomeArquivoDados = "dadosProdutos.csv";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        int opcao = -1;
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> localizarProdutos();
                case 3 -> cadastrarProduto();
            }
            pausa();
        }while(opcao !=0);

        salvarProdutos(nomeArquivoDados);
        teclado.close();
    }
}