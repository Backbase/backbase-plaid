package com.backbase.proto.plaid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "mask")
    private String mask;

    @Column(name = "name")
    private String name;

    @Column(name = "sub_type")
    private String subtype;

    @Column(name = "type")
    private String type;


    public Account withItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

}