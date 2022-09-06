package com.example.football.models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity(name = "stats")
public class Stat extends BaseEntity{
    private Double shooting;
    private Double passing;
    private Double endurance;
    private Set<Player> players;

    public Stat() {
    }

    @Column(nullable = false)
    public Double getShooting() {
        return shooting;
    }

    public void setShooting(Double shooting) {
        this.shooting = shooting;
    }

    @Column(nullable = false)
    public Double getPassing() {
        return passing;
    }

    public void setPassing(Double passing) {
        this.passing = passing;
    }

    @Column(nullable = false)
    public Double getEndurance() {
        return endurance;
    }

    public void setEndurance(Double endurance) {
        this.endurance = endurance;
    }

    @OneToMany(mappedBy = "stat", fetch = FetchType.EAGER)
    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }
}
