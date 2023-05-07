package io.github.magicdgs.gaming.ygoprodeck.testutils;

import io.github.magicdgs.gaming.ygoprodeck.model.*;

import java.util.List;

// TODO: the resources should be updated from time to time
// TODO: maybe use a script for it that runs periodically with GitHub Actions
// TODO: that will be better that having here a test against the real API
public interface DatabaseClientTester {
    List<CheckDBVersionDTO> callGetCheckDbVersion() throws Exception;

    List<CardSetItemDTO> callGetCardSets() throws Exception;

    List<ArchetypesItemDTO> callGetArchetypes() throws Exception;

    CardSetInfo callGetCardSetInfo(String setCode) throws Exception;

    Card callGetRandomCard() throws Exception;

    CardInfoDTO callGetCardInfo() throws Exception;

    CardInfoDTO callGetCardInfoWithMiscParam(YesSwitch yesSwitch) throws Exception;

    CardInfoDTO callGetCardInfoWithPagination(final int num, final int offset) throws Exception;

    void callGetCardInfoWithWrongTypeParam(final String wrongType) throws Exception;

}
