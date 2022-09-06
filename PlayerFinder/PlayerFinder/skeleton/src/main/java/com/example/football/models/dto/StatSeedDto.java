package com.example.football.models.dto;

import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stat")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatSeedDto {

    @XmlElement
    private Double passing;
    @XmlElement
    private Double shooting;
    @XmlElement
    private Double endurance;

    @Positive
    public Double getPassing() {
        return passing;
    }

    public void setPassing(Double passing) {
        this.passing = passing;
    }

    @Positive
    public Double getShooting() {
        return shooting;
    }

    public void setShooting(Double shooting) {
        this.shooting = shooting;
    }

    @Positive
    public Double getEndurance() {
        return endurance;
    }

    public void setEndurance(Double endurance) {
        this.endurance = endurance;
    }
}
