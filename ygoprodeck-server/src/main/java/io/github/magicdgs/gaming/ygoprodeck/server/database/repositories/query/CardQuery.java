package io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query;

import io.github.magicdgs.gaming.ygoprodeck.server.DatabaseApiDelegate;

public interface CardQuery<T> {

    public T query();

}
