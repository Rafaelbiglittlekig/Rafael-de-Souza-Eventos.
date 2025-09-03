package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Evento {
    private int id;
    private String nome;
    private String endereco;
    private Categoria categoria;
    private LocalDateTime horario;
    private String descricao;
    private List<Integer> participantesIds; // armazenar ids dos usuários participantes

    public Evento(int id, String nome, String endereco, Categoria categoria, LocalDateTime horario, String descricao) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
        this.participantesIds = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Integer> getParticipantesIds() {
        return participantesIds;
    }

    public void adicionarParticipante(int usuarioId) {
        if (!participantesIds.contains(usuarioId)) {
            participantesIds.add(usuarioId);
        }
    }

    public void removerParticipante(int usuarioId) {
        participantesIds.remove(Integer.valueOf(usuarioId));
    }

    public boolean estaOcorrrendoAgora() {
        LocalDateTime agora = LocalDateTime.now();
        // Considera evento ocorrendo se estiver no mesmo dia e hora +/- 1 hora
        return !agora.isBefore(horario) && agora.isBefore(horario.plusHours(1));
    }

    public boolean jaOcorreu() {
        return LocalDateTime.now().isAfter(horario.plusHours(1));
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Evento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", categoria=" + categoria +
                ", horario=" + horario.format(fmt) +
                ", descricao='" + descricao + '\'' +
                ", participantes=" + participantesIds.size() +
                '}';
    }
}
