package io.github.magicdgs.gaming.ygoprodeck.server.database;

import io.github.magicdgs.gaming.ygoprodeck.model.ArchetypesItemDTO;
import io.github.magicdgs.gaming.ygoprodeck.model.Card;
import io.github.magicdgs.gaming.ygoprodeck.model.CardInfoDTO;
import io.github.magicdgs.gaming.ygoprodeck.server.common.DatabaseReloader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.github.magicdgs.gaming.ygoprodeck.server.DatabaseApi;
import io.github.magicdgs.gaming.ygoprodeck.server.DatabaseApiDelegate;
import lombok.AllArgsConstructor;

import java.util.List;


@RestController
@RequestMapping("/api/v7")
@AllArgsConstructor
public class YgoprodeckDatabaseApiController implements DatabaseApi {

    private final DatabaseService service;

    @Override
    public DatabaseApiDelegate getDelegate() {
        return service;
    }

}
