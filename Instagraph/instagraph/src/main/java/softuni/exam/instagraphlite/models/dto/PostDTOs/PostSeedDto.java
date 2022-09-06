package softuni.exam.instagraphlite.models.dto.PostDTOs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "post")
@XmlAccessorType(XmlAccessType.FIELD)
public class PostSeedDto {

    @XmlElement(name = "caption")
    private String caption;
    @XmlElement
    private UsernameDTO user;
    @XmlElement
    private PathDto picture;

    @Size(min = 21)
    @NotBlank
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public UsernameDTO getUser() {
        return user;
    }

    public void setUser(UsernameDTO user) {
        this.user = user;
    }

    public PathDto getPicture() {
        return picture;
    }

    public void setPicture(PathDto picture) {
        this.picture = picture;
    }
}
