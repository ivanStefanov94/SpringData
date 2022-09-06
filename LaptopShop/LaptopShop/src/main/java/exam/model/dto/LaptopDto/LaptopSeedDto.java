package exam.model.dto.LaptopDto;

import com.google.gson.annotations.Expose;
import exam.model.entity.enums.WarrantyType;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class LaptopSeedDto {

    @Expose
    private String macAddress;
    @Expose
    private double cpuSpeed;
    @Expose
    private Integer ram;
    @Expose
    private Integer storage;
    @Expose
    private String description;
    @Expose
    private BigDecimal price;
    @Expose
    private WarrantyType warrantyType;
    @Expose
    private ShopNameDto shop;


    @Size(min = 9)
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Positive
    public double getCpuSpeed() {
        return cpuSpeed;
    }

    public void setCpuSpeed(double cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    @Min(8)
    @Max(128)
    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    @Min(128)
    @Max(1024)
    public Integer getStorage() {
        return storage;
    }

    public void setStorage(Integer storage) {
        this.storage = storage;
    }

    @Size(min = 10)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Positive
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @NotNull
    public WarrantyType getWarrantyType() {
        return warrantyType;
    }

    public void setWarrantyType(WarrantyType warrantyType) {
        this.warrantyType = warrantyType;
    }

    @NotNull
    public ShopNameDto getShop() {
        return shop;
    }

    public void setShop(ShopNameDto shop) {
        this.shop = shop;
    }
}
