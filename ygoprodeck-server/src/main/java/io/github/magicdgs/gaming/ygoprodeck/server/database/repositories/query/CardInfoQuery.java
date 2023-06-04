package io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query;

import io.github.magicdgs.gaming.ygoprodeck.server.DatabaseApiDelegate.GetCardInfoQuery;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query.CardQuery;


public record CardInfoQuery(GetCardInfoQuery query)
        implements CardQuery<GetCardInfoQuery> {

}
