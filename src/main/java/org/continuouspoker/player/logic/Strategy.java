package org.continuouspoker.player.logic;

import org.continuouspoker.player.model.Bet;
import org.continuouspoker.player.model.Card;
import org.continuouspoker.player.model.Player;
import org.continuouspoker.player.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Strategy {

    public Bet decide(final Table table) {
        Bet bet = new Bet().bet(0);

        Player own = table.getPlayers().get(table.getActivePlayer());

        List<Card> cardsTable = table.getCommunityCards();
        List<Card> ourCards = own.getCards();
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(cardsTable);
        allCards.addAll(ourCards);

        Map<String, Integer> rankCount = new HashMap<>();
        allCards.stream()
                .map(card -> card.getRank().getValue()).collect(Collectors.toList()).forEach(rank -> {
                    int count = 0;
                    if (rankCount.containsKey(rank)) {
                        count = rankCount.get(rank);
                    }
                    rankCount.put(rank, ++count);
                });

        List<String> ownRank = ourCards.stream().map(card -> card.getRank().getValue()).collect(Collectors.toList());

        boolean hasGoodCard = false;
        boolean hasAllInCard = false;

        if (isaBoolean(rankCount, 4, ownRank)) {
            hasGoodCard = true;
            hasAllInCard = true;
        } else if (isaBoolean(rankCount, 3, ownRank) && isaBoolean(rankCount, 2, ownRank)) {
            hasGoodCard = true;
            hasAllInCard = true;
        } else if (isaBoolean(rankCount, 3, ownRank)) {
            hasGoodCard = true;
        } else if (isaBoolean(rankCount, 2, ownRank)) {
            hasGoodCard = true;
        }

        if (table.getRound() <= 3) bet.bet(table.getMinimumBet());

        if (hasGoodCard && table.getRound() <= 4) {
            bet.bet(table.getMinimumRaise());
        } else if (hasGoodCard) {
            bet.bet(table.getMinimumBet());
        }
        if (hasAllInCard) {
            bet.bet(table.getMinimumRaise());
        }

        return bet;
    }

    private boolean isaBoolean(Map<String, Integer> rankCount, int value, List<String> ownRank) {
        return rankCount.containsValue(value) &&
                keys(rankCount, value).collect(Collectors.toList()).contains(ownRank.get(0)) ||
                keys(rankCount, value).collect(Collectors.toList()).contains(ownRank.get(1));
    }


    public <K, V> Stream<K> keys(Map<K, V> map, V value) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey);
    }

}
