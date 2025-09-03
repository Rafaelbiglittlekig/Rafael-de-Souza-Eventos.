package app;

import model.Categoria;
import model.Evento;
import model.Usuario;
import service.EventoService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class App {
    private static EventoService service = new EventoService();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        System.out.println("Carregando dados...");
        service.carregarDados();
        System.out.println("Bem-vindo ao Sistema de Eventos!");

        boolean sair = false;
        while (!sair) {
            mostrarMenu();
            int opcao = lerInt("Escolha uma opção: ");
            switch (opcao) {
                case 1:
                    cadastrarUsuario();
                    break;
                case 2:
                    cadastrarEvento();
                    break;
                case 3:
                    listarEventos();
                    break;
                case 4:
                    participarEvento();
                    break;
                case 5:
                    cancelarParticipacao();
                    break;
                case 6:
                    verEventosConfirmados();
                    break;
                case 7:
                    verEventosOcorrrendoAgora();
                    break;
                case 8:
                    verEventosJaOcorreram();
                    break;
                case 9:
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
        System.out.println("Salvando dados...");
        service.salvarDados();
        System.out.println("Programa finalizado.");
    }

    private static void mostrarMenu() {
        System.out.println("\nMenu:");
        System.out.println("1 - Cadastrar Usuário");
        System.out.println("2 - Cadastrar Evento");
        System.out.println("3 - Listar Eventos");
        System.out.println("4 - Participar de Evento");
        System.out.println("5 - Cancelar Participação");
        System.out.println("6 - Ver Eventos Confirmados");
        System.out.println("7 - Ver Eventos Ocorrendo Agora");
        System.out.println("8 - Ver Eventos Já Ocorridos");
        System.out.println("9 - Sair");
    }

    private static void cadastrarUsuario() {
        System.out.println("\nCadastro de Usuário:");
        String nome = lerString("Nome: ");
        String email = lerString("Email: ");
        String telefone = lerString("Telefone: ");
        Usuario u = service.cadastrarUsuario(nome, email, telefone);
        System.out.println("Usuário cadastrado com ID: " + u.getId());
    }

    private static void cadastrarEvento() {
        System.out.println("\nCadastro de Evento:");
        String nome = lerString("Nome do evento: ");
        String endereco = lerString("Endereço: ");
        System.out.println("Categorias disponíveis:");
        for (Categoria c : Categoria.values()) {
            System.out.println("- " + c);
        }
        Categoria categoria = null;
        while (categoria == null) {
            String catStr = lerString("Categoria: ").toUpperCase();
            try {
                categoria = Categoria.valueOf(catStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Categoria inválida. Tente novamente.");
            }
        }
        LocalDateTime horario = null;
        while (horario == null) {
            String dataStr = lerString("Horário (dd/MM/yyyy HH:mm): ");
            try {
                horario = LocalDateTime.parse(dataStr, formatter);
            } catch (Exception e) {
                System.out.println("Formato inválido. Tente novamente.");
            }
        }
        String descricao = lerString("Descrição: ");
        Evento e = service.cadastrarEvento(nome, endereco, categoria, horario, descricao);
        System.out.println("Evento cadastrado com ID: " + e.getId());
    }

    private static void listarEventos() {
        System.out.println("\nLista de Eventos:");
        List<Evento> eventos = service.listarEventos();
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return;
        }
        for (Evento e : eventos) {
            System.out.println(e);
        }
    }

    private static void participarEvento() {
        System.out.println("\nParticipar de Evento:");
        int usuarioId = lerInt("ID do usuário: ");
        int eventoId = lerInt("ID do evento: ");
        boolean sucesso = service.participarEvento(eventoId, usuarioId);
        if (sucesso) {
            System.out.println("Participação confirmada.");
        } else {
            System.out.println("Erro ao confirmar participação. Verifique os IDs.");
        }
    }

    private static void cancelarParticipacao() {
        System.out.println("\nCancelar Participação:");
        int usuarioId = lerInt("ID do usuário: ");
        int eventoId = lerInt("ID do evento: ");
        boolean sucesso = service.cancelarParticipacao(eventoId, usuarioId);
        if (sucesso) {
            System.out.println("Participação cancelada.");
        } else {
            System.out.println("Erro ao cancelar participação. Verifique os IDs.");
        }
    }

    private static void verEventosConfirmados() {
        System.out.println("\nEventos Confirmados:");
        int usuarioId = lerInt("ID do usuário: ");
        List<Evento> confirmados = service.eventosConfirmados(usuarioId);
        if (confirmados.isEmpty()) {
            System.out.println("Nenhum evento confirmado para este usuário.");
            return;
        }
        for (Evento e : confirmados) {
            System.out.println(e);
        }
    }

    private static void verEventosOcorrrendoAgora() {
        System.out.println("\nEventos Ocorrendo Agora:");
        List<Evento> ocorrendo = service.eventosOcorrrendoAgora();
        if (ocorrendo.isEmpty()) {
            System.out.println("Nenhum evento ocorrendo no momento.");
            return;
        }
        for (Evento e : ocorrendo) {
            System.out.println(e);
        }
    }

    private static void verEventosJaOcorreram() {
        System.out.println("\nEventos Já Ocorridos:");
        List<Evento> passados = service.eventosJaOcorreram();
        if (passados.isEmpty()) {
            System.out.println("Nenhum evento já ocorreu.");
            return;
        }
        for (Evento e : passados) {
            System.out.println(e);
        }
    }

    // Métodos auxiliares para leitura
    private static String lerString(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    private static int lerInt(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                String linha = scanner.nextLine();
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Tente novamente.");
            }
        }
    }
}
