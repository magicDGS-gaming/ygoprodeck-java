package io.github.magicdgs.gaming.ygoprodeck.testutils;

import io.github.magicdgs.gaming.ygoprodeck.model.*;

import java.io.Closeable;
import java.util.List;

import static io.github.magicdgs.gaming.ygoprodeck.api.DatabaseApi.*;

// TODO: the resources should be updated from time to time
// TODO: maybe use a script for it that runs periodically with GitHub Actions
// TODO: that will be better that having here a test against the real API
public interface DatabaseClientTester {
    List<CheckDBVersionDTO> callGetCheckDbVersion() throws Exception;

    List<CardSetItemDTO> callGetCardSets() throws Exception;

    List<ArchetypesItemDTO> callGetArchetypes() throws Exception;

    CardSetInfo callGetCardSetInfo(final GetCardSetInfoQueryMap params) throws Exception;

    Card callGetRandomCard() throws Exception;

    CardInfoDTO callGetCardInfo(final GetCardInfoQueryMap params) throws Exception;

    CardInfoDTO callGetCardInfoWithMiscParam(final GetCardInfoQueryMap params) throws Exception;

    CardInfoDTO callGetCardInfoWithPagination(final GetCardInfoQueryMap params) throws Exception;

    void callGetCardInfoWithWrongTypeParam(final GetCardInfoQueryMap params) throws Exception;

}
