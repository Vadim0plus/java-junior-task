package com.mcb.javajuniortask.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Debt {
    @Id
    private UUID id;
    private BigDecimal value;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!Debt.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        final Debt other = (Debt) o;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
