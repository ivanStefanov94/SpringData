package exam.model.dto.ShopDto;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

//@XmlAccessorType(XmlAccessType.FIELD)
public class TownNameDto {

 //   @XmlElement
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
