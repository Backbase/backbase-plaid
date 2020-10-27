package com.backbase.proto.plaid.mapper;

import com.backbase.proto.plaid.model.Institution;
import com.backbase.proto.plaid.model.Item;
import com.backbase.proto.plaid.model.LinkItem;
import com.backbase.proto.plaid.repository.AccountRepository;
import com.backbase.proto.plaid.repository.InstitutionRepository;
import liquibase.pro.packaged.L;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class LinkItemMapper {

    InstitutionRepository institutionRepository;
    AccountRepository accountRepository;

    private LinkItem map(Item item){
        LinkItem itemForDisplay = new LinkItem();
        itemForDisplay.itemId(item.getItemId());
        Institution institution = institutionRepository.getByInstitutionId(item.getInstitutionId()).orElseThrow(() -> new IllegalArgumentException("Instition not found"));
        itemForDisplay.institutionName(institution.getName());
        List<String> accounts = accountRepository.findAllByItemId(item.getItemId()).stream().map(account -> account.getName()).collect(Collectors.toList());
        itemForDisplay.accounts(accounts);
        itemForDisplay.experationDate(item.getExpiryDate());

        return itemForDisplay;
    }

    public List<LinkItem> map(List<Item> items){
        return items.stream().map(item -> map(item)).collect(Collectors.toList());
    }
}
