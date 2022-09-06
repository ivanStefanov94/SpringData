package com.example.football.models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity(name = "towns")
public class Town extends BaseEntity{

    private String name;
    private Integer population;
    private String travelGuide;


    public Town() {
    }

    @Column(unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false)
    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    @Column(name = "travel_guide", columnDefinition = "TEXT", nullable = false)
    public String getTravelGuide() {
        return travelGuide;
    }

    public void setTravelGuide(String travelGuide) {
        this.travelGuide = travelGuide;
    }


}
