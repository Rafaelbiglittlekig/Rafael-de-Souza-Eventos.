package service;

import model.Categoria;
import model.Evento;
import model.Usuario;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EventoService {
    private List<Evento> eventos = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private int proximoIdEvento = 1;
    private int proximoIdUsuario = 1;
    private final String ARQUIVO = "events.data";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Cadastrar usuário
    public Usuario cadastrarUsuario(String nome, String email, String telefone) {
        Usuario u = new Usuario(proximoIdUsuario++, nome, email, telefone);
        usuarios.add(u);
        return u;
    }

    // Cadastrar evento
    public Evento cadastrarEvento(String nome, String endereco, Categoria categoria, LocalDateTime horario, String descricao) {
        Evento e = new Evento(proximoIdEvento++, nome, endereco, categoria, horario, descricao);
        eventos.add(e);
        return e;
    }

    // Listar eventos ordenados por horário
    public List<Evento> listarEventos() {
        eventos.sort(Comparator.comparing(Evento::getHorario));
        return eventos;
    }

    // Buscar usuário por id
    public Usuario buscarUsuarioPorId(int id) {
        for (Usuario u : usuarios) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    // Buscar evento por id
    public Evento buscarEventoPorId(int id) {
        for (Evento e : eventos) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    // Participar de evento
    public boolean participarEvento(int eventoId, int usuarioId) {
        Evento e = buscarEventoPorId(eventoId);
        Usuario u = buscarUsuarioPorId(usuarioId);
        if (e != null && u != null) {
            e.adicionarParticipante(usuarioId);
            return true;
        }
        return false;
    }

    // Cancelar participação
    public boolean cancelarParticipacao(int eventoId, int usuarioId) {
        Evento e = buscarEventoPorId(eventoId);
        if (e != null) {
            e.removerParticipante(usuarioId);
            return true;
        }
        return false;
    }

    // Eventos confirmados para usuário
    public List<Evento> eventosConfirmados(int usuarioId) {
        List<Evento> confirmados = new ArrayList<>();
        for (Evento e : eventos) {
            if (e.getParticipantesIds().contains(usuarioId)) {
                confirmados.add(e);
            }
        }
        confirmados.sort(Comparator.comparing(Evento::getHorario));
        return confirmados;
    }

    // Eventos ocorrendo agora
    public List<Evento> eventosOcorrrendoAgora() {
        List<Evento> ocorrendo = new ArrayList<>();
        for (Evento e : eventos) {
            if (e.estaOcorrrendoAgora()) {
                ocorrendo.add(e);
            }
        }
        return ocorrendo;
    }

    // Eventos já ocorridos
    public List<Evento> eventosJaOcorreram() {
        List<Evento> passados = new ArrayList<>();
        for (Evento e : eventos) {
            if (e.jaOcorreu()) {
                passados.add(e);
            }
        }
        return passados;
    }

    // Salvar eventos e usuários no arquivo
    public void salvarDados() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            // Salvar usuários
            writer.write("#USUARIOS");
            writer.newLine();
            for (Usuario u : usuarios) {
                writer.write(u.getId() + ";" + u.getNome() + ";" + u.getEmail() + ";" + u.getTelefone());
                writer.newLine();
            }
            // Salvar eventos
            writer.write("#EVENTOS");
            writer.newLine();
            for (Evento e : eventos) {
                String participantesStr = "";
                if (!e.getParticipantesIds().isEmpty()) {
                    participantesStr = String.join(",", e.getParticipantesIds().stream().map(String::valueOf).toArray(String[]::new));
                }
                writer.write(e.getId() + ";" + e.getNome() + ";" + e.getEndereco() + ";" + e.getCategoria() + ";" +
                        e.getHorario().format(formatter) + ";" + e.getDescricao() + ";" + participantesStr);
                writer.newLine();
            }
        } catch (IOException ex) {
            System.out.println("Erro ao salvar dados: " + ex.getMessage());
        }
    }

    // Carregar dados do arquivo
    public void carregarDados() {
        if (!Files.exists(Paths.get(ARQUIVO))) {
            return; // arquivo não existe, nada a carregar
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            boolean lendoUsuarios = false;
            boolean lendoEventos = false;
            usuarios.clear();
            eventos.clear();
            while ((linha = reader.readLine()) != null) {
                if (linha.equals("#USUARIOS")) {
                    lendoUsuarios = true;
                    lendoEventos = false;
                    continue;
                } else if (linha.equals("#EVENTOS")) {
                    lendoUsuarios = false;
                    lendoEventos = true;
                    continue;
                }
                if (lendoUsuarios) {
                    // linha formato: id;nome;email;telefone
                    String[] partes = linha.split(";");
                    if (partes.length == 4) {
                        int id = Integer.parseInt(partes[0]);
                        String nome = partes[1];
                        String email = partes[2];
                        String telefone = partes[3];
                        Usuario u = new Usuario(id, nome, email, telefone);
                        usuarios.add(u);
                        if (id >= proximoIdUsuario) proximoIdUsuario = id + 1;
                    }
                } else if (lendoEventos) {
                    // linha formato: id;nome;endereco;categoria;horario;descricao;participantes
                    String[] partes = linha.split(";", 7);
                    if (partes.length >= 6) {
                        int id = Integer.parseInt(partes[0]);
                        String nome = partes[1];
                        String endereco = partes[2];
                        Categoria categoria = Categoria.valueOf(partes[3]);
                        LocalDateTime horario = LocalDateTime.parse(partes[4], formatter);
                        String descricao = partes[5];
                        Evento e = new Evento(id, nome, endereco, categoria, horario, descricao);
                        if (partes.length == 7 && !partes[6].isEmpty()) {
                            String[] participantesStr = partes[6].split(",");
                            for (String p : participantesStr) {
                                e.adicionarParticipante(Integer.parseInt(p));
                            }
                        }
                        eventos.add(e);
                        if (id >= proximoIdEvento) proximoIdEvento = id + 1;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Erro ao carregar dados: " + ex.getMessage());
        }
    }
}
