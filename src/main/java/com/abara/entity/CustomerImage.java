package com.abara.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class CustomerImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_image_seq")
    @SequenceGenerator(
            name = "customer_image_seq",
            sequenceName = "customer_image_sequence",
            allocationSize = 20
    )
    private Long id;

    @NotNull
    @Size(max = 256)
    private String name;

    @NotNull
    @Size(max = 256)
    private String type;

    @Lob
    @NotEmpty
    @Size(max = 1048576)
    private byte[] data;

    CustomerImage() {
    }

    public CustomerImage(@NotNull String name, @NotNull String type, @NotEmpty byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object that) {
        if (getClass() != that.getClass()) return false;
        return EqualsBuilder.reflectionEquals(this, that, "id");
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, "data");
    }

}
